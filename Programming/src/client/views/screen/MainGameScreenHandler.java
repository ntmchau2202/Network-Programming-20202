package client.views.screen;

import client.controller.GameModeScreenController;
import client.controller.HomeScreenController;
import client.controller.MainGameScreenController;
import client.utils.Configs;
import client.utils.Misc;
import entity.Player.RankPlayer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import message.ServerMessage;
import message.drawconfirm.DrawConfirmServerMessage;
import message.drawrequest.DrawRequestServerMessage;
import server.entity.match.ChatMessage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    @FXML
    private Label xPlayerName;
    @FXML
    private Label oPlayerName;
    @FXML
    private Label xPlayerElo;
    @FXML
    private Label oPlayerElo;
    @FXML
    private Label xPlayerWin;
    @FXML
    private Label oPlayerWin;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private TextField chatTextField;
    @FXML
    private Button sendButton;
    @FXML
    private ImageView drawRequestImageView;
    @FXML
    private ImageView quitRequestImageView;

    private VBox chatVbox;
    private Label chatName;
    // locking clicking other moves when a move is chosen
    private boolean isLockMove;

    private int[][] status = new int[15][15];

    // private int[][] player = new String[15][15];

    private final MainGameScreenController mainGameScreenController;
    private Image MOVE_IMAGE;

    private AtomicInteger isGameEnded; // 0 = nothing, 1 = win, -1 = draw
    /**
     * @param stage      stage of screen.
     * @param screenPath path to screen fxml
     * @throws IOException exception for IO operations
     */
    public MainGameScreenHandler(Stage stage, String screenPath, MainGameScreenController mainGameScreenController)
            throws IOException {
        super(stage, screenPath);
        this.mainGameScreenController = mainGameScreenController;

        drawRequestImageView.setOnMouseClicked(e -> {
            showDrawDialog();
        });

        quitRequestImageView.setOnMouseClicked(e -> {
            showConfirmationQuit();
        });

        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {

                this.status[row][col] = 0;
            }
        }
        // display player info
        xPlayerName.setText(mainGameScreenController.amIFirstPlayer() ? mainGameScreenController.getCurrentPlayer().getUsername() : mainGameScreenController.getOpponentPlayerName());
        oPlayerName.setText(!mainGameScreenController.amIFirstPlayer() ? mainGameScreenController.getCurrentPlayer().getUsername() : mainGameScreenController.getOpponentPlayerName());
        if (mainGameScreenController.getCurrentPlayer() instanceof RankPlayer) {
            xPlayerElo.setText(Integer.toString(mainGameScreenController.amIFirstPlayer() ? ((RankPlayer) mainGameScreenController.getCurrentPlayer()).getElo() : mainGameScreenController.getOpponentElo()));
            oPlayerElo.setText(Integer.toString(!mainGameScreenController.amIFirstPlayer() ? ((RankPlayer) mainGameScreenController.getCurrentPlayer()).getElo() : mainGameScreenController.getOpponentElo()));
            System.out.println(Integer.toString(mainGameScreenController.getOpponentElo()));
        }

        // display player turn
        if (mainGameScreenController.isMyTurn()) {
            playerTurnText.setText(mainGameScreenController.getCurrentPlayer().getUsername());
        } else {
            playerTurnText.setText("Opponent");
        }

        // initialize player move to be X if first player or O second player
        yourMove.setText(this.mainGameScreenController.amIFirstPlayer() ? "X" : "O");
        this.MOVE_IMAGE = this.mainGameScreenController.amIFirstPlayer() ? Misc.getImageByName(Configs.X_ICON_PATH) : Misc.getImageByName(Configs.O_ICON_PATH);

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
        isGameEnded = new AtomicInteger(0);

        // if not first player to play, listen move from opponent
        if (!this.mainGameScreenController.amIFirstPlayer()) {
            Task<Boolean> listenMoveTask = new Task<Boolean>() {
                protected Boolean call() {
                    Boolean isSuccessfull = false;
                    try {
                        // send move to server
                        isSuccessfull = mainGameScreenController.listenMove();
                        if (mainGameScreenController.getMoveResult().compareToIgnoreCase("")==0) {
	                        int recvX1 = mainGameScreenController.getX();
	                        int recvY1 = mainGameScreenController.getY();
	                        System.out.printf("Opponent plays move on coordinate [%d, %d]%n", recvX1, recvY1);
	
	                        // display move on pane ??
	                        addImageToPane((Pane)getNodeByRowColumnIndex(recvX1, recvY1, gameBoardGridPane), mainGameScreenController.getOpponentPlayerName());
	
	                        mainGameScreenController.setTurn(true);
                        } else if (mainGameScreenController.getMoveResult().compareToIgnoreCase("win")==0) {
                        	isGameEnded.set(1);
                        	this.cancel();
                        } else if (mainGameScreenController.getMoveResult().compareToIgnoreCase("draw")==0) {
                        	isGameEnded.set(-1);
                        	this.cancel();
                        }

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
            
            listenMoveTask.setOnCancelled(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent arg0) {
					try {
						if(isGameEnded.get() == 1) {
							notifySuccess("Game ended! The winner is " + mainGameScreenController.getFinalMovePlayer());
						} else if(isGameEnded.get() == -1) {
							notifySuccess("Game draw! You are awesome!");
						}
						// send update_user msg here
						if(mainGameScreenController.getCurrentGameMode().compareToIgnoreCase("guest")!=0) {
							try {
								mainGameScreenController.updateUserInformation();
								GameModeScreenHandler gameModeHandler = new GameModeScreenHandler(stage, Configs.GAME_MODE_SCREEN_PATH, new GameModeScreenController((RankPlayer)mainGameScreenController.getCurrentPlayer()));
		                        gameModeHandler.setScreenTitle("Game mode");
		                        gameModeHandler.show();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							// guest mode
							HomeScreenHandler homeScreenHandler = new HomeScreenHandler(stage, Configs.HOME_SCREEN_PATH, new HomeScreenController());
							homeScreenHandler.setScreenTitle("Home Screen");
							homeScreenHandler.show();
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
            	
            });
            
            
            
            Thread thread = new Thread(listenMoveTask);
            thread.start();
        };
        
        // thread for chat listening
        
        Task<Void> listenChatTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				while(true) {
					System.out.println("================================= Start listening to chatttttttttttt");
					ChatMessage recvMsg = mainGameScreenController.listenChat();
					if(recvMsg!= null && recvMsg.getMessage().length() != 0) {
						System.out.println("Message length !=0: " + recvMsg);
						updateChat(recvMsg);
					} else {
						System.out.println("Empty message :(");
					}
				}
			}
        	
        };
        
        Thread listenChatThread = new Thread(listenChatTask);
        listenChatThread.start();

        // test display in (0, 0)
        // addImageToPane((Pane)getNodeByRowColumnIndex(0, 0, gameBoardGridPane), mainGameScreenController.getCurrentPlayer().getUsername());

        // init chat vbox
        chatVbox = new VBox();
        chatVbox.setSpacing(5);
        chatScrollPane.setContent(chatVbox);

        chatTextField.setOnKeyTyped(e -> {
            sendButton.setDisable(chatTextField.getText().isEmpty());
        });
        
        Task<ServerMessage> listenDrawTask = new Task<ServerMessage>() {

			@Override
			protected ServerMessage call() throws Exception {
				while(true) {
					ServerMessage request = mainGameScreenController.listenDrawRequest();
					if (request != null) {
						if (request instanceof DrawRequestServerMessage) {
							DrawRequestServerMessage realReq = (DrawRequestServerMessage)request;
							// display dialog box for confirming draw here
							// how to connect this dialog with the current controller?
							// yes will send a confirm yes
							// no will send a reject
							showConfirmationDraw();
						} else if (request instanceof DrawConfirmServerMessage) {
							DrawConfirmServerMessage realReq = (DrawConfirmServerMessage)request;
							if(realReq.getAcceptance()) {
								// both accept
								// display draw dialog box here
								// then end the game
								isGameEnded.set(-1);
								notifySuccess("Game draw! You're awesome!");
								// return to main screen
								if(mainGameScreenController.getCurrentGameMode().compareToIgnoreCase("guest")!=0) {
									try {
										mainGameScreenController.updateUserInformation();
										GameModeScreenHandler gameModeHandler = new GameModeScreenHandler(stage, Configs.GAME_MODE_SCREEN_PATH, new GameModeScreenController((RankPlayer)mainGameScreenController.getCurrentPlayer()));
				                        gameModeHandler.setScreenTitle("Game mode");
				                        gameModeHandler.show();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									// guest mode
									HomeScreenHandler homeScreenHandler = new HomeScreenHandler(stage, Configs.HOME_SCREEN_PATH, new HomeScreenController());
									homeScreenHandler.setScreenTitle("Home Screen");
									homeScreenHandler.show();
								}
								break;
							} else {
								// notify request refused
								notifyError("Sorry, your opponent " + realReq.getPlayer() + " does not accept your draw request :(");
							}
						}
					}
					
				}
	        return null;
			}
        };
        
        Thread listenDrawThread = new Thread(listenDrawTask);
        listenDrawThread.start();
    }

    private void updateChat(ChatMessage recvMsg) {
    	Platform.runLater(new Runnable() {

			@Override
			public void run() {
				chatVbox.getChildren().add(addMessage(recvMsg));
			}
    		
    	});
    }
    
    private void addImageToPane(Pane pane, String movePlayerName) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                ImageView x = new ImageView();
                x.setFitHeight(39);
                x.setFitWidth(39);

                // initialize player move to be X if first player or O second player
                if (mainGameScreenController.checkFirstPlayerByName(movePlayerName)) {
                    x.setImage(Misc.getImageByName(Configs.X_ICON_PATH));
//                    x.setImage(new Image(X_IMAGE_FILE.toURI().toString()));
                } else {
                    x.setImage(Misc.getImageByName(Configs.O_ICON_PATH));
//                    x.setImage(new Image(O_IMAGE_FILE.toURI().toString()));
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
                    

                    // send move
                    try {
                        // lock move
                        isLockMove = true;
                        Task<Boolean> sendMoveTask = new Task<Boolean>() {
                            protected Boolean call() {
                                Boolean isSuccessfull = false;
                                try {
                                    // send move to server
                                	int isWin = hasWinner(rowIndex, colIndex);
                                	System.out.println(isWin);
                                	String result = "";
                                	if (isWin == 1) {
                                		result = "win"; 
                                	} else if (isWin == 0) {
                                		result = "draw";
                                	}
                                	
                                    if (mainGameScreenController.sendMove(rowIndex, colIndex, result)) {
                                        int recvX = mainGameScreenController.getX();
                                        int recvY = mainGameScreenController.getY();
                                        System.out.printf("Successfully place move on coordinate [%d, %d]%n", recvX, recvY);
                                        
                                        if (mainGameScreenController.getMoveResult().compareToIgnoreCase("win")==0) {
                                        	isGameEnded.set(1);
                                        	System.out.println("Result from send: Win player: " + mainGameScreenController.getFinalMovePlayer());
                                        	this.cancel();
                                        } else if (mainGameScreenController.getMoveResult().compareToIgnoreCase("draw")==0) {
                                        	isGameEnded.set(-1);
                                        	System.out.println("Result from send: draw");
                                        	this.cancel();
                                        }

                                        if(isGameEnded.get()==0) {
                                        	mainGameScreenController.setTurn(false);
                                            
                                            // start listening for opponent move
                                        	if (mainGameScreenController.listenMove()) {
                                                int recvX1 = mainGameScreenController.getX();
                                                int recvY1 = mainGameScreenController.getY();
                                                System.out.printf("Opponent plays move on coordinate [%d, %d]%n", recvX1, recvY1);
                                                // display move on pane ??
                                                addImageToPane((Pane)getNodeByRowColumnIndex(recvX1, recvY1, gameBoardGridPane), mainGameScreenController.getOpponentPlayerName());
                                                if (mainGameScreenController.getMoveResult().compareToIgnoreCase("win")==0) {
                                                	System.out.println("Result from listen: Win player: " + mainGameScreenController.getFinalMovePlayer());
                                                	isGameEnded.set(1);
                                                	this.cancel();
                                                } else if (mainGameScreenController.getMoveResult().compareToIgnoreCase("draw")==0) {
                                                	System.out.println("Result from listen: draw");
                                                	isGameEnded.set(-1);
                                                	this.cancel();
                                                }
                                                mainGameScreenController.setTurn(true);

                                                // release lock move
                                                isLockMove = false;
                                                isSuccessfull = true;
                                            } else {
                                                // TODO: handle send failed here
                                                isSuccessfull = false;
                                            }
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
                        
                        sendMoveTask.setOnCancelled(new EventHandler<WorkerStateEvent>() {

							@Override
							public void handle(WorkerStateEvent arg0) {
								try {
									if(isGameEnded.get() == 1) {
										notifySuccess("Game ended! The winner is " + mainGameScreenController.getFinalMovePlayer());
									} else if(isGameEnded.get() == -1) {
										notifySuccess("Game draw! You are awesome!");
									}
									// send update_user msg here
									if(mainGameScreenController.getCurrentGameMode().compareToIgnoreCase("guest")!=0) {
										try {
											mainGameScreenController.updateUserInformation();
											GameModeScreenHandler gameModeHandler = new GameModeScreenHandler(stage, Configs.GAME_MODE_SCREEN_PATH, new GameModeScreenController((RankPlayer)mainGameScreenController.getCurrentPlayer()));
					                        gameModeHandler.setScreenTitle("Game mode");
					                        gameModeHandler.show();
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									} else {
										// guest mode
										HomeScreenHandler homeScreenHandler = new HomeScreenHandler(stage, Configs.HOME_SCREEN_PATH, new HomeScreenController());
										homeScreenHandler.setScreenTitle("Home Screen");
										homeScreenHandler.show();
									}
									
								} catch (IOException e) {
									e.printStackTrace();
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
    public int hasWinner(int row, int col) {

        Check myCheck = new Check(15, 15);
        int prePlayer = (this.mainGameScreenController.amIFirstPlayer() ? 1 : 2);
        System.out.println("Player " + prePlayer + "Win? " + myCheck.checkIt(row, col, this.status, prePlayer));
        return myCheck.checkIt(row, col, this.status, prePlayer);
    }

    // lock/ unlock board, true: lock; false: unlock
    public void setGameBoardGridPaneLock(boolean b) {
        this.gameBoardGridPane.setDisable(b);
    }

    @FXML
    void sendMessage(final MouseEvent event) {
        // send msg here
    	String msgToSend = chatTextField.getText();
    	chatTextField.setText("");
    	sendButton.setDisable(true);
        Task<ChatMessage> sendChatTask = new Task<ChatMessage>() {
			@Override
			protected ChatMessage call() throws Exception {
				// TODO Auto-generated method stub
				System.out.println("Gotta send something...: " + msgToSend);
				return mainGameScreenController.sendChatMessage(msgToSend);
			}
        	
        };
        
        sendChatTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent arg0) {
				ChatMessage ret = (ChatMessage) arg0.getSource().getValue();
				updateChat(ret);
				sendButton.setDisable(false);
			}
        });
        Thread sendChatThread = new Thread(sendChatTask);
        sendChatThread.start();
    }

    public HBox addMessage(ChatMessage recvMsg)
    {
    	Label msg = null;
    	chatName = new Label();
    	
    	if (recvMsg == null) {
    		chatName.setText("[SERVER]: ");
    		chatName.setTextFill(Color.web("#ff0000"));
    		chatName.setPrefWidth(120);
            chatName.setStyle("-fx-font-size: 20px");
            chatName.setWrapText(true);
            
            msg = new Label(mainGameScreenController.getFinalErrorMessage());
            msg.setWrapText(true);
            msg.setPrefWidth(400);
            msg.setMaxWidth(500);
            msg.setStyle("-fx-font-size: 20px");
    	} else {
    		// chat name set
           
            chatName.setText(recvMsg.getSendPlayerName() +": ");
            chatName.setTextFill(Color.web(this.mainGameScreenController.getCurrentPlayer().getUsername().compareToIgnoreCase(recvMsg.getSendPlayerName()) == 0 ? "#FF4A05" : "#0082ec"));
            chatName.setPrefWidth(120);
            chatName.setStyle("-fx-font-size: 20px");
            chatName.setWrapText(true);

        	msg = new Label(recvMsg.getMessage());
            msg.setWrapText(true);
            msg.setPrefWidth(400);
            msg.setMaxWidth(500);
            msg.setStyle("-fx-font-size: 20px");
    	}
        
        HBox hbox = new HBox();
        hbox.setPrefWidth(600);
        hbox.getChildren().addAll(chatName,msg);
        return hbox;
    }

    private void showConfirmationDraw() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ask for Draw");
        alert.setHeaderText("Are you sure to accept draw request?");
        alert.setContentText("Your opponent have just asked for draw");

        // option != null.
        Optional<ButtonType> option = alert.showAndWait();
        
        Task<Void> acceptDrawTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				if(mainGameScreenController.sendDrawConfirm(true)) {
					// show a dialog box here 
					isGameEnded.set(-1);
					notifySuccess("Game draw! You are awesome");
				}
				return null;
			}
        };
        Task<Void> rejectDrawTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				if(mainGameScreenController.sendDrawConfirm(true)) {
					// show a dialog box here 
					isGameEnded.set(0);
				}
				return null;
			}
        };

        if (option.get() == null) {
            System.out.println("No selection!");
            Thread rejectDrawThread = new Thread(rejectDrawTask);
            rejectDrawThread.start();
        } else if (option.get() == ButtonType.OK) {
            // handle action here
            System.out.println("Draw accepted!");
        	Thread acceptDrawThread = new Thread(acceptDrawTask);
        	acceptDrawThread.start();
            
        } else if (option.get() == ButtonType.CANCEL) {
            System.out.println("Draw rejected!");
            Thread rejectDrawThread = new Thread(rejectDrawTask);
            rejectDrawThread.start();
        } else {
            System.out.println("-"); // ?
        }
    }
    
    @FXML
    void showDrawDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ask for Draw");
        alert.setHeaderText("Are you sure to ask for a draw?");

        // option != null.
        Optional<ButtonType> option = alert.showAndWait();
        
        Task<Void> acceptDrawTask = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				if(mainGameScreenController.sendDrawRequest()) {
					// show a dialog box here 
					notifySuccess("Waiting for confirmation...");
					// freeze the table here
				}
				return null;
			}
        };
        if (option.get() == null) {
            System.out.println("No selection!");
        } else if (option.get() == ButtonType.OK) {
            // handle action here
        	Thread acceptDrawThread = new Thread(acceptDrawTask);
        	acceptDrawThread.start();
            
        } else if (option.get() == ButtonType.CANCEL) {
            System.out.println("Draw rejected!");
        } else {
            System.out.println("-"); 
        }
    }

    @FXML
    void showConfirmationQuit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit Game");
        alert.setHeaderText("Are you sure to quit playing?");
        alert.setContentText("You will lose if you quit");

        // option != null.
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == null) {
            System.out.println("No selection!");
        } else if (option.get() == ButtonType.OK) {
            // handle action here
        	Task<Boolean> requestQuitTask = new Task<Boolean>() {

				@Override
				protected Boolean call() throws Exception {
					return mainGameScreenController.sendQuitGameRequest();
				}
        	};
        	
        	requestQuitTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent arg0) {
	                try {
	                	Boolean isOK = (Boolean) arg0.getSource().getValue();
	    				if (isOK) {
	    					notifySuccess("Game quitted successfully");
	    					if(mainGameScreenController.getCurrentGameMode().compareToIgnoreCase("guest")!=0) {
								try {
									mainGameScreenController.updateUserInformation();
									GameModeScreenHandler gameModeHandler = new GameModeScreenHandler(stage, Configs.GAME_MODE_SCREEN_PATH, new GameModeScreenController((RankPlayer)mainGameScreenController.getCurrentPlayer()));
			                        gameModeHandler.setScreenTitle("Game mode");
			                        gameModeHandler.show();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								// guest mode
								HomeScreenHandler homeScreenHandler = new HomeScreenHandler(stage, Configs.HOME_SCREEN_PATH, new HomeScreenController());
								homeScreenHandler.setScreenTitle("Home Screen");
								homeScreenHandler.show();
							}
	    				} else {
	    					notifyError("An error occured when quitting the game. Please try again later");
	    				}
	                } catch (Exception e) {
	                	e.printStackTrace();
	                }
				}
        	});
        	Thread requestDrawThread = new Thread(requestQuitTask);
        	requestDrawThread.start();
        	System.out.println("Quit confirmed!");
        } else if (option.get() == ButtonType.CANCEL) {
            System.out.println("Quit Cancelled!");
            
        } else {
            System.out.println("-");
        }
    }


}
