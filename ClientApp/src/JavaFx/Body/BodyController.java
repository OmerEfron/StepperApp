package JavaFx.Body;

import ClientUtils.ClientUtils;
import ClientUtils.Constants;
import ClientUtils.Requester.flow.FlowRequestImpl;
import DTO.FlowDetails.FlowDetails;
import DTO.FlowExecutionData.FlowExecutionData;
import JavaFx.AppController;
import JavaFx.Body.ExecutionData.ExecutionData;
import JavaFx.Body.ExecutionData.FlowExecutionDataImpUI;
import JavaFx.Body.FlowDefinition.FlowDefinition;
import JavaFx.Body.FlowExecution.FlowExecution;
import JavaFx.Body.FlowHistory.FlowHistory;

import StepperEngine.Step.api.StepStatus;
import StepperEngine.Stepper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BodyController {

    @FXML private TabPane bodyComponent;
    @FXML private Tab flowDefinitionTab;
    @FXML private FlowDefinition flowDefinitionController;
    @FXML private Tab flowExecutionTab;
    @FXML private FlowExecution flowExecutionController;
    @FXML private Tab flowHistoryTab;
    @FXML private FlowHistory flowHistoryController;
    @FXML private Tab flowStatsTab;


    private Map<String, ExecutionData> executionDataMap=new HashMap<>();
    private Map<String, Map<String,ExecutionData>> executionStepsInFLow=new HashMap<>();
    private AppController mainController;


    @FXML
    public void initialize(){
        flowDefinitionController.setMainController(this);
        flowExecutionController.setMainController(this);
        flowHistoryController.setMainController(this);

        bodyComponent.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null && newTab == flowHistoryTab) {
                // Update the TableView with information
                flowHistoryController.setFlowsExecutionTable();
            }
        });
    }
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setFlowDetailsList(List<FlowDetails> flowDetails){
        flowDefinitionController.setDataByFlowName(flowDetails);
    }



    public void goToExecuteFlowTab(FlowDetails flow) {
        flowExecutionController.setFlowToExecute(flow);
        bodyComponent.getSelectionModel().select(flowExecutionTab);
    }


    public Stepper getStepper(){
        return mainController.getStepper();
    }

    public void updateFlowHistory() {
        flowHistoryController.setFlowsExecutionTable();
    }


    public ExecutionData getFlowExecutionData(FlowExecutionData flow){
        if(!executionDataMap.containsKey(flow.getUniqueExecutionId()))
            executionDataMap.put(flow.getUniqueExecutionId(),new FlowExecutionDataImpUI(flow));
        return executionDataMap.get(flow.getUniqueExecutionId());
    }
    public Node getStepExecutionData(FlowExecutionData flow, String stepName){
        return executionDataMap.get(flow.getUniqueExecutionId()).getStepVbox(stepName);
    }
    public ImageView getExecutionStatusImage(String status){
        ImageView imageView ;

        switch (StepStatus.valueOf(status)){
            case WARNING:
                imageView=new ImageView("JavaFx/Body/resource/warning.png");
                break;
            case SUCCESS:
                imageView=new ImageView("JavaFx/Body/resource/success.png");
                break;
            default:
                imageView=new ImageView("JavaFx/Body/resource/fail.png");
                break;
        }
        imageView.setFitHeight(15);
        imageView.setFitWidth(15);
        return imageView;

    }

    public String continuationFlow(String uuidFlow,String flowToContinue){
        return mainController.getStepper().applyContinuation(uuidFlow,flowToContinue);
    }
    public void rerunFlow(FlowExecutionData flow){
        bodyComponent.getSelectionModel().select(flowExecutionTab);
        flowExecutionController.runFlowAgain(getStepper().getFlowsDetailsByName(flow.getFlowName()),mainController.getStepper().reRunFlow(flow.getUniqueExecutionId()));
    }

    public void rerunFlow2(FlowExecutionData flow){
        bodyComponent.getSelectionModel().select(flowExecutionTab);
        flowExecutionController.runFlowAgain(getStepper().getFlowsDetailsByName(flow.getFlowName()),mainController.getStepper().reRunFlow(flow.getUniqueExecutionId()));
    }
    public void applyContinuationFromHistoryTab(String pastFlowUUID,String flowToContinue){
        bodyComponent.getSelectionModel().select(flowExecutionTab);
        flowExecutionController.applyContinuation(pastFlowUUID,flowToContinue);
    }

    public void getFlows(){
        String flowsUrl = HttpUrl
                .parse(Constants.BASE_URL + Constants.FLOW_URL)
                .newBuilder()
                .build()
                .toString();

        ClientUtils.runAsync(new FlowRequestImpl().getAllFlowRequest(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson = Constants.GSON_INSTANCE;
                Type listType = new TypeToken<List<FlowDetails>>() {}.getType();
                List<FlowDetails> flowDetailsList = gson.fromJson(response.body().string(), listType);
                for(FlowDetails flowDetails: flowDetailsList){
                    System.out.println(flowDetails.getFlowName());
                }
            }
        });
    }

}