package JavaFx.Body.UserManagement;

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

import java.util.Set;

public class UserManagement {

    @FXML private ListView<String> UsersListView;
    @FXML private Label userNameLabel;
    @FXML private TextField numOfFlows;
    @FXML private TextField numOfExecutions;
    @FXML private ListView<String> rolesList;
    @FXML private ListView<String> addRoleListView;
    @FXML private Button managerButton;
    @FXML private ImageView saveImage;

    private AdminBodyController adminBodyController;

    public void setMainController(AdminBodyController adminBodyController) {
        this.adminBodyController = adminBodyController;
    }
    @FXML
    void initialize(){
        disappearSaveButton();
    }
    public void setUsersListView(Set<String> users){
        UsersListView.setItems(FXCollections.observableArrayList(users));
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

    }
    private void disappearSaveButton() {
        saveImage.opacityProperty().set(0.2);
        saveImage.cursorProperty().set(Cursor.DISAPPEAR);
        saveImage.setMouseTransparent(true);
    }

}

