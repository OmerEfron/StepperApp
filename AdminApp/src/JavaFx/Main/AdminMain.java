package JavaFx.Main;

import JavaFx.AdminUtils;
import Utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import okhttp3.HttpUrl;
import okhttp3.Request;

import static JavaFx.AdminUtils.HTTP_CLIENT;
import static Utils.Constants.ADMIN_LOGIN_PAGE;
import static Utils.Constants.BASE_URL;


public class AdminMain extends Application {
    private Scene scene;
    @Override
    public void start(Stage primaryStage) throws Exception {
        signInAsAdmin();
        AdminUtils adminUtils=new AdminUtils();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AdminMain.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    private void signInAsAdmin(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + ADMIN_LOGIN_PAGE).newBuilder();
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Utils.runSync(request, HTTP_CLIENT);
    }
}
