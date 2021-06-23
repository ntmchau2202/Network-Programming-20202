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


public class WaitingScreenHandler extends BaseScreenHandler{


    public WaitingScreenHandler(Stage stage) throws IOException{
        super(stage, Configs.WAITING_PATH);
//        stage.initModality(Modality.APPLICATION_MODAL);
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
