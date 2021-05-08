package client.utils;

public class Configs {
    public static final String IP_ADDRESS = "localhost";
    public static final int PORT = 6666;


    // first slash indicate that FXMLLoader find from the root : Programming folder
    // i.e: "/client" is different from "client"
    // if no slash at the head, location will be the place we load the screen
    public static final String SPLASH_SCREEN_PATH = "/client/views/fxml/SplashScreen.fxml";
    public static final String APP_ICON_PATH = "/client/views/fxml/assets/img/open.png";
    public static final String HOME_SCREEN_PATH  = "/client/views/fxml/HomeScreen.fxml";
    public static final String LOGIN_FORM_PATH  = "/client/views/fxml/LoginForm.fxml";
    public static final String REGISTER_FORM_PATH  = "/client/views/fxml/RegisterForm.fxml";
    public static final String LEADERBOARD_SCREEN_PATH  = "/client/views/fxml/Leaderboard.fxml";
}
