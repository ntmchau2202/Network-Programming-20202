package test;

import protocol.ClientMessage;
import protocol.ServerMessage;
import protocol.StatusCode;

public class TestProtocol {

	public static void main(String[] args) {
		ClientMessage loginRequest = new ClientMessage();
		loginRequest.createLoginRequest("hikaru", "abcde");
		System.out.println(loginRequest.toString());
		
		ServerMessage loginResponse = new ServerMessage();
		loginResponse.createLoginResponse("hikaru", "AOUJBSGOAW01p39u5P", 15000, StatusCode.SUCCESS, "");
		System.out.println(loginResponse.toString());

	}

}
