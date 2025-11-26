package org.example.unitcalculator_2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class UnitCalculatorApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Load FXML
        URL fxmlUrl = UnitCalculatorApp.class.getResource("main-view.fxml");
        if (fxmlUrl == null) {
            throw new IllegalStateException(
                    "Cannot find main-view.fxml in /org/example/unitcalculator_2/");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);

        // Initial screen size
        Scene scene = new Scene(fxmlLoader.load(), 700, 475);

        // Dark theme css
        URL cssUrl = UnitCalculatorApp.class.getResource("dark-theme.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        stage.setTitle("Physics Unit Calculator");
        stage.setScene(scene);

        // Minimum size
        stage.setMinWidth(300);
        stage.setMinHeight(275);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
