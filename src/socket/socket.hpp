#ifndef __SOCKET_HPP__
#define __SOCKET_HPP__

#include <WinSock2.h>
#include <Windows.h>
#include <WS2tcpip.h>
#include <string>

#define MAX_LEN_BUFFER 8096
#define LOCAL_HOST "127.0.0.1"
#define DEFAULT_PORT 8888

#define ERROR_SOCKET_ENV_CREATION_FAILED -9999

#define ERROR_CLIENT_CREATION_FAILED -1
#define ERROR_CLIENT_CONNECTION_FAILED -2

#define ERROR_SERVER_CREATION_FAILED 1
#define ERROR_SERVER_BINDING_FAILED 2
#define ERROR_SERVER_LISTENING_FAILED 3
#define ERROR_SERVER_ACCEPT_FAILED 4

#define ERROR_CONNECTION_OK 200
#define ERROR_REQUEST_CREATE_OK 201
#define ERROR_BAD_REQUEST 400
#define ERROR_UNAUTHORIZED 401
#define ERROR_RESOURCE_NOT_FOUND 404

using namespace std;

class Socket {
    public:
    int ReceiveMsg(char *recv_buff, SOCKET *client_socket);
    int SendMsg(string msg, SOCKET *target_socket);
};

#endif