
#include <stdlib.h>
#include <iostream>
#include <thread>
#include <boost/thread.hpp>
#include "ConnectionHandler.h"
#include <boost/algorithm/string.hpp>
#include <ClientToServer.h>
#include <ServerToClient.h>

using namespace boost;
using namespace std;
using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;



int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    
    std::string host = argv[1];
    auto port = (short)atoi(argv[2]);
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    ClientToServer ClientToServer(&connectionHandler);
    std::thread ClientToServerThread(&ClientToServer::run,&ClientToServer);
    ServerToClient ServerToClientTask(&connectionHandler);
    std::thread ServerToClientTaskThread(&ServerToClient::run, &ServerToClientTask);
    ClientToServerThread.join();
    ServerToClientTaskThread.join();
    return 0;
}