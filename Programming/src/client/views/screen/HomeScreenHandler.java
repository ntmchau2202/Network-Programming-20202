package client.views.screen;

import client.controller.HomeScreenController;
import client.controller.LeaderBoardController;
import client.controller.LoginFormController;
import client.controller.RegisterFormController;
import client.utils.Configs;
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
        } else if (evt.getSource() == returnPlayerBtn) {
            System.out.println("back to player");
            try {
                BaseScreenHandler loginFormHandler = new LoginFormHandler(this.stage,
                        Configs.LOGIN_FORM_PATH, new LoginFormController());
                loginFormHandler.setScreenTitle("Login");
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
                registerFormHandler.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (evt.getSource() == leaderBoardImageView) {
            System.out.println("leaderboard");
            try {
                BaseScreenHandler leaderboardHandler = new LeaderBoardHandler(this.stage,
                        Configs.LEADERBOARD_SCREEN_PATH, new LeaderBoardController());
                leaderboardHandler.setScreenTitle("Leaderboard");
                leaderboardHandler.show();
            } catch (IOException e) {
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
