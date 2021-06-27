package server.core.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import entity.Player.GuestPlayer;
import javafx.util.Pair;
import server.core.authentication.T3Authenticator;
import server.entity.match.Match;
import entity.Player.Player;
import entity.Player.RankPlayer;

public class QueueController {
    private ArrayList<Player> hall;
    private ArrayList<Player> normalQueue;
    private ArrayList<RankPlayer> rankedQueue;
    private ArrayList<Match> ingameList;

    private Semaphore mutex, endgameMutex;

    public QueueController() {
        hall = new ArrayList<>();
        normalQueue = new ArrayList<>();
        rankedQueue = new ArrayList<>();
        ingameList = new ArrayList<>();
        mutex = new Semaphore(1);
        endgameMutex = new Semaphore(1);
    }

    public void startQueueController() {
        Thread queueThread = new Thread(new QueueThread());
        queueThread.start();
        Thread cleanUpThread = new Thread(new CleanUpThread());
        cleanUpThread.start();
    }

    public ArrayList<Player> getHall() {
        return hall;
    }

    public ArrayList<Player> getNormalQueue() {
        return normalQueue;
    }

    public ArrayList<RankPlayer> getRankedQueue() {
        return rankedQueue;
    }

    public Match getMatchById(int matchID) {
        Match match = null;
        for (Match m : this.ingameList) {
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
            if (m.isPlayerOfMatch(player) && !m.isEnded()) {
                match = m;
                break;
            }
        }
        return match;
    }

    public void viewHall() {
        System.out.println("///////// list of players in hall");
        for (Player rankPlayer : hall) {
            System.out.println(">>> Hall Player " + rankPlayer.getUsername());
        }
    }

    public void viewNormalQueue() {
        System.out.println("///////// list of players in normal queue");
        for (Player player : normalQueue) {
            System.out.println(">>> Normal Queue Player " + player.getUsername());
        }
    }

    public void viewRankedQueue() {
        System.out.println("///////// list of players in ranked queue");
        for (Player player : rankedQueue) {
            System.out.println(">>> Ranked Queue Player " + player.getUsername());
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

    public void pushToHall(Player player) {
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
            switch (mode) {
                case "normal": {
                    mutex.acquire();
                    normalQueue.add(player);
//                    System.out.println("Mutex info before acquiring " + mutex.toString());
                    mutex.release();
//                    System.out.println("Mutex info after releasing " + mutex.toString());
                    break;
                }
                case "ranked": {
                    mutex.acquire();
                    if (rankedQueue.size() <= 0) {
                        rankedQueue.add((RankPlayer) player);
                    } else {
                        boolean isAdded = false;

                        for (int i = 0; i < rankedQueue.size(); i++) {
                            if (rankedQueue.get(i).getElo() < ((RankPlayer) player).getElo()) {
                                rankedQueue.add(i, (RankPlayer) player);
                                isAdded = true;
                                break;
                            }
                        }
                        if (!isAdded) {
                            rankedQueue.add((RankPlayer) player);
                        }
                    }
//					rankedQueue.add((RankPlayer)player);
                    mutex.release();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean endGame(String winnerUserName, int matchID) {
        try {
            endgameMutex.acquire();
            for (Match m : ingameList) {
                if (m.getMatchID() == matchID) {
                    // mark as end
                    m.setWinner(winnerUserName);
                    m.setEnd(true);

                    // pop players back to the hall, but do not delete match immediately
                    // let the thread do that shiet
                    Player player1 = m.getPlayer1();
                    Player player2 = m.getPlayer2();

                    // only RankPlayer back to hall
                    if (player1 instanceof RankPlayer) {
                        hall.add((RankPlayer) player1);
                    } else if (player1 instanceof GuestPlayer) {
                        // logout guest player
                        T3Authenticator.getT3AuthenticatorInstance().logout(player1.getUsername(), player1.getSessionId());
                    }

                    if (player2 instanceof RankPlayer) {
                        hall.add((RankPlayer) player2);
                    } else if (player2 instanceof  GuestPlayer) {
                        // logout guest player
                        T3Authenticator.getT3AuthenticatorInstance().logout(player2.getUsername(), player2.getSessionId());
                    }

                    // just for sure: view list of players in hall
                    this.viewHall();
                    endgameMutex.release();
                    return true;
                }
            }
            endgameMutex.release();
            return false;
        } catch (Exception e) {
            System.out.println("Error when removing game");
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFromQueue(String username) {
        System.out.println("*** Processing: remove user " + username);
        boolean isOK = false;
        try {
            for (Player p : normalQueue) {
                if (p.getUsername().equalsIgnoreCase(username)) {
                    mutex.acquire();
//						hall.add((RankPlayer)p);
                    normalQueue.remove(p);
                    isOK = true;
                    break;
                }
            }

            for (RankPlayer p : rankedQueue) {
                if (p.getUsername().equalsIgnoreCase(username)) {
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
            while (true) {
                try {
                    Thread.sleep(1000);

                    // normal queue
                    if (normalQueue.size() > 1) {
                        Player player1 = normalQueue.remove(0);
                        Player player2 = normalQueue.remove(0);
                        ingameList.add(new Match(player1, player2, false));
                    }

                    // rank queue
                    for (int i = 0; i < rankedQueue.size() - 1; i++) {
                        if (rankedQueue.get(i).getElo() - 500 < rankedQueue.get(i + 1).getElo()) {
                            // remember that we remove ele at i + 1 , but after remove ele at i, ele at (i + 1) becomes ele at i
                            Player player1 = rankedQueue.remove(i);
                            Player player2 = rankedQueue.remove(i);
                            ingameList.add(new Match(player1, player2, true));
                            break;
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private class CleanUpThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5000);
                    Iterator<Match> ingameListIterator = ingameList.iterator();
//					for(Match m : ingameList) {
//						if (m.isEnded()) {
//							ingameList.remove(m);
//						}
//					}
                    while (ingameListIterator.hasNext()) {
                        Match m = ingameListIterator.next();
                        if (m.isEnded()) {
                            ingameListIterator.remove();
                            System.out.println("Number of game currently: " + ingameList.size());
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}


