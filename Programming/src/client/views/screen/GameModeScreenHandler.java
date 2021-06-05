package client.views.screen;

import client.controller.*;
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
	
	@FXML
	private Button quitQueue;
	
	private final GameModeScreenController gameModeScreenController;

	private Thread findGameThread, quitQueueThread;
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
		quitQueue.setDisable(true);
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	@FXML
	private void handleFindGameAction(javafx.event.Event evt) {
		try {
			MainGameScreenController mainGameScreenController = new MainGameScreenController(
					this.gameModeScreenController.getCurPlayer());
			GameModeScreenHandler currentHandler = this;
			

			if (evt.getSource() == practicePlay) {
				System.out.println("practice play");
				// Boolean isFound = false;
				practicePlay.setDisable(true);
				rankPlay.setDisable(true);
				quitQueue.setDisable(false);
				Task<Integer> findGameTask = new Task<Integer>() {
					protected Integer call() {
						Integer isFound = 1;
						try {
							isFound = gameModeScreenController.findPracticeGame();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return isFound;
					}
				};

				findGameTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

					public void handle(WorkerStateEvent t) {
						Integer isFound = (Integer) t.getSource().getValue();
						System.out.println("done:" + isFound);
						if (isFound == 0) {
							mainGameScreenController.setOpponent(gameModeScreenController.getOpponentName(),
									gameModeScreenController.getOpponentElo());
							mainGameScreenController.setMatchID(gameModeScreenController.getMatchID());
							System.out.println("Am I first player? " + gameModeScreenController.amIFirstPlayer());
							mainGameScreenController.setIsFirstPlayer(gameModeScreenController.amIFirstPlayer());
							mainGameScreenController.setTurn(gameModeScreenController.amIFirstPlayer());
							try {
								notifySuccess("Yeah! Found a match! Let's practice");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							BaseScreenHandler mainGameScreenHandler = null;
							try {
								mainGameScreenHandler = new MainGameScreenHandler(currentHandler.stage,
										Configs.MAINGAME_SCREEN_PATH, mainGameScreenController);
								mainGameScreenHandler.setScreenTitle("Tic Tac Toe - In game");
								mainGameScreenHandler.setPreviousScreen(currentHandler);
								mainGameScreenHandler.show();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						} else if (isFound == 1) {
							try {
								notifyError("Can not find practice play match, please try again later");
								practicePlay.setDisable(false);
								rankPlay.setDisable(false);
								quitQueue.setDisable(true);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else if (isFound == -1) {
							// join queue interrupted by user
							practicePlay.setDisable(false);
							rankPlay.setDisable(false);
							quitQueue.setDisable(true);
						}
					}
				});

				findGameThread = new Thread(findGameTask);
				findGameThread.start();
				// thread.join();
			} else if (evt.getSource() == rankPlay) {
				practicePlay.setDisable(true);
				rankPlay.setDisable(true);
				quitQueue.setDisable(false);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleQuitQueueAction(javafx.event.Event evt) {
		try {
			System.out.println("Requested quit queue");
			quitQueue.setDisable(true);
			
			Task<Boolean> quitQueueTask = new Task<Boolean>() {
				protected Boolean call() {
					try {
						return gameModeScreenController.quitQueue();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
				}
				
			};
			
			quitQueueTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent arg0) {
					Boolean isSuccess = (Boolean) arg0.getSource().getValue();
					System.out.println("quit queue done:" + isSuccess);	
					try {
						if(isSuccess) {
							notifySuccess("Quit queue successfully!");
							System.out.println("Quit queue successfully!");
//							findGameThread.interrupt();
							practicePlay.setDisable(false);
							rankPlay.setDisable(false);
						} else {
							notifyError("Quit queue failed. Please try again later");
							System.out.println("Quit queue failed. Please try again later");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			});
			
			quitQueueThread = new Thread(quitQueueTask);
			quitQueueThread.run();
		} catch (Exception e) {
			e.printStackTrace();
			quitQueue.setDisable(false);
			System.out.println("Cannot quit queue properly");
		}
	}
}
