package cliente;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import juego.InterfazJuego;
import juego.Juego;
import juego.MapDrawer;
import servidor.Sala;

public class App extends Application {

    private Socket socket;

    private Stage primaryStage;

    private String nombre = "Invitado";

    private Scene scene;

    @Override
    public void start(Stage _primaryStage) {
        this.primaryStage = _primaryStage;
        try {
            socket = new Socket("localhost", 42000);
            
            SeleccionNombre selNombre = new SeleccionNombre(this, socket);
            
            this.scene = new Scene(selNombre.getView());
            primaryStage.setTitle("Seleccion Nombre");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
    /*Iniciar interfaz juego (TEST)
    @Override
    public void start(Stage _primaryStage) {
        this.primaryStage = _primaryStage;
        primaryStage.setTitle("Polygon Drawer");

        InterfazJuego interfazJuego = new InterfazJuego(this);

        this.scene = new Scene(interfazJuego.getView(),1000,700);
        primaryStage.setTitle("Seleccion Nombre");
        primaryStage.setScene(scene);
        primaryStage.show();
    }*/

    public void iniciarJuego(String[] ips, int miturno){
        primaryStage.setTitle("Risk");

        InterfazJuego interfazJuego = new InterfazJuego(this);

        this.scene = new Scene(interfazJuego.getView(),1000,700);
        primaryStage.setTitle("Seleccion Nombre");
        primaryStage.setScene(scene);
        primaryStage.show();
        Juego j = new Juego(ips, miturno, interfazJuego);
        interfazJuego.setJuego(j);

        j.iniciarPartida();
    }   
    

    @Override
    public void stop() throws Exception {
        if (socket != null) {
            socket.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    protected void iniciarSalas(String nombre){
        if(nombre != null){
            this.nombre = nombre;
        }
        LobbyView listView = new LobbyView(socket, this);
        
        this.scene = new Scene(listView.getView());
        primaryStage.setTitle("Client ListView");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    protected void unirseSala(Sala s){

        WaitingRoomView listView = new WaitingRoomView(socket, s,  this, nombre);
        this.scene = new Scene(listView.getView());
        primaryStage.setTitle("Sala de espera: "+s.getNombre());
        primaryStage.setScene(scene);
        primaryStage.show();        
    }

}
