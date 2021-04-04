#ifndef __CLIENT_HPP__
#define __CLIENT_HPP__

#include "../socket/socket.hpp"

class Client : public Socket {
    private:
    Client();
    ~Client();

    public:
    SOCKET client_socket;
    struct sockaddr_in client;
    static Client* StartSession();
    void Connect(char *host_ip, int port);
    void Close();

};

#endif