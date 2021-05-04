package client.views.screen;

import client.controller.LeaderBoardController;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LeaderBoardHandler extends BaseScreenHandler implements Initializable {

    private final LeaderBoardController leaderBoardController;

    /**
     * @param stage stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public LeaderBoardHandler(Stage stage, String screenPath, LeaderBoardController leaderBoardController) throws IOException {
        super(stage, screenPath);
        this.leaderBoardController = leaderBoardController;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * this method get base controller of current screen.
     * @return LeaderBoardController
     */
    public LeaderBoardController getBaseController() {
        if (super.getBaseController() == null) {
            return new LeaderBoardController();
        }
        return (LeaderBoardController) super.getBaseController();
    }
}
