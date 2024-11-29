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

public class WaitingRoomView {

    private App parent;
    private VBox view;
    private PrintWriter out = null;
    private BufferedReader inp = null;
    private boolean admin = false;

    public WaitingRoomView(Socket s, Sala sala, App _parent, Boolean admin) {
        try {
            this.parent = _parent;
            this.admin = admin;
            this.out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8));
            this.inp = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("espera.fxml"));
            loader.setController(this);
            view = loader.load();
            
            this.out.println(Protocolo.UNIRSE_SALA+Protocolo.DEL+sala.getNombre());



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected VBox getView() {
        return view;
    }
}
