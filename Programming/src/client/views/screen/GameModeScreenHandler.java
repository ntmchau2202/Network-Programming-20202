package client.views.screen;

import client.controller.GameModeScreenController;
import client.controller.RegisterFormController;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameModeScreenHandler extends BaseScreenHandler
        implements Initializable {

    private final GameModeScreenController gameModeScreenController;

    /**
     * @param stage stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public GameModeScreenHandler(Stage stage, String screenPath, GameModeScreenController gameModeScreenController) throws IOException {
        super(stage, screenPath);
        this.gameModeScreenController = gameModeScreenController;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
