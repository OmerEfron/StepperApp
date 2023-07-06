package JavaFx.Body.FlowExecution;


import DTO.FlowDetails.FlowDetails;
import DTO.FlowDetails.StepDetails.FlowIODetails.Input;
import DTO.FlowExecutionData.FlowExecutionData;
import DTO.FlowExecutionData.IOData;
import JavaFx.Body.BodyController;
import JavaFx.ClientUtils;
import Requester.execution.ExecutionRequestImpl;
import Requester.flow.FlowRequestImpl;
import StepperEngine.DataDefinitions.Enumeration.ZipEnumerator;
import StepperEngine.Flow.execute.StepData.StepExecuteData;
import StepperEngine.Step.api.DataNecessity;
import Utils.Constants;
import Utils.Utils;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FlowExecution {
    private static final int INPUT_NAME_COLUMN = 0;
    private static final int INPUT_MANDATORY_COLUMN = 1;
    private static final int INPUT_DATA_DISPLAY_COLUMN = 2;

    @FXML private AnchorPane flowExecutionAnchorPane;
    @FXML private GridPane executionDetailsGridPane;
    @FXML private ListView<String> stepInputListView;
    @FXML private ListView<String> stepOutputListView;
    @FXML private ListView<String> formalOutputsListView;
    @FXML private ListView<String> stepsInfoListView;
    @FXML private Label executionUuidLabel;
    @FXML private Label executionTimestampLabel;
    @FXML private Label executionResultLabel;
    @FXML private Separator executionDetailsDataSeperator;
    @FXML private Separator StepOutputSeperator;
    @FXML private Label stepNameDisplayNameLabel;
    @FXML private GridPane freeInputsGridPane;
    @FXML private ScrollPane freeInputsScrollPane;
    @FXML private TableView<FreeInputsTableRow> freeInputsTableView;
    @FXML private TableColumn<FreeInputsTableRow, String> freeInputNameCol;
    @FXML private TableColumn<FreeInputsTableRow, String> freeInputValueCol;
    @FXML private TableColumn<FreeInputsTableRow, String> freeInputApiNameCol;
    @FXML private Label outputNameDisplayNameLabel;
    @FXML private ImageView flowExecutionButtonImage;
    @FXML private ProgressBar executionProgressBar;
    @FXML private Label floeDetailsLabel;
    @FXML private GridPane flowDetailGridPane;
    @FXML private Label flowNameLabel;
    @FXML private Label floeDescriptionLabel;
    @FXML private Label floeStepsLabel;
    @FXML private ImageView continuationButtonImage;
    @FXML private ChoiceBox<String> continuationChoiceBox;
    @FXML private Label stepNameTitleLabel;
    @FXML private GridPane stepDetailsGridPane;
    @FXML private AnchorPane outputPresentationAnchorPane;
    @FXML private Label stepLogsLabel;
    @FXML private Label stepResutLabel;
    @FXML private AnchorPane stepIODataDisplayAnchorPane;
    @FXML private Label stepTimeLabel;
    @FXML private Label CentralFlowName;
    @FXML private TreeView<String> StepsTreeVIew;
    @FXML private VBox MainExecutionDataVbox;
    @FXML private Button rerunButton;


    private final Callback executeCallback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("execute request didn't deliverd");
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            if(response.isSuccessful()){
                System.out.println("execution finished");
            }else{
                System.out.println(response.body().string());
            }
        }
    };
    private final Callback setFlowDetailsCallback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("cannot go to server");
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            if(response.isSuccessful()){
                flowDetails = Constants.GSON_INSTANCE.fromJson(response.body().string(), FlowDetails.class);
                updateFlowExecutionData();
            }
            else {
                System.out.println(response.body().string());
            }
        }
    };
    private final Callback getNewExecutionCallback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("connection failed");
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            Platform.runLater(() -> {
                try {
                    currFlowExecutionUuid = Constants.GSON_INSTANCE.fromJson(response.body().string(), String.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        }
    };
    private BodyController bodyController;
    private FlowDetails flowDetails;
    private FlowExecutionData flowExecutionData;
    private FlowExecutionData flowExecutionDataImp;

    private String lastFlowRunningUuid;
    private String currFlowExecutionUuid;

    @FXML
    void initialize(){
        initButtons();
        freeInputNameCol.setCellValueFactory(new PropertyValueFactory<>("apiName"));
        freeInputValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        addFreeInputsFirstRow();
        continuationChoiceBox.setOnAction(this::startContinuation);
    }
    public void startContinuation(ActionEvent event){
        makeContinuationButtonEnabled();
    }
    @FXML
    void continueFlow(MouseEvent event) {
        String flowToContinue=continuationChoiceBox.getValue();
        applyContinuation(flowExecutionData.getUniqueExecutionId(),flowToContinue);
    }

    @FXML
    void rerunFlow(ActionEvent event) {
        bodyController.rerunFlow(flowExecutionDataImp);
    }


    public void applyContinuation(String UUID,String flowToContinue) {
        currFlowExecutionUuid=bodyController.continuationFlow(UUID, flowToContinue);
        Utils.runAsync(new FlowRequestImpl().getFlowRequest(flowToContinue), setFlowDetailsCallback, ClientUtils.HTTP_CLIENT);
    }

    public void runFlowAgain(FlowDetails flow, String UUID){
        flowDetails=flow;
        currFlowExecutionUuid=UUID;
        updateFlowExecutionData();
    }

    private void updateFlowExecutionData() {
        cleanUpScreen();
        setFreeInputsDisplay();
        FlowExecutionData flowToRerunExecutionData = Utils.runSync(new ExecutionRequestImpl().executionRequest(currFlowExecutionUuid), FlowExecutionData.class, ClientUtils.HTTP_CLIENT);
        Map<String, IOData> prevExecuteDataMap = flowToRerunExecutionData.getFreeInputs().stream()
                .collect(Collectors.toMap(IOData::getFullQualifiedName, Function.identity()));
        for(Input input:flowDetails.getFreeInputs()){
            if(prevExecuteDataMap.containsKey(input.getFullQualifideName())) {
                addNewValue(input, prevExecuteDataMap.get(input.getFullQualifideName()).toString());
                addInputToTable(input, prevExecuteDataMap.get(input.getFullQualifideName()).toString());
            }
        }
        CentralFlowName.setText(flowDetails.getFlowName());
        continuationChoiceBox.getItems().clear();
        initContinuationButton();
        setContinuation();
        initRerunButton();
    }

    @FXML
    void executeFlow(MouseEvent event) {
        if(flowExecutionButtonImage.opacityProperty().get() == 1) {
            executionProgressBar.setProgress(0);
            initExecuteButton();
            Utils.runAsync(new ExecutionRequestImpl().executeRequest(currFlowExecutionUuid), executeCallback, ClientUtils.HTTP_CLIENT);
            new Thread(new Runnable() {
                String uuid = currFlowExecutionUuid;
                @Override
                public void run() {
                    executeFlowTask(uuid);
                }
            }).start();
            lastFlowRunningUuid = currFlowExecutionUuid;
        }
    }

    void executeFlowTask(String uuid) {
        synchronized (this) {
            Boolean isEnded = Utils.runSync(new ExecutionRequestImpl().executionStatusRequest(uuid), Boolean.class, ClientUtils.HTTP_CLIENT);
            while (!isEnded) {
                System.out.println("not ended...");
                try {
                    Thread.sleep(300);
                    isEnded = Utils.runSync(new ExecutionRequestImpl().executionStatusRequest(uuid), Boolean.class, ClientUtils.HTTP_CLIENT);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Platform.runLater(() ->{
            executionProgressBar.setProgress(1);
            flowExecutionData = Utils.runSync(new ExecutionRequestImpl().executionRequest(uuid), FlowExecutionData.class, ClientUtils.HTTP_CLIENT);
            flowExecutionDataImp=flowExecutionData;
            cleanUpExecutionDetails();
            setExecutionDetails();
            makeRerunButtonEnabled();
        });
    }

    void setExecutionDetails() {
        executionProgressBar.setProgress(1);
        CentralFlowName.setText( flowExecutionDataImp.getFlowName());
        MainExecutionDataVbox.getChildren().add(bodyController.getFlowExecutionData(flowExecutionDataImp).getVbox());
        TreeItem root = new TreeItem(flowExecutionDataImp.getFlowName(), bodyController.getExecutionStatusImage(flowExecutionDataImp.getExecutionResult()));
        StepsTreeVIew.setRoot(root);
        for (DTO.FlowExecutionData.StepExecuteData step : flowExecutionDataImp.getStepExecuteDataList()) {
            TreeItem<String> childItem = new TreeItem<>(step.getFinalName(), bodyController.getExecutionStatusImage(step.getStepStatus().toString()));
            root.getChildren().add(childItem);
        }
        bodyController.updateFlowHistory();
        setContinuation();
    }

    @FXML
    void setStepData(MouseEvent event) {
        TreeItem<String> selectedItem = StepsTreeVIew.getSelectionModel().getSelectedItem();
        if(selectedItem!=null){
            MainExecutionDataVbox.getChildren().clear();
            boolean isRoot = selectedItem.getParent() == null;
            if (isRoot)
                MainExecutionDataVbox.getChildren().add(bodyController.getFlowExecutionData(flowExecutionDataImp).getVbox());
            else {
                MainExecutionDataVbox.getChildren().add(bodyController.getStepExecutionData(flowExecutionDataImp, selectedItem.getValue()));
            }
        }
    }



    private void addFreeInputsFirstRow() {
        freeInputsGridPane.add(new Label("Name"), INPUT_NAME_COLUMN, 0);
        freeInputsGridPane.add(new Label("Is madantory?"), INPUT_MANDATORY_COLUMN, 0);
        freeInputsGridPane.add(new Label(), INPUT_DATA_DISPLAY_COLUMN, 0);
    }
    private void initButtons() {
        initExecuteButton();
        initContinuationButton();
        initRerunButton();
    }
    private void initRerunButton()
    {
        rerunButton.opacityProperty().set(0.2);
        rerunButton.cursorProperty().set(Cursor.DISAPPEAR);
    }

    private void makeRerunButtonEnabled(){
        rerunButton.opacityProperty().set(1);
        rerunButton.cursorProperty().set(Cursor.HAND);
    }
    private void initContinuationButton() {
        continuationButtonImage.opacityProperty().set(0.2);
        continuationButtonImage.cursorProperty().set(Cursor.DISAPPEAR);
    }

    private void makeContinuationButtonEnabled() {
        continuationButtonImage.opacityProperty().set(1);
        continuationButtonImage.cursorProperty().set(Cursor.HAND);
    }

    private void initExecuteButton() {
        flowExecutionButtonImage.opacityProperty().set(0.2);
        flowExecutionButtonImage.cursorProperty().set(Cursor.DISAPPEAR);
    }

    void makeExecutionButtonEnabled(){
        flowExecutionButtonImage.opacityProperty().set(1);
        flowExecutionButtonImage.cursorProperty().set(Cursor.HAND);
    }

    public void setMainController(BodyController bodyController) {
        this.bodyController = bodyController;
    }

    public void setFlowToExecute(FlowDetails flow){
        cleanUpScreen();
        flowDetails = flow;
        CentralFlowName.setText(flowDetails.getFlowName());
        setFreeInputsDisplay();
        Utils.runAsync(new ExecutionRequestImpl().createExecuteRequest(flow.getFlowName()), getNewExecutionCallback, ClientUtils.HTTP_CLIENT);
    }


    public void setFreeInputsDisplay(){
        for(int i = 0; i < flowDetails.getFreeInputs().size(); i++){
            setInputRowData(flowDetails.getFreeInputs().get(i), i + 1);
            freeInputsScrollPane.setVvalue(freeInputsScrollPane.getVmax());
            freeInputsTableView.getItems().add(new FreeInputsTableRow(flowDetails.getFreeInputs().get(i), "not provided"));
        }
    }

    public void setInputRowData(Input input, int row){
        freeInputsGridPane.add(new Label(input.getDataName()), INPUT_NAME_COLUMN, row);
        freeInputsGridPane.add(new Label(
                DataNecessity.valueOf(input.getNecessity()).equals(DataNecessity.MANDATORY)? "Yes":"NO"),
                INPUT_MANDATORY_COLUMN,
                row);
        freeInputsGridPane.add(getInputDataDisplayer(input), INPUT_DATA_DISPLAY_COLUMN, row);
        freeInputsGridPane.getRowConstraints().get(freeInputsGridPane.getRowConstraints().size() - 1).setPrefHeight(Region.USE_COMPUTED_SIZE);
    }

    public Node getInputDataDisplayer(Input input){
        String typeName = input.getTypeName();
        if(typeName.equals("Enumerator")){
            return getEnumeratorChoiceBox(input);
        }
        else if(typeName.equals("File path") || typeName.equals("Folder path")){
            return getFileChooserButton(input);
        }
        else{
            return getTextFieldChooser(input);
        }
    }

    private ChoiceBox<String> getEnumeratorChoiceBox(Input input) {
        ChoiceBox<String> choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(EnumSet.allOf(ZipEnumerator.class).stream()
                .map(Enum::toString)
                .collect(Collectors.toList())));
        choiceBox.setOnAction(event -> {
            if(choiceBox.getValue() != null){
                boolean isAdded = addNewValue(input, choiceBox.getValue());
                if(!isAdded){
                    choiceBox.setValue(null);
                }
                else {
                    addInputToTable(input, choiceBox.getValue());
                }
            }
        });
        return choiceBox;
    }



    public HBox getFileChooserButton(Input input){
        HBox hBox = new HBox();
        Tooltip existinFile = new Tooltip("choose existing file");
        Tooltip existingFolder = new Tooltip("choose existing folder");
        Tooltip newFile = new Tooltip("choose new file");
        ImageView fileChooserButton = new ImageView();
        ImageView folderChooserButton = new ImageView();
        TextField textField = new TextField();
        Tooltip.install(fileChooserButton, existinFile);
        Tooltip.install(folderChooserButton, existingFolder);
        Tooltip.install(textField, newFile);
        if(input.getTypeName().equals("Folder path")){
            hBox.getChildren().add(folderChooserButton);
        }
        else {
            hBox.getChildren().add(textField);
            hBox.getChildren().add(fileChooserButton);
            hBox.getChildren().add(folderChooserButton);
        }
        EventHandler<MouseEvent> directoryHandler = event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File folderChoose = directoryChooser.showDialog(folderChooserButton.getScene().getWindow());
            if(folderChoose != null)
                if(addNewValue(input, folderChoose.getAbsolutePath())){
                    addInputToTable(input, folderChoose.getName());
                }
        };
        EventHandler<MouseEvent> fileHandler = event -> {
            FileChooser fileChooser = new FileChooser();
            File fileChoose = fileChooser.showOpenDialog(fileChooserButton.getScene().getWindow());
            if(fileChoose!=null){
                if(addNewValue(input, fileChoose.getAbsolutePath())){
                    addInputToTable(input, fileChoose.getName());
                }
            }
        };
        EventHandler<ActionEvent> newFileNameHandler = event -> {
            String newFileName = textField.getText();
            if(!newFileName.isEmpty()){
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File folderChoose = directoryChooser.showDialog(fileChooserButton.getScene().getWindow());
                if(folderChoose != null){
                    String folderChoosePath = folderChoose.getAbsolutePath();
                    String newFilePath = String.format("%s\\%s", folderChoosePath, newFileName);
                    if(addNewValue(input, newFilePath)){
                        addInputToTable(input, newFileName);
                    }
                }
            }
            textField.clear();
        };
        fileChooserButton.setImage(new Image(getClass().getResourceAsStream("fileIcon.png")));
        fileChooserButton.setFitHeight(30);
        fileChooserButton.setFitWidth(30);
        fileChooserButton.setOnMouseClicked(fileHandler);
        folderChooserButton.setImage(new Image(getClass().getResourceAsStream("folderIcon.png")));
        folderChooserButton.setFitWidth(30);
        folderChooserButton.setFitHeight(30);
        folderChooserButton.setOnMouseClicked(directoryHandler);
        textField.setOnAction(newFileNameHandler);
        return hBox;
    }
    public HBox getTextFieldChooser(Input input){
        HBox hBox = new HBox();
        TextField textField = new TextField();
        Button addButton = new Button("+");
        Label invalidInputLabel = new Label();
        invalidInputLabel.setTextFill(Color.RED);
        invalidInputLabel.setPadding(new Insets(0, 0, 0, 10));
        hBox.getChildren().add(textField);
        hBox.getChildren().add(addButton);
        hBox.getChildren().add(invalidInputLabel);
        EventHandler<ActionEvent> eventHandler = event -> {
            if(!addNewValue(input, textField.getText())){
                invalidInputLabel.textProperty().setValue("invalid input");
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), invalidInputLabel);
                fadeTransition.setFromValue(1.0);
                fadeTransition.setToValue(0.0);
                fadeTransition.setCycleCount(5);
                fadeTransition.play();
            }
            else{
                invalidInputLabel.textProperty().setValue("");
                addInputToTable(input, textField.getText());
            }
        };
        addButton.setOnAction(eventHandler);
        textField.setOnAction(eventHandler);
        return hBox;
    }



    private boolean addNewValue(Input input, String value) {
        try {
            //TODO the server should get Object as value, and than here need to convert the value to the right object.
            Boolean result = Utils.runSync(new ExecutionRequestImpl().addFreeInputRequest(currFlowExecutionUuid, input.getDataName(), value),
                    Boolean.class, ClientUtils.HTTP_CLIENT);
            if(Boolean.TRUE.equals(result) && Boolean.TRUE.equals(Utils.runSync(new ExecutionRequestImpl().isExecutionReadyRequest(currFlowExecutionUuid), Boolean.class, ClientUtils.HTTP_CLIENT))){
                makeExecutionButtonEnabled();
            }
            return Boolean.TRUE.equals(result);
        }catch (NumberFormatException e){
            return false;
        }
    }



    public Object convertValue(String value, String type){
        if (type.equals("Number")) {
            return Integer.parseInt(value);
        } else if (type.equals("Double")) {
            return Double.parseDouble(value);
        } else {
            return value;
        }
    }

    public void addInputToTable(Input input, String newVal){
        ObservableList<FreeInputsTableRow> freeInputsTableRows = freeInputsTableView.getItems();
        for(int i=0; i<freeInputsTableRows.size(); i++){
            if(freeInputsTableRows.get(i).getApiName().equals(input.getDataName())){
                freeInputsTableRows.get(i).setValue(newVal);
                freeInputsTableView.getItems().set(i, freeInputsTableRows.get(i));
                return;
            }
        }
    }

    public void cleanUpScreen(){
        cleanUpFreeInputs();
        cleanUpExecutionDetails();
    }

    private void cleanUpExecutionDetails() {
        MainExecutionDataVbox.getChildren().clear();
        executionProgressBar.setProgress(0.0);
        if(StepsTreeVIew.getRoot()!= null) {
            StepsTreeVIew.getRoot().getChildren().clear();
            StepsTreeVIew.setRoot(null);
        }
    }

    private void cleanUpFreeInputs() {
        freeInputsTableView.getItems().remove(0, freeInputsTableView.getItems().size());
        freeInputsGridPane.getChildren().clear();
        addFreeInputsFirstRow();
    }

    public void setStepInputListView(StepExecuteData step){
        stepInputListView.setItems(FXCollections.observableArrayList(step.getDataMap().keySet()));
    }

    public void setContinuation(){
        continuationChoiceBox.setItems(FXCollections.observableArrayList(flowDetails.getContinuationNames()));
    }


}
