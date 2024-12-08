package juego;

import java.io.IOException;
import java.net.Socket;

import cliente.App;
import cliente.SeleccionNombre;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class InterfazJuego{

    private App parent;
    private Pane view;
    private Juego juego;
    private MapDrawer map;
    private boolean isClicked;
    private String selected;
    private int jugador;
    private Button boton;

    private static final Color[] colores = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.PURPLE, Color.BLACK, Color.GRAY};

    public void setJuego(Juego juego) {
        this.juego = juego;
    }

    public void setJugador(int j){
        this.jugador = j;
    }

    public Button getButton(){
        return boton;
    }

    public InterfazJuego(App _parent){
        this.parent = _parent;
        this.isClicked = false;
        try {
            this.map = new MapDrawer();
            this.view = map.getView();
            for (String npais : map.getPaises().keySet()) {
                Pais pais = map.getPaises().get(npais);
                SVGPath paisvg = pais.getSvg();

                paisvg.setOnMouseClicked(event -> clickPais(npais));

            }
            
            boton = new Button("Fin turno");

            boton.setLayoutX(50);
            boton.setLayoutY(20);

            boton.setOnMouseClicked(event ->{
                if(juego.isTurno()){
                    juego.acabarTurno();
                }
            });

            view.getChildren().add(boton);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void iniciarTurno(){
        this.isClicked = false;
        this.selected = null;
    }

    public void acabarTurno(){
        if(this.selected != null){
            map.getPaises().get(this.selected).getSvg().setStroke(Color.BLACK);
            map.getPaises().get(this.selected).getSvg().setStrokeWidth(1);
        }
    }

    public Pane getView() {
        return view;
    }
    private void clickPais(String npais){       
        if(this.juego.isTurno()){
                if(isClicked){
                    if(this.selected.equals(npais)){
                        map.getPaises().get(this.selected).getSvg().setStroke(Color.BLACK);
                        map.getPaises().get(this.selected).getSvg().setStrokeWidth(1);
                        this.selected = null;
                        this.isClicked = false;
                    }else if(map.getPaises().get(this.selected).getConexiones().contains(npais)){
                        map.getPaises().get(this.selected).getSvg().setStroke(Color.BLACK);
                        map.getPaises().get(this.selected).getSvg().setStrokeWidth(1);
                        juego.atacar(this.selected, npais);
                        this.selected = null;
                        this.isClicked = false;
                        
                    }
                }else if(!isClicked){
                    
                    if(map.getPaises().get(npais).getNumtropas() == 0){
                        return;
                    }
                    if(map.getPaises().get(npais).getJugadorControl() == this.jugador){
                        this.isClicked = true;
                        this.selected = npais;
                        map.getPaises().get(this.selected).getSvg().setStroke(Color.WHITE);
                        map.getPaises().get(this.selected).getSvg().setStrokeWidth(5);
                    }                                  
                    

                }
        }
    }

    protected void actualizarPais(String npais, int tropas, int jugador){
        Pais pais = map.getPaises().get(npais);
        pais.setNumtropas(tropas);
        pais.setJugadorControl(jugador);

        SVGPath paisvg = pais.getSvg();
        paisvg.setFill(colores[jugador]);

        Text texto = pais.getTexto();
        texto.setText(Integer.toString(tropas));
    }

    public MapDrawer getMap(){
        return map;
    }

}
