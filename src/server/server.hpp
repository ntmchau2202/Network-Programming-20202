#ifndef __SERVER_HPP__
#define __SERVER_HPP__

#include <map>

#include "../socket/socket.hpp"

#define MAXIMUM_CLIENT 10

using namespace std;

class Server : public Socket{
    private:

    struct sockaddr_in server;

    Server();
    ~Server();

    public:

    SOCKET server_socket;
    fd_set readfds;
    map<int, SOCKET> list_client;
    
    void Listen();
    void Bind();
    SOCKET Accept();
    static Server* StartSession();


};

#endif