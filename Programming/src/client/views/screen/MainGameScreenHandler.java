package client.views.screen;

import client.controller.GameModeScreenController;
import client.controller.HomeScreenController;
import client.controller.MainGameScreenController;
import client.network.ClientSocketChannel;
import client.network.InGameListener;
import client.utils.Configs;
import javafx.application.Platform;
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
import java.util.concurrent.TimeUnit;

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

        // if not first player to play, listen move from opponent
        if (!this.mainGameScreenController.amIFirstPlayer()) {
            Task<Boolean> listenMoveTask = new Task<Boolean>() {
                protected Boolean call() {
                    Boolean isSuccessfull = false;
                    try {
                        // send move to server
                        isSuccessfull = mainGameScreenController.listenMove();
                        int recvX1 = mainGameScreenController.getX();
                        int recvY1 = mainGameScreenController.getY();
                        System.out.printf("Opponent plays move on coordinate [%d, %d]%n", recvX1, recvY1);

                        // display move on pane ??
                        addImageToPane((Pane)getNodeByRowColumnIndex(recvX1, recvY1, gameBoardGridPane), mainGameScreenController.getOpponentPlayerName());

                        mainGameScreenController.setTurn(true);

                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    return isSuccessfull;
                }
            };

            listenMoveTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

                public void handle(WorkerStateEvent t) {
                    Boolean isFound = (Boolean) t.getSource().getValue();
                    System.out.println("done:" + isFound);
                    if (isFound) {
                        System.out.println("listen move successfully");
                        // display move on pane ??
                    } else {
                        try {
                            notifyError("Can not listen move");
                        } catch (IOException e3) {
                            // TODO Auto-generated catch block
                            e3.printStackTrace();
                        }
                    }
                }
            });
            Thread thread = new Thread(listenMoveTask);
            thread.start();
        }

        // test display in (0, 0)
//        addImageToPane((Pane)getNodeByRowColumnIndex(0, 0, gameBoardGridPane), mainGameScreenController.getCurrentPlayer().getUsername());

    }

    private void addImageToPane(Pane pane, String movePlayerName) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                ImageView x = new ImageView();
                x.setFitHeight(39);
                x.setFitWidth(39);

                // initialize player move to be X if first player or O second player
                if (mainGameScreenController.checkFirstPlayerByName(movePlayerName)) {
                    x.setImage(new Image(X_IMAGE_FILE.toURI().toString()));
                } else {
                    x.setImage(new Image(O_IMAGE_FILE.toURI().toString()));
                }

                // add to pane
                if (pane.getChildren().isEmpty()) {
                    pane.getChildren().add(x);
                }
            }
        });
    }

    private void addPane(int rowIndex, int colIndex, Image move) {
        Pane pane = new Pane();
        pane.setPrefHeight(39);
        pane.setPrefWidth(39);

        pane.setOnMousePressed(e -> {

            // clickable only when it's player turn
            if (this.mainGameScreenController.isMyTurn() && !isLockMove) {
                if (pane.getChildren().isEmpty()) {
                    System.out.printf("Mouse clicked cell [%d, %d]%n", rowIndex, colIndex);
                    addImageToPane(pane, this.mainGameScreenController.getCurrentPlayer().getUsername());

                    // set move status array for checking winning state
                    this.status[rowIndex][colIndex] = (this.mainGameScreenController.amIFirstPlayer() ? 1 : 2);

                    // check victory
                    System.out.println(hasWinner(rowIndex, colIndex));

                    // send move
                    try {
                        // lock move
                        isLockMove = true;
                        Task<Boolean> sendMoveTask = new Task<Boolean>() {
                            protected Boolean call() {
                                Boolean isSuccessfull = false;
                                try {
                                    // send move to server
                                    if (mainGameScreenController.sendMove(rowIndex, colIndex)) {
                                        int recvX = mainGameScreenController.getX();
                                        int recvY = mainGameScreenController.getY();
                                        System.out.printf("Successfully place move on coordinate [%d, %d]%n", recvX, recvY);

                                        mainGameScreenController.setTurn(false);

                                        // start listening for opponent move
                                        if (mainGameScreenController.listenMove()) {
                                            int recvX1 = mainGameScreenController.getX();
                                            int recvY1 = mainGameScreenController.getY();
                                            System.out.printf("Opponent plays move on coordinate [%d, %d]%n", recvX1, recvY1);
                                            // display move on pane ??
                                            addImageToPane((Pane)getNodeByRowColumnIndex(recvX1, recvY1, gameBoardGridPane), mainGameScreenController.getOpponentPlayerName());

                                            mainGameScreenController.setTurn(true);

                                            // release lock move
                                            isLockMove = false;
                                            isSuccessfull = true;
                                        } else {
                                            // TODO: handle send failed here
                                            isSuccessfull = false;
                                        }
                                    } else {
                                        isSuccessfull = false;
                                    }


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
                                    System.out.println("listen move successfully");
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
                        Thread thread = new Thread(sendMoveTask);
                        thread.start();

                    } catch (Exception e4) {
                        // TODO Auto-generated catch block
                        e4.printStackTrace();
                    }
                } else {
                    pane.setDisable(true);
                    System.out.printf("Already clicked [%d, %d]%n", rowIndex, colIndex);
                }
            }
        });
        this.gameBoardGridPane.add(pane, colIndex, rowIndex);
    }

    public Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            Integer columnIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            if (rowIndex != null && rowIndex == row && columnIndex != null && columnIndex == column) {
                result = node;
                break;
            }
        }

        return result;
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
        System.out.println("Player " + prePlayer + "Win? " + myCheck.checkIt(row, col, this.status, prePlayer));
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
