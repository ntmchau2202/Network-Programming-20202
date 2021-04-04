#include "socket.hpp"

#include <string>
#include <iostream>

using namespace std;

int Socket::ReceiveMsg(char *recv_buff, SOCKET *client_socket){
    return recv(*client_socket, recv_buff, MAX_LEN_BUFFER, 0);
}
int Socket::SendMsg(string msg, SOCKET *target_socket){
    char *cmsg = strcpy(new char[msg.length()+1], msg.c_str());
    cout << "Prepare msg: " << string(cmsg) << endl;
    cout << "Socket to send: " << *target_socket << endl;
    int send_result = send(*target_socket, cmsg, msg.length()+1, 0);
    return send_result;
}