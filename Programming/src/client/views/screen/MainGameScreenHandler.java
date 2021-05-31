package client.views.screen;

import client.controller.GameModeScreenController;
import client.controller.HomeScreenController;
import client.controller.MainGameScreenController;
import client.network.ClientSocketChannel;
import client.network.InGameListener;
import client.utils.Configs;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import message.ServerMessage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainGameScreenHandler extends BaseScreenHandler implements Initializable {
    @FXML
    private ImageView prevScreenImageView;
    @FXML
    private ImageView homeScreenImageView;
    @FXML
    private Pane boardPane;
    @FXML
    private GridPane gameBoardGridPane;
    @FXML
    private Text playerTurnText;
    @FXML
    private Label yourMove;

    // locking clicking other moves when a move is chosen
    private boolean isLockMove;

    private int[][] status = new int[15][15];

    // private int[][] player = new String[15][15];

    private final MainGameScreenController mainGameScreenController;
    private static final File X_IMAGE_FILE = new File(Configs.X_ICON_PATH);
    private static final File O_IMAGE_FILE = new File(Configs.O_ICON_PATH);
    private Image MOVE_IMAGE;

    private Thread alwaysListener;

    /**
     * @param stage      stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public MainGameScreenHandler(Stage stage, String screenPath, MainGameScreenController mainGameScreenController)
            throws IOException {
        super(stage, screenPath);
        this.mainGameScreenController = mainGameScreenController;
        HomeScreenHandler homeHandler = new HomeScreenHandler(this.stage, Configs.HOME_SCREEN_PATH,
                new HomeScreenController());
        prevScreenImageView.setOnMouseClicked(e -> {
            this.getPreviousScreen().show();
            this.getPreviousScreen().setScreenTitle("Game Mode");
        });
        homeScreenImageView.setOnMouseClicked(e -> {
            homeHandler.show();
            homeHandler.setScreenTitle("Home Screen");
        });
        //
        // this.gameBoardGridPane.setPrefHeight(591);
        // this.gameBoardGridPane.setPrefWidth(604);
        // this.gameBoardGridPane.setStyle("-fx-background-color: white");

        // display player turn
        if (mainGameScreenController.isMyTurn()) {
            playerTurnText.setText(mainGameScreenController.getCurrentPlayer().getUsername());
        } else {
            playerTurnText.setText("Opponent");
        }

        // initialize player move to be X if first player or O second player
        yourMove.setText(this.mainGameScreenController.amIFirstPlayer() ? "X" : "O");
        this.MOVE_IMAGE = new Image(this.mainGameScreenController.amIFirstPlayer() ? X_IMAGE_FILE.toURI().toString()
                : O_IMAGE_FILE.toURI().toString());

        // initialize game board grid pane
        int numCols = 15;
        int numRows = 15;

        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setHgrow(Priority.SOMETIMES);
            colConstraints.setMinWidth(10);
            colConstraints.setPrefWidth(100);
            this.gameBoardGridPane.getColumnConstraints().add(colConstraints);
        }

        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.SOMETIMES);
            rowConstraints.setMinHeight(10);
            rowConstraints.setPrefHeight(30);
            this.gameBoardGridPane.getRowConstraints().add(rowConstraints);
        }

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                addPane(i, j, this.MOVE_IMAGE);
            }
        }

        // unlock move
        isLockMove = false;
    }

    private void addPane(int rowIndex, int colIndex, Image move) {
        Pane pane = new Pane();
        ImageView x = new ImageView();
        pane.setPrefHeight(39);
        pane.setPrefWidth(39);
        x.setFitHeight(39);
        x.setFitWidth(39);
        x.setImage(move);

        pane.setOnMousePressed(e -> {
            this.status[rowIndex][colIndex] = (this.mainGameScreenController.amIFirstPlayer() ? 1 : 2);

            // clickable only when it's player turn
            if (this.mainGameScreenController.isMyTurn() && !isLockMove) {
                if (pane.getChildren().isEmpty()) {
                    System.out.printf("Mouse clicked cell [%d, %d]%n", rowIndex, colIndex);
                    pane.getChildren().add(x);

                    // send move
                    try {
                        // lock move
                        isLockMove = true;
                        Task<Boolean> sendMoveTask = new Task<Boolean>() {
                            protected Boolean call() {
                                Boolean isSuccessfull = false;
                                try {
                                    // send move to server
                                    isSuccessfull = mainGameScreenController.sendMove(rowIndex, colIndex);
                                    int recvX = mainGameScreenController.getX();
                                    int recvY = mainGameScreenController.getY();
                                    System.out.printf("Successfully place move on coordinate [%d, %d]%n", recvX, recvY);
                                    // release lock move
                                    isLockMove = false;
                                } catch (Exception e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                                return isSuccessfull;
                            }
                        };

                        sendMoveTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

                            public void handle(WorkerStateEvent t) {
                                Boolean isFound = (Boolean) t.getSource().getValue();
                                System.out.println("done:" + isFound);
                                if (isFound) {
                                    // start listening for opponent move
                                    try {
                                        if (mainGameScreenController.listenMove()) {
                                            int recvX1 = mainGameScreenController.getX();
                                            int recvY1 = mainGameScreenController.getY();
                                            System.out.printf("Opponent plays move on coordinate [%d, %d]%n", recvX1, recvY1);

                                            // display move on pane ??

                                        } else {
                                            // TODO: handle send failed here
                                        }

                                    } catch (Exception e2) {
                                        // TODO Auto-generated catch block
                                        e2.printStackTrace();
                                    }
                                } else {
                                    try {
                                        notifyError("Can not place move");
                                    } catch (IOException e3) {
                                        // TODO Auto-generated catch block
                                        e3.printStackTrace();
                                    }
                                }
                            }
                        });

                    } catch (Exception e4) {
                        // TODO Auto-generated catch block
                        e4.printStackTrace();
                    }

                    System.out.println(hasWinner(rowIndex, colIndex));
                } else {
                    pane.setDisable(true);
                    System.out.printf("Already clicked [%d, %d]%n", rowIndex, colIndex);
                }
            }
        });
        this.gameBoardGridPane.add(pane, colIndex, rowIndex);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /*
     *
     * Kiểm tra người chơi hiện tại có chiến thắng hay không?
     */
    public boolean hasWinner(int row, int col) {

        Check myCheck = new Check(15, 15);
        int prePlayer = (this.mainGameScreenController.amIFirstPlayer() ? 1 : 2);
        System.out.println("Player " + prePlayer + "Win?" + myCheck.checkIt(row, col, this.status, prePlayer));
        return myCheck.checkIt(row, col, this.status, prePlayer);

        /*
         * Kiểm tra bảng có còn ô trống nào không ?
         */
        // public boolean boardFilledUp() {
        // for (int row = 0; row < 16; row++) {
        // for (int col = 0; col < 16; col++) {
        // if (board[row][col] == null) {
        // return false;
        // }
        // }
        // }
        // return true;
        //
        // }
    }
}
