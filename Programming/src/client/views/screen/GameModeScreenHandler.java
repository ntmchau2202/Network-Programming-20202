package client.views.screen;

import client.controller.*;
import client.network.ClientSocketChannel;
import client.utils.Configs;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import message.matchfound.MatchFoundServerMessage;
import protocol.StatusCode;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameModeScreenHandler extends BaseScreenHandler implements Initializable {

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
	@FXML
	private ImageView prevScreenImageView;
	@FXML
	private ImageView homeScreenImageView;
	@FXML
	private ImageView leaderboardImageView;
	private final GameModeScreenController gameModeScreenController;

	/**
	 * @param stage      stage of screen.
	 * @param screenPath path to screen fxml
	 * @throws IOException exception for IO operations
	 */
	public GameModeScreenHandler(Stage stage, String screenPath, GameModeScreenController gameModeScreenController)
			throws IOException {
		super(stage, screenPath);
		this.gameModeScreenController = gameModeScreenController;
		this.username.setText(this.gameModeScreenController.getCurPlayer().getUsername());
		this.elo.setText(Integer.toString(this.gameModeScreenController.getCurPlayer().getElo()));
		this.rank.setText(Integer.toString(this.gameModeScreenController.getCurPlayer().getRank()));
		this.noPlayedMatch.setText(Integer.toString(this.gameModeScreenController.getCurPlayer().getNoPlayedMatch()));
		this.noWonMatch.setText(Integer.toString(this.gameModeScreenController.getCurPlayer().getNoWonMatch()));
		this.winningRate.setText(Float.toString(this.gameModeScreenController.getCurPlayer().getWinningRate()));
		HomeScreenHandler homeHandler = new HomeScreenHandler(this.stage, Configs.HOME_SCREEN_PATH,
				new HomeScreenController());
		prevScreenImageView.setOnMouseClicked(e -> {
			homeHandler.show();
			homeHandler.setScreenTitle("Home Screen");
		});
		homeScreenImageView.setOnMouseClicked(e -> {
			homeHandler.show();
			homeHandler.setScreenTitle("Home Screen");
		});
		leaderboardImageView.setOnMouseClicked(ev -> {
			System.out.println("leaderboard");
			try {
				BaseScreenHandler leaderboardHandler = new LeaderBoardHandler(this.stage,
						Configs.LEADERBOARD_SCREEN_PATH, new LeaderBoardController());
				leaderboardHandler.setScreenTitle("Leaderboard");
				leaderboardHandler.setPreviousScreen(this);
				leaderboardHandler.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	@FXML
	private void handleFindGameAction(javafx.event.Event evt) {
		try {
			MainGameScreenController mainGameScreenController = new MainGameScreenController(
					this.gameModeScreenController.getCurPlayer().getUsername());

			if (evt.getSource() == practicePlay) {
				System.out.println("practice play");

				boolean isFound = gameModeScreenController.findPracticeGame();
				if (isFound) {
					// TODO: may need other analyze here
					notifySuccess("Yeah! Found a match! Let's practice");
					if (gameModeScreenController.amIFirstPlayer()) {
						System.out.println("i play first mother fucker");
					}
					mainGameScreenController.setTurn(gameModeScreenController.amIFirstPlayer());
				} else {
					notifyError("Can not find practice play match");
				}
			} else if (evt.getSource() == rankPlay) {
				System.out.println("rank play");

				boolean isFound = gameModeScreenController.findRankGame();
				if (isFound) {
					// TODO: may need other analyze here
					notifySuccess("Yeah! Found a rank match! Hope you win");
					mainGameScreenController.setTurn(gameModeScreenController.amIFirstPlayer());

				} else {
					notifyError("Can not find rank play match");
				}

			}
			BaseScreenHandler mainGameScreenHandler = new MainGameScreenHandler(this.stage,
					Configs.MAINGAME_SCREEN_PATH, mainGameScreenController, gameModeScreenController.amIFirstPlayer());
			mainGameScreenHandler.setScreenTitle("Tic Tac Toe - In game");
			mainGameScreenHandler.setPreviousScreen(this);
			mainGameScreenHandler.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
