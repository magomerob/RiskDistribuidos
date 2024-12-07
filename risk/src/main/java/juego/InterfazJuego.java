package juego;

import java.io.IOException;
import java.net.Socket;

import cliente.App;
import cliente.SeleccionNombre;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public class InterfazJuego{

    private App parent;
    private Pane view;
    private Juego juego;

    public void setJuego(Juego juego) {
        this.juego = juego;
    }

    public InterfazJuego(App _parent){
        this.parent = _parent;
        try {
            MapDrawer mapDrawer = new MapDrawer();
            this.view = mapDrawer.getView();
            for (String npais : mapDrawer.getPaises().keySet()) {
                Pais pais = mapDrawer.getPaises().get(npais);
                SVGPath paisvg = pais.getSvg();

                paisvg.setOnMouseClicked(event -> {

                    pais.setNumtropas(pais.getNumtropas()+1);
                    pais.getTexto().setText(Integer.toString( pais.getNumtropas()));
                    System.out.println("Hay "+ Integer.toString( pais.getNumtropas())+" tropas en "+npais);
            });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Pane getView() {
        return view;
    }


}
