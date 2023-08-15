
#include "ServerToClient.h"


ServerToClient::ServerToClient(ConnectionHandler* connectionHandler): ch(connectionHandler){}




void ServerToClient::run() {
    bool run = true;
    while(run){
        std::string output =  ch->decodeMessage();
        std::cout << output << std::endl;
        if (output == "ACK 3 ") {
            run = false;
            break;
        }
        else if(output == "ERROR 3"){
            std::cout << "Logout ERORR" <<std::endl;
        }
    }
    std::cout << "SERVER CLOSED" <<std::endl;
}

ServerToClient::~ServerToClient(){
    ch ->close();
}