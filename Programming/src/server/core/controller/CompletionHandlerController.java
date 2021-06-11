package server.core.controller;

import protocol.Command;
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
		System.out.println(">>>>>> remove handler successfully: " + handler.getHandlerID());
		return listReadHandler.remove(handler);
	}

	public boolean cancelLowPriorityHandler(Command cmd) {
		for(ReadCompletionHandler h : listReadHandler) {
			if(h.getCommand().getCommandPriority().getPriorityOrder() < cmd.getCommandPriority().getPriorityOrder()) {
				// cancel low priority handler
				h.stopHandler();

				// remove priority handler from list
				if (this.removeHandlerFromList(h)) {
					System.out.println("HANDLERCONTROLLER: Cancel other low priority operations successfully");
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	// This function return a ReadCompletionHandler with same command if there is
	// If there isn't any, the function returns null
//	public ReadCompletionHandler getCouple(ReadCompletionHandler curHandler) {
//		for(ReadCompletionHandler h : listReadHandler) {
//			if(h.getCommand().compareTo(curHandler.getCommand())==0) {
//				return h;
//			}
//		}
//		return null;
//	}
	
	public ReadCompletionHandler getHandlerByCommand(Command cmd) {
		for(ReadCompletionHandler h : listReadHandler) {
			if(h.getCommand().compareTo(cmd)==0) {
				return h;
			}
		}
		return null;
	}
}
