package JavaFx;

import DTO.ExecutionsStatistics.FlowExecutionStats;
import DTO.StepperDTO;
import JavaFx.Body.AdminBodyController;
import JavaFx.Header.HeaderController;
import Requester.Stats.FlowStatsRequestImp;
import Requester.fileupload.FileUploadImpl;
import Requester.flow.flowNames.FlowsNamesRequestImpl;
import StepperEngine.Stepper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.IOException;
import Utils.Utils;
import static Utils.Constants.STRING_LIST_INSTANCE;

public class AppController {
    @FXML private VBox headerComponent;
    @FXML private TabPane bodyComponent;
    @FXML HeaderController headerComponentController;
    @FXML AdminBodyController bodyComponentController;
    private final StepperDTO stepperDTO=new StepperDTO();
    private Stepper stepper;
    boolean isStepperIn=false;


    @FXML
    public void initialize() {
        headerComponentController.setMainController(this);
        bodyComponentController.setMainController(this);
    }

    public void loadFile(String filePath,File selectedFile) throws IOException {
        try {
            String res=Utils.runSyncFile(new FileUploadImpl().fileUploadRequest(selectedFile), AdminUtils.HTTP_CLIENT);
            if(res!=null)
                failureMessage(res);
            else{
                isStepperIn = true;
                Platform.runLater(() -> {
                    headerComponentController.updateFilePathLabel(filePath);
                    updateStats();
                });
            }
        }catch (RuntimeException e) {
            failureMessage(e.getMessage());
        }
    }

    private void updateStats()  {
        bodyComponentController.initStats(Utils.runSync(new FlowsNamesRequestImpl().getAllFlowNamesRequest(),STRING_LIST_INSTANCE.getClass(),AdminUtils.HTTP_CLIENT));
    }

    private static void failureMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Stepper");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isStepperIn() {
        return isStepperIn;
    }

    public Stepper getStepper() {
        return stepper;
    }

    public FlowExecutionStats getFlowExecutionsStats(String flowName) {
        return Utils.runSync(new FlowStatsRequestImp().getFlowRequest(flowName),FlowExecutionStats.class,AdminUtils.HTTP_CLIENT);
    }
}
