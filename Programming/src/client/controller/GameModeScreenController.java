package client.controller;

import entity.Player.RankPlayer;

public class GameModeScreenController extends BaseController{
    private RankPlayer curPlayer;
    public GameModeScreenController(RankPlayer curPlayer) {
        this.curPlayer = curPlayer;
    }

    public RankPlayer getCurPlayer() {
        return this.curPlayer;
    }

    public boolean findPracticeGame() {
        return false;
    }

    public boolean findRankGame() {
        return false;
    }
}
