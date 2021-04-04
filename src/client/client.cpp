#include <iostream>

#include "client.hpp"

using namespace std;

Client::Client(){
    this->client_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (this->client_socket < 0) {
        throw ERROR_CLIENT_CREATION_FAILED;
    }
}

void Client::Connect(char *host_ip, int port){
    this->client.sin_family = AF_INET;
    this->client.sin_port = htons(port);

    // convert address from text to binary form
    if(inet_pton(AF_INET, host_ip, &this->client.sin_addr) <= 0){
        throw ERROR_CLIENT_CONNECTION_FAILED;
    }

    // connect to server
    if(connect(this->client_socket, (struct sockaddr*)&this->client, sizeof(this->client)) < 0) {
        throw ERROR_CLIENT_CONNECTION_FAILED;
    }
}

void Client::Close(){
    closesocket(this->client_socket);
}

Client *Client::StartSession(){
    // for windows, need to init this before using sockets
    WORD wVersionRequested;
    WSADATA wsaData;
    int err;

    /* Use the MAKEWORD(lowbyte, highbyte) macro declared in Windef.h */
    wVersionRequested = MAKEWORD(2, 2);

    err = WSAStartup(wVersionRequested, &wsaData);
    if (err != 0) {
        throw err;
    }
    Client *current_client = NULL;

    try {
        current_client = new Client();
    } catch (int err) {
        throw err;
    }

    try {
        current_client->Connect(LOCAL_HOST, DEFAULT_PORT);
        cout << "Connected successfuly. Communication started\n";
        cout << "Current socket: " << current_client->client_socket << endl;
    } catch (int err) {
        throw err;
    }
    return current_client;
}

