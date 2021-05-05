package client.views.screen;

import client.controller.GameModeScreenController;
import client.controller.MainGameScreenController;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainGameScreenHandler extends BaseScreenHandler
        implements Initializable {

    private final MainGameScreenController mainGameScreenController;

    /**
     * @param stage stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public MainGameScreenHandler(Stage stage, String screenPath, MainGameScreenController mainGameScreenController) throws IOException {
        super(stage, screenPath);
        this.mainGameScreenController = mainGameScreenController;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
