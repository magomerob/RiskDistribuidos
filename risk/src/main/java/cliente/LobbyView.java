package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import protocolo.Mensaje;
import protocolo.Protocolo;
import servidor.Sala;
import servidor.Servidor;

import java.nio.charset.StandardCharsets;

public class LobbyView {
    @FXML
    private ListView<String> listView;
    @FXML
    private TextField nombreSalaTF;
    @FXML
    private TextField capacidadSalaTF;
    @FXML
    private Button crearSalaButton;
    @FXML
    private Button unirseButton;

    private PrintWriter out = null;
    private VBox view;
    private List<Sala> localsalas = new ArrayList<>();
    private BufferedReader inp = null;
    private App parent;
    private boolean enLobby = true;
    private Thread conexionServidor;


    public LobbyView(Socket s, App _parent) {
        this.parent = _parent;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("lobby.fxml"));
            loader.setController(this);
            view = loader.load();
            conexionServidor = new Thread(() -> connectToServer(s));
            conexionServidor.start();

        crearSalaButton.setOnAction(event -> {
            nuevaSala();
        });

        unirseButton.setOnAction(event ->{
            int i = listView.getSelectionModel().getSelectedIndex();
            if(i==-1){return;}
            unirseSala(localsalas.get(i));
        });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nuevaSala() {
        if(this.nombreSalaTF.getText().isEmpty()){
            return;
        }
        int capacidad;
        try{
            capacidad = Integer.parseInt(capacidadSalaTF.getText());
        }catch(NumberFormatException e){
            capacidad = 4;
        }
        this.out.println(Mensaje.nuevaSala(
            nombreSalaTF.getText(),
            capacidad));
        this.out.flush();
        
    }

    protected VBox getView() {
        return view;
    }

    private void connectToServer(Socket s) {
        try{

            this.out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));
            this.inp = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
            
            String msg;

            while (enLobby) {
                msg = inp.readLine();            
                if(msg!=null){
                    System.out.println(msg);
                    procesarMensaje(msg);
                }                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void procesarMensaje(String mensaje) {
        String[] separado = mensaje.split(Protocolo.DEL);
        if(separado[0].equals(Protocolo.LISTA_SALAS)){
            this.localsalas.clear();
            for (int i = 1; i < separado.length; i+=3) {
                Sala nuevSala = new Sala(separado[i], Integer.parseInt(separado[i+2]));
                nuevSala.setNumJugadores(Integer.parseInt(separado[i+1]));
                this.localsalas.add(nuevSala);
            }
            mostrarSalas();
        }
        if(separado[0].equals(Protocolo.CREAR_SALA)){
            if(separado[1].equals(Protocolo.OK)){
                this.setEnLobby(false);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        int capacidad;
                        try{
                            capacidad = Integer.parseInt(capacidadSalaTF.getText());
                        }catch(NumberFormatException e){
                            capacidad = 4;
                        parent.unirseSala(new Sala(nombreSalaTF.getText(),capacidad));
                    }
                }
                });
            }
            
            if(separado[1].equals(Protocolo.ERROR)){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(AlertType.ERROR, "Error al crear la sala", ButtonType.OK);
                        alert.showAndWait();
                    }
                });                
            }
        }
    }

    private void unirseSala(Sala s){
        this.setEnLobby(false);
        parent.unirseSala(s);        
    }

    private void mostrarSalas(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listView.getItems().clear();
        for (Sala sala : localsalas) {
            listView.getItems().add(sala.getNombre()+" "+sala.getNumJugadores()+"/"+sala.getCapacidad());
        }
            }
        });
    }

    private void setEnLobby(boolean b){
        this.enLobby = b;
    }
}
