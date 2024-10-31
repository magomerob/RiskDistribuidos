package cliente;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private Socket socket;

    @Override
    public void start(Stage primaryStage) {
        try {
            socket = new Socket("localhost", 42000);
            
            LobbyView listView = new LobbyView(socket);
            
            Scene scene = new Scene(listView.getView());
            primaryStage.setTitle("Client ListView");
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
}
