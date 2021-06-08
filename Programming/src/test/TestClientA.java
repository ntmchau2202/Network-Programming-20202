package test;

import client.controller.GameModeScreenController;
import client.controller.LoginFormController;
import client.controller.MainGameScreenController;
import client.network.ClientSocketChannel;
import client.utils.Configs;
import client.views.screen.BaseScreenHandler;
import client.views.screen.MainGameScreenHandler;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;

public class TestClientA extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // initialize the scene
            AnchorPane root = FXMLLoader.load(getClass().getResource(Configs.SPLASH_SCREEN_PATH));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Caro Game");
            primaryStage.getIcons().add(new Image(new File(Configs.APP_ICON_PATH).toURI().toString()));
            primaryStage.show();

            // Load splash screen with fade in effect
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);

            // Finish splash with fade out effect
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), root);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setCycleCount(1);

            // After fade in, start fade out
            fadeIn.play();
            fadeIn.setOnFinished((e) -> {
                fadeOut.play();
                // connect client
                try {

                    ClientSocketChannel.getSocketInstance();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });

            // After fade out, load actual content
            fadeOut.setOnFinished((e) -> {
                LoginFormController loginFormController = new LoginFormController();
                try {
                    if (!loginFormController.isLoginSuccessfully("duong", "123456")) {
                        System.out.println("Login failed");
                        System.exit(-1);
                    }
                    GameModeScreenController gameModeScreenController = new GameModeScreenController(loginFormController.getLoggedPlayer());
                    MainGameScreenController mainGameScreenController = new MainGameScreenController(gameModeScreenController.getCurPlayer());
                    int foundResult = gameModeScreenController.findPracticeGame();
                    if (foundResult == 0) {
                        if (gameModeScreenController.amIFirstPlayer()) {
                            System.out.println("i play first mother fucker");
                        }
                        mainGameScreenController.setOpponent(gameModeScreenController.getOpponentName(),
                                gameModeScreenController.getOpponentElo());
                        mainGameScreenController.setMatchID(gameModeScreenController.getMatchID());
                        System.out.println("Am I first player? " + gameModeScreenController.amIFirstPlayer());
                        mainGameScreenController.setIsFirstPlayer(gameModeScreenController.amIFirstPlayer());
                        mainGameScreenController.setTurn(gameModeScreenController.amIFirstPlayer());
                        BaseScreenHandler mainGameScreenHandler = new MainGameScreenHandler(primaryStage,
                                Configs.MAINGAME_SCREEN_PATH, mainGameScreenController);
                        mainGameScreenHandler.setScreenTitle("Tic Tac Toe - In game");
                        mainGameScreenHandler.show();
                    } else {
                        System.out.println("Can not find practice play match: " + foundResult);
                        System.exit(-1);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
