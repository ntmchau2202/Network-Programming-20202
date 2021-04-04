#include "server.hpp"

Server::Server(){
    this->server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (this->server_socket == INVALID_SOCKET){
        throw ERROR_SERVER_CREATION_FAILED;
    }

    int opt = 1; // timeout value in sec
    if(setsockopt(this->server_socket, SOL_SOCKET, SO_REUSEADDR, (char*)&opt, sizeof(opt))){
        throw ERROR_SERVER_CREATION_FAILED;
    }

    this->server.sin_family = AF_INET;
    this->server.sin_addr.s_addr = htonl(INADDR_ANY);
    this->server.sin_port = htons(DEFAULT_PORT);

    // initialize list of socket
    for (int i = 0; i < MAXIMUM_CLIENT; i++){
        this->list_client[i] = 0;
    }
} 

void Server::Bind(){
    if(bind(this->server_socket, (struct sockaddr*)&this->server, sizeof(this->server)) < 0){
        throw ERROR_SERVER_BINDING_FAILED;
    }  
}

void Server::Listen(){
    if(listen(this->server_socket, MAXIMUM_CLIENT) < 0){
        throw ERROR_SERVER_LISTENING_FAILED;
    }
}

SOCKET Server::Accept(){
    int addr_len = sizeof(this->server);
    SOCKET return_socket = accept(this->server_socket, (struct sockaddr*)&this->server, (socklen_t*)&addr_len);
    if(return_socket < 0 ){
        throw ERROR_SERVER_ACCEPT_FAILED;
    }
    return return_socket;
}

Server* Server::StartSession(){
    // initialize environment
    WORD wVersionRequested;
    WSADATA wsaData;
    int err;

    /* Use the MAKEWORD(lowbyte, highbyte) macro declared in Windef.h */
    wVersionRequested = MAKEWORD(2, 2);

    err = WSAStartup(wVersionRequested, &wsaData);
    if (err != 0) {
        throw ERROR_SOCKET_ENV_CREATION_FAILED;
    }

    Server *current_server = NULL;

    try {
        current_server = new Server();
    } catch (...){
        throw ERROR_SERVER_CREATION_FAILED;
    }

    try {
        current_server->Bind();
    } catch (...){
        throw ERROR_SERVER_BINDING_FAILED;
    }

    return current_server;
}