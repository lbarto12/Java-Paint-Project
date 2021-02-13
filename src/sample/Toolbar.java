package sample;

import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;

public class Toolbar extends TilePane {
    public Toolbar() {
        super();
        this.drawStyle();
        this.colorPicker();
        this.shapePanel();
    }

    private void drawStyle() {
        // Toolbar Pane
        this.setStyle("-fx-background-color: #c7c7c7;");
        this.setPrefSize(200,100);


        // Tools Pane
        var tools = new GridPane();
        tools.setStyle("-fx-background-color: Black;");
        tools.setPrefSize(50, 100);

        var draw_image = new Image("resources/draw.png");
        var draw = new Button();
        draw.setGraphic(new ImageView(draw_image));
        draw.setPrefSize(10, 10);
        draw.setOnAction(e -> this.currentMode = Mode.Draw);

        var fill_image = new Image("resources/fill.png");
        var fill = new Button();
        fill.setGraphic(new ImageView(fill_image));
        fill.setPrefSize(10, 10);
        fill.setOnAction(e -> this.currentMode = Mode.Fill);

        var erase_image = new Image("resources/erase.png");
        var erase = new Button();
        erase.setGraphic(new ImageView(erase_image));
        erase.setPrefSize(10, 10);
        erase.setOnAction(e -> this.currentMode = Mode.Erase);

        var dropper_image = new Image("resources/dropper.png");
        var dropper = new Button();
        dropper.setGraphic(new ImageView(dropper_image));
        dropper.setPrefSize(10, 10);
        dropper.setOnAction(e -> this.currentMode = Mode.Dropper);

        undo = new Button("Undo");

        load = new Button("Load");

        save = new Button("Save");

        tools.addColumn(0, draw);
        tools.addColumn(1, fill);
        tools.addColumn(2, erase);
        tools.addColumn(3, dropper);
        tools.addRow(1, undo);
        tools.addRow(1, load);
        tools.addRow(1, save);

        this.getChildren().addAll(tools);
    }


    private void colorPicker(){
        this.cp = new ColorPicker();
        var cptx = new Text("         Brush Color");

        this.brushSize = new Slider();
        this.brushSize.setMin(1);
        this.brushSize.setMax(50);
        this.brushSize.setShowTickLabels(true);
        this.brushSize.setShowTickMarks(true);
        this.brushSize.setValue(1);
        var bstx = new Text("           Brush Size");



        var attributes = new GridPane();
        attributes.addRow(0, cptx);
        attributes.addRow(1, cp);
        attributes.addRow(2, bstx);
        attributes.addRow(3, brushSize);

        this.getChildren().add(attributes);
    }

    private void shapePanel() {
        var sqr = new Button("Rectangle");
        sqr.setPrefWidth(100);
        sqr.setOnAction(e -> currentMode = Mode.Square);

        var cir = new Button("Circle");
        cir.setPrefWidth(100);
        cir.setOnAction(e -> currentMode = Mode.Circle);

        var shapes = new GridPane();

        shapes.addColumn(0, new Text("          Shapes"));
        shapes.addRow(1, sqr);
        shapes.addRow(2, cir);

        this.getChildren().add(shapes);
    }

    public enum Mode {
        Draw,
        Fill,
        Erase,
        Dropper,
        Square,
        Circle
    }

    public Button undo;
    public Button load;
    public Button save;

    public Mode currentMode = Mode.Draw;
    public ColorPicker cp;
    public Slider brushSize;
}
