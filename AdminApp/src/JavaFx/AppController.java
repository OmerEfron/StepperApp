package JavaFx;

import DTO.ExecutionsStatistics.FlowExecutionStats;
import DTO.StepperDTO;
import JavaFx.Body.AdminBodyController;
import JavaFx.Header.HeaderController;
import StepperEngine.Flow.execute.FlowExecution;
import StepperEngine.Stepper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import Util.Constans;
import Util.http.HttpClientUtil;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static Util.Constans.FLOW_EXECUTION_STATS;


public class AppController {
    @FXML private VBox headerComponent;
    @FXML private TabPane bodyComponent;
    @FXML HeaderController headerComponentController;
    @FXML AdminBodyController bodyComponentController;
//
    private final StepperDTO stepperDTO=new StepperDTO();
    private Stepper stepper;
    boolean isStepperIn=false;


    @FXML
    public void initialize() {
        headerComponentController.setMainController(this);
        bodyComponentController.setMainController(this);
    }

    public void loadFile(String filePath,File selectedFile) throws IOException {
        HttpClientUtil.runSyncFileUpload(selectedFile, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        failureMessage(e.getMessage())
                );
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Platform.runLater(() ->
                            {
                                try {
                                    String responseBody = response.body().string();
                                    JsonElement jsonElement = JsonParser.parseString(responseBody);
                                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                                    String errorMessage = jsonObject.get("message").getAsString();
                                    failureMessage(errorMessage);
                                } catch (IOException e) {
                                    failureMessage(e.getMessage());
                                }
                            }
                    );
                }else {
                    isStepperIn = true;
                    Platform.runLater(() -> {
                        headerComponentController.updateFilePathLabel(filePath);
                    });
                }
            }
        });
        updateStats();
    }

    private void updateStats()  {
        final List<String>[] flowNames = new List[]{new ArrayList<>()};
        try {
            HttpClientUtil.runSync(Constans.GET_FLOW_NAMES, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            flowNames[0] = Constans.GSON_INSTANCE.fromJson(response.body().string(), new TypeToken<List<String>>() {
                            }.getType());
                        } catch (IOException e) {
                            failureMessage(e.getMessage());
                        }
                }
            });
        } catch (IOException e) {
             failureMessage(e.getMessage());
        }
        bodyComponentController.initStats(flowNames[0]);

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
        final FlowExecutionStats[] flowExecutionStats = new FlowExecutionStats[1];
        String finalUrl = HttpUrl
                .parse(Constans.FLOW_STATS)
                .newBuilder()
                .addQueryParameter(Constans.FLOW_NAME_PARAMETER, flowName)
                .build()
                .toString();
        try {
            HttpClientUtil.runSync(finalUrl, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        flowExecutionStats[0] = Constans.GSON_INSTANCE.fromJson(response.body().string(), FLOW_EXECUTION_STATS.getClass());
                    } catch (IOException e) {
                        failureMessage(e.getMessage());
                    }
                }
            });
        } catch (IOException e) {
            failureMessage(e.getMessage());
        }
        return flowExecutionStats[0];
    }
}
