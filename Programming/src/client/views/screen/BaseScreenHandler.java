package client.views.screen;

import client.controller.BaseController;
import client.network.ClientSocketChannel;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Optional;

public class BaseScreenHandler extends FXMLScreenHandler {
    private Scene scene;
    private BaseScreenHandler prev;
    protected final Stage stage;
    protected Hashtable<String, String> messages;
    private BaseController baseController;

    private BaseScreenHandler(String screenPath) throws IOException {
        super(screenPath);
        this.stage = new Stage();
        this.stage.setResizable(false);
        this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	 Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                 alert.setTitle("Exit confirmation");
                 alert.setHeaderText("Are you sure to exit the application?");

                 // option != null.
                 Optional<ButtonType> option = alert.showAndWait();
                 try {
                	if(option.get() == ButtonType.OK) {
	                	 closeConnection();
	                 } else {
	                	 we.consume();
	                 }
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
        });       
    }

    public BaseScreenHandler(Stage stage, String screenPath) throws IOException {
        super(screenPath);
        this.stage = stage;
        this.stage.setResizable(false);
        
        this.stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	 Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                 alert.setTitle("Exit confirmation");
                 alert.setHeaderText("Are you sure to exit the application?");

                 // option != null.
                 Optional<ButtonType> option = alert.showAndWait();
                 try {
                	if(option.get() == ButtonType.OK) {
	                	 closeConnection();
	                 } else {
	                	 we.consume();
	                 }
                } catch (Exception e) {
                	e.printStackTrace();
                }
            }
        });       
    }

    public void setPreviousScreen(BaseScreenHandler prev) {
        this.prev = prev;
    }

    public BaseScreenHandler getPreviousScreen() {
        return this.prev;
    }

    public void setScreenTitle(String string) {
        this.stage.setTitle(string);
    }

    public BaseController getBaseController() {
        return baseController;
    }

    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
    }

    public void forward(Hashtable<String, String> messages) {
        this.messages = messages;
    }

    public void show() {
        if (this.scene == null) {
            this.scene = new Scene(this.content);
        }
        this.stage.setScene(this.scene);
        this.stage.centerOnScreen();
        this.stage.show();
    }
    /**
     * notifying error
     * @param error error message
     * @throws IOException io exception
     */
    void notifyError(String error) throws IOException {
        System.out.println(error);
        PopupScreen.error(error);
    }

    /**
     * notifying success
     * @param success success message
     * @throws IOException io exception
     */
    void notifySuccess(String success) throws IOException {
        System.out.println(success);
        PopupScreen.success(success);
    }

    /**
     * notifying game over
     * @param gameResult success game over message
     * @throws IOException io exception
     */
    void notifyGameOver(String gameResult) throws IOException {
        System.out.println(gameResult);
        PopupScreen.success(gameResult);
    }

    void notifyWin(String gameResult) throws IOException {
        System.out.println(gameResult);
        PopupScreen.success(gameResult);
    }

    void notifyLose(String gameResult) throws IOException {
        System.out.println(gameResult);
        PopupScreen.success(gameResult);
    }
    
    void closeConnection() throws Exception {
    	if(ClientSocketChannel.isConnected()) {
    		ClientSocketChannel.closeConnection();
    	}
    	Platform.exit();
    	System.exit(0);
    }
}
