package server.core.processor;

import entity.Player.GuestPlayer;
import message.ClientMessage;
import entity.Player.LeaderboardPlayer;
import message.drawconfirm.DrawConfirmClientMessage;
import message.drawconfirm.DrawConfirmServerMessage;
import message.drawrequest.DrawRequestClientMessage;
import message.drawrequest.DrawRequestServerMessage;
import message.drawrequest.ListenDrawClientMessage;
import message.leaderboard.LeaderboardClientMessage;
import message.leaderboard.LeaderboardServerMessage;
import message.logout.LogoutClientMessage;
import message.logout.LogoutServerMessage;
import protocol.RequestBody;
import server.core.logger.T3Logger;
import server.entity.match.Match;
import entity.Move.Move;
import entity.Player.Player;
import entity.Player.RankPlayer;
import message.chat.ChatClientMessage;
import message.chat.ChatServerMessage;
import message.chat.ListenChatClientMessage;
import message.joinqueue.JoinQueueClientMessage;
import message.joinqueue.JoinQueueServerMessage;
import message.login.LoginClientMessage;
import message.login.LoginServerMessage;
import message.move.ListenMoveClientMessage;
import message.move.MoveClientMessage;
import message.move.MoveServerMessage;
import message.quitgame.QuitGameClientMessage;
import message.quitgame.QuitGameServerMessage;
import message.quitqueue.QuitQueueClientMessage;
import message.quitqueue.QuitQueueServerMessage;
import message.register.RegisterClientMessage;
import message.register.RegisterServerMessage;
import message.updateuser.UpdateUserClientMessage;
import message.updateuser.UpdateUserServerMessage;

