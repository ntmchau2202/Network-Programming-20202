package server.entity.network.request;

import entity.Match.Match;
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
import message.quitqueue.QuitQueueClientMessage;
import message.quitqueue.QuitQueueServerMessage;
import message.register.RegisterClientMessage;
import message.register.RegisterServerMessage;
import org.json.JSONObject;
import protocol.Command;
import protocol.StatusCode;
import server.core.authentication.T3Authenticator;
import server.core.controller.CompletionHandlerController;
import server.core.controller.QueueController;
import server.entity.chat.ChatMessage;
import server.entity.network.completionHandler.ReadCompletionHandler;

public class RequestProcessor {

    private final QueueController queueController;
    private boolean isCancel;
    private Command cmd;
    private CompletionHandlerController handlerController;

    public RequestProcessor(QueueController queueController, CompletionHandlerController handlerController) {
        this.queueController = queueController;
        this.handlerController = handlerController;
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
            case LISTEN_MOVE: {
                resMsg = this.processListenMove(recvMsg);
                break;
            }
            case LISTEN_CHAT: {
                resMsg = this.processListenChat(recvMsg);
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
            case CHAT: {
                resMsg = this.processChatRequest(recvMsg);
                break;
            }
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
            serverResponse = new LoginServerMessage(clientRequest.getMessageCommandID(), "", "", 0, 0, 0, 0, 0, StatusCode.ERROR,
                    "Username / Password is not valid");
        } else {
//            loggedPlayer.setUserSocket(sock);
            queueController.pushToHall(loggedPlayer);
            serverResponse = new LoginServerMessage(clientRequest.getMessageCommandID(), username, loggedPlayer.getSessionId(), loggedPlayer.getElo(),
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
            serverResponse = new RegisterServerMessage(clientRequest.getMessageCommandID(), "", "", 0, 0, 0, 0, 0, StatusCode.ERROR,
                    "Username / Password is not valid");
        } else {
//            loggedPlayer.setUserSocket(sock);
            queueController.pushToHall(loggedPlayer);
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
        System.out.println("Normal request!");
        RankPlayer loggedPlayer = null;
        for (RankPlayer player : queueController.getHall()) {
            if (sessionID.compareTo(player.getSessionId()) == 0) {
                loggedPlayer = player;
                break;
            }
        }

        if (loggedPlayer != null) {
            if (mode.compareToIgnoreCase("normal") == 0 || mode.compareToIgnoreCase("ranked") == 0) {
                System.out.println("requested username: " + loggedPlayer.getUsername());
                queueController.viewHall();
                queueController.viewNormalQueue();
                // remove player from hall
                queueController.removeFromHall(loggedPlayer);
                System.out.println("remove player from hall successfully");
                // add player to normal / ranked queue
                queueController.pushToQueue(loggedPlayer, mode);
                System.out.println("push player to queue successfully");

                queueController.viewHall();
                queueController.viewNormalQueue();

                // prepare message according to each player
                Match match = null;

                for (int i = 0; i < 100; i++) {
                    System.out.println("- in the loop: " + i);
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

                Player opponent = null;
                if (match != null) {
                    if (loggedPlayer.getUsername().equalsIgnoreCase(match.getPlayer1().getUsername())) {
                        // user is player 1
                        opponent = match.getPlayer2();
                    } else {
                        // user is player 2
                        opponent = match.getPlayer1();
                    }
                    serverResponse = new JoinQueueServerMessage(clientRequest.getMessageCommandID(), loggedPlayer.getUsername(), loggedPlayer.getSessionId(), opponent.getUsername(), 1234 /*mimic elo here*/, match.getMatchID(), match.getPlayer1().getUsername(), StatusCode.SUCCESS, "");
                } else {
                    if (isCancel) {
                        serverResponse = new JoinQueueServerMessage(clientRequest.getMessageCommandID(), "", "", "", 0, -1, "", StatusCode.ERROR, "QUIT_QUEUE sent from user");
                    } else {
                        serverResponse = new JoinQueueServerMessage(clientRequest.getMessageCommandID(), "", "", "", 0, -1, "", StatusCode.ERROR, "Cannot find appropriate match. Please try again later");
                    }
                    // when join queue meets error or cancelation, push player back to hall
                    queueController.pushToHall(loggedPlayer);
                    // and remove player from normal/ranked queue
                    queueController.removeFromQueue(loggedPlayer.getUsername());
                }
//                listResponse.put(sock, serverResponse.toString());
            } else {
                // error
                serverResponse = new JoinQueueServerMessage(clientRequest.getMessageCommandID(), "", "", "", 0, -1, "", StatusCode.ERROR, "Invalid match request. Please try again");
            }
        } else {
            // if it is not ranked user, call to create a new guest player account

        }

        queueController.viewHall();
        queueController.viewNormalQueue();
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
        Match match = queueController.getMatchById(matchID);

        // should have a check here
        // but first, let's try to successfully send data to both clients

        String errMsg = "";
        StatusCode statCode = null;

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

        MoveServerMessage fwdMsg = new MoveServerMessage(moveMsg.getMessageCommandID(), matchID, movePlayer, x, y, state, result, statCode, errMsg);

//        listResponse.put(sock, fwdMsg.toString());

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

        String username = listenMsg.getUsername();
        int matchID = listenMsg.getMatchID();
        Match match = queueController.getMatchById(matchID);

        Move latestMove;
        String movePlayer = "";
        while (true) {
            try {
                Thread.sleep(500);
                if (match.getNumberOfMoves() > 0) {
                    if (username.equalsIgnoreCase(match.getPlayer1().getUsername())) {

                        if ((match.getNumberOfMoves() % 2) == 0) {
                            latestMove = match.getLatestMove();
                            movePlayer = match.getPlayer2().getUsername();
                            break;
                        }
                    } else {
                        if ((match.getNumberOfMoves() % 2 == 1)) {
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
        MoveServerMessage fwdMsg = new MoveServerMessage(listenMsg.getMessageCommandID(), matchID, movePlayer, latestMove.getX(), latestMove.getY(), latestMove.getState(), latestMove.getResult(), StatusCode.SUCCESS, "");
//        listResponse.put(sock, fwdMsg.toString());
        return fwdMsg.toString();
    }

    private String processRequestDrawRequest() throws Exception {
        // TODO: Finish the function here
        String serverResponse = "";
        return serverResponse;
    }

    private String processConfirmDrawRequest() throws Exception {
        // TODO: Finish the function here
        String serverResponse = "";
        return serverResponse;
    }

    private String processGetLeaderBoardRequest() throws Exception {
        // TODO: Finish the function here
        String serverResponse = "";
        return serverResponse;
    }

    private String processChatRequest(String input) throws Exception {
        String errMsg = "";
        StatusCode statCode = null;

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
        StatusCode statCode = null;
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
                    chatMsg = match.getUnreadMsg();
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

    private String processChatACKRequest() throws Exception {
        // TODO: Finish the function here
        String serverResponse = "";
        return serverResponse;
    }

    private void processLogoutRequest() throws Exception {

    }

    private String notifyMatchFound() throws Exception {
        // TODO: Finish the function here
        String serverResponse = "";
        return serverResponse;
    }

    private String notifyEndgame() throws Exception {
        // TODO: Finish the function here
        String serverResponse = "";
        return serverResponse;
    }

    private String notifyUnknownCommand() throws Exception {
        // TODO: Finish the function here
        String serverResponse = "";
        return serverResponse;
    }
}
