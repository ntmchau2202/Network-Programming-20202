package client.controller;

import entity.Player.Player;

public class GameModeScreenController extends BaseController{
    private Player curPlayer;
    public GameModeScreenController(Player curPlayer) {
        this.curPlayer = curPlayer;
    }

    public Player getCurPlayer() {
        return this.curPlayer;
    }

    public boolean findPracticeGame() {
        return false;
    }

    public boolean findRankGame() {
        return false;
    }
}
