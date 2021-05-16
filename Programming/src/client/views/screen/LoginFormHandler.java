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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginFormHandler extends BaseScreenHandler
        implements Initializable {

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

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

    @FXML
    private void confirmLoginAction(javafx.event.Event evt) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        if (username.isEmpty()) {
            System.out.println("username is null. Insert again");
            return ;
        }
        if (password.isEmpty()) {
            System.out.println("password is null. Insert again");
            return ;
        }
        System.out.println("User name: " + username);
        System.out.println("Password: " + password);
        try {
            boolean isLoginSuccess = this.loginFormController.isLoginSuccessfully(username, password);
            if (isLoginSuccess) {
	            BaseScreenHandler gameModeScreenHandler = new GameModeScreenHandler(this.stage,
	                    Configs.GAME_MODE_SCREEN_PATH, new GameModeScreenController(this.loginFormController.getLoggedPlayer()));
	            gameModeScreenHandler.setScreenTitle("Game Mode");
	            gameModeScreenHandler.show();
            } else {
            	// TODO: show some notification here
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
