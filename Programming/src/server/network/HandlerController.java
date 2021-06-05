package server.network;

import java.util.ArrayList;

public class HandlerController {
	private ArrayList<ReadCompletionHandler> listReadHandler;
	
	public HandlerController() {
		listReadHandler = new ArrayList<ReadCompletionHandler>();
	}
	
	public void addToListController(ReadCompletionHandler handler) {
		listReadHandler.add(handler);
	}
	
	public boolean isHandlerInList(ReadCompletionHandler handler) {
		for(ReadCompletionHandler h : listReadHandler) {
			if(h.equals(handler)) {
				return true;
			}
		}
		return false;
	}
}
