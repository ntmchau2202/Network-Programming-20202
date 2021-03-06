package client.views.screen;

import client.controller.GameModeScreenController;
import client.controller.HomeScreenController;
import client.controller.RegisterFormController;
import client.utils.Configs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterFormHandler extends BaseScreenHandler
        implements Initializable {

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private PasswordField confirmPasswordTextField;
    @FXML
    private ImageView prevScreenImageView;
    private final RegisterFormController registerFormController;

    /**
     * @param stage stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public RegisterFormHandler(Stage stage, String screenPath, RegisterFormController registerFormController) throws IOException {
        super(stage, screenPath);
        this.registerFormController = registerFormController;
        prevScreenImageView.setOnMouseClicked(e -> {
            this.getPreviousScreen().show();
            this.getPreviousScreen().setScreenTitle("Home Screen");
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void confirmRegisterAction(javafx.event.Event evt) throws IOException {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        String confirmPassword = confirmPasswordTextField.getText();
        if (username.isEmpty()) {
            System.out.println("username is null. Insert again");
            notifyError("Username is empty. Insert again");
            return ;
        }
        if (password.isEmpty()) {
            System.out.println("password is null. Insert again");
            notifyError("Password is null. Insert again");
            return ;
        }
        if (confirmPassword.isEmpty()) {
            System.out.println("password is null. Insert again");
            notifyError("Please confirm your password. Insert again");
            return ;
        }
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Password Confirm: " + confirmPassword);
        if (!confirmPassword.equals(password)) {
            System.out.println("Password doesn't match");
            notifyError("Password doesn't match");
            return ;
        }
        try {
            if (!this.registerFormController.isRegisterSuccessfully(username, password)) {
                System.out.println("Register failed");
                notifyError("Register failed");
                return ;
            }
            notifySuccess("Register successfully");
            BaseScreenHandler gameModeScreenHandler = new GameModeScreenHandler(this.stage,
                    Configs.GAME_MODE_SCREEN_PATH, new GameModeScreenController(this.registerFormController.getLoggedPlayer()));

            gameModeScreenHandler.setScreenTitle("Game Mode");
            gameModeScreenHandler.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
