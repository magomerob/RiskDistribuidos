package juego;

import java.util.List;

import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

public class Pais {
    private SVGPath svg;
    private Text texto;
    private List<String> conexiones;
    private int numtropas;
    private int jugadorControl;

    public Pais(SVGPath _svg, Text _text){
        this.svg = _svg;
        this.texto = _text;
        this.jugadorControl = 0;
        this.numtropas = 0;
    }

    public List<String> getConexiones() {
        return conexiones;
    }
    public void setConexiones(List<String> conexiones) {
        this.conexiones = conexiones;
    }

    public Text getTexto() {
        return texto;
    }
    public void setTexto(Text texto) {
        this.texto = texto;
    }

    public SVGPath getSvg() {
        return svg;
    }
    public void setSvg(SVGPath svg) {
        this.svg = svg;
    }

    public int getNumtropas() {
        return numtropas;
    }

    public void setNumtropas(int numtropas) {
        this.numtropas = numtropas;
    }

    public int getJugadorControl() {
        return jugadorControl;
    }

    public void setJugadorControl(int jugadorControl) {
        this.jugadorControl = jugadorControl;
    }
}
