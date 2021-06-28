package server.core.controller;

import entity.Player.Player;
import protocol.Command;
import server.core.utils.Misc;
import server.entity.match.Match;
import server.entity.network.completionHandler.ReadCompletionHandler;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class CompletionHandlerController {
	public Player curPlayer;
	public Match curMatch;
	private Semaphore mutex;

	private final ArrayList<ReadCompletionHandler> listReadHandler;
	private final String handlerCtrlID;
	
	public CompletionHandlerController() {
		listReadHandler = new ArrayList<ReadCompletionHandler>();
		this.handlerCtrlID = "HandlerCtrl-" + Misc.genShortUUID();
		mutex = new Semaphore(1);
	}

	public String getHandlerCtrlID() {
		return this.handlerCtrlID;
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

	public void forceStopAllHandlers() {
		try {
			mutex.acquire();
			for(ReadCompletionHandler h : listReadHandler) {
				h.forceStopHandler();
			}
			mutex.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean cancelLowPriorityHandler(Command cmd) {
		for(ReadCompletionHandler h : listReadHandler) {
			if(h.getCommand().getCommandPriority().getPriorityOrder() < cmd.getCommandPriority().getPriorityOrder()) {
				// cancel low priority handler
				h.cancelHandler();

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
