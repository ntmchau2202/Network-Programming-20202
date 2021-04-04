#include <iostream>

#include "server.hpp"

using namespace std;

int main(int argc, char *argv[]){
    Server *server_instance = Server::StartSession();
    while(true){
        // clear socket read fd set
        FD_ZERO(&(server_instance->readfds));

        // add master socket to read fd set
        FD_SET(server_instance->server_socket, &(server_instance->readfds));

        // add child sockets to read fd set
        for (int i = 0; i < MAXIMUM_CLIENT; i++){
            SOCKET child_socket = server_instance->list_client[i];
            if (child_socket > 0) {
                FD_SET(server_instance->server_socket, &(server_instance->readfds));
            }

            if  (child_socket > server_instance->server_socket) {
                server_instance->server_socket = child_socket;
            }
        }

        // wait for an activity on any of the socket, timeout = NULL ~> wait infinitely
        int activity = select(server_instance->server_socket + 1, &(server_instance->readfds), NULL, NULL, NULL);
        if (activity == SOCKET_ERROR) {
            // error, print error and exit
            cout << "select() error.\nError details: " + GetLastError() << endl;
            exit(-1);
        }

        // if something happens on the master socket, then there is a new incoming connection
        if (FD_ISSET(server_instance->server_socket, &(server_instance->readfds))){
            // accept the new client
        }
    }
}