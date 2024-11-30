package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import javafx.scene.layout.StackPane;
import protocolo.Mensaje;
import protocolo.Protocolo;

import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader inp;
    private String nombreSala;
    private Jugador jugador;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try{
            this.out = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream(), StandardCharsets.UTF_8));
            this.inp = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream(), StandardCharsets.UTF_8));

            System.out.println("Cliente conectado");

            Servidor.broadcastSalas();
            
            String msg;

            while (!this.clientSocket.isClosed()) {
                msg = inp.readLine();
                procesarMensaje(msg);
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void broadcast(String s) {
            out.println(s);
            out.flush();
    }

    public boolean isEnSala(){
        if(this.nombreSala == null){
            return false;
        }
        return true;
    }

    public boolean isEnNombreSala(String nombre){
        if(this.nombreSala == null){
            return false;
        }
        if(this.nombreSala.equals(nombre)){
            return true;
        }
        return false;
    }

    private void procesarMensaje(String mensaje){
        String[] separado = mensaje.split(Protocolo.DEL);
        if(separado[0].equals(Protocolo.CREAR_SALA)){
            boolean creada = Servidor.crearSala(separado[1], Integer.parseInt(separado[2]), this);
            out.println(Mensaje.salaCreada(creada));
            out.flush();
        }
        if(separado[0].equals(Protocolo.UNIRSE_SALA)){
            this.nombreSala = separado[1];
            Servidor.unirClienteASala(this, separado[1]);            
        }
        if(separado[0].equals(Protocolo.INICIAR_SESION)){
            this.jugador =  new Jugador(separado[1], this.clientSocket.getInetAddress());
        }
        if(separado[0].equals(Protocolo.SET_LISTO)){
            this.jugador.setListo(Boolean.parseBoolean(separado[2]));
            Servidor.actualizarListoSala(this.jugador, separado[1]);
        }
    }

    public Jugador getJugador(){
        return jugador;
    }
}
