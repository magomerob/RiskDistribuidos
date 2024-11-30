package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
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
import servidor.Sala;

public class SeleccionNombre {
    private App parent;
    private VBox view;
    @FXML
    private TextField nombreTF;
    @FXML
    private Button unirseButton;
    private Socket s;

    public SeleccionNombre(App _parent, Socket _s){
        this.parent = _parent;
        this.s=_s;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("nombre.fxml"));
            loader.setController(this);
            view = loader.load();

            unirseButton.setOnAction(event -> {
                unirse();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unirse(){
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(this.s.getOutputStream(), StandardCharsets.UTF_8));
            if(nombreTF.getText().isEmpty()){return;}
            if(nombreTF.getText().contains(Protocolo.DEL)){return;}
            out.println(Protocolo.INICIAR_SESION+Protocolo.DEL+nombreTF.getText());
            out.flush();
            parent.iniciarSalas(nombreTF.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected VBox getView() {
        return view;
    }
}