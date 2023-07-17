package JavaFx.Body.RolesManagement;

import DTO.FlowDetails.FlowDetails;
import JavaFx.Body.AdminBodyController;
import Utils.Constants;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.roles.RoleImpl;

import java.io.IOException;
import java.util.*;

public class RolesManagement {
    @FXML private TableView<RoleImpl> RoleTable;
    @FXML private TableColumn<RoleImpl, String> roleNameCol;
    @FXML private TableColumn<RoleImpl, String> roleDescriptionCol;
    @FXML private Button newRoleButton;
    @FXML private VBox rolePresnterVbox;
    @FXML private ImageView saveButton;
    @FXML private Label roleName;
    @FXML private Label roleDiscription;
    @FXML private ListView<String> flowsListView;
    @FXML private ListView<String> usersListView;
    @FXML private Label editRoleLabel;
    @FXML private Label invalidRoleName;
    @FXML private TextField roleNameTextFiled;
    @FXML private TextField changeDescriptionTextFiled;
    @FXML private ListView<String> flowsEditListView;
    @FXML private ImageView removeFlowImage;
    @FXML private ListView<String> flowsAddListView;
    @FXML private ImageView addFlowImage;
    @FXML private ListView<String> usersEditListView;
    @FXML private ImageView removeUserImage;
    @FXML private ListView<String> usersAddListView;
    @FXML private ImageView addUserImage;

    private BooleanProperty flowsEditListViewProperty=new SimpleBooleanProperty();
    private BooleanProperty flowsAddListViewProperty=new SimpleBooleanProperty();
    private BooleanProperty usersEditListViewProperty=new SimpleBooleanProperty();
    private BooleanProperty usersAddListViewProperty=new SimpleBooleanProperty();
    private Boolean canRoleBeChanged=true;
    private List<RoleImpl> roles=new ArrayList<>();
    private AdminBodyController adminBodyController;
    private RoleImpl newRole;
    private RoleImpl oldRole;

    @FXML
    void initialize(){
        initRoleColumns();
        disappearSaveButton();
        disappearNewRoleButton();
        setFlowsEditListView();
        setUsersEditListView();
        setFlowsAddListView();
        setUsersAddListView();
    }

    private void unableTextFields() {
        roleNameTextFiled.setEditable(true);
        changeDescriptionTextFiled.setEditable(true);
    }

    private void disappearNewRoleButton() {
        newRoleButton.opacityProperty().set(0.2);
        newRoleButton.cursorProperty().set(Cursor.DISAPPEAR);
        newRoleButton.setMouseTransparent(true);
    }
    public void appearNewRoleButton() {
        newRoleButton.opacityProperty().set(1);
        newRoleButton.cursorProperty().set(Cursor.HAND);
        newRoleButton.setMouseTransparent(false);
    }

    private void initRoleColumns() {
        roleNameCol.setCellValueFactory(new PropertyValueFactory<RoleImpl, String>("name"));
        roleDescriptionCol.setCellValueFactory(new PropertyValueFactory<RoleImpl, String>("description"));
    }

    @FXML
    void newRole(ActionEvent event) {
        updateScreenToNewRole();

    }

    private void updateScreenToNewRole() {
        disappearSaveButton();
        unableTextFields();
        oldRole=null;
        updateNewRoleDetails();
        canRoleBeChanged=true;
        newRole=new RoleImpl();
        updateRoleNameInChangeSection("");
        updateRoleDescriptionInChangeSection("");
        updateListsInChangeRoleSection(newRole.getAllowedFlows(),newRole.getUsers());
        editRoleLabel.setText("Add role (to save the role you have to add name!)");
    }

    public void  setRoleTable(List<RoleImpl> roleList){
        roles=roleList;
        RoleTable.setItems(FXCollections.observableList(roles));
    }

    public void setMainController(AdminBodyController adminBodyController) {
        this.adminBodyController = adminBodyController;
    }
    @FXML
    void saveRole(MouseEvent event) {
        newRole.setFlows(new ArrayList<>(flowsEditListView.getItems()));
        newRole.setUsers(new HashSet<>(usersEditListView.getItems()));
        if(oldRole!= null){
            RoleTable.getItems().remove(oldRole);
        }
        adminBodyController.sendNewRole(newRole);
        addNewRoleToTable();
        updateScreen();
    }

