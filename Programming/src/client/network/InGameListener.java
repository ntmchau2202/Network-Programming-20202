package client.network;

import java.io.IOException;

import org.json.JSONObject;

import client.views.screen.MainGameScreenHandler;
import javafx.application.Platform;
import message.chat.ChatServerMessage;
import message.chatack.ChatACKServerMessage;
import message.drawconfirm.DrawConfirmServerMessage;
import message.drawrequest.DrawRequestServerMessage;
import message.move.MoveServerMessage;
import protocol.Command;

public class InGameListener implements Runnable {
	private MainGameScreenHandler mainGameScreenHandler; // for updating GUI
	
	public InGameListener(MainGameScreenHandler handler) {
		this.mainGameScreenHandler = handler; 
	}
	
	private String unconditionalMessage;
	@Override
	public void run() {
		while(true){
			try {
				unconditionalMessage = ClientSocketChannel.getSocketInstance().listenIngameMessage();
				JSONObject jsMsg = new JSONObject(unconditionalMessage);
				Command cmd = Command.toCommand(jsMsg.getString("command_code"));
				
				switch(cmd) {
				case MOVE:{
					MoveServerMessage moveMsg = new MoveServerMessage(unconditionalMessage);
					int x = moveMsg.getX();
					int y = moveMsg.getY();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					Platform.runLater(new Runnable(){

						@Override
						public void run() {
							// update GUI here
							System.out.printf("Coordinate received: %d;%d\n", x, y);
							mainGameScreenHandler.switchTurn(true);
						}
						
					});
					
				}
				case CHAT:{
					ChatServerMessage chatMsg = new ChatServerMessage(unconditionalMessage);
					String incommingMsg = chatMsg.getMessage();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					// TODO: do analysis here
					
					Platform.runLater(new Runnable(){

						@Override
						public void run() {
							// TODO: update GUI here
							System.out.println("Message received: " + incommingMsg);
						}
						
					});
					
				}
				case CHATACK:{
					ChatACKServerMessage ack = new ChatACKServerMessage(unconditionalMessage);
					String msgID = ack.getMessageID();
					int matchID = ack.getMatchID();
					
					// TODO: do analysis here
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					Platform.runLater(new Runnable(){

						@Override
						public void run() {
							// TODO: update GUI here
							System.out.println("msgID: " + msgID);
							System.out.println("matchID: " + matchID);
						}
						
					});
					
				}
				
				case DRAW_REQUEST: {
					DrawRequestServerMessage drawRequest = new DrawRequestServerMessage(unconditionalMessage);
					int matchID = drawRequest.getMatchID();
					// TODO: do analysis here
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					Platform.runLater(new Runnable(){

						@Override
						public void run() {
							// TODO: update GUI here
							System.out.println("matchID: " + matchID);
						}						
					});
				}
				case DRAW_CONFIRM:{
					DrawConfirmServerMessage drawConfirm = new DrawConfirmServerMessage(unconditionalMessage);
					int matchID = drawConfirm.getMatchID();
					boolean acceptance = drawConfirm.getAcceptance();
					
					// TODO: do analysis here
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					Platform.runLater(new Runnable(){

						@Override
						public void run() {
							// TODO: update GUI here
							System.out.println("matchID: " + matchID);
							System.out.println("acceptance: " + acceptance);
						}						
					});
				}
				
				}
								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
