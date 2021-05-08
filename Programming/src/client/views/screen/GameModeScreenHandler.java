package client.views.screen;

import client.controller.GameModeScreenController;
import client.controller.LeaderBoardController;
import client.controller.LoginFormController;
import client.controller.RegisterFormController;
import client.utils.Configs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameModeScreenHandler extends BaseScreenHandler
        implements Initializable {

    @FXML
    private Label username;

    @FXML
    private Label elo;

    @FXML
    private Label rank;

    @FXML
    private Label noPlayedMatch;

    @FXML
    private Label noWonMatch;

    @FXML
    private Label winningRate;

    @FXML
    private Button practicePlay;

    @FXML
    private Button rankPlay;

    private final GameModeScreenController gameModeScreenController;

    /**
     * @param stage stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public GameModeScreenHandler(Stage stage, String screenPath, GameModeScreenController gameModeScreenController) throws IOException {
        super(stage, screenPath);
        this.gameModeScreenController = gameModeScreenController;
        this.username.setText(this.gameModeScreenController.getCurPlayer().getUsername());
        this.elo.setText(Integer.toString(this.gameModeScreenController.getCurPlayer().getElo()));
        this.rank.setText(Integer.toString(this.gameModeScreenController.getCurPlayer().getRank()));
        this.noPlayedMatch.setText(Integer.toString(this.gameModeScreenController.getCurPlayer().getNoPlayedMatch()));
        this.noWonMatch.setText(Integer.toString(this.gameModeScreenController.getCurPlayer().getNoWonMatch()));
        this.winningRate.setText(Float.toString(this.gameModeScreenController.getCurPlayer().getWinningRate()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void handleFindGameAction(javafx.event.Event evt) {
        if (evt.getSource() == practicePlay) {
            System.out.println("practice play");
        } else if (evt.getSource() == rankPlay) {
            System.out.println("rank play");

        }

    }
}
