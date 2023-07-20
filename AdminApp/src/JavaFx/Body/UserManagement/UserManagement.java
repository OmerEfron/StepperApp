package JavaFx.Body.UserManagement;

import DTO.UserData;
import JavaFx.AdminUtils;
import JavaFx.Body.AdminBodyController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import users.roles.RoleImpl;

import java.util.*;
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
    @FXML private ImageView saveImage;
    @FXML private ImageView removeRoleImage;
    @FXML private ImageView addRoleImage;
    @FXML private TextField ManagerTextFiled;
    @FXML private ImageView managerImage;

    private Tooltip tooltip = new Tooltip();
    private Boolean isManagerInStart=false;
    private BooleanProperty isManager=new SimpleBooleanProperty(false);
    private Set<String> roles=new HashSet<>();
    private BooleanProperty rolesEditProperty=new SimpleBooleanProperty();
    private BooleanProperty addRolesProperty=new SimpleBooleanProperty();
    private String selectedName=null;
    private AdminBodyController adminBodyController;
    private Image greenManager=new Image("JavaFx/Body/resource/managerGreen.png");
    private Image redManager=new Image("JavaFx/Body/resource/managerRed.png");

    public void setMainController(AdminBodyController adminBodyController) {
        this.adminBodyController = adminBodyController;
    }
    @FXML
    void initialize(){
        disappearSaveButton();
        disappearManagerImage();
        setProperty(rolesList, rolesEditProperty, removeRoleImage);
        setProperty(addRoleListView, addRolesProperty, addRoleImage);
        setManageProperties();
    }

    private void setManageProperties() {
        tooltip.textProperty().bind(Bindings.when(isManager).then("Disable the manager option")
                .otherwise("Set as manager"));
        managerImage.imageProperty().bind(Bindings.when(isManager).then(greenManager)
                .otherwise(redManager));
        ManagerTextFiled.textProperty().bind(Bindings.when(isManager).then("The user is manager")
                .otherwise("The user is not manager"));

    }

    @FXML
    void managerImageClicked(MouseEvent event) {
        boolean isManagerNow = isManager.get();
        isManager.set(!isManagerNow);
        changeSaveButtonAppears();
    }
    @FXML
    void disableTextInManagerIcon(MouseEvent event) {
        tooltip.hide();
    }

    @FXML
    void setTextInManagerIcon(MouseEvent event) {
        Point2D mousePosition = managerImage.localToScreen(event.getX(), event.getY());
        tooltip.show(managerImage, mousePosition.getX() + 10, mousePosition.getY());
    }

    public void setUsersListView(Collection<String> users){
        UsersListView.setItems(FXCollections.observableArrayList(users));
    }
    public synchronized void updateNewData(Collection<UserData>users,List<UserData> newUsers){
        List<String> names = users.stream()
                .map(UserData::getUserName)
                .collect(Collectors.toList());
        setUsersListView(names);

        List<String>newUsersName=newUsers.stream()
                .map(UserData::getUserName)
                .collect(Collectors.toList());
        setUsersListView(names);

        if(selectedName!=null && newUsersName.contains(selectedName)){
            for(UserData user :users){
                if(user.getUserName().equals(selectedName))
                    if(saveImage.mouseTransparentProperty().get())
                        updateScreen(user);
                    else
                        updateTextFileds(user);
            }
        }

    }


    private void generateListView(ListView<String> removeFromeListView, ListView<String> addToListView){
        swapElementBetweenLists(removeFromeListView, addToListView);
    }

    private void swapElementBetweenLists(ListView<String> removeFromeListView, ListView<String> addToListView) {
        if (removeFromeListView.getSelectionModel().getSelectedItem() != null) {
            String selectedItem = removeFromeListView.getSelectionModel().getSelectedItem();
            removeFromeListView.getItems().remove(selectedItem);
            addToListView.getItems().add(selectedItem);
            removeFromeListView.getSelectionModel().clearSelection();
            changeSaveButtonAppears();
        }
    }

    private void changeSaveButtonAppears() {
        if (isRolesChanged() || isManagerInStart!= isManager.get())
            appearSaveButton();
        else
            disappearSaveButton();
    }


    @FXML
    void removeRole(MouseEvent event) {
        generateListView(rolesList,addRoleListView);
    }

    @FXML
    void addRole(MouseEvent event) {
        generateListView(addRoleListView,rolesList);
    }


    @FXML
    void save(MouseEvent event) {
        String userName = selectedName;
        ObservableList<String> rolesListItems = rolesList.getItems();
        for (String roleName : rolesListItems) {
            if (!roles.contains(roleName))
                adminBodyController.addOrRemoveUserFromRole(userName, roleName,true);
        }
        for (String roleName : roles) {
            if (!rolesListItems.contains(roleName))
                adminBodyController.addOrRemoveUserFromRole(userName, roleName,false);
        }
        if (isManager.get() != isManagerInStart) {
            adminBodyController.updateUserAsManager(userName);
        }
        disappearSaveButton();
    }
    @FXML
    void showUser(MouseEvent event) {
        if (UsersListView.getItems() != null) {
            selectedName = UsersListView.getSelectionModel().getSelectedItem();
            UserData userData = adminBodyController.getUserData(selectedName);
            updateScreen(userData);
            appearManagerImage();
        }
    }

    private void updateScreen(UserData userData) {
        updateTextFileds(userData);
        rolesList.setItems(FXCollections.observableArrayList(userData.getRoles()));
        addRoleListView.setItems(FXCollections.observableList(getRolesToAdd(userData)));
        isManagerInStart=userData.isManager();
        isManager.set(isManagerInStart);
    }

    private void updateTextFileds(UserData userData) {
        roles = userData.getRoles();
        userNameLabel.setText(userData.getUserName());
        numOfExecutions.setText(userData.getNumOfExecutions().toString());
        numOfFlows.setText(userData.getNumOfFlow().toString());
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
    private void appearSaveButton() {
        saveImage.opacityProperty().set(1);
        saveImage.cursorProperty().set(Cursor.HAND);
        saveImage.setMouseTransparent(false);
    }
    private void disappearManagerImage() {
        managerImage.opacityProperty().set(0.2);
        managerImage.cursorProperty().set(Cursor.DISAPPEAR);
        managerImage.setMouseTransparent(true);
    }
    private void appearManagerImage() {
        managerImage.opacityProperty().set(1);
        managerImage.cursorProperty().set(Cursor.HAND);
        managerImage.setMouseTransparent(false);
    }

    private boolean isRolesChanged() {
        ObservableList<String> rolesListItems = rolesList.getItems();
        for(String roleName:rolesListItems){
            if(!roles.contains(roleName))
                return true;
        }
        if(rolesListItems.size()==0){
            return true;
        }
        return false;
    }

    public static void setProperty(ListView<String> listView, BooleanProperty booleanProperty, ImageView image) {
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean isItemSelected = newValue != null;
            booleanProperty.set(isItemSelected);
        });

        listView.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                booleanProperty.set(false);
            }
        });
        image.opacityProperty().bind(Bindings.when(booleanProperty).then(1.0).otherwise(0.2));
        image.cursorProperty().bind(Bindings.when(booleanProperty).then(Cursor.HAND).otherwise(Cursor.DISAPPEAR));
    }

}

