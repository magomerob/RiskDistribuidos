package cliente;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
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

        this.nombre = nombre;

        LobbyView listView = new LobbyView(socket, this);
        
        this.scene = new Scene(listView.getView());
        primaryStage.setTitle("Client ListView");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void cargarMapa(Stage stage){
        stage.setTitle("Polygon Drawer");

        String filename = "risk\\src\\main\\resources\\paises.json";

        try {
            MapDrawer mapDrawer = new MapDrawer(filename);
            Scene scene = new Scene(mapDrawer.getRoot(), MapDrawer.CANVAS_WIDTH, MapDrawer.CANVAS_HEIGHT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void unirseSala(Sala s){

        WaitingRoomView listView = new WaitingRoomView(socket, s,  this);
        this.scene = new Scene(listView.getView());
        primaryStage.setTitle("Sala de espera: "+s.getNombre());
        primaryStage.setScene(scene);
        primaryStage.show();        
    }
}
