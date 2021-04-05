#include <iostream>

#include "server.hpp"
#include "..\socket\socket.hpp"

using namespace std;

int main(int argc, char *argv[]){
    Server *server_instance = Server::StartSession();

    try {
        cout << "Start listening to connection...\n";
        server_instance->Listen();
    } catch (int err) {
        cout << "Cannot listen to connection.\nServer error: " << err << "\nError details: " + GetLastError();
        exit(-1);
    }

    while(true){
        // clear socket read fd set
        FD_ZERO(&(server_instance->readfds));
        cout << "Done zeroing\n";
        // add master socket to read fd set
        FD_SET(server_instance->server_socket, &(server_instance->readfds));

        // add child sockets to read fd set
        for (int i = 0; i < MAXIMUM_CLIENT; i++){
            SOCKET child_socket = server_instance->list_client[i];
            if (child_socket > 0) {
                FD_SET(child_socket, &(server_instance->readfds));
            }
        }
        cout << "Adding socket done\nserver_socket: " << server_instance->server_socket << endl;
        // wait for an activity on any of the socket, timeout = NULL ~> wait infinitely
        int activity = select(server_instance->server_socket + 1, &(server_instance->readfds), NULL, NULL, NULL);
        if (activity == SOCKET_ERROR) {
            // error, print error and exit
            cout << "select() error.\nError details: " + GetLastError() << endl;
            exit(-1);
        }
        cout << "Select OK\n";
        // if something happens on the master socket, then there is a new incoming connection
        if (FD_ISSET(server_instance->server_socket, &(server_instance->readfds))){
            try {
                SOCKET new_client = server_instance->Accept();
                cout << "New client: " << new_client << endl;
                string msg = "Welcome to the game";
                server_instance->SendMsg(msg, &new_client);
            } catch (int error) {
                cout << "Error occurs.\nServer error code: " << error << "\nError details: " << GetLastError() << endl;
                exit(-1);
            }
        } else {
            // else, this is an io op from other sockets
            for (int i = 0; i < MAXIMUM_CLIENT; i++){
                SOCKET cur_socket = server_instance->list_client[i];
                if (FD_ISSET(cur_socket, &(server_instance->readfds))){
                    // get information about client
                    sockaddr_in client_address;
                    int addr_len = sizeof(sockaddr_in);
                    getpeername(cur_socket, (struct sockaddr*)&client_address, (int*)&addr_len);

                    char *buff = (char*)calloc(MAX_LEN_BUFFER, sizeof(char));
                    int bytes_recv = server_instance->ReceiveMsg(buff, &cur_socket);
                    if (bytes_recv == SOCKET_ERROR) {
                        int err_code = WSAGetLastError();
                        if (err_code == WSAECONNRESET){
                            cout << "Connection closed unexpectedly from the client\n";
                            closesocket(cur_socket);
                            server_instance->list_client[i] = 0;
                        } else {
                            cout << "Receiving messaged failed.\nError code: " << err_code << endl;
                        }
                    } else if (bytes_recv == 0) {
                        cout << "Client leaved the server...";
                        closesocket(cur_socket);
                        server_instance->list_client[i] = 0;
                    } else {
                        cout << "Bytes received: " << bytes_recv << "; Messages from client: " << string(buff) << endl;
                        server_instance->SendMsg(string(buff), &cur_socket);
                    }
                }
            }
        }
    }
}