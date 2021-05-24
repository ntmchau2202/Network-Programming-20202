package client.views.screen;

import client.controller.LeaderBoardController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LeaderBoardHandler extends BaseScreenHandler implements Initializable {

    private final LeaderBoardController leaderBoardController;

    @FXML
    private ImageView prevScreenImageView;
    /**
     * @param stage stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public LeaderBoardHandler(Stage stage, String screenPath, LeaderBoardController leaderBoardController) throws IOException {
        super(stage, screenPath);
        this.leaderBoardController = leaderBoardController;
        prevScreenImageView.setOnMouseClicked(e -> {
            this.getPreviousScreen().show();
            this.getPreviousScreen().setScreenTitle("Home Screen");
        });
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
