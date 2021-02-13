package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.util.Stack;

public class PaintCanvas extends Canvas{
    public PaintCanvas(Stage window, VBox layout, Toolbar tooblar){
        this.window = window;
        this.layout = layout;
        this.toolbar = tooblar;
        this.init();
    }

    private void init(){
        this.g = canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        this.undos.push(canvas.snapshot(null, null));
        this.toolbar.cp.setValue(Color.BLACK);

        canvas.setOnMousePressed(e -> {
            switch (toolbar.currentMode){
                case Draw -> _draw(e); // done
                case Fill -> _fill(e);
                case Erase -> _erase(e); // done
                case Dropper -> _dropper(e); // done
                case Square -> _square(e); // done
                case Circle -> _circle(e); // done
            }
        });

        canvas.setOnMouseDragged(e -> {
            switch (toolbar.currentMode){
                case Draw -> __draw(e);
                case Erase -> __erase(e);
                case Square -> __square(e);
                case Circle -> __circle(e);
            }
        });

        canvas.setOnMouseReleased(e -> {
            if (this.toolbar.currentMode != Toolbar.Mode.Dropper
            && this.toolbar.currentMode != Toolbar.Mode.Square
            && this.toolbar.currentMode != Toolbar.Mode.Circle)
                this.undos.push(canvas.snapshot(null, null));

        });


        this.layout.getChildren().add(canvas);


        // Undo action
        this.toolbar.undo.setOnAction(e -> this.undo());

        this.window.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN).match(keyEvent))
                this.undo();
        });

        // Load action
        this.toolbar.load.setOnAction(e -> this.load());

        this.window.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN).match(keyEvent))
                this.load();
        });

        // Save action
        this.toolbar.save.setOnAction(e -> this.save());

        this.window.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {
            if (new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN).match(keyEvent))
                this.save();
        });
    }

    // Drawing
    private void _draw(MouseEvent e) {
        this.changeColor();
        this.g.setLineWidth(toolbar.brushSize.getValue());
        this.g.beginPath();
        this.g.lineTo(e.getX(), e.getY());
        this.g.stroke();
    }
    private void __draw(MouseEvent e){
        this.g.lineTo(e.getX(), e.getY());
        this.g.stroke();
    }

    // Fill
    private void _fill(MouseEvent e) {
        // NA
    }
    private void __fill(int px, int py, WritableImage img){
        // NA
    }

    // Erasing
    private void _erase(MouseEvent e) {
        this.g.setStroke(Color.WHITE);
        this.g.setLineWidth(toolbar.brushSize.getValue());
        this.g.beginPath();
        this.g.lineTo(e.getX(), e.getY());
        this.g.stroke();
    }
    private void __erase(MouseEvent e) {
        this.g.lineTo(e.getX(), e.getY());
        this.g.stroke();
    }

    // Get Color
    private void _dropper( MouseEvent e) {
        toolbar.cp.setValue(this.canvas.snapshot(null, null).getPixelReader().getColor((int)e.getX(), (int)e.getY()));
    }


    // SHAPES:

    private double shapeStartX;
    private double shapeStartY;

    // Square
    private void _square(MouseEvent e) {
        this.shapeStartX = e.getX();
        this.shapeStartY = e.getY();
        this.undos.push(snap());
        this.changeColor();
    }

    // Square dragged
    private void __square(MouseEvent e) {
        this.undo();

        double posX1 = this.shapeStartX;
        double posY1 = this.shapeStartY;
        double posX2 = e.getX() - this.shapeStartX;
        double posY2 = e.getY() - this.shapeStartY;
        if (e.getX() < this.shapeStartX) {
            posX1 = e.getX();
            posX2 = this.shapeStartX - e.getX();
        }
        if (e.getY() < this.shapeStartY) {
            posY1 = e.getY();
            posY2 = this.shapeStartY - e.getY();
        }
        this.g.strokeRect(posX1, posY1, posX2, posY2);

        this.undos.push(snap());
    }

    // Circle
    private void _circle(MouseEvent e) {
        this.shapeStartX = e.getX();
        this.shapeStartY = e.getY();
        this.undos.push(snap());
        this.changeColor();
    }

    // Circle Dragged
    private void __circle(MouseEvent e) {
        this.undo();

        double posX1 = this.shapeStartX;
        double posY1 = this.shapeStartY;
        double posX2 = e.getX() - this.shapeStartX;
        double posY2 = e.getY() - this.shapeStartY;
        if (e.getX() < this.shapeStartX) {
            posX1 = e.getX();
            posX2 = this.shapeStartX - e.getX();
        }
        if (e.getY() < this.shapeStartY) {
            posY1 = e.getY();
            posY2 = this.shapeStartY - e.getY();
        }
        this.g.strokeOval(posX1, posY1, posX2, posY2);

        this.undos.push(snap());
    }



    // Undo function
    private void undo(){
        if (this.undos.size() > 1){
            this.undos.pop();
            this.g.drawImage(this.undos.lastElement(),0, 0);
        }
    }

    // Save
    private void save() {
        var fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        fc.setInitialFileName("picutre.png");
        fc.setTitle("Save Picture");
        var file = fc.showSaveDialog(this.window);
        if (file != null){
            try{
                String file_name = file.getName().substring(1+file.getName().lastIndexOf(".")).toLowerCase();
                var pic = SwingFXUtils.fromFXImage(this.snap(), null);
                ImageIO.write(pic, file_name, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Load
    private void load() {
        var fc = new FileChooser();
        fc.setTitle("Load Picture");
        var file = fc.showOpenDialog(this.window);
        if (file != null){
            try{
                var img = new Image(new FileInputStream(file));
                double endx = img.getWidth(), endy = img.getHeight();

                // Keep on screen / preserve aspect ratio
                while(endy > this.canvas.getHeight() || endx > this.canvas.getWidth()){
                    endx -= endx / 1000;
                    endy -= endy / 1000;
                }

                this.g.drawImage(img, 0, 0, endx, endy);
                this.undos.push(snap());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Change Color Function:
    private void changeColor(){
        this.g.setStroke(toolbar.cp.getValue());
        this.g.setFill(toolbar.cp.getValue());
        this.g.setLineWidth(toolbar.brushSize.getValue());
    }


    private Image snap(){
        return canvas.snapshot( null, null);
    }

    private final VBox layout;
    private final Toolbar toolbar;
    private final Stage window;
    private final Canvas canvas = new Canvas(1000, 500);

    private GraphicsContext g;

    private final Stack<Image> undos = new Stack<>();
}
