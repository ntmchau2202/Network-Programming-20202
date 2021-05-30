package client.controller;

import entity.Player.LeaderboardPlayer;
import entity.Player.LeaderboardPlayerList;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaderBoardController extends BaseController{
    /**
     * This method process the return bike request.
     * @return lstStation list of available stations
     */
    public List<LeaderboardPlayer> getTopPlayers() throws InterruptedException, IOException, SQLException {
        List<LeaderboardPlayer> lstPlayer = new ArrayList<>();

        lstPlayer = (List<LeaderboardPlayer>) LeaderboardPlayerList.getLeaderboardPlayerListInstance().getLeaderboardPlayerList();
        return lstPlayer;
    }
}
