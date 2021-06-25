package client.views.screen;

import client.controller.*;
import client.utils.Configs;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
	private ImageView logoutImageView;
//	@FXML
//	private ImageView homeScreenImageView;
	@FXML
	private ImageView leaderboardImageView;
	

	
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
		this.winningRate.setText(Float.toString(this.gameModeScreenController.getCurPlayer().getWinningRate()*100));
//		HomeScreenHandler homeHandler = new HomeScreenHandler(this.stage, Configs.HOME_SCREEN_PATH,
//				new HomeScreenController());
//		homeScreenImageView.setOnMouseClicked(e -> {
//			// TODO: gotta rethink ab this...
//			homeHandler.show();
//			homeHandler.setScreenTitle("Home Screen");
//		});

		logoutImageView.setOnMouseClicked(e -> {
			// logout
			showLogoutPrompt();
		});
		Tooltip.install(logoutImageView, new Tooltip("Logout"));
		leaderboardImageView.setOnMouseClicked(ev -> {
			System.out.println("leaderboard");
			try {
				BaseScreenHandler leaderboardHandler = new LeaderBoardHandler(this.stage,
						Configs.LEADERBOARD_SCREEN_PATH, new LeaderBoardController(gameModeScreenController.getCurPlayer().getUsername(),
								gameModeScreenController.getCurPlayer().getSessionId()));
				leaderboardHandler.setScreenTitle("Leaderboard");
				leaderboardHandler.setPreviousScreen(this);
				leaderboardHandler.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		//quitQueue.setDisable(true);
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}

	@FXML
	private void handleFindGameAction(javafx.event.Event evt) {
		try {
			
			GameModeScreenHandler currentHandler = this;
			WaitingScreenHandler waitingScreenHandler = new WaitingScreenHandler(currentHandler.stage);
			waitingScreenHandler.show();
			if (evt.getSource() == practicePlay) {
				MainGameScreenController mainGameScreenController = new MainGameScreenController(
						this.gameModeScreenController.getCurPlayer(), "normal");
				System.out.println("practice play");
				// Boolean isFound = false;
				practicePlay.setDisable(true);
				rankPlay.setDisable(true);
				//quitQueue.setDisable(false);
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
								//quitQueue.setDisable(true);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else if (isFound == -1) {
							// join queue interrupted by user
							practicePlay.setDisable(false);
							rankPlay.setDisable(false);
							//quitQueue.setDisable(true);
						}
					}
				});

				findGameThread = new Thread(findGameTask);
				findGameThread.start();
				// thread.join();
			} else if (evt.getSource() == rankPlay) {
				practicePlay.setDisable(true);
				rankPlay.setDisable(true);
				//quitQueue.setDisable(false);
				MainGameScreenController mainGameScreenController = new MainGameScreenController(
						this.gameModeScreenController.getCurPlayer(), "ranked" );
				Task<Integer> findGameTask = new Task<Integer>() {
					protected Integer call() {
						Integer isFound = 1;
						try {
							isFound = gameModeScreenController.findRankedGame();
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
								notifySuccess("Yeah! Found a match! Let's solo");
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
								notifyError("Can not find ranked play match, please try again later");
								practicePlay.setDisable(false);
								rankPlay.setDisable(false);
								currentHandler.show();
								//quitQueue.setDisable(true);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else if (isFound == -1) {
							// join queue interrupted by user
							practicePlay.setDisable(false);
							rankPlay.setDisable(false);
							//quitQueue.setDisable(true);
						}
					}
				});

				findGameThread = new Thread(findGameTask);
				findGameThread.start();

			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleQuitQueueAction(javafx.event.Event evt) {
		try {
			System.out.println("Requested quit queue");
			//quitQueue.setDisable(true);
			
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
			//quitQueue.setDisable(false);
			System.out.println("Cannot quit queue properly");
		}
	}
	
	@FXML
	public void handlerGetLeaderboardAction() {
		System.out.println("leaderboard");
        try {
        	LeaderBoardController leaderboardController = new LeaderBoardController(this.gameModeScreenController.getCurPlayer().getUsername(), this.gameModeScreenController.getCurPlayer().getSessionId());
            GameModeScreenHandler curScreen = this;
            
            Task<Boolean> leaderboardTask = new Task<Boolean>() {

				@Override
				protected Boolean call() throws Exception {
					return leaderboardController.getLeaderboard();
				}
            	
            };
            
            leaderboardTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent arg0) {
					Boolean isSuccess = (Boolean) arg0.getSource().getValue();
					try {
						if(!isSuccess) {
							notifyError("Cannot get leaderboard. Please try again later");
						} else {
							BaseScreenHandler leaderboardHandler = new LeaderBoardHandler(curScreen.stage,
				                    Configs.LEADERBOARD_SCREEN_PATH, leaderboardController);
				            leaderboardHandler.setScreenTitle("Leaderboard");
				            leaderboardHandler.setPreviousScreen(curScreen);
							leaderboardHandler.show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
            	
            });
            Thread leaderboardThread = new Thread(leaderboardTask);
            leaderboardThread.start();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private void showLogoutPrompt() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log out");
        alert.setHeaderText("Are you sure to log out?");
        GameModeScreenHandler curHandler = this;
        // option != null.
        Optional<ButtonType> option = alert.showAndWait();
        if(option.get() == ButtonType.OK) {
        	Task<Void> logoutTask = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					if(gameModeScreenController.logout(gameModeScreenController.getCurPlayer().getUsername(), gameModeScreenController.getCurPlayer().getSessionId())) {
						notifySuccess("Log out successfully");
						// return to home screen
						HomeScreenHandler homeHandler = new HomeScreenHandler(curHandler.stage, Configs.HOME_SCREEN_PATH,
								new HomeScreenController());
						homeHandler.setScreenTitle("Home Screen");
						homeHandler.show();
					} else {
						notifyError("An error occured when logging out. Please try again");
					}
					return null;
				}
        	};
        	Thread logoutThread = new Thread(logoutTask);
        	logoutThread.start();
        } 
	}
}
