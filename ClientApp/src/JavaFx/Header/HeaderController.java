package JavaFx.Header;


import JavaFx.AppController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HeaderController {
    private AppController mainController;
    @FXML
    private VBox headerComponent;
    @FXML
    private Button greenSytle;
    @FXML
    private Button seaSytle;
    @FXML
    private Button classicSytleButton;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }


    @FXML
    void classicSytleButton(ActionEvent event) {
        BorderPane root = (BorderPane) classicSytleButton.getScene().getRoot();
        ObservableList<String> stylesheets = root.getStylesheets();;
        stylesheets.clear();

        // Add the path to the new stylesheet
        String cssPath = getClass().getResource("/JavaFx/Body/resource/classic-style.css").toExternalForm();
        stylesheets.add(cssPath);
    }

    @FXML
    void greenSytleButton(ActionEvent event) {
        BorderPane root = (BorderPane) greenSytle.getScene().getRoot();
        ObservableList<String> stylesheets = root.getStylesheets();;
        stylesheets.clear();

        // Add the path to the new stylesheet
        String cssPath = getClass().getResource("/JavaFx/Body/resource/water-melon-style.css").toExternalForm();
        stylesheets.add(cssPath);
    }

    @FXML
    void seaSytleButton(ActionEvent event) {
        BorderPane root = (BorderPane) seaSytle.getScene().getRoot();
        ObservableList<String> stylesheets = root.getStylesheets();;
        stylesheets.clear();

        // Add the path to the new stylesheet
        String cssPath = getClass().getResource("/JavaFx/Body/resource/sea-style.css").toExternalForm();
        stylesheets.add(cssPath);
    }
}
