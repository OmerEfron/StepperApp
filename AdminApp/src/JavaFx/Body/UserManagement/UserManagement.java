package JavaFx.Body.UserManagement;

import DTO.UserData;
import JavaFx.AdminUtils;
import JavaFx.Body.AdminBodyController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;
import users.roles.RoleImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static JavaFx.AdminUtils.HTTP_CLIENT;
import static JavaFx.AdminUtils.ROLE_REQUEST;

public class UserManagement {

    @FXML private ListView<String> UsersListView;
    @FXML private Label userNameLabel;
    @FXML private TextField numOfFlows;
    @FXML private TextField numOfExecutions;
    @FXML private ListView<String> rolesList;
    @FXML private ListView<String> addRoleListView;
    @FXML private Button managerButton;
    @FXML private ImageView saveImage;
    @FXML private ImageView removeRoleImage;
    @FXML private ImageView addRoleImage;


    private BooleanProperty rolesEditProperty=new SimpleBooleanProperty();
    private BooleanProperty addRolesProperty=new SimpleBooleanProperty();
    private String selectedName=null;
    private AdminBodyController adminBodyController;

    public void setMainController(AdminBodyController adminBodyController) {
        this.adminBodyController = adminBodyController;
    }
    @FXML
    void initialize(){
        disappearSaveButton();
        setRolesEditListView();
        setRolesAddListView();
    }


    public void setUsersListView(Collection<String> users){
        UsersListView.setItems(FXCollections.observableArrayList(users));
    }
    public synchronized void updateNewData(Collection<UserData>users,List<UserData> newUsers){
        List<String> names = users.stream()
                .map(UserData::getUserName)
                .collect(Collectors.toList());
        setUsersListView(names);
        if(selectedName!=null && newUsers.contains(selectedName)
         && saveImage.mouseTransparentProperty().get() ){
            users.stream()
                    .filter(user -> user.getUserName().equals(selectedName))
                    .findFirst()
                    .ifPresent(this::updateScreen);
        }

    }

    private void setRolesEditListView() {
        rolesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean isItemSelected = newValue != null;
            rolesEditProperty.set(isItemSelected);
        });

        rolesList.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                rolesEditProperty.set(false);
            }
        });
        removeRoleImage.opacityProperty().bind(Bindings.when(rolesEditProperty).then(1.0).otherwise(0.2));
        removeRoleImage.cursorProperty().bind(Bindings.when(rolesEditProperty).then(Cursor.HAND).otherwise(Cursor.DISAPPEAR));
    }
    private void generateListView(ListView<String> removeFromeListView, ListView<String> addToListView){
        swapElementBetweenLists(removeFromeListView, addToListView);
    }

    private void swapElementBetweenLists(ListView<String> removeFromeListView, ListView<String> addToListView) {
        if (removeFromeListView.getSelectionModel().getSelectedItem() != null) {
            String selectedItem= removeFromeListView.getSelectionModel().getSelectedItem();
            removeFromeListView.getItems().remove(selectedItem);
            addToListView.getItems().add(selectedItem);
            removeFromeListView.getSelectionModel().clearSelection();
        }
    }


    @FXML
    void removeRole(MouseEvent event) {
        generateListView(rolesList,addRoleListView);
    }

    @FXML
    void addRole(MouseEvent event) {
        generateListView(addRoleListView,rolesList);

    }

    private void setRolesAddListView() {
        addRoleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean isItemSelected = newValue != null;
            addRolesProperty.set(isItemSelected);
        });

        addRoleListView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                addRolesProperty.set(false);
            }
        });
        addRoleImage.opacityProperty().bind(Bindings.when(addRolesProperty).then(1.0).otherwise(0.2));
        addRoleImage.cursorProperty().bind(Bindings.when(addRolesProperty).then(Cursor.HAND).otherwise(Cursor.DISAPPEAR));
    }

    @FXML
    void managerButtonClicked(ActionEvent event) {

    }

    @FXML
    void save(MouseEvent event) {

    }

    @FXML
    void showUser(MouseEvent event) {
        if (UsersListView.getItems() != null) {
            selectedName = UsersListView.getSelectionModel().getSelectedItem();
            UserData userData = adminBodyController.getUserData(selectedName);
            updateScreen(userData);
        }
    }

    private void updateScreen(UserData userData) {
        userNameLabel.setText(userData.getUserName());
        numOfExecutions.setText(userData.getNumOfExecutions().toString());
        numOfFlows.setText(userData.getNumOfFlow().toString());
        rolesList.setItems(FXCollections.observableArrayList(userData.getRoles()));
        addRoleListView.setItems(FXCollections.observableList(getRolesToAdd(userData)));
    }


    private List<String> getRolesToAdd(UserData userData) {
        List<RoleImpl> roles= AdminUtils.getRoles(ROLE_REQUEST.getAllRoles(),HTTP_CLIENT);
        List<String> rolesName=new ArrayList<>();
        for(RoleImpl role:roles){
            if(!userData.getRoles().contains(role.getName())){
                rolesName.add(role.getName());
            }
        }
        return rolesName;
    }

    private void disappearSaveButton() {
        saveImage.opacityProperty().set(0.2);
        saveImage.cursorProperty().set(Cursor.DISAPPEAR);
        saveImage.setMouseTransparent(true);
    }

}

