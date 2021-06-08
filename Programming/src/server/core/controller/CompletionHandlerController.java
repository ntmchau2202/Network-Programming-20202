package server.core.controller;

import server.entity.network.completionHandler.ReadCompletionHandler;

import java.util.ArrayList;

public class CompletionHandlerController {
	private final ArrayList<ReadCompletionHandler> listReadHandler;
	
	public CompletionHandlerController() {
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
	public ReadCompletionHandler getCouple(ReadCompletionHandler curHandler) {
		for(ReadCompletionHandler h : listReadHandler) {
			if(h.getCommand().compareTo(curHandler.getCommand())==0) {
				return h;
			}
		}
		return null;
	}
	
}
