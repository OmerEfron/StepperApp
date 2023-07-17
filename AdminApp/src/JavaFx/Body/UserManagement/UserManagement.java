package JavaFx.Body.UserManagement;

import DTO.UserData;
import JavaFx.Body.AdminBodyController;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserManagement {

    @FXML private ListView<String> UsersListView;
    @FXML private Label userNameLabel;
    @FXML private TextField numOfFlows;
    @FXML private TextField numOfExecutions;
    @FXML private ListView<String> rolesList;
    @FXML private ListView<String> addRoleListView;
    @FXML private Button managerButton;
    @FXML private ImageView saveImage;

    private String selectedName=null;
    private AdminBodyController adminBodyController;

    public void setMainController(AdminBodyController adminBodyController) {
        this.adminBodyController = adminBodyController;
    }
    @FXML
    void initialize(){
        disappearSaveButton();
    }


    public void setUsersListView(Collection<String> users){
        UsersListView.setItems(FXCollections.observableArrayList(users));
    }
    public synchronized void updateNewData(Collection<UserData> users){
        List<String> names = users.stream()
                .map(UserData::getUserName)
                .collect(Collectors.toList());
        setUsersListView(names);
        if(selectedName!=null && names.contains(selectedName)
         && saveImage.mouseTransparentProperty().get() ){
            users.stream()
                    .filter(user -> user.getUserName().equals(selectedName))
                    .findFirst()
                    .ifPresent(this::updateScreen);
        }

    }

    @FXML
    void RemoveRole(MouseEvent event) {

    }

    @FXML
    void addRole(MouseEvent event) {

    }

    @FXML
    void managerButtonClicked(ActionEvent event) {

    }

    @FXML
    void save(MouseEvent event) {

    }

    @FXML
    void showUser(MouseEvent event) {
        if(event.getClickCount()==2){
            if(UsersListView.getItems() != null){
                selectedName=UsersListView.getSelectionModel().getSelectedItem();
                UserData userData=adminBodyController.getUserData(selectedName);
                updateScreen(userData);
            }
        }
    }

    private void updateScreen(UserData userData) {
        userNameLabel.setText(userData.getUserName());
        numOfExecutions.setText(userData.getNumOfExecutions().toString());
        numOfFlows.setText(userData.getNumOfFlow().toString());
        rolesList.setItems(FXCollections.observableArrayList(userData.getRoles()));

    }

    private void disappearSaveButton() {
        saveImage.opacityProperty().set(0.2);
        saveImage.cursorProperty().set(Cursor.DISAPPEAR);
        saveImage.setMouseTransparent(true);
    }

}

