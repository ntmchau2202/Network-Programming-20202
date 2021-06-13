package server.core.controller;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import entity.Match.Match;
import entity.Player.Player;
import entity.Player.RankPlayer;

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
	
	public Match getMatchById(int matchID) {
		Match match = null;
		for(Match m : this.ingameList) {
			if (m.getMatchID() == matchID) {
				match = m;
				break;
			}
		}
		return match;
	}

	public Match getMatchByPlayer(Player player) {
		Match match = null;
		for (Match m : this.ingameList) {
			if (m.isPlayerOfMatch(player)) {
				match = m;
				break;
			}
		}
		return match;
	}

	public void viewHall() {
		System.out.println("///////// list of players in hall");
		for(RankPlayer rankPlayer: hall) {
			System.out.println(">>> Hall Player " + rankPlayer.getUsername());
		}
	}

	public void viewNormalQueue() {
		System.out.println("///////// list of players in normal queue");
		for(Player player: normalQueue) {
			System.out.println(">>> Normal Queue Player " + player.getUsername());
		}
	}
	
	public void removeFromHall(Player player) {
		try {
			mutex.acquire();
			hall.remove(player);
			mutex.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pushToHall(RankPlayer player) {
		try {
			mutex.acquire();
			hall.add(player);
			mutex.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pushToQueue(Player player, String mode) {
		try {
				switch(mode) {
				case "normal":{
					mutex.acquire();
					normalQueue.add(player);
					System.out.println("Mutex info before acquiring " + mutex.toString());
					mutex.release();
					System.out.println("Mutex info after releasing " + mutex.toString());
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
//						hall.add((RankPlayer)p);
						normalQueue.remove(p);
						isOK = true;
						break;
					}
				}
			}
			
			for(RankPlayer p : rankedQueue) {
				if(p.getUsername().equalsIgnoreCase(username)) {
					mutex.acquire();
//					hall.add(p);
					rankedQueue.remove(p);
					isOK = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			System.out.println("i made it here");
//			if(mutex.isFair()) {
//				System.out.println("it's fair");
				mutex.release();
//				System.out.println("it released");
//			}
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


