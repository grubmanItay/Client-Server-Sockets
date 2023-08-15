#ifndef CONNECTION_HANDLER__
#define CONNECTION_HANDLER__
                                           
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <EncoderDecoder.h>

using boost::asio::ip::tcp;

class ConnectionHandler {
private:
	const std::string host_;
	const short port_;
	boost::asio::io_service io_service_;  
	tcp::socket socket_; 
    EncoderDecoder endDec_;
    
    
 
public:
    ConnectionHandler(std::string host, short port);
    virtual ~ConnectionHandler();
    bool connect();
    std::string decodeMessage();
    bool getBytes(char bytes[], unsigned int bytesToRead);
    bool sendBytes(const char bytes[], int bytesToWrite);
    bool getLine(std::string& line);
    bool sendLine(std::string& line);
    bool getFrameAscii(std::string& frame, char delimiter);
    bool sendFrameAscii(const std::string& frame, char delimiter);
    void close();
    bool sendData(std::string data);
    //std::string decodeMessage();

    
}; 
 
#endif
