package cliente;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
        VBox root = new VBox();

        mensajesArea = new TextArea();
        mensajesArea.setEditable(false);
        mensajeInput = new TextField();
        enviarBtn = new Button("Enviar");

        enviarBtn.setOnAction(e -> enviarMensaje());

        root.getChildren().addAll(mensajesArea, mensajeInput, enviarBtn);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Cliente JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Intentar conectar al servidor
        conectarAlServidor();
    }

    // Método para conectar al servidor
    private void conectarAlServidor() {
        try {
            socket = new Socket("localhost", 12345); // Conectar al servidor en localhost y puerto 12345
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Hilo para escuchar mensajes del servidor
            new Thread(() -> {
                try {
                    String respuesta;
                    while ((respuesta = input.readLine()) != null) {
                        String finalRespuesta = respuesta;
                        // Actualizar el TextArea en el hilo de la GUI
                        javafx.application.Platform.runLater(() -> {
                            mensajesArea.appendText(finalRespuesta + "\n");
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            mensajesArea.appendText("Error al conectar con el servidor\n");
            e.printStackTrace();
        }
    }

    // Método para enviar mensaje al servidor
    private void enviarMensaje() {
        String mensaje = mensajeInput.getText();
        if (mensaje != null && !mensaje.isEmpty()) {
            output.println(mensaje); // Enviar mensaje al servidor
            mensajeInput.clear();    // Limpiar el campo de texto
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
