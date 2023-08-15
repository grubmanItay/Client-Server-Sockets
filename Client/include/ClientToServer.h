
#ifndef BOOST_ECHO_CLIENT_ClientToServer_H
#define BOOST_ECHO_CLIENT_ClientToServer_H

#include "ConnectionHandler.h"

class ClientToServer {

    ConnectionHandler *ch;
public:
    ClientToServer(ConnectionHandler *connectionHandler);
    ~ClientToServer();
    void run();
};

#endif 
