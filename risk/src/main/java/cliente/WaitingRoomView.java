package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrintColor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
    @FXML
    private Button listoButton;
    @FXML
    private Button salirButton;
    @FXML
    private Button enviarButton;
    @FXML
    private Label capacidadText;
    @FXML
    private TextArea logText;
    @FXML
    private TextField mensajeText;
    
    private Sala sala;
    private boolean listo;
    private String nombre;

    public WaitingRoomView(Socket s, Sala sala, App _parent, String nombre) {
        try {
            this.parent = _parent;
            this.sala = sala;
            this.out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));
            this.inp = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
            this.listo=false;
            this.nombre = nombre;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("espera.fxml"));
            loader.setController(this);
            view = loader.load();
            
            this.capacidadText.setText(sala.getNumJugadores()+"/"+sala.getCapacidad());
            this.logText.setEditable(false);
            
            Thread escuchar = new Thread(() -> escuchar());
            escuchar.start();

            this.out.println(Protocolo.UNIRSE_SALA+Protocolo.DEL+sala.getNombre());
            this.out.flush();

            listoButton.setOnAction(event -> {
                botonListo();
            });

            salirButton.setOnAction(event -> {
                salirSala();
            });

            enviarButton.setOnAction(event -> {
                enviarMensaje();
            });
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
            while (msg != null && this.sala!=null) {
                procesarMensaje(msg);
                msg = this.inp.readLine();      
            }
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
        }if(separado[0].equals(Protocolo.MENSAJE)){
            mostrarMensaje(separado[1]+": "+separado[2]);
        }
    }
            
    private void mostrarJugadores() {
        Platform.runLater(new Runnable() {
            @Override
            public void run(){
                listView.getItems().clear();
                for (Jugador j : jugadores) {
                    if(j.isListo()){
                        listView.getItems().add(j.getNombre()+" [LISTO]");
                    }else{
                        listView.getItems().add(j.getNombre()+" [ESPERANDO]");
                    }
                    
                }
                capacidadText.setText(jugadores.size()+"/"+sala.getCapacidad());
            }
            
        });
    }

    private void botonListo(){
        this.listo = !this.listo;

        if(this.listo){
            listoButton.setText("No estoy listo");
        }else{
            listoButton.setText("Listo");
        }

        this.out.println(Protocolo.SET_LISTO+Protocolo.DEL+this.sala.getNombre()+Protocolo.DEL+this.listo);
        this.out.flush();
    }

    private void salirSala(){
        String nombre = this.sala.getNombre();
        this.sala = null;
        parent.iniciarSalas(null);
        this.out.println(Protocolo.CERRAR+Protocolo.DEL+nombre);
        this.out.flush();
    }

    private void enviarMensaje(){
        String mensaje = this.mensajeText.getText();
        if(mensaje.isBlank()){return;}
        this.mensajeText.setText("");
        this.out.println(Protocolo.MENSAJE+Protocolo.DEL+this.sala.getNombre()+Protocolo.DEL+this.nombre+Protocolo.DEL+mensaje);
        this.out.flush();
    }

    private void mostrarMensaje(String msg){
        Platform.runLater(new Runnable() {
            @Override
            public void run(){
                String s = logText.getText();
                s+="\n";
                s+=msg;
                logText.setText(s);
        }});
    }
}
