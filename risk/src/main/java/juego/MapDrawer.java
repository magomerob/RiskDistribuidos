package juego;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MapDrawer {
    public static final int CANVAS_WIDTH = 1000;
    public static final int CANVAS_HEIGHT = 1000;
    private HashMap<String, Pais> paises;
    private static final String mapaPaises = "risk\\src\\main\\resources\\paises.json";
    private static final String caminos = "risk\\src\\main\\resources\\caminos.json";
    private Pane view;

    public MapDrawer() throws IOException {
        paises = new HashMap<>();
        this.view = new Pane();
        JSONObject jsonPaises = loadData(mapaPaises);
        JSONObject jsonCaminos = loadData(caminos);
        drawCountries(jsonPaises);
        drawCaminos(jsonCaminos);
    }


    protected Pane getView() {
        return view;
    }

    private JSONObject loadData(String filename) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line);
            }
        }
        return new JSONObject(contentBuilder.toString());
    }

    private void drawCountries(JSONObject datapaises) {
        for (String name : datapaises.keySet()) {
            String path = datapaises.getJSONObject(name).getString("path");
            SVGPath svg = new SVGPath();

            svg.setContent(path);
            svg.setFill(Color.TRANSPARENT);
            svg.setStroke(Color.BLUE);
            svg.setStrokeWidth(2);

            Text texto = new Text("0");
            texto.setFill(Color.BLACK);
            texto.setStyle("-fx-font-weight: bold;");

            //Fondo del texto
            Rectangle background = new Rectangle();
            background.setFill(Color.WHITE);
            background.setStroke(Color.GRAY);
            double width = texto.boundsInLocalProperty().get().getWidth()+20;
            background.setWidth(width);
            double height = texto.boundsInLocalProperty().get().getHeight();
            background.setHeight(height);
            Bounds bounds = svg.getLayoutBounds();

            //Redireccion de los eventos del texto al svg
            background.setOnMouseClicked(event -> svg.fireEvent(event));
            texto.setOnMouseClicked(event -> svg.fireEvent(event));

            texto.setX(bounds.getCenterX());
            texto.setY(bounds.getCenterY());
            background.setX(bounds.getCenterX());
            background.setY(bounds.getCenterY()-height/2);
            this.view.getChildren().add(svg);
            this.view.getChildren().add(background);
            this.view.getChildren().add(texto);
            Pais p = new Pais(svg, texto);
            paises.put(name, p);
        }
    }

    private void drawCaminos(JSONObject datacaminos) {
        for (String name : datacaminos.keySet()) {
            String path = datacaminos.getJSONObject(name).getString("path");
            SVGPath svg = new SVGPath();

            svg.setContent(path);
            svg.setStroke(Color.BLACK);
            svg.setStrokeWidth(2);

            this.view.getChildren().add(svg);
        }
    }

    public HashMap<String, Pais> getPaises(){
        return paises;
    }
}
