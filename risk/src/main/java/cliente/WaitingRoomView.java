package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import protocolo.Protocolo;
import servidor.Jugador;
import servidor.Sala;

public class WaitingRoomView {

    private App parent;
    private VBox view;
    private PrintWriter out = null;
    private BufferedReader inp = null;
    private List<Jugador> jugadores = new ArrayList<>();
    @FXML
    private ListView<String> listView;

    public WaitingRoomView(Socket s, Sala sala, App _parent) {
        try {
            this.parent = _parent;
            this.out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));
            this.inp = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("espera.fxml"));
            loader.setController(this);
            view = loader.load();
            
            Thread escuchar = new Thread(() -> escuchar());
            escuchar.start();

            this.out.println(Protocolo.UNIRSE_SALA+Protocolo.DEL+sala.getNombre());
            this.out.flush();

            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected VBox getView() {
        return view;
    }

    private void escuchar(){
        try{
            String msg = this.inp.readLine();
            while (msg != null) {
                procesarMensaje(msg);
                msg = this.inp.readLine();      
            }

            System.out.println("dejo de escuchar");
        }catch(IOException e){
            e.printStackTrace();
        }
        
    }

    private void procesarMensaje(String msg) {
        String[] separado = msg.split(Protocolo.DEL);
        if(separado[0].equals(Protocolo.ACTUALIZAR_SALA)){
            this.jugadores.clear();
            for (int i = 1; i < separado.length; i+=3) {
                InetAddress ip = null;
                try {
                    ip = InetAddress.getByName(separado[i+1]);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                Jugador j  = new Jugador(separado[i], ip);
                j.setListo(Boolean.parseBoolean(separado[i+2]));
                this.jugadores.add(j);
            }
            this.mostrarJugadores();
        }
    }
            
    private void mostrarJugadores() {
        this.listView.getItems().clear();
        for (Jugador j : jugadores) {
            if(j.isListo()){
                this.listView.getItems().add(j.getNombre()+" [LISTO]");
            }else{
                this.listView.getItems().add(j.getNombre()+" [ESPERANDO]");
            }
            
        }
    }
}
