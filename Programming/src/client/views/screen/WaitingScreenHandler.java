package client.views.screen;


import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import client.controller.GameModeScreenController;
import client.controller.HomeScreenController;
import client.utils.Configs;
//import client.views.screen.BaseScreenHandler;
import client.utils.Misc;

import java.io.IOException;


public class WaitingScreenHandler extends BaseScreenHandler{
	private Thread quitQueueThread;
	private GameModeScreenController gameModeScreenController;
    @FXML
    private Button quitQueue;

    public WaitingScreenHandler(Stage stage, GameModeScreenController gameModeScreenController) throws IOException{
        super(stage, Configs.WAITING_PATH);
        this.gameModeScreenController = gameModeScreenController;
        // let this be a feature :)
        if(this.gameModeScreenController == null) {
        	quitQueue.setDisable(true);
        }
        quitQueue.setOnMouseClicked(e -> {
        	try {
            	handleQuitQueueAction();
        	} catch (Exception ex) {
        		ex.printStackTrace();
        	}

        });
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
    
    
	private void handleQuitQueueAction() {
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
			WaitingScreenHandler curHandler = this;
			quitQueueTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent arg0) {
					Boolean isSuccess = (Boolean) arg0.getSource().getValue();
					System.out.println("quit queue done:" + isSuccess);	
					try {
						if(isSuccess) {
							notifySuccess("Quit queue successfully!");
							System.out.println("Quit queue successfully!");
						} else {
							notifyError("Quit queue failed. Please try again later");
							System.out.println("Quit queue failed. Please try again later");
						}
						
						// if the controller is null, then its guest
						if(curHandler.gameModeScreenController == null) {
							HomeScreenHandler homeScr = new HomeScreenHandler(curHandler.stage, Configs.HOME_SCREEN_PATH, new HomeScreenController());
							homeScr.setScreenTitle("Home Screen");
							homeScr.show();
						} else {
							GameModeScreenHandler gameModeScr = new GameModeScreenHandler(curHandler.stage, Configs.GAME_MODE_SCREEN_PATH,
									new GameModeScreenController(curHandler.gameModeScreenController.getCurPlayer()));
							gameModeScr.setScreenTitle("Game Mode");
							gameModeScr.show();
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
}
