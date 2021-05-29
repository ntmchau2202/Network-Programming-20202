package client.views.screen;

import client.controller.GameModeScreenController;
import client.controller.HomeScreenController;
import client.controller.MainGameScreenController;
import client.network.ClientSocketChannel;
import client.network.InGameListener;
import client.utils.Configs;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainGameScreenHandler extends BaseScreenHandler
        implements Initializable {
    @FXML
    private ImageView prevScreenImageView;
    @FXML
    private ImageView homeScreenImageView;
    @FXML
    private Pane boardPane;
    @FXML
    private GridPane gameBoardGridPane;

    private final MainGameScreenController mainGameScreenController;
    private static final File X_IMAGE_FILE
            = new File(Configs.X_ICON_PATH);
    private static final File O_IMAGE_FILE
            = new File(Configs.O_ICON_PATH);
    private Image MOVE_IMAGE;


    /**
     * @param stage stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public MainGameScreenHandler(Stage stage, String screenPath, MainGameScreenController mainGameScreenController) throws IOException {
        super(stage, screenPath);
        this.mainGameScreenController = mainGameScreenController;
        HomeScreenHandler homeHandler = new HomeScreenHandler(this.stage, Configs.HOME_SCREEN_PATH, new HomeScreenController());
        prevScreenImageView.setOnMouseClicked(e -> {
            this.getPreviousScreen().show();
            this.getPreviousScreen().setScreenTitle("Game Mode");
        });
        homeScreenImageView.setOnMouseClicked(e -> {
            homeHandler.show();
            homeHandler.setScreenTitle("Home Screen");
        });
//
//        this.gameBoardGridPane.setPrefHeight(591);
//        this.gameBoardGridPane.setPrefWidth(604);
//        this.gameBoardGridPane.setStyle("-fx-background-color: white");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int numCols = 15 ;
        int numRows = 15;
//        this.gameBoardGridPane = new GridPane();

        for (int i = 0 ; i < numCols ; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setHgrow(Priority.SOMETIMES);
            colConstraints.setMinWidth(10);
            colConstraints.setPrefWidth(100);
            this.gameBoardGridPane.getColumnConstraints().add(colConstraints);
        }

        for (int i = 0 ; i < numRows ; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.SOMETIMES);
            rowConstraints.setMinHeight(10);
            rowConstraints.setPrefHeight(30);
            this.gameBoardGridPane.getRowConstraints().add(rowConstraints);
        }
        // if 1st player enter then x
        MOVE_IMAGE = new Image(X_IMAGE_FILE.toURI().toString());
        // else o MOVE_IMAGE = new Image(O_IMAGE_FILE.toURI().toString());
        for (int i = 0 ; i < numCols ; i++) {
            for (int j = 0; j < numRows; j++) {
                addPane(i, j, MOVE_IMAGE);
            }
        }
//        this.boardPane.getChildren().add(gameBoardGridPane);
//        Node source = (Node)e.getSource() ;
//        Integer colIndex = GridPane.getColumnIndex(source);
//        Integer rowIndex = GridPane.getRowIndex(source);
//        System.out.printf("Mouse entered cell [%d, %d]%n", colIndex.intValue(), rowIndex.intValue());
        
        Thread alwaysListener = new Thread(new InGameListener(this));
        alwaysListener.setDaemon(true);
        alwaysListener.start();
    }
    private void addPane(int colIndex, int rowIndex, Image move) {
        Pane pane = new Pane();
        ImageView x = new ImageView();
        pane.setPrefHeight(39);
        pane.setPrefWidth(39);
        x.setFitHeight(39);
        x.setFitWidth(39);
        x.setImage(move);

        pane.setOnMousePressed(e -> {
            if(mainGameScreenController.isMyTurn()) {
            	System.out.printf("Mouse clicked cell [%d, %d]%n", colIndex, rowIndex);
            	 pane.getChildren().add(x);
            	 
            	 // send information here
            	 try {
					if(mainGameScreenController.sendMove(colIndex, rowIndex)) {
						int recvX = mainGameScreenController.getX();
						int recvY = mainGameScreenController.getY();
						System.out.printf("Recv coordinate [%d, %d]%n", recvX, recvY);
						// TODO: display X or O here
					} else {
						// TODO: handle send failed here
					}
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	 
            	 
            	 // then switch it to false
            	 mainGameScreenController.setTurn(false);
            }         	       
        });
     
    }

//    public void removeNodeByRowColumnIndex(final int row,final int column,GridPane gridPane) {
//
//        ObservableList<Node> childrens = gridPane.getChildren();
//        for(Node node : childrens) {
//            if(node instanceof ImageView && gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
//                ImageView imageView= new ImageView(String.valueOf(node)); // use what you want to remove
//                gridPane.getChildren().remove(imageView);
//                break;
//            }
//        }
//    }
//    @FXML
//    private void mousePressed(MouseEvent e) {
//
//        Image x = new Image(Configs.X_ICON_PATH);
//        Image o = new Image(Configs.O_ICON_PATH);
//        Image empty = new Image();

//        Node source = (Node)event.getSource() ;
//        Integer colIndex = GridPane.getColumnIndex(source);
//        Integer rowIndex = GridPane.getRowIndex(source);
//        System.out.printf("Mouse clicked cell [%d, %d]%n", colIndex.intValue(), rowIndex.intValue());

//        gameBoardGridPane.add(new ImageView(x), colIndex, rowIndex);
//    }
}
