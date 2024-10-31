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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import servidor.Sala;

public class LobbyView {
    @FXML
    private ListView<String> listView;
    @FXML
    private TextField nombreSalaTF;
    @FXML
    private Button crearSalaButton;
    private PrintWriter output = null;
    private VBox view;
    private List<Sala> localsalas = new ArrayList<>();
    private ObjectInputStream ois;

    public LobbyView(Socket s) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("lobby.fxml"));
            loader.setController(this);
            view = loader.load();
        new Thread(() -> connectToServer(s)).start();

        crearSalaButton.setOnAction(event -> {
            String newItem = nombreSalaTF.getText();
            if (!newItem.isEmpty() && output != null) {
                this.output.println(newItem);
                this.output.flush();
                nombreSalaTF.clear();
            }
        });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected VBox getView() {
        return view;
    }

    private void connectToServer(Socket s) {
        try{

            this.ois = new ObjectInputStream(s.getInputStream());

            this.output = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));

            while ((localsalas = (List<Sala>) ois.readObject() ) != null) {
                List<String> textos = new ArrayList<>();
                for (Sala sala: localsalas){
                    textos.add(sala.getNombre()+" "+sala.getJugadores()+"/"+sala.getCapacidad());
                }
                Platform.runLater(() -> this.listView.getItems().clear());
                Platform.runLater(() -> this.listView.getItems().setAll(textos));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
