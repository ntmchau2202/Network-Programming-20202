package server.entity.network.request;

import entity.Match.Match;
import entity.Move.Move;
import entity.Player.Player;
import entity.Player.RankPlayer;
import message.joinqueue.JoinQueueClientMessage;
import message.joinqueue.JoinQueueServerMessage;
import message.login.LoginClientMessage;
import message.login.LoginServerMessage;
import message.move.ListenMoveClientMessage;
import message.move.MoveClientMessage;
import message.move.MoveServerMessage;
import message.quitqueue.QuitQueueClientMessage;
import message.quitqueue.QuitQueueServerMessage;
import message.register.RegisterClientMessage;
import message.register.RegisterServerMessage;
import org.json.JSONObject;
import protocol.Command;
import protocol.StatusCode;
import server.core.authentication.T3Authenticator;
import server.core.controller.QueueController;
import server.entity.network.completionHandler.ReadCompletionHandler;

public class RequestProcessor {

    private final QueueController queueController;
    private boolean isCancel;
    private Command cmd;

    public RequestProcessor(QueueController queueController) {
        this.queueController = queueController;
        this.isCancel = false;
    }

    public void stopProcessingRequest() {
        this.isCancel = true;
    }

    public void storeCurrentCommand(String recvMsg) {
        JSONObject clientMsg = new JSONObject(recvMsg);
        this.cmd = Command.toCommand(clientMsg.getString("command_code"));
        // TODO: check cmd if it's null
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
            case LISTEN_MOVE:{
                resMsg = this.processListenMove(recvMsg);
                break;
            }
//            case DRAW_REQUEST: {
//                resMsg = this.processRequestDrawRequest();
//                break;
//            }
//            case DRAW_CONFIRM: {
//                resMsg = this.processConfirmDrawRequest();
//                break;
//            }
//            case ENDGAME: {
//                resMsg = this.notifyEndgame();
//                break;
//            }
//            case LEADERBOARD: {
//                resMsg = this.processGetLeaderBoardRequest();
//                break;
//            }
//            case CHAT: {
//                resMsg = this.processChatRequest();
//                break;
//            }
//            case CHATACK: {
//                resMsg = this.processChatACKRequest();
//                break;
//            }
//            case LOGOUT: {
//                this.processLogoutRequest();
//                break;
//            }
//            default: {
//                resMsg = this.notifyUnknownCommand();
//                break;
//            }
        }

