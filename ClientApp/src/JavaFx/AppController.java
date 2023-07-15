package JavaFx;


import DTO.StepperDTO;
import JavaFx.Body.BodyController;
import JavaFx.Header.HeaderController;
import JavaFx.Login.LoginController;
import Requester.login.LoginRequestImpl;
import StepperEngine.Flow.FlowBuildExceptions.FlowBuildException;
import StepperEngine.Stepper;
import StepperEngine.StepperReader.Exception.ReaderException;
import Utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;

import static JavaFx.ClientUtils.HTTP_CLIENT;
import static JavaFx.ClientUtils.LOGIN_PAGE_FXML_RESOURCE_LOCATION;

public class AppController {
    @FXML private VBox headerComponent;
    @FXML private TabPane bodyComponent;

    @FXML
    HeaderController headerComponentController;
    @FXML
    BodyController bodyComponentController;

    @FXML
    LoginController loginController;
    private final StepperDTO stepperDTO=new StepperDTO();
    private Stepper stepper;
    @FXML
    public void initialize() {
        headerComponentController.setMainController(this);
        bodyComponentController.setMainController(this);
        headerComponentController.setUsernameLabel(getCurrUsername());
    }

    public void switchToMainApp() throws IOException {
        Stage stage = (Stage) bodyComponent.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main/main.fxml"));
        Parent main = fxmlLoader.load();
        Scene scene = new Scene(main);
        stage.setScene(scene);
    }

    public String getCurrUsername(){
        String username = Utils.runSync(new LoginRequestImpl().getUsername(), String.class, HTTP_CLIENT);
        return username;
    }

    public Stepper getStepper() {
        return stepper;
    }
}
