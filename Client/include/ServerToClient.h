

#ifndef BOOST_ECHO_CLIENT_ServerToClient_H
#define BOOST_ECHO_CLIENT_ServerToClient_H

#include "ConnectionHandler.h"


class ServerToClient{
private:

    ConnectionHandler* ch;
public:

    ServerToClient(ConnectionHandler* connectionHandler);

    void run();
    ~ServerToClient();
};

#endif 
