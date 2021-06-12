package message.login;

import protocol.StatusCode;

public class LoginTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LoginClientMessage loginRequest = new LoginClientMessage("hikaru", "123456");
		String tmp = loginRequest.toString();
		System.out.println("Raw login request: " + tmp);
		System.out.println("username: " + loginRequest.getUsername());
		System.out.println("password: " + loginRequest.getPassword());
		System.out.println("command: " + loginRequest.getCommand().toString());
		
		LoginClientMessage loginRequestFromString = new LoginClientMessage(tmp);
		System.out.println("New request with same content: " + loginRequestFromString.toString());
		System.out.println("username: " + loginRequestFromString.getUsername());
		System.out.println("password: " + loginRequestFromString.getPassword());
		System.out.println("command: " + loginRequestFromString.getCommand().toString());
		
		LoginServerMessage loginResponse = new LoginServerMessage(1324, "hikaru", "ASKLRJGBARSKLHJBGA", 1500, 5000, (float)0.69, 1000, 69, StatusCode.SUCCESS, "");
		tmp = loginResponse.toString();
		System.out.println("Raw login response: " + tmp);
		System.out.println("username: " + loginResponse.getUsername());
		System.out.println("session: " + loginResponse.getSessionID());
		System.out.println("elo: " + loginResponse.getELO());
		System.out.println("rank: " + loginResponse.getRank());
		System.out.println("win rate: " + loginResponse.getWinrate());
		System.out.println("no game played: " + loginResponse.getNumberOfMatchPlayed());
		System.out.println("no game won: " + loginResponse.getNumberOfMatchWon());
		System.out.println("status code: " + loginResponse.getStatusCode().toString());
		System.out.println("error message: " + loginResponse.getErrorMessage());
		
		LoginServerMessage loginResponseFromString = new LoginServerMessage(tmp);
		System.out.println("New response with same content: " + loginResponseFromString.toString());
		System.out.println("username: " + loginResponseFromString.getUsername());
		System.out.println("session: " + loginResponseFromString.getSessionID());
		System.out.println("elo: " + loginResponseFromString.getELO());
		System.out.println("rank: " + loginResponseFromString.getRank());
		System.out.println("win rate: " + loginResponseFromString.getWinrate());
		System.out.println("no game played: " + loginResponseFromString.getNumberOfMatchPlayed());
		System.out.println("no game won: " + loginResponseFromString.getNumberOfMatchWon());
		System.out.println("status code: " + loginResponseFromString.getStatusCode().toString());
		System.out.println("error message: " + loginResponseFromString.getErrorMessage());
	}

}
