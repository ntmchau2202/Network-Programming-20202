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
    private void handleHomeAction(javafx.event.Event evt) {
        if (evt.getSource() == guestPlayBtn) {
            System.out.println("guest player");
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
					System.out.println("done:" + isFound);
					if (isFound == 0) {
						MainGameScreenController mainGameScreenController = new MainGameScreenController(homeScreenController.getCurrentPlayer(), "guest");
						mainGameScreenController.setOpponent(homeScreenController.getOpponentName(),
								homeScreenController.getOpponentElo());
						mainGameScreenController.setMatchID(homeScreenController.getMatchID());
						System.out.println("Am I first player? " + homeScreenController.amIFirstPlayer());
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
							System.out.println("Prepare to show up!");
							mainGameScreenHandler.show();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					} else if (isFound == 1) {
						try {
							notifyError("Can not find practice play match, please try again later");
							guestPlayBtn.setDisable(false);
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
            System.out.println("back to player");
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
            System.out.println("register time");
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
//            System.out.println("leaderboard");
//            try {
//                BaseScreenHandler leaderboardHandler = new LeaderBoardHandler(this.stage,
//                        Configs.LEADERBOARD_SCREEN_PATH, new LeaderBoardController());
//                leaderboardHandler.setScreenTitle("Leaderboard");
//                leaderboardHandler.setPreviousScreen(this);
//                leaderboardHandler.show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
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
