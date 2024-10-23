package cliente;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class App extends Application {

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    private TextArea mensajesArea;
    private TextField mensajeInput;
    private Button enviarBtn;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Polygon Drawer");

        String filename = "risk\\src\\main\\resources\\paises.json";

        try {
            MapDrawer mapDrawer = new MapDrawer(filename);
            Scene scene = new Scene(mapDrawer.getRoot(), MapDrawer.CANVAS_WIDTH, MapDrawer.CANVAS_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void conectarAlServidor() {
        try {
            socket = new Socket("localhost", 12345);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));


        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    private void enviarMensaje() {
        String mensaje = mensajeInput.getText();
        if (mensaje != null && !mensaje.isEmpty()) {
            output.println(mensaje);
            mensajeInput.clear();
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
}
