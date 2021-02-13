package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage window) throws Exception {
        window.setTitle("MS Paint");
        window.setResizable(false);


        // Layout
        var layout = new VBox();

        // Toolbar
        var toolbar = new Toolbar();
        layout.getChildren().add(toolbar);

        // Paint Canvas
        var paintArea = new PaintCanvas(window, layout, toolbar);
        layout.getChildren().add(paintArea);

        window.setScene(new Scene(layout, 1000, 600));
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
