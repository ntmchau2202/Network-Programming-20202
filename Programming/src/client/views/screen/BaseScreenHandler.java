package client.views.screen;

import client.controller.BaseController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Hashtable;

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
    }

    public BaseScreenHandler(Stage stage, String screenPath) throws IOException {
        super(screenPath);
        this.stage = stage;
        this.stage.setResizable(false);
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
}
