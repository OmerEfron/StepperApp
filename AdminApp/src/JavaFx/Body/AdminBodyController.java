package JavaFx.Body;

import DTO.ExecutionsStatistics.FlowExecutionStats;
import DTO.FlowExecutionData.FlowExecutionData;
import JavaFx.AdminUtils;
import JavaFx.AppController;

import JavaFx.Body.ExecutionData.ExecutionData;
import JavaFx.Body.ExecutionData.FlowExecutionDataImpUI;
import JavaFx.Body.FlowHistory.FlowHistory;
import JavaFx.Body.FlowStats.FlowStats;

import JavaFx.Body.RolesManagement.RolesManagement;
import JavaFx.Body.UserManagement.UserManagement;
import Refresher.FlowExecutionListRefresher;
import Refresher.FlowStatsListRefresher;
import Refresher.StatsWithVersion;
import StepperEngine.Step.api.StepStatus;
import StepperEngine.Stepper;
import Utils.Utils;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import users.roles.RoleImpl;

import java.util.*;
import java.util.stream.Collectors;

import static JavaFx.AdminUtils.FLOWS_NAMES_REQUEST;
import static Utils.Constants.STRING_LIST_INSTANCE;

public class AdminBodyController {

    @FXML private TabPane bodyComponent;
    @FXML private Tab UserManagementTab;
    @FXML private UserManagement userManagementController;
    @FXML private Tab RolesManagementTab;
    @FXML private RolesManagement rolesManagementController;
    @FXML private Tab flowHistoryTab;
    @FXML private FlowHistory flowHistoryController;
    @FXML private Tab flowStatsTab;
    @FXML private FlowStats flowStatsController;

    private Map<String, ExecutionData> executionDataMap=new HashMap<>();
    private Map<String, Map<String,ExecutionData>> executionStepsInFLow=new HashMap<>();
    private AppController mainController;
    private TimerTask flowExecutionsRefresher;
    private Timer timer;
    private Timer timerStats;
    private FlowStatsListRefresher FlowStatsListRefresher;
    private IntegerProperty statsVersion;
    private Boolean statsRefresherIn=false;



    @FXML
    public void initialize(){
        rolesManagementController.setMainController(this);
//        userManagementController.setMainController(this);
        flowHistoryController.setMainController(this);
        flowStatsController.setMainController(this);
        statsVersion= new SimpleIntegerProperty();
        flowsHistoryRefresher();

    }
    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public boolean isStepperIn(){
        return mainController.isStepperIn();
    }

    public void updateFlowHistory() {
        flowHistoryController.setFlowsExecutionTable();
    }

    public void initStats(List<String> flowNames){
        flowStatsController.initStats(flowNames);
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

    public FlowExecutionStats getFlowExecutionsStats(String flowName) {
        return mainController.getFlowExecutionsStats(flowName);
    }
    public void flowsHistoryRefresher(){
        flowExecutionsRefresher = new FlowExecutionListRefresher(this::setFlowExecutionDetailsList);
        timer = new Timer();
        timer.schedule(flowExecutionsRefresher, 2000, 2000);
    }
    public void setFlowExecutionDetailsList(List<FlowExecutionData> flowExecutionDataList){
        flowHistoryController.setFlowExecutionDataList(flowExecutionDataList);
    }
    public void updateStats(){
        FlowStatsListRefresher = new FlowStatsListRefresher(
                this::updateStatsWithVersion,
                statsVersion);
        timer = new Timer();
        timer.schedule(FlowStatsListRefresher, 2000, 2000);
    }
    public void updateStatsWithVersion(StatsWithVersion statsWithVersion){
        if((statsWithVersion.getVersion() != statsVersion.get()))
        {
            Platform.runLater(()->{
                setFlowExecutionStatsList(statsWithVersion.getEntries());
                statsVersion.set(statsWithVersion.getVersion());
            });

        }
    }
    public void setFlowExecutionStatsList(List<FlowExecutionStats> flowExecutionStatsList){
        flowStatsController.setFlowExecutionStatsList(flowExecutionStatsList);
    }
    public Boolean getStatsRefresherIn() {
        return statsRefresherIn;
    }

    public void setStatsRefresherIn(Boolean statsRefresherIn) {
        this.statsRefresherIn = statsRefresherIn;
    }
    public void setRoles(List<RoleImpl> roles){
        rolesManagementController.setRoleTable(roles);
        rolesManagementController.appearNewRoleButton();
    }

    public List<String> getAllFlows() {
        return Utils.runSync(FLOWS_NAMES_REQUEST.getAllFlowNamesRequest(),
                STRING_LIST_INSTANCE.getClass(), AdminUtils.HTTP_CLIENT);
    }

    public void sendNewRole(RoleImpl newRole) {
        Utils.runAsync(AdminUtils.ROLE_REQUEST.addRole(newRole),rolesManagementController.setNewRoleCallback,AdminUtils.HTTP_CLIENT );

    }
}