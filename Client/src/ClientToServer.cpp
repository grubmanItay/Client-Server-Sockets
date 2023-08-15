
#include <boost/algorithm/string.hpp>
#include "ClientToServer.h"

ClientToServer::ClientToServer(ConnectionHandler *connectionHandler) : ch(connectionHandler){}


void ClientToServer::run() {
    while(true) {
        std::string cIn;
        std::getline(std::cin ,cIn);
        if (!ch->sendData(cIn)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        if(boost::to_upper_copy<std::string>(cIn) == "LOGOUT"){
            break;
        }
        
    }
}
ClientToServer::~ClientToServer(){
     ch->close();
}