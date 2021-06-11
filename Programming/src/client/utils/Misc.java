package client.utils;

import javafx.scene.image.Image;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Misc {
    public static Image getImageByName(String imageName) {
        String imgPath = Configs.IMAGE_PATH + imageName;
        if (Files.notExists(Path.of(imgPath))) {
            // add backup path in here
            imgPath = "../" + Configs.IMAGE_PATH + imageName;
            System.out.println("Path is invalid");
            // remove this exit statement
//            System.exit(-1);
        }
        return new Image(new File(imgPath).toURI().toString());
    }
}
