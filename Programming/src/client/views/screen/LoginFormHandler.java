package client.views.screen;

import client.controller.HomeScreenController;
import client.controller.LoginFormController;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginFormHandler extends BaseScreenHandler
        implements Initializable {

    private final LoginFormController loginFormController;

    /**
     * @param stage stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public LoginFormHandler(Stage stage, String screenPath, LoginFormController loginFormController) throws IOException {
        super(stage, screenPath);
        this.loginFormController = loginFormController;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