    private void addNewRoleToTable() {
        RoleTable.getItems().add(newRole);
        oldRole=new RoleImpl(newRole);
        newRole=new RoleImpl(newRole);
    }

    @FXML
    void showRole(MouseEvent event) {
        if(event.getClickCount()==2){
            unableTextFields();
            oldRole=new RoleImpl(RoleTable.getSelectionModel().getSelectedItem());
            newRole=new RoleImpl(oldRole);
            updateScreen();
        }
    }

    private void updateScreen() {
        disappearSaveButton();
        updateRoleDetails();
        editRoleLabel.setText("Change selected role");
        checkIfCanBeChanged(oldRole);
        updateRoleNameInChangeSection(oldRole.getName());
        updateRoleDescriptionInChangeSection(oldRole.getDescription());
        updateListsInChangeRoleSection(oldRole.getAllowedFlows(),oldRole.getUsers());
    }

    private void updateRoleNameInChangeSection(String name) {
        roleNameTextFiled.setText(name);
        roleNameTextFiled.setEditable(canRoleBeChanged);
    }
    @FXML
    void changeRoleName(ActionEvent event) {
        if (oldRole == null || !oldRole.getName().equals(roleNameTextFiled.getText())) {
            if (checkIfRoleExsitsByName(roleNameTextFiled.getText())) {
                invalidRoleName.textProperty().setValue("invalid input");
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), invalidRoleName);
                fadeTransition.setFromValue(1.0);
                fadeTransition.setToValue(0.0);
                fadeTransition.setCycleCount(5);
                fadeTransition.play();
                disappearSaveButton();
            } else {
                invalidRoleName.textProperty().setValue("");
                showSaveButton();
                newRole.setName(roleNameTextFiled.getText());
            }
        }

    }
    private void updateRoleDescriptionInChangeSection(String description) {
        changeDescriptionTextFiled.setPrefWidth(description.length()*9.0);
        changeDescriptionTextFiled.setText(description);
        changeDescriptionTextFiled.setEditable(canRoleBeChanged);
    }


    @FXML
    void changeDescription(ActionEvent event) {
        if(oldRole!=null) {
            showSaveButton();
        }
        newRole.setDescription(changeDescriptionTextFiled.getText());
    }

    private void updateListsInChangeRoleSection(List<String> flowsInRole, Collection<String> users) {
        flowsEditListView.setItems(FXCollections.observableList(flowsInRole));
        flowsAddListView.setItems(getFlowsThatNotInRole(flowsInRole));
        usersEditListView.setItems(FXCollections.observableArrayList(users));
       usersAddListView.setItems(getUsersThatNotInRole(users));

    }


    private void updateRoleDetails() {
        roleName.setText(oldRole.getName());
        roleDiscription.setPrefWidth(oldRole.getDescription().length()*9.0);
        roleDiscription.setText(oldRole.getDescription());
        flowsListView.setItems(FXCollections.observableList(oldRole.getAllowedFlows()));
        usersListView.setItems(FXCollections.observableArrayList(oldRole.getUsers()));
    }
    private void updateNewRoleDetails() {
        roleName.setText("New role name");
        roleDiscription.setText("New role Description");
        if(flowsListView.getItems()!=null){
            flowsListView.getItems().clear();
        }
        if(usersListView.getItems()!=null){
            flowsListView.getItems().clear();
        }
    }


    private void checkIfCanBeChanged(RoleImpl oldRole) {
        canRoleBeChanged = !(oldRole.getName().equals("Read Only Flows") || oldRole.getName().equals("All Flows"));
    }


    private boolean checkIfRoleExsitsByName(String newName){
        for(RoleImpl role:roles){
            if(role.getName().equals(newName))
                return true;
        }
        return false;
    }

    private void disappearSaveButton() {
        saveButton.opacityProperty().set(0.2);
        saveButton.cursorProperty().set(Cursor.DISAPPEAR);
        saveButton.setMouseTransparent(true);
    }
    private void showSaveButton() {
        saveButton.opacityProperty().set(1);
        saveButton.cursorProperty().set(Cursor.HAND);
        saveButton.setMouseTransparent(false);
    }
    @FXML
    void removeFlow(MouseEvent event) {
        generateListView(flowsEditListView, flowsAddListView);
    }
    @FXML
    void removeUser(MouseEvent event) {
        swapElementBetweenLists(usersEditListView,usersAddListView);
    }
    private void generateListView(ListView<String> removeFromeListView, ListView<String> addToListView) {
        if(canRoleBeChanged) {
            swapElementBetweenLists(removeFromeListView, addToListView);
        }
    }

    private void swapElementBetweenLists(ListView<String> removeFromeListView, ListView<String> addToListView) {
        if (removeFromeListView.getSelectionModel().getSelectedItem() != null) {
            String flowSelected= removeFromeListView.getSelectionModel().getSelectedItem();
            removeFromeListView.getItems().remove(flowSelected);
            addToListView.getItems().add(flowSelected);
            removeFromeListView.getSelectionModel().clearSelection();
            if(oldRole!=null)
                showSaveButton();
        }
    }




    @FXML
    void addFlow(MouseEvent event) {
        generateListView(flowsAddListView, flowsEditListView);
    }
    @FXML
    void addUser(MouseEvent event) {
        swapElementBetweenLists(usersAddListView,usersEditListView);
    }

    private ObservableList<String> getFlowsThatNotInRole(List<String>flowsInRole) {
        List<String> allFlows=adminBodyController.getAllFlows();
        List<String> flowsNotInRole=new ArrayList<>();
        for (String flow:allFlows){
            if (!flowsInRole.contains(flow)){
                flowsNotInRole.add(flow);
            }
        }
        return FXCollections.observableList(flowsNotInRole);
    }
    private ObservableList<String> getUsersThatNotInRole(Collection<String> usersInRole) {
        Set<String> allUsers = adminBodyController.getUsers();
        List<String> usersNotInRole=new ArrayList<>();
        for (String user:allUsers){
            if (!usersInRole.contains(user)){
                usersNotInRole.add(user);
            }
        }
        return FXCollections.observableList(usersNotInRole);

    }


    private void setUsersEditListView() {
        usersEditListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean isItemSelected = newValue != null;
            usersEditListViewProperty.set(isItemSelected);
        });

        usersEditListView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                usersEditListViewProperty.set(false);
            }
        });
        removeUserImage.opacityProperty().bind(Bindings.when(usersEditListViewProperty).then(1.0).otherwise(0.2));
        removeUserImage.cursorProperty().bind(Bindings.when(usersEditListViewProperty).then(Cursor.HAND).otherwise(Cursor.DISAPPEAR));
    }
    private void setUsersAddListView() {
        usersAddListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean isItemSelected = newValue != null;
            usersAddListViewProperty.set(isItemSelected);
        });

        usersAddListView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                usersAddListViewProperty.set(false);
            }
        });
        addUserImage.opacityProperty().bind(Bindings.when(usersAddListViewProperty).then(1.0).otherwise(0.2));
        addUserImage.cursorProperty().bind(Bindings.when(usersAddListViewProperty).then(Cursor.HAND).otherwise(Cursor.DISAPPEAR));
    }
    private void setFlowsAddListView() {
        flowsAddListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean isItemSelected = newValue != null;
            flowsAddListViewProperty.set(isItemSelected);
        });

        flowsAddListView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                flowsAddListViewProperty.set(false);
            }
        });
        addFlowImage.opacityProperty().bind(Bindings.when(flowsAddListViewProperty).then(1.0).otherwise(0.2));
        addFlowImage.cursorProperty().bind(Bindings.when(flowsAddListViewProperty).then(Cursor.HAND).otherwise(Cursor.DISAPPEAR));
    }

    private void setFlowsEditListView() {
        flowsEditListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean isItemSelected = newValue != null;
            flowsEditListViewProperty.set(isItemSelected);
        });

        flowsEditListView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                flowsEditListViewProperty.set(false);
            }
        });
        removeFlowImage.opacityProperty().bind(Bindings.when(flowsEditListViewProperty).then(1.0).otherwise(0.2));
        removeFlowImage.cursorProperty().bind(Bindings.when(flowsEditListViewProperty).then(Cursor.HAND).otherwise(Cursor.DISAPPEAR));
    }
    public final Callback setNewRoleCallback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println("cannot go to server");
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            if(response.isSuccessful()){
            }
            else {
            }
        }
    };

}