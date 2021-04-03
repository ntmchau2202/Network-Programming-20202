#ifndef __SOCKET_HPP__
#define __SOCKET_HPP__

#include <WinSock2.h>
#include <windows.h>
#include <string>

#define MAX_BUFFER 8096
#define MAX_CLIENT 10
#define LOCAL_HOST "127.0.0.1"
#define DEFAULT_PORT 8888

using namespace std;

class Socket {
    public:
    void Listen();
    void Bind();
    SOCKET Accept();
    int SendMsg(string msg, int id);
    int ReceiveMsg(char *buffer);
};

#endif