package client.views.screen;

import client.controller.GameModeScreenController;
import client.controller.HomeScreenController;
import client.controller.LoginFormController;
import client.controller.RegisterFormController;
import client.utils.Configs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginFormHandler extends BaseScreenHandler
        implements Initializable {

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private ImageView prevScreenImageView;

    private final LoginFormController loginFormController;

    /**
     * @param stage stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public LoginFormHandler(Stage stage, String screenPath, LoginFormController loginFormController) throws IOException {
        super(stage, screenPath);
        this.loginFormController = loginFormController;
        prevScreenImageView.setOnMouseClicked(e -> {
            this.getPreviousScreen().show();
            this.getPreviousScreen().setScreenTitle("Home Screen");
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void confirmLoginAction(javafx.event.Event evt) throws IOException {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        if (username.isEmpty()) {
            notifyError("Username is empty. Insert again");
            return ;
        }
        if (password.isEmpty()) {
            notifyError("Password is null. Insert again");
            return ;
        }
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        try {

            if (!this.loginFormController.isLoginSuccessfully(username, password)) {
                notifyError("Login failed");
                return ;
            }
            notifySuccess("Login successfully");
            BaseScreenHandler gameModeScreenHandler = new GameModeScreenHandler(this.stage,
                    Configs.GAME_MODE_SCREEN_PATH, new GameModeScreenController(this.loginFormController.getLoggedPlayer()));
            gameModeScreenHandler.setScreenTitle("Game Mode");
            gameModeScreenHandler.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
