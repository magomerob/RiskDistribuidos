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
    private boolean enSala;
    private String nombre;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.enSala = false;
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
                System.out.println(msg);
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

    protected void broadcast(List<Sala> salas) {
            System.out.println("enviando salas");
            String msg = Mensaje.listaSalas(salas);
            System.out.println(msg);
            out.println(msg);
            out.flush();
    }

    public boolean isEnSala(){
        return this.enSala;
    }

    private void procesarMensaje(String mensaje){
        String[] separado = mensaje.split(Protocolo.DEL);
        if(separado[0].equals(Protocolo.CREAR_SALA)){
            boolean creada = Servidor.crearSala(separado[1], Integer.parseInt(separado[2]), clientSocket);
            out.println(Mensaje.salaCreada(creada));
            out.flush();
        }
        if(separado[0].equals(Protocolo.UNIRSE_SALA)){
            Servidor.unirClienteASala(clientSocket, separado[1]);
        }
        if(separado[0].equals(Protocolo.INICIAR_SESION)){
            this.nombre = separado[1];
        }
    }
}