import org.json.JSONObject;
import protocol.Command;
import protocol.StatusCode;
import server.core.authentication.T3Authenticator;
import server.core.controller.CompletionHandlerController;
import server.core.controller.QueueController;
import server.entity.match.ChatMessage;
import server.entity.network.IProcessor;
import server.entity.match.MatchMode;
import server.entity.match.MatchResult;
import server.entity.network.completionHandler.ReadCompletionHandler;
import server.model.LeaderboardModel;
import server.model.RankPlayerModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RequestProcessor implements IProcessor {
    public static Logger LOGGER = T3Logger.getLogger(RequestProcessor.class.getName());

    private final QueueController queueController;
    private final CompletionHandlerController handlerController;


    private Command cmd;

    private boolean isCancel;
    private boolean isStop;

    public RequestProcessor(QueueController queueController, CompletionHandlerController handlerController) {
        this.queueController = queueController;
        this.handlerController = handlerController;
        this.isCancel = false;
        this.isStop = false;
    }

    @Override
    public void cancelProcessingRequest() {
        this.isCancel = true;
    }

    @Override
    public void stopAll() {
        this.isStop = true;
        if (this.handlerController.curPlayer == null) {
            LOGGER.info("Current player is null");
        } else {
            if (handlerController.curMatch != null && !handlerController.curMatch.isEnded()) {
                Player opponent = handlerController.curMatch.getAnotherPlayer(handlerController.curPlayer.getUsername());
                if (opponent != null) {
                    if (!queueController.endGame(opponent.getUsername(), handlerController.curMatch.getMatchID())) {
                        LOGGER.info("Cannot endgame: " + handlerController.curMatch.getMatchID() + " with the winner is: " + opponent.getUsername());
                    }
                    // find player in hall to logout after endgame only when player is ranked player
                    safeLogoutPlayer();
                }
            } else {
                // check if current player exists in queue
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (this.queueController.isPlayerInNormalQueue(handlerController.curPlayer.getUsername()) || this.queueController.isPlayerInRankedQueue(handlerController.curPlayer.getUsername())) {
                        this.queueController.removeFromQueue(handlerController.curPlayer.getUsername());
                        break;
                    }
                }
                safeLogoutPlayer();
            }
        }
        this.queueController.viewHall();
        this.queueController.viewNormalQueue();
        this.queueController.viewRankedQueue();
    }

    private void safeLogoutPlayer() {
        if (this.handlerController.curPlayer instanceof RankPlayer) {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
//                    System.out.println("im herererrrere " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (this.queueController.isPlayerInHall(this.handlerController.curPlayer.getUsername())) {
                    this.queueController.removeFromHall(this.handlerController.curPlayer);
                    break;
                }
            }
            try {
                T3Authenticator.getT3AuthenticatorInstance().logout(this.handlerController.curPlayer.getUsername(), this.handlerController.curPlayer.getSessionId());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void storeCurrentCommand(String recvMsg) {
        JSONObject clientMsg = new JSONObject(recvMsg);
        this.cmd = Command.toCommand(clientMsg.getString("command_code"));
        // TODO: check cmd if it's null
    }

    public String getClientUsername(String recvMsg) {
        JSONObject clientMsg = new JSONObject(recvMsg);
        RequestBody requestBody = new RequestBody();
        requestBody.setBody(clientMsg.getJSONObject("info"));
        return requestBody.getBody().getString("username");
    }

    public Command getCommand() {
        return this.cmd;
    }

    public String processReturn(String recvMsg)
            throws Exception {

        String resMsg = "";

        switch (this.cmd) {
            case LOGIN: {
                resMsg = this.processLoginRequest(recvMsg);
                break;
            }
            case REGISTER: {
                resMsg = this.processRegisterRequest(recvMsg);
                break;
            }
            case JOIN_QUEUE: {
                resMsg = this.processJoinQueueRequest(recvMsg);
                break;
            }
            case QUIT_QUEUE: {
                resMsg = this.processQuitQueue(recvMsg);
                break;
            }
            case MOVE: {
                resMsg = this.processMoveRequest(recvMsg);
                break;
            }
            case LISTEN_MOVE: {
                resMsg = this.processListenMove(recvMsg);
                break;
            }
            case LISTEN_CHAT: {
                resMsg = this.processListenChat(recvMsg);
                break;
            }
            case DRAW_REQUEST: {
                resMsg = this.processRequestDrawRequest(recvMsg);
                break;
            }
            case DRAW_CONFIRM: {
                resMsg = this.processConfirmDrawRequest(recvMsg);
                break;
            }
            case LISTEN_DRAW: {
            	resMsg = this.processListenDraw(recvMsg);
            	break;
            }
            case QUIT_GAME: {
                resMsg = this.processQuitGameRequest(recvMsg);
                break;
            }
            case LEADERBOARD: {
                resMsg = this.processGetLeaderBoardRequest(recvMsg);
                break;
            }
            case CHAT: {
                resMsg = this.processChatRequest(recvMsg);
                break;
            }

            case UPDATE_USER: {
                resMsg = this.processUpdateUserRequest(recvMsg);
                break;
            }
//            case CHATACK: {
//                resMsg = this.processChatACKRequest();
//                break;
//            }
            case LOGOUT: {
                resMsg = this.processLogoutRequest(recvMsg);
                break;
            }
//            default: {
//                resMsg = this.notifyUnknownCommand();
//                break;
//            }
        }

        return resMsg;
    }

    private String processLoginRequest(String input)
            throws Exception {
        LoginServerMessage serverResponse;

        LoginClientMessage clientRequest = new LoginClientMessage(input);
        String username = clientRequest.getUsername();
        String password = clientRequest.getPassword();
        // get logged player
        RankPlayer loggedPlayer = T3Authenticator.getT3AuthenticatorInstance().login(username, password);
        if (loggedPlayer == null) {
            serverResponse = new LoginServerMessage(clientRequest.getMessageCommandID(), "", "", 0, 0, 0, 0, 0, StatusCode.ERROR,
                    "Username / Password is not valid Or this username has already been logged in");
        } else {
//            loggedPlayer.setUserSocket(sock);
            queueController.pushToHall(loggedPlayer);
            // store current player
            this.handlerController.curPlayer = loggedPlayer;

            serverResponse = new LoginServerMessage(clientRequest.getMessageCommandID(), username, loggedPlayer.getSessionId(), loggedPlayer.getElo(),
                    loggedPlayer.getRank(), loggedPlayer.getWinningRate(), loggedPlayer.getNoPlayedMatch(),
                    loggedPlayer.getNoWonMatch(), StatusCode.SUCCESS, "");
        }

        return serverResponse.toString();
    }

    private String processRegisterRequest(String input)
            throws Exception {
        RegisterServerMessage serverResponse;

        RegisterClientMessage clientRequest = new RegisterClientMessage(input);
        String username = clientRequest.getUsername();

        String password = clientRequest.getPassword();
        // register new player
        RankPlayer loggedPlayer = T3Authenticator.getT3AuthenticatorInstance().register(username, password);
        if (loggedPlayer == null) {
            serverResponse = new RegisterServerMessage(clientRequest.getMessageCommandID(), "", "", 0, 0, 0, 0, 0, StatusCode.ERROR,
                    "Username / Password is not valid");
        } else {
//            loggedPlayer.setUserSocket(sock);
            queueController.pushToHall(loggedPlayer);
            // store current playername
            this.handlerController.curPlayer = loggedPlayer;

            serverResponse = new RegisterServerMessage(clientRequest.getMessageCommandID(), username, loggedPlayer.getSessionId(), loggedPlayer.getElo(),
                    loggedPlayer.getRank(), loggedPlayer.getWinningRate(), loggedPlayer.getNoPlayedMatch(),
                    loggedPlayer.getNoWonMatch(), StatusCode.SUCCESS, "");
        }
        return serverResponse.toString();
    }

    private String processJoinQueueRequest(String input)
            throws Exception {
        System.out.println("======== Start processing join queue request");

        JoinQueueClientMessage clientRequest = new JoinQueueClientMessage(input);
        String sessionID = clientRequest.getSessionID();
        String mode = clientRequest.getGameMode();

        JoinQueueServerMessage serverResponse = null;
        System.out.println("Starting process match request with mode: " + mode);

        // check if this is ranked user
        Player loggedPlayer = null;
        for (Player player : queueController.getHall()) {
            if (sessionID.compareTo(player.getSessionId()) == 0) {
                loggedPlayer = player;
                break;
            }
        }

        // if it is not ranked user, call to create a new guest player account ONLY if mode is normal
        if (loggedPlayer == null) {
            if (mode.compareToIgnoreCase("normal") == 0) {
                GuestPlayer guestPlayer = T3Authenticator.getT3AuthenticatorInstance().createGuestPlayer();
                if (guestPlayer == null) {
                    serverResponse = new JoinQueueServerMessage(clientRequest.getMessageCommandID(), "", "", "", 0, -1, "", StatusCode.ERROR,
                            "Username / Password is not valid");
                } else {
                    // push guest player to hall
                    queueController.pushToHall(guestPlayer);

                    loggedPlayer = guestPlayer;
                }
            } else if (mode.compareToIgnoreCase("ranked") == 0) {
                serverResponse = new JoinQueueServerMessage(clientRequest.getMessageCommandID(), "", "", "", 0, -1, "", StatusCode.ERROR, "You must login to play in ranked mode. Please try again");
            } else {
                serverResponse = new JoinQueueServerMessage(clientRequest.getMessageCommandID(), "", "", "", 0, -1, "", StatusCode.ERROR, "Invalid match request. Please try again");
            }

        }

        // if no error, continue join queue
        if (serverResponse == null) {
            if (mode.compareToIgnoreCase("normal") == 0 || mode.compareToIgnoreCase("ranked") == 0) {
                System.out.println("requested username: " + loggedPlayer.getUsername());
                // store current playername
                this.handlerController.curPlayer = loggedPlayer;

                queueController.viewHall();
                queueController.viewNormalQueue();
                queueController.viewRankedQueue();
                // remove player from hall
                queueController.removeFromHall(loggedPlayer);
                // add player to normal / ranked queue
                queueController.pushToQueue(loggedPlayer, mode);

//                queueController.viewHall();
//                queueController.viewNormalQueue();
//                queueController.viewRankedQueue();

                // prepare message according to each player
                Match match = null;

                for (int i = 0; i < 100; i++) {
                    // force stop joining queue
                    if (isStop) {
                        return "";
                    }
                    if (!isCancel) {
                        match = queueController.getMatchByPlayer(loggedPlayer);
                        if (match != null) {
                            break;
                        }
                        Thread.sleep(500);
                    } else {
                        System.out.println("Cancelled from user. Quiting...");
                        break;
                    }
                }

                Player opponent;
                if (match != null) {
                    // store current match id
                    this.handlerController.curMatch = match;

                    if (loggedPlayer.getUsername().equalsIgnoreCase(match.getPlayer1().getUsername())) {
                        // user is player 1
                        opponent = match.getPlayer2();
                    } else {
                        // user is player 2
                        opponent = match.getPlayer1();
                    }
                    serverResponse = new JoinQueueServerMessage(clientRequest.getMessageCommandID(), loggedPlayer.getUsername(), loggedPlayer.getSessionId(), opponent.getUsername(),  opponent.getElo()/*mimic elo here*/, match.getMatchID(), match.getPlayer1().getUsername(), StatusCode.SUCCESS, "");
                } else {
                    if (isCancel) {
                        serverResponse = new JoinQueueServerMessage(clientRequest.getMessageCommandID(), "", "", "", 0, -1, "", StatusCode.ERROR, "QUIT_QUEUE sent from user");
                    } else {
                        serverResponse = new JoinQueueServerMessage(clientRequest.getMessageCommandID(), "", "", "", 0, -1, "", StatusCode.ERROR, "Cannot find appropriate match. Please try again later");
                    }
                    // when join queue meets error or cancelation
                    // push only ranked player back to hall
                    if (loggedPlayer instanceof RankPlayer) {
                        System.out.println("<<< Im a ranked player");
                        queueController.pushToHall(loggedPlayer);
                    } else {
                        System.out.println("<<< Im a guest player");
                    }

                    // and remove player from normal/ranked queue
                    if (queueController.removeFromQueue(loggedPlayer.getUsername())) {
                        System.out.println("====== remove " + loggedPlayer.getUsername() + " from queue successfully");
                    } else {
                        System.out.println("====== remove " + loggedPlayer.getUsername() + " from queue faileddddddd");
                    }
                }
            } else {
                // error
                serverResponse = new JoinQueueServerMessage(clientRequest.getMessageCommandID(), "", "", "", 0, -1, "", StatusCode.ERROR, "Invalid match request. Please try again");
            }
        }
//        queueController.viewHall();
//        queueController.viewNormalQueue();
//        queueController.viewRankedQueue();
        return serverResponse.toString();
    }

    private String processMoveRequest(String input)
            throws Exception {
        MoveClientMessage moveMsg = new MoveClientMessage(input);

        int matchID = moveMsg.getMatchID();
        int x = moveMsg.getX();
        int y = moveMsg.getY();
        String movePlayer = moveMsg.getMovePlayer();
        String state = moveMsg.getState();
        String result = moveMsg.getResult();

        // find user with match id. Should add a field of match ID for player?
        // Nah, maybe query in database
        // only do a prototype here
        Match match = queueController.getMatchById(matchID);

        // should have a check here
        // but first, let's try to successfully send data to both clients

        String errMsg = "";
        StatusCode statCode;

        if (movePlayer.equalsIgnoreCase(match.getPlayer1().getUsername())) {
            if ((match.getNumberOfMoves() % 2) == 0) {
                match.addNewMoveRecord(x, y, movePlayer, state, result);
                statCode = StatusCode.SUCCESS;
            } else {
                errMsg = "Invalid turn";
                statCode = StatusCode.ERROR;
            }
        } else {
            if ((match.getNumberOfMoves() % 2) == 1) {
                match.addNewMoveRecord(x, y, movePlayer, state, result);
                statCode = StatusCode.SUCCESS;
            } else {
                errMsg = "Invalid turn";
                statCode = StatusCode.ERROR;
            }
        }

        MatchResult matchResult = null;
        if (moveMsg.getResult().compareToIgnoreCase("win") == 0) {
            // win case
            matchResult = MatchResult.Win;
        } else if (moveMsg.getResult().compareToIgnoreCase("draw") == 0){
            // draw case
            movePlayer = "";
            matchResult = MatchResult.Draw;
        }

        if (moveMsg.getResult().compareToIgnoreCase("win") == 0 || moveMsg.getResult().compareToIgnoreCase("draw") == 0) {
            if (!queueController.endGame(movePlayer, matchID)) {
                statCode = StatusCode.ERROR;
                errMsg = "Cannot end the game...";
            } else {
                MatchMode matchMode;
                if (match.isRanked()) {
                    matchMode = MatchMode.Ranked;
                } else {
                    matchMode = MatchMode.Normal;
                }
                // process player info
                // only update for ranked player
                Player movePlayerObj = match.getPlayerByName(movePlayer);
                if (movePlayerObj instanceof RankPlayer) {
                    // who sending this quit request is the loser
                    RankPlayer wonPlayer =  (RankPlayer) movePlayerObj;

                    // update player info into both obj and db
                    RankPlayerModel.getRankPlayerModelInstance().updateRankPlayerInfo(wonPlayer, matchResult, matchMode);

                    // update info of player into leaderboard
                    LeaderboardModel.getLeaderboardModelInstance().updateLeaderboard(wonPlayer);
                }

                Player opponent = match.getAnotherPlayer(movePlayer);
                if (opponent instanceof RankPlayer) {
                    // who sending this quit request is the loser
                    RankPlayer lostPlayer = (RankPlayer) opponent;

                    // update player info into both obj and db
                    RankPlayerModel.getRankPlayerModelInstance().updateRankPlayerInfo(lostPlayer, matchResult, matchMode);

                    // update info of player into leaderboard
                    LeaderboardModel.getLeaderboardModelInstance().updateLeaderboard(lostPlayer);
                }
            }
        }

        MoveServerMessage fwdMsg = new MoveServerMessage(moveMsg.getMessageCommandID(), matchID, movePlayer, x, y, state, result, statCode, errMsg);

        return fwdMsg.toString();
    }

    private String processQuitQueue(String input) {
        QuitQueueClientMessage quitQueueMsg = new QuitQueueClientMessage(input);

        boolean isOK = queueController.removeFromQueue(quitQueueMsg.getUsername());
        StatusCode statCode;
        String errMsg = "";

        if (isOK) {
            ReadCompletionHandler joinQueueHandler = handlerController.getHandlerByCommand(Command.JOIN_QUEUE);
            if (joinQueueHandler == null) {
                statCode = StatusCode.ERROR;
                errMsg = "Player doesn't exist in match queue";
            } else {
                joinQueueHandler.cancelHandler();
                statCode = StatusCode.SUCCESS;
            }
        } else {
            statCode = StatusCode.ERROR;
            errMsg = "An error occured when quiting queue";
        }
        // wait a minute so that the previous message is sent back appropiately...
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        QuitQueueServerMessage response = new QuitQueueServerMessage(quitQueueMsg.getMessageCommandID(), quitQueueMsg.getUsername(), statCode, errMsg);
        return response.toString();
    }

    private String processListenMove(String input) {
        // strategy: polling until there is a new move

        ListenMoveClientMessage listenMsg = new ListenMoveClientMessage(input);
        String errMsg = "";
        StatusCode statCode;

        String username = listenMsg.getUsername();
        int matchID = listenMsg.getMatchID();
        Match match = queueController.getMatchById(matchID);

        Move latestMove;
        String movePlayer;
        while (true) {
            try {
                Thread.sleep(500);

                if (isStop) {
                    return "";
                }

                if (match.getNumberOfMoves() > 0) {
                    // get latest move
                    latestMove = match.getLatestMove();

                    if (username.equalsIgnoreCase(match.getPlayer1().getUsername())) {
                        if ((match.getNumberOfMoves() % 2) == 0 && !latestMove.isSeen()) {
                            latestMove.setSeen(true);
                            movePlayer = match.getPlayer2().getUsername();
                            break;
                        }
                    } else {
                        if ((match.getNumberOfMoves() % 2 == 1) && !latestMove.isSeen()) {
                            latestMove.setSeen(true);
                            movePlayer = match.getPlayer1().getUsername();
                            break;
                        }
                    }
                }

                // check status of match: break if winner has been found (this case is for opponent quits the game)
                if (match.isEnded() && !match.getWinner().isEmpty()) {
                    // now move player becomes the winner
                    System.out.println("the winner is: " + match.getWinner());
//                    movePlayer = match.getAnotherPlayer(username) != null ? match.getAnotherPlayer(username).getUsername() : "";
                    movePlayer = match.getPlayerByName(match.getWinner()) != null ? match.getPlayerByName(match.getWinner()).getUsername() : "";
                    match.addNewMoveRecord(-1, -1, movePlayer, "valid", "win");
                    // assign the last move
                    latestMove = match.getLatestMove();
                    break;
                }

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        statCode = StatusCode.SUCCESS;
        errMsg = "";
        MoveServerMessage fwdMsg = new MoveServerMessage(listenMsg.getMessageCommandID(), matchID, movePlayer, latestMove.getX(), latestMove.getY(), latestMove.getState(), latestMove.getResult(), statCode, errMsg);
//        listResponse.put(sock, fwdMsg.toString());
        return fwdMsg.toString();
    }

    private String processUpdateUserRequest(String input) throws Exception {
        // TODO: integrate this command with login command
        UpdateUserClientMessage req = new UpdateUserClientMessage(input);
        String username = req.getUsername();
        RankPlayer loggedPlayer = T3Authenticator.getT3AuthenticatorInstance().getPlayerInfo(username);
        UpdateUserServerMessage res;
        //int elo, int rank, float wRate, int nMatchPlayed, int nMatchWon,
        if (loggedPlayer != null) {
            res = new UpdateUserServerMessage(req.getMessageCommandID(), loggedPlayer.getUsername(), loggedPlayer.getElo(), loggedPlayer.getRank(), loggedPlayer.getWinningRate(), loggedPlayer.getNoPlayedMatch(), loggedPlayer.getNoWonMatch(), StatusCode.SUCCESS, "");
        } else {
            res = new UpdateUserServerMessage(req.getMessageCommandID(), username, -1, -1, -1, -1, -1, StatusCode.ERROR, "Cannot find user");
        }
        return res.toString();
    }

    private String processRequestDrawRequest(String input) throws Exception {
        String errMsg = "";
        StatusCode statCode;
        DrawRequestClientMessage requestMsg = new DrawRequestClientMessage(input);
        String requestUsername = requestMsg.getRequestPlayerName();
        String requestSessionID = requestMsg.getRequestSessionID();
        int matchID = requestMsg.getMatchID();

        Match match = queueController.getMatchById(matchID);
        if (match == null) {
            statCode = StatusCode.ERROR;
            errMsg = "Cannot process draw request: match " + matchID + " doesn't exist";
        } else {
            if (match.initDrawRequest(requestUsername)) {
                statCode = StatusCode.SUCCESS;
            } else {
                statCode = StatusCode.ERROR;
                errMsg = "Cannot process draw request: request player doesn't exist in match " + matchID;
            }
        }
        DrawRequestServerMessage serverMsg = new DrawRequestServerMessage(requestMsg.getMessageCommandID(), matchID, requestUsername,
                requestSessionID, statCode, errMsg);
        return serverMsg.toString();
    }

    private String processConfirmDrawRequest(String input) throws Exception {
        String errMsg = "";
        StatusCode statCode;
        DrawConfirmClientMessage confirmMsg = new DrawConfirmClientMessage(input);
        String confirmUsername = confirmMsg.getConfirmPlayer();
        String confirmSessionID = confirmMsg.getSessionID();
        boolean acceptance = confirmMsg.getAcceptance();
        int matchID = confirmMsg.getMatchID();

        Match match = queueController.getMatchById(matchID);
        if (match == null) {
            statCode = StatusCode.ERROR;
            errMsg = "Cannot process draw confirmation: match " + matchID + " doesn't exist";
        } else {
            if (acceptance) {
                if (match.confirmDrawRequest(confirmUsername)) {
                    statCode = StatusCode.SUCCESS;
                    // end this game with empty winner player name
                    if (!queueController.endGame("", matchID)) {
                        statCode = StatusCode.ERROR;
                        errMsg = "Cannot end the game...";
                    } else {
                        MatchMode matchMode;
                        if (match.isRanked()) {
                            matchMode = MatchMode.Ranked;
                        } else {
                            matchMode = MatchMode.Normal;
                        }
                        // process player info
                        // only update for ranked player in normal match
                        Player confirmPlayer = match.getPlayerByName(confirmUsername);
                        if (confirmPlayer instanceof RankPlayer) {
                            RankPlayer confirmRankPlayer =  (RankPlayer) confirmPlayer;

                            // update player info into both obj and db
                            RankPlayerModel.getRankPlayerModelInstance().updateRankPlayerInfo(confirmRankPlayer, MatchResult.Draw, matchMode);

                            // update info of player into leaderboard
                            LeaderboardModel.getLeaderboardModelInstance().updateLeaderboard(confirmRankPlayer);
                        }

                        Player opponent = match.getAnotherPlayer(confirmUsername);
                        if (opponent instanceof RankPlayer) {
                            // who sending this quit request is the loser
                            RankPlayer opponentRankPlayer = (RankPlayer) opponent;

                            // update player info into both obj and db
                            RankPlayerModel.getRankPlayerModelInstance().updateRankPlayerInfo(opponentRankPlayer, MatchResult.Draw, matchMode);

                            // update info of player into leaderboard
                            LeaderboardModel.getLeaderboardModelInstance().updateLeaderboard(opponentRankPlayer);
                        }
                    }

                } else {
                    statCode = StatusCode.ERROR;
                    errMsg = "Cannot process draw confirmation: request player doesn't exist in match " + matchID;
                }
            } else {
                if (match.denyDrawRequest(confirmUsername)) {
                    statCode = StatusCode.SUCCESS;
                } else {
                    statCode = StatusCode.ERROR;
                    errMsg = "Cannot process draw declination: request player doesn't exist in match " + matchID;
                }
            }
        }
        DrawConfirmServerMessage serverMsg = new DrawConfirmServerMessage(confirmMsg.getMessageCommandID(), matchID, confirmUsername,
                confirmSessionID, acceptance, statCode, errMsg);
        // reset? is it ok to put it here?
        
        return serverMsg.toString();
    }
    
    private String processListenDraw(String input) throws Exception {
        String serverMsg = "";

        ListenDrawClientMessage listenMsg = new ListenDrawClientMessage(input);
        String listenUsername = listenMsg.getUsername();
        String listenSessionID = listenMsg.getSessionID();
        int matchID = listenMsg.getMatchID();
        boolean acceptance = false;

        Match match = queueController.getMatchById(matchID);

        while (true) {
            try {
                Thread.sleep(500);

                if (isStop) {
                    return "";
                }

                if (match.isIncomingDrawRequest(listenUsername)) {
                	match.pendingDrawRequest(listenUsername);
                    String requestPlayerName = match.getAnotherPlayer(listenUsername) != null ? match.getAnotherPlayer(listenUsername).getUsername() : "";
                    DrawRequestServerMessage drawRequestServerMessage = new DrawRequestServerMessage(listenMsg.getMessageCommandID(),
                            matchID, requestPlayerName, listenSessionID, StatusCode.SUCCESS, "");
                    serverMsg = drawRequestServerMessage.toString();
                    break;
                }

                String responsePlayerName = match.getAnotherPlayer(listenUsername) != null ? match.getAnotherPlayer(listenUsername).getUsername() : "";
                if (match.isDrawDeny(responsePlayerName)) {
                    acceptance = false;
                    match.resetDrawRequest();
                    break;
                }

                if (match.isDrawSucceed(responsePlayerName)) {
                    acceptance = true;
                    match.resetDrawRequest();
                    break;
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        // if null -> server needs to send back draw confirm message from opponent
        if (serverMsg == null || serverMsg.isEmpty()) {
            String confirmPlayerName = match.getAnotherPlayer(listenUsername) != null ? match.getAnotherPlayer(listenUsername).getUsername() : "";
            DrawConfirmServerMessage fwdMsg = new DrawConfirmServerMessage(listenMsg.getMessageCommandID(), matchID,
                    confirmPlayerName, listenSessionID, acceptance, StatusCode.SUCCESS, "");
            serverMsg = fwdMsg.toString();
        }
        
        System.out.println("Server message for returning draw request: " + serverMsg);

        return serverMsg;
    }

    private String processGetLeaderBoardRequest(String input) throws Exception {
        // parse client message to get requesting user
        LeaderboardClientMessage clientRequest = new LeaderboardClientMessage(input);
        String username = clientRequest.getUsername();
        String sessionID = clientRequest.getSessionID();

        // get leaderboard user
        // TODO: add number of records into request field
        List<LeaderboardPlayer> leaderboardPlayerList = LeaderboardModel.getLeaderboardModelInstance().getLeaderBoardData(10);

        // get current user rank
        LeaderboardPlayer clientPlayer = LeaderboardModel.getLeaderboardModelInstance().getLeaderboardPlayerByUsername(username, leaderboardPlayerList);

        List<String> listUsr = new ArrayList<String>();
        List<Integer> listElo = new ArrayList<Integer>();
        List<Integer> listRank = new ArrayList<Integer>();
        List<Integer> listMatchPlayed = new ArrayList<Integer>();
        List<Integer> listMatchWon = new ArrayList<Integer>();

        // add requesting player to the first field of response
        // if clientPlayer is null
        if (clientPlayer == null) {
            listUsr.add("");
            listElo.add(-1);
            listRank.add(-1);
            listMatchPlayed.add(-1);
            listMatchWon.add(-1);
        } else {
            listUsr.add(clientPlayer.getUsername());
            listElo.add(clientPlayer.getElo());
            listRank.add(clientPlayer.getRank());
            listMatchPlayed.add(clientPlayer.getNoPlayedMatch());
            listMatchWon.add(clientPlayer.getNoWonMatch());
        }

        // add each player from leaderboard to response
        for (LeaderboardPlayer leaderboardPlayer: leaderboardPlayerList) {
            listUsr.add(leaderboardPlayer.getUsername());
            listElo.add(leaderboardPlayer.getElo());
            listRank.add(leaderboardPlayer.getRank());
            listMatchPlayed.add(leaderboardPlayer.getNoPlayedMatch());
            listMatchWon.add(leaderboardPlayer.getNoWonMatch());
        }

        LeaderboardServerMessage leaderboardResponse = new LeaderboardServerMessage(clientRequest.getMessageCommandID(), listUsr, listElo, listRank, listMatchPlayed, listMatchWon, StatusCode.SUCCESS, "");
        return leaderboardResponse.toString();
    }

    private String processChatRequest(String input) throws Exception {
        String errMsg = "";
        StatusCode statCode;

        ChatClientMessage clientRequest = new ChatClientMessage(input);
        int matchID = clientRequest.getMatchID();

        // find user with match id. Should add a field of match ID for player?
        // Nah, maybe query in database
        // only do a prototype here
        Match match = queueController.getMatchById(matchID);
        if (match == null) {
            errMsg = "Message is not from the valid match";
            statCode = StatusCode.ERROR;
        } else {
            if (match.addNewMsgRecord(matchID, clientRequest.getSendUser(), clientRequest.getReceiveUser(), clientRequest.getChatMessageID(), clientRequest.getChatMessage())) {
                statCode = StatusCode.SUCCESS;
            } else {
                errMsg = "Cannot send message";
                statCode = StatusCode.ERROR;
            }

        }
        ChatServerMessage serverResponse = new ChatServerMessage(clientRequest, statCode, errMsg);
        return serverResponse.toString();
    }

    private String processListenChat(String input) {
        // strategy: polling until there is a new move

        String errMsg = "";
        StatusCode statCode;
        ChatMessage chatMsg = null;

        ListenChatClientMessage listenMsg = new ListenChatClientMessage(input);

        String username = listenMsg.getUsername();
        int matchID = listenMsg.getMatchID();
        Match match = queueController.getMatchById(matchID);

        if (match == null) {
            errMsg = "Message is not from the valid match";
            statCode = StatusCode.ERROR;
        } else {
            while (true) {
                try {
                    Thread.sleep(500);

                    if (isStop) {
                        return "";
                    }

                    chatMsg = match.getUnreadMsg(username);
                    if (chatMsg == null) {
                        // if no message, just continue
                        continue;
                    } else {
                        statCode = StatusCode.SUCCESS;
                        break;
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    errMsg = e.toString();
                    statCode = StatusCode.ERROR;
                }
            }
        }
        if (chatMsg == null) {
            chatMsg = new ChatMessage(-1, "", "", "INVALIDMESSAGE", "null message");
        }
        ChatServerMessage fwdMsg = new ChatServerMessage(listenMsg.getMessageCommandID(), chatMsg, statCode, errMsg);
        return fwdMsg.toString();
    }

    private String processLogoutRequest(String input) throws Exception {
        LogoutServerMessage serverResponse;
        LogoutClientMessage clientRequest = new LogoutClientMessage(input);
        String username = clientRequest.getUsername();
        String sessionID = clientRequest.getSessionID();
        if (T3Authenticator.getT3AuthenticatorInstance().logout(username, sessionID)) {
            serverResponse = new LogoutServerMessage(clientRequest, StatusCode.SUCCESS, "");
        } else {
            serverResponse = new LogoutServerMessage(clientRequest.getMessageCommandID(), "", "", StatusCode.ERROR, "cannot logout");
        }
        return serverResponse.toString();
    }

    private String notifyUnknownCommand() throws Exception {
        // TODO: Finish the function here
        String serverResponse = "";
        return serverResponse;
    }
    
    private String processQuitGameRequest(String input) throws Exception {
    	QuitGameClientMessage request = new QuitGameClientMessage(input);
        String errMsg = "";
        StatusCode statCode;

    	// parse info from request
        String requestUsername = request.getUsername();
        String requestSessionID = request.getSessionID();
        int matchID = request.getMatchID();
        Match match = queueController.getMatchById(matchID);
        if (match == null) {
            errMsg = "Message is not from the valid match";
            statCode = StatusCode.ERROR;
        } else {
            // update info of match
            Player opponent = match.getAnotherPlayer(requestUsername);
            if (opponent != null) {
                if (!queueController.endGame(opponent.getUsername(), matchID)) {
                    statCode = StatusCode.ERROR;
                    errMsg = "Cannot end the game...";
                } else {
                    MatchMode matchMode;
                    if (match.isRanked()) {
                        matchMode = MatchMode.Ranked;
                    } else {
                        matchMode = MatchMode.Normal;
                    }
                    // update info only for ranked player
                    if (opponent instanceof RankPlayer) {
                        // who sending this quit request is the loser
                        RankPlayer wonPlayer =  (RankPlayer) opponent;

                        // update player info into both obj and db
                        RankPlayerModel.getRankPlayerModelInstance().updateRankPlayerInfo(wonPlayer, MatchResult.Win, matchMode);

                        // update info of player into leaderboard
                        LeaderboardModel.getLeaderboardModelInstance().updateLeaderboard(wonPlayer);
                    }

                    Player quitPlayer = match.getPlayerByName(requestUsername);
                    if (quitPlayer instanceof RankPlayer) {
                        // who sending this quit request is the loser
                        RankPlayer lostPlayer = (RankPlayer) quitPlayer;

                        // update player info into both obj and db
                        RankPlayerModel.getRankPlayerModelInstance().updateRankPlayerInfo(lostPlayer, MatchResult.Lost, matchMode);

                        // update info of player into leaderboard
                        LeaderboardModel.getLeaderboardModelInstance().updateLeaderboard(lostPlayer);
                    }
                    statCode = StatusCode.SUCCESS;
                    errMsg = "";
                }
            } else {
                statCode = StatusCode.ERROR;
                errMsg = "Cannot find opponent!!!";
            }
        }
    	QuitGameServerMessage response = new QuitGameServerMessage(request, statCode, errMsg);
    	return response.toString();
    }
}
