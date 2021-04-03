#ifndef __SERVER_HPP__
#define __SERVER_HPP__

#include <map>

#include "../socket/socket.hpp"

using namespace std;

class Server : Socket{
    private:
    struct sockaddr_in server;
    map<int, SOCKET> list_client;

    public:
    Server();
    ~Server();
    map<int, SOCKET> GetClientList();
};

#endif