package client.views.screen;


import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import client.utils.Configs;
//import client.views.screen.BaseScreenHandler;
import client.utils.Misc;

import java.io.IOException;


public class PopupScreen extends BaseScreenHandler{


    @FXML
    ImageView tickIcon;

    @FXML
    Label message;


    public PopupScreen(Stage stage) throws IOException{
        super(stage, Configs.POPUP_PATH);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    private static PopupScreen popup(String message, String imagePath, Boolean undecorated) throws IOException{
        PopupScreen popup = new PopupScreen(new Stage());
        if (undecorated) popup.stage.initStyle(StageStyle.UNDECORATED);
        popup.message.setText(message);
        popup.setImage(imagePath);
        return popup;
    }

    public static void success(String message) throws IOException{
        popup(message, Misc.getImageByName(Configs.IMAGE_PATH) + "/" + "checked.png", false).show(true);
    }

    public static void error(String message) throws IOException{
        popup(message, Misc.getImageByName(Configs.IMAGE_PATH) + "/" + "warn.png", false).show(false);
    }

    public static PopupScreen loading(String message) throws IOException{
        return popup(message, Misc.getImageByName(Configs.IMAGE_PATH) + "/" + "loading.gif", true);
    }

    public void setImage(String path) {
        super.setImage(tickIcon, path);
    }

    public void show(Boolean autoClose) {
        super.show();
        if (autoClose) close(2.5);
    }

    public void show(double time) {
        super.show();
        close(time);
    }

    public void close(double time){
        PauseTransition delay = new PauseTransition(Duration.seconds(time));
        delay.setOnFinished( event -> stage.close() );
        delay.play();
    }
}
