package client.network;

import java.io.IOException;

import org.json.JSONObject;

import client.views.screen.MainGameScreenHandler;
import javafx.application.Platform;
import message.move.MoveServerMessage;
import protocol.Command;

public class InGameListener implements Runnable {
	private MainGameScreenHandler mainGameScreenHandler;
	
	public InGameListener(MainGameScreenHandler handler) {
		this.mainGameScreenHandler = handler; // for updating GUI
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
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					Platform.runLater(new Runnable(){

						@Override
						public void run() {
							// update GUI here
							System.out.printf("Coordinate received: %d;%d\n", x, y);
						}
						
					});
					
				}
				case CHAT:{
					
				}
				case CHATACK:{
					
				}
				// other cases here if needed
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
