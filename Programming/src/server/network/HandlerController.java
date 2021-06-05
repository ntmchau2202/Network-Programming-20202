package server.network;

import java.util.ArrayList;

public class HandlerController {
	private ArrayList<ReadCompletionHandler> listReadHandler;
	
	private HandlerController() {
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
	
	public boolean removeHandlerFromList(ReadCompletionHandler handler) {
		return listReadHandler.remove(handler);
	}
	
	// This function return a ReadCompletionHandler with same command if there is
	// If there isn't any, the function returns null
	// Note that each WriteCompletionHandler must remove the handler from the list before finish
	// If not, the result of this function will be false
	public ReadCompletionHandler getCouple(ReadCompletionHandler curHandler) {
		for(ReadCompletionHandler h : listReadHandler) {
			if(h.getCommand().compareTo(curHandler.getCommand())==0) {
				return h;
			}
		}
		return null;
	}
	
}
