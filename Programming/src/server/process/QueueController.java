package server.process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;

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
	private  ArrayList<Player> normalQueue;
	private  ArrayList<RankPlayer> rankedQueue;
	private  ArrayList<Match> ingameList;
	
	private Semaphore mutex;
	
	public QueueController() {
		hall = new ArrayList<RankPlayer>();
		normalQueue = new ArrayList<Player>();
		rankedQueue = new ArrayList<RankPlayer>();
		ingameList = new ArrayList<Match>();
		mutex = new Semaphore(1);
	}
	public void startQueueController() {
		Thread queueThread = new Thread(new QueueThread());
		queueThread.start();
	}
	
	public  ArrayList<RankPlayer> getHall() {
		return hall;
	}
	
	public  ArrayList<Player> getNormalQueue() {
		return normalQueue;
	}
	
	public  ArrayList<RankPlayer> getRankedQueue(){
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
	
	public void pushToQueue(Player player, String mode) {
		try {
				switch(mode) {
				case "normal":{
					mutex.acquire();
					normalQueue.add(player);
					mutex.release();
					break;
				}
				case "ranked":{
					mutex.acquire();
					rankedQueue.add((RankPlayer)player);
					mutex.release();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean removeFromQueue(String username) {
		boolean isOK = false;
		try {
			for(Player p : normalQueue) {
				if(p.getUsername().equalsIgnoreCase(username)) {
					if(p instanceof RankPlayer) {
						mutex.acquire();
						hall.add((RankPlayer)p);
						normalQueue.remove(p);
						isOK = true;
						break;
					}
				}
			}
			
			for(RankPlayer p : rankedQueue) {
				if(p.getUsername().equalsIgnoreCase(username)) {
					mutex.acquire();
					hall.add(p);
					rankedQueue.remove(p);
					isOK = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(mutex.isFair()) {
				mutex.release();
			}
			return isOK;
		}
	}
	
	private class QueueThread implements Runnable {

		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(1000);
					if(normalQueue.size() > 1) {
						Player player1 = normalQueue.remove(0);
						Player player2 = normalQueue.remove(0);
						
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


