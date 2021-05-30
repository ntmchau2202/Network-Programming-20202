package server.process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import entity.Match.Match;
import entity.Player.Player;
import entity.Player.RankPlayer;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.util.Pair;
import message.matchfound.MatchFoundServerMessage;

public class QueueController {
	private  ArrayList<RankPlayer> hall;
	private  Queue<Player> normalQueue;
	private  Queue<RankPlayer> rankedQueue;
	private  ArrayList<Match> ingameList;
	
	public QueueController() {
		hall = new ArrayList<RankPlayer>();
		normalQueue = new LinkedList<Player>();
		rankedQueue = new LinkedList<RankPlayer>();
		ingameList = new ArrayList<Match>();
//		queueTask = new Task<Void>() {
//			protected Void call() {
////				transferToIngameList();
////				Random random = new Random();
//				while(true) {
//					try {
//						Thread.sleep(1000);
//						if(normalQueue.size() > 1) {
//							Player player1 = normalQueue.poll();
//							Player player2 = normalQueue.poll();
//							
//							ingameList.add(new Match(player1, player2));
//							// ingameList.add(new Pair<Integer, Player>(matchID, player2));
//							
//							// ingameList.
//						}
//						// other queues here
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
////				return null;
//			}
//		};
//		queueTask.setOnSucceeded((EventHandler) new EventHandler<WorkerStateEvent>() {
//
//			@Override
//			public void handle(WorkerStateEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
	}
	public void startQueueController() {
		Thread queueThread = new Thread(new QueueThread());
		queueThread.start();
	}
	
	public  ArrayList<RankPlayer> getHall() {
		return hall;
	}
	
	public  Queue<Player> getNormalQueue() {
		return normalQueue;
	}
	
	public  Queue<RankPlayer> getRankedQueue(){
		return rankedQueue;
	}
	
	public  ArrayList<Match> getIngameList(){
		return ingameList;
	}
	
	public  void removeFromHall(Player player) {
		hall.remove(player);
	}
	
	public  void pushToHall(RankPlayer player) {
		hall.add(player);
	}
	
	public  void pushToQueue(Player player, String mode) {
		switch(mode) {
		case "normal":{
			normalQueue.add(player);
			break;
		}
		case "ranked":{
			rankedQueue.add((RankPlayer)player);
		}
		}
	}
	
	private void transferToIngameList() {
//		Random random = new Random();
//		while(true) {
//			try {
//				Thread.sleep(1000);
//				if(normalQueue.size() > 1) {
//					Player player1 = normalQueue.poll();
//					Player player2 = normalQueue.poll();
//					
//					ingameList.add(new Match(player1, player2));
//					// ingameList.add(new Pair<Integer, Player>(matchID, player2));
//					
//					// ingameList.
//				}
//				// other queues here
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		System.out.println("Hello world its middnite now");
	}
	
	private class QueueThread implements Runnable {

		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(1000);
					if(normalQueue.size() > 1) {
						Player player1 = normalQueue.poll();
						Player player2 = normalQueue.poll();
						
						ingameList.add(new Match(player1, player2));
						// ingameList.add(new Pair<Integer, Player>(matchID, player2));
						
						// ingameList.
					}
					// other queues here
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
