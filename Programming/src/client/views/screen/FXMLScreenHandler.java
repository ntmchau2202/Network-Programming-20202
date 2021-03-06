package client.views.screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FXMLScreenHandler {
    protected FXMLLoader loader;
    protected AnchorPane content;

    public FXMLScreenHandler(String screenPath) throws IOException {
        URL screenLocation = getClass().getResource(screenPath);
        this.loader = new FXMLLoader(screenLocation);
        this.loader.setController(this); // Set this class as the controller
        this.content = loader.load();
    }

    public AnchorPane getContent() {
        return this.content;
    }

    public FXMLLoader getLoader() {
        return this.loader;
    }

    public void setImage(ImageView imageView, String path) {
        File file = new File(path);
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
    }
}
