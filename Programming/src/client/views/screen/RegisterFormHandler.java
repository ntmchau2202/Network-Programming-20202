package client.views.screen;

import client.controller.HomeScreenController;
import client.controller.RegisterFormController;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterFormHandler extends BaseScreenHandler
        implements Initializable {

    private final RegisterFormController registerFormController;

    /**
     * @param stage stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public RegisterFormHandler(Stage stage, String screenPath, RegisterFormController registerFormController) throws IOException {
        super(stage, screenPath);
        this.registerFormController = registerFormController;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
