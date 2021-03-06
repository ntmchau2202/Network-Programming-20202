package client;

import client.controller.HomeScreenController;
import client.network.ClientSocketChannel;
import client.utils.Configs;
import client.utils.Misc;
import client.views.screen.HomeScreenHandler;
import entity.Player.LeaderboardPlayerList;
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
import java.sql.SQLException;

public class ClientApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // initialize the scene
            AnchorPane root = FXMLLoader.load(getClass().getResource(Configs.SPLASH_SCREEN_PATH));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Caro Game");
            primaryStage.getIcons().add(Misc.getImageByName(Configs.APP_ICON_PATH));
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
//                try {
//                	ClientSocketChannel.getSocketInstance();
//                    LeaderboardPlayerList.getLeaderboardPlayerListInstance().synchronizeLeaderboardPlayerWithDb();
//                    System.out.println("synchronize done");
//                } catch (SQLException ioException) {
//                    ioException.printStackTrace();
//                }
            });

            // After fade out, load actual content
            fadeOut.setOnFinished((e) -> {
                try {
                    HomeScreenHandler homeHandler = new HomeScreenHandler(primaryStage, Configs.HOME_SCREEN_PATH, new HomeScreenController());
                    homeHandler.setScreenTitle("Home Screen");
                    homeHandler.show();
                } catch (IOException e1) {
                    e1.printStackTrace();
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