        return resMsg;
    }

    private String processLoginRequest(String input)
            throws Exception {
        LoginServerMessage serverResponse = null;

        LoginClientMessage clientRequest = new LoginClientMessage(input);
        String username = clientRequest.getUsername();
        String password = clientRequest.getPassword();
        // get logged player
        RankPlayer loggedPlayer = new T3Authenticator().login(username, password);
        if (loggedPlayer == null) {
            serverResponse = new LoginServerMessage("", "", 0, 0, 0, 0, 0, StatusCode.ERROR,
                    "Username / Password is not valid");
        } else {
//            loggedPlayer.setUserSocket(sock);
            queueController.pushToHall(loggedPlayer);
            serverResponse = new LoginServerMessage(username, loggedPlayer.getSessionId(), loggedPlayer.getElo(),
                    loggedPlayer.getRank(), loggedPlayer.getWinningRate(), loggedPlayer.getNoPlayedMatch(),
                    loggedPlayer.getNoWonMatch(), StatusCode.SUCCESS, "");
        }

        return serverResponse.toString();
    }

    private String processRegisterRequest(String input)
            throws Exception {
        RegisterServerMessage serverResponse = null;

        RegisterClientMessage clientRequest = new RegisterClientMessage(input);
        String username = clientRequest.getUsername();

        String password = clientRequest.getPassword();
        // register new player
        RankPlayer loggedPlayer = new T3Authenticator().register(username, password);
        if (loggedPlayer == null) {
            serverResponse = new RegisterServerMessage("", "", 0, 0, 0, 0, 0, StatusCode.ERROR,
                    "Username / Password is not valid");
        } else {
//            loggedPlayer.setUserSocket(sock);
            queueController.pushToHall(loggedPlayer);
            serverResponse = new RegisterServerMessage(username, loggedPlayer.getSessionId(), loggedPlayer.getElo(),
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

        if (mode.compareToIgnoreCase("normal") == 0) {
            // check if this is ranked user
            System.out.println("Normal request!");
            RankPlayer loggedPlayer = null;
            for (RankPlayer player : queueController.getHall()) {
                if (sessionID.compareTo(player.getSessionId()) == 0) {
                    loggedPlayer = player;
                    break;
                }
            }

            if (loggedPlayer != null) {
                queueController.removeFromHall(loggedPlayer);
                queueController.pushToQueue(loggedPlayer, mode);

                // prepare message according to each player
                Match match = null;
                boolean isFound = false;

                for(int i = 0; i < 100; i++) {
                    if(!isCancel) {
                        for(Match m : queueController.getIngameList()) {
                            if(m.isPlayerOfMatch(loggedPlayer)) {
                                match = m;
                                isFound = true;
                                break;
                            }
                        }

                        if(isFound) {
                            break;
                        } else {
                            Thread.sleep(500);
                        }
                    } else {
                        break;
                    }
                }

                Player opponent = null;
                if(isFound) {
                    if(loggedPlayer.getUsername().equalsIgnoreCase(match.getPlayer1().getUsername())){
                        // user is player 1
                        opponent = match.getPlayer2();
                    } else {
                        // user is player 2
                        opponent = match.getPlayer1();
                    }
                    serverResponse = new JoinQueueServerMessage(loggedPlayer.getUsername(), loggedPlayer.getSessionId(), opponent.getUsername(), 1234 /*mimic elo here*/, match.getMatchID(), match.getPlayer1().getUsername(), StatusCode.SUCCESS, "");
                } else if (!isFound){
                    serverResponse = new JoinQueueServerMessage("", "", "", 0, -1, "", StatusCode.ERROR, "Cannot find appropiate match. Please try again later");
                } else if (isCancel) {
                    serverResponse = new JoinQueueServerMessage("", "", "", 0, -1, "", StatusCode.ERROR, "QUIT_QUEUE sent from user");
                }

//                listResponse.put(sock, serverResponse.toString());
            } else {
                // if it is not ranked user, call to create a new guest player account

            }

        } else if (mode.compareToIgnoreCase("ranked") == 0) {

        } else {
            // error
        }
        return serverResponse.toString();
    }

    private String processMoveRequest(String input)
            throws Exception {
        // TODO: Finish the function here
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
        Match match = null;
        for (Match m : queueController.getIngameList()) {
            if (m.getMatchID() == matchID) {
                match = m;
                break;
            }
        }

        // should have a check here
        // but first, let's try to successfully send data to both clients

        String errMsg = "";
        StatusCode statCode = null;

        if(movePlayer.equalsIgnoreCase(match.getPlayer1().getUsername())) {
            if((match.getNumberOfMoves() % 2) == 0) {
                match.addNewMoveRecord(x, y, movePlayer, state, result);
                statCode = StatusCode.SUCCESS;
            } else {
                errMsg = "Invalid turn";
                statCode = StatusCode.ERROR;
            }
        } else {
            if((match.getNumberOfMoves() % 2) == 1) {
                match.addNewMoveRecord(x, y, movePlayer, state, result);
                statCode = StatusCode.SUCCESS;
            } else {
                errMsg = "Invalid turn";
                statCode = StatusCode.ERROR;
            }
        }

        MoveServerMessage fwdMsg = new MoveServerMessage(matchID, movePlayer, x, y, state, result, statCode, errMsg);

//        listResponse.put(sock, fwdMsg.toString());

        return fwdMsg.toString();
    }

    private String processQuitQueue(String input){
        QuitQueueClientMessage quitQueueMsg = new QuitQueueClientMessage(input);

        boolean isOK = queueController.removeFromQueue(quitQueueMsg.getUsername());
        StatusCode statCode;
        String errMsg = "";

        if(isOK) {
            statCode = StatusCode.SUCCESS;
        } else {
            statCode = StatusCode.ERROR;
            errMsg = "An error occured when quiting queue";
        }

        QuitQueueServerMessage response = new QuitQueueServerMessage(quitQueueMsg.getUsername(), statCode, errMsg);

        // and send this msg
//        listResponse.put(sock, response.toString());

        return response.toString();
    }

    private String processListenMove(String input){
        // strategy: polling until there is a new move

        ListenMoveClientMessage listenMsg = new ListenMoveClientMessage(input);

        String username = listenMsg.getUsername();
        int matchID = listenMsg.getMatchID();
        Match match = null;

//		for(Match m : queueController.getIngameList()) {
//			if (m.getMatchID() == matchID) {
//				match = m;
//				break;
//			}
//		}

        Move latestMove;
        String movePlayer = "";
        while(true) {
            try {
                Thread.sleep(500);
                for(Match m : queueController.getIngameList()) {
                    if (m.getMatchID() == matchID) {
                        match = m;
                        break;
                    }
                }

                if(match.getNumberOfMoves() > 0) {
                    if(username.equalsIgnoreCase(match.getPlayer1().getUsername())) {

                        if((match.getNumberOfMoves() % 2) == 0) {
                            latestMove = match.getLatestMove();
                            movePlayer = match.getPlayer2().getUsername();
                            break;
                        }
                    } else {
                        if((match.getNumberOfMoves() % 2 == 1)) {
                            latestMove = match.getLatestMove();
                            movePlayer = match.getPlayer1().getUsername();
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        MoveServerMessage fwdMsg = new MoveServerMessage(matchID, movePlayer, latestMove.getX(), latestMove.getY(), latestMove.getState(), latestMove.getResult(), StatusCode.SUCCESS, "");
//        listResponse.put(sock, fwdMsg.toString());
        return fwdMsg.toString();
    }

//    private Map<AsynchronousSocketChannel, String> processRequestDrawRequest() throws Exception {
//        // TODO: Finish the function here
//        Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
//        return listResponse;
//    }
//
//    private Map<AsynchronousSocketChannel, String> processConfirmDrawRequest() throws Exception {
//        // TODO: Finish the function here
//        Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
//        return listResponse;
//    }
//
//    private Map<AsynchronousSocketChannel, String> processGetLeaderBoardRequest() throws Exception {
//        // TODO: Finish the function here
//        Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
//        return listResponse;
//    }
//
//    private Map<AsynchronousSocketChannel, String> processChatRequest() throws Exception {
//        // TODO: Finish the function here
//        Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
//        return listResponse;
//    }
//
//    private Map<AsynchronousSocketChannel, String> processChatACKRequest() throws Exception {
//        // TODO: Finish the function here
//        Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
//        return listResponse;
//    }
//
//    private void processLogoutRequest() throws Exception {
//
//    }
//
//    private Map<AsynchronousSocketChannel, String> notifyMatchFound() throws Exception {
//        // TODO: Finish the function here
//        Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
//
//
//        return listResponse;
//    }
//
//    private Map<AsynchronousSocketChannel, String> notifyEndgame() throws Exception {
//        // TODO: Finish the function here
//        Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
//        return listResponse;
//    }
//
//    private Map<AsynchronousSocketChannel, String> notifyUnknownCommand() throws Exception {
//        // TODO: Finish the function here
//        Map<AsynchronousSocketChannel, String> listResponse = new HashMap<AsynchronousSocketChannel, String>();
//        return listResponse;
//    }
}
