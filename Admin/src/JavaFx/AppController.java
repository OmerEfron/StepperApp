package JavaFx;

import DTO.ExecutionsStatistics.api.FlowExecutionStatsDefinition;
import DTO.ExecutionsStatistics.impl.FlowExecutionStatsImpl;
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
        final String[] fromBody = new String[1];
        HttpClientUtil.runSync(Constans.TEST, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Fail");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("from response");
                fromBody[0] =response.body().string();
            }
        });
        System.out.println(fromBody[0]);
        System.out.println("from main");
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
                }else
                    isStepperIn=true;
                    Platform.runLater(()->{
                        headerComponentController.updateFilePathLabel(filePath);
                    });
            }
        });
        updateStats();
        System.out.println(isStepperIn);
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
            throw new RuntimeException(e);
        }
        bodyComponentController.initStats(flowNames[0]);

    }

    private void updateStepperIn(){
        isStepperIn=true;
    }

    private static void failureMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Stepper");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Optional<FlowExecution> getFlowExecution(String name){
        return stepperDTO.getFlowExecution(name);
    }

    public Stepper getStepper() {
        return stepper;
    }
    public void changeCSS(){

    }

    public FlowExecutionStatsDefinition getFlowExecutionsStats(String flowName) {
        final FlowExecutionStatsDefinition[] flowExecutionStatsDefinition = new FlowExecutionStatsDefinition[1];

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
                        flowExecutionStatsDefinition[0] = Constans.GSON_INSTANCE.fromJson(response.body().string(), flowExecutionStatsDefinition[0].getClass());
                        System.out.println("from response flow stats afte");
                    } catch (IOException e) {
                        System.out.println("from run time 1 flow stats");
                        System.out.println(e.getMessage());
                    }
                }
            });
        } catch (IOException e) {
            System.out.println("from run time 1 flow stats");
            System.out.println(e.getMessage());
        }
        System.out.println("from main flow stats");
        return flowExecutionStatsDefinition[0];

    }
}
