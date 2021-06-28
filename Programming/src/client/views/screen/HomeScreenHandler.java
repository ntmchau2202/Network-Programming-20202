package client.views.screen;

import client.controller.HomeScreenController;
import client.controller.LeaderBoardController;
import client.controller.LoginFormController;
import client.controller.MainGameScreenController;
import client.controller.RegisterFormController;
import client.utils.Configs;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeScreenHandler extends BaseScreenHandler
        implements Initializable {

    @FXML
    private ImageView leaderBoardImageView;

    @FXML
    private Button guestPlayBtn;

    @FXML
    private Button returnPlayerBtn;

    @FXML
    private Label registerLabel;

    private final HomeScreenController homeScreenController;

    /**
     * @param stage stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public HomeScreenHandler(Stage stage, String screenPath, HomeScreenController homeScreenController) throws IOException {
        super(stage, screenPath);
        this.homeScreenController = homeScreenController;

    }

    /**
     * setup method.
     * @param arg0 URL of screen path
     * @param arg1 resource of fxml
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

    @FXML
    private void handleHomeAction(javafx.event.Event evt) throws IOException {
        if (evt.getSource() == guestPlayBtn) {
            WaitingScreenHandler waitingScreenHandler = new WaitingScreenHandler(this.stage, null);
            waitingScreenHandler.show();
            // TODO: log guest player here
			
			HomeScreenHandler currentHandler = this;
            guestPlayBtn.setDisable(true);
			Task<Integer> findGameTask = new Task<Integer>() {
				protected Integer call() {
					Integer isFound = 1;
					try {
						isFound = homeScreenController.findGuestGame();
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
					if (isFound == 0) {
						MainGameScreenController mainGameScreenController = new MainGameScreenController(homeScreenController.getCurrentPlayer(), "guest");
						mainGameScreenController.setOpponent(homeScreenController.getOpponentName(),
								homeScreenController.getOpponentElo());
						mainGameScreenController.setMatchID(homeScreenController.getMatchID());
						mainGameScreenController.setIsFirstPlayer(homeScreenController.amIFirstPlayer());
						mainGameScreenController.setTurn(homeScreenController.amIFirstPlayer());
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
							guestPlayBtn.setDisable(false);
							currentHandler.show();
//							rankPlay.setDisable(false);
//							quitQueue.setDisable(true);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (isFound == -1) {
						// join queue interrupted by user
						guestPlayBtn.setDisable(false);
					}
				}
			});

			Thread findGameThread = new Thread(findGameTask);
			findGameThread.start();
        } else if (evt.getSource() == returnPlayerBtn) {
            try {
            	// TODO: must implement Task here to avoid freezing the client while waiting for the server
                BaseScreenHandler loginFormHandler = new LoginFormHandler(this.stage,
                        Configs.LOGIN_FORM_PATH, new LoginFormController());
                loginFormHandler.setScreenTitle("Login");
                loginFormHandler.setPreviousScreen(this);
                loginFormHandler.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (evt.getSource() == registerLabel) {
            try {
                BaseScreenHandler registerFormHandler = new RegisterFormHandler(this.stage,
                        Configs.REGISTER_FORM_PATH, new RegisterFormController());
                registerFormHandler.setScreenTitle("Register");
                registerFormHandler.setPreviousScreen(this);
                registerFormHandler.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (evt.getSource() == leaderBoardImageView) {
            try {
            	HomeScreenHandler curHandler = this;
            	LeaderBoardController leaderboardControllerTmp = new LeaderBoardController("", "");
            	Task<Boolean> getLeaderboardTask = new Task<Boolean>() {

					@Override
					protected Boolean call() throws Exception {
						return leaderboardControllerTmp.getLeaderboard();
					}
				};
				
				getLeaderboardTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

					@Override
					public void handle(WorkerStateEvent arg0) {
						try {
							Boolean isOK = (Boolean) arg0.getSource().getValue();
							if(isOK) {
								BaseScreenHandler leaderboardHandler = new LeaderBoardHandler(curHandler.stage,
										Configs.LEADERBOARD_SCREEN_PATH, leaderboardControllerTmp);
								leaderboardHandler.setScreenTitle("Leaderboard");
								leaderboardHandler.setPreviousScreen(curHandler);
								leaderboardHandler.show();
							} else {
								notifyError("Cannot fetch leaderboard at the moment. Please try again later");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				Thread leaderboardThread = new Thread(getLeaderboardTask);
				leaderboardThread.start();
            	
//                BaseScreenHandler leaderboardHandler = new LeaderBoardHandler(this.stage,
//                        Configs.LEADERBOARD_SCREEN_PATH, leaderboardControllerTmp);
//                leaderboardHandler.setScreenTitle("Leaderboard");
//                leaderboardHandler.setPreviousScreen(this);
//                leaderboardHandler.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * this method get base controller of current screen.
     * @return HomeScreenController
     */
    public HomeScreenController getBaseController() {
        if (super.getBaseController() == null) {
            return new HomeScreenController();
        }
        return (HomeScreenController) super.getBaseController();
    }
}
