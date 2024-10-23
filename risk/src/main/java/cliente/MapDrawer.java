package cliente;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MapDrawer {
    public static final int CANVAS_WIDTH = 1000;
    public static final int CANVAS_HEIGHT = 1000;
    private Group root;

    public MapDrawer(String filename) throws IOException {
        this.root = new Group();
        JSONObject jsonObject = loadData(filename);
        drawCountries(jsonObject);
    }

    public Group getRoot() {
        return root;
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

    private void drawCountries(JSONObject data) {
        for (String name : data.keySet()) {
            String path = data.getJSONObject(name).getString("path");
            SVGPath svg = new SVGPath();

            svg.setContent(path);
            svg.setFill(Color.AQUAMARINE);
            svg.setStroke(Color.BLUE);
            svg.setStrokeWidth(2); 
            root.getChildren().add(svg);


        }
    }
}
