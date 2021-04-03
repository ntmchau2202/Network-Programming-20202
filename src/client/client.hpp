#ifndef __CLIENT_HPP__
#define __CLIENT_HPP__

#include "../socket/socket.hpp"

class Client : Socket {
    private:
    struct sockaddr_in client_socket;

    public:
    Client();
    ~Client();
};

#endif