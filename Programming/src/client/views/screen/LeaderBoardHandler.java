package client.views.screen;

import client.controller.LeaderBoardController;
import entity.Player.LeaderboardPlayer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class LeaderBoardHandler extends BaseScreenHandler implements Initializable {

    private final LeaderBoardController leaderBoardController;

    @FXML
    private ImageView prevScreenImageView;
    @FXML
    private TableView<LeaderboardPlayer> leaderboardTableView;
    @FXML
    private TableColumn<LeaderboardPlayer, Integer> rankTableColumn;
    @FXML
    private TableColumn<LeaderboardPlayer, Integer> eloTableColumn;
    @FXML
    private TableColumn<LeaderboardPlayer, Integer> matchTableColumn;
    @FXML
    private TableColumn<LeaderboardPlayer, Integer> winTableColumn;
    @FXML
    private TableColumn<LeaderboardPlayer, Float> rateTableColumn;
    @FXML
    private TableColumn<LeaderboardPlayer, String> usernameTableColumn;
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

        try {
            List<LeaderboardPlayer> lstLeaderboardPlayer =
                    this.leaderBoardController.getTopPlayers();
            rankTableColumn.setCellValueFactory(
                    new PropertyValueFactory<>("rank"));
            eloTableColumn.setCellValueFactory(
                    new PropertyValueFactory<>("elo"));
            matchTableColumn.setCellValueFactory(
                    new PropertyValueFactory<>("noPlayedMatch"));
            winTableColumn.setCellValueFactory(
                    new PropertyValueFactory<>("noWonMatch"));
            usernameTableColumn.setCellValueFactory(
                    new PropertyValueFactory<>("username"));
            rateTableColumn.setCellValueFactory(
                    new  PropertyValueFactory<>("winningRate"));

            for (LeaderboardPlayer leaderboardPlayer :lstLeaderboardPlayer) {
                leaderboardTableView.getItems().add(leaderboardPlayer);
            }
//            System.out.println(lstLeaderboardPlayer);
        } catch (SQLException | IOException | InterruptedException throwables) {
            throwables.printStackTrace();
        }
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
