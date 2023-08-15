#include <ConnectionHandler.h>
#include <iostream>
#include <EncoderDecoder.h>
#include <thread>
using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
using std::vector;
using namespace std;


ConnectionHandler::ConnectionHandler(string host, short port):host_(host), port_(port), io_service_(), socket_(io_service_),endDec_(){}

    
ConnectionHandler::~ConnectionHandler() {close();}

bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); 
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);			
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\n');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, '\n');
}
  
bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    try {
		do{
			getBytes(&ch, 1);
            frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.length());
	if(!result) return false;
	return sendBytes(&delimiter,1);
}
 

void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}
std::string ConnectionHandler::decodeMessage() {
    char c;
    std::vector<char> message;
    for(int i = 0;i < 2; i++){
        getBytes(&c,1);
        message.push_back(c);
    }
    char opArr[2] = {message[0],message[1]};
    short opcode = this->endDec_.bytesToShort(opArr);
    if(opcode == 10){
        for(int i = 0;i < 2; i++){
            getBytes(&c,1);
            message.push_back(c);
        }
        char opArr1[2] = {message[2],message[3]};
        short ackOp = this->endDec_.bytesToShort(opArr1);
        std::vector<char> messageContent;
        if(ackOp == 4 || ackOp == 7 || ackOp == 8){
            while(c != ';'){
                getBytes(&c,1); 
                messageContent.push_back(c);
            }
        }
        else{    
                getBytes(&c,1);
        }
    
        return this->endDec_.ackDecode(ackOp, messageContent);
    }
    else if(opcode == 11){
        for(int i = 0;i < 2; i++){
            getBytes(&c,1);
            message.push_back(c);
        }
        char opArr2[2] = {message[2],message[3]};
        short opcodeError = this->endDec_.bytesToShort(opArr2);
        getBytes(&c,1);
        return "Error " + std::to_string(opcodeError);
    }
        else{  
            std::vector<char> messageContent;
            while(c != ';'){
                getBytes(&c,1);
                messageContent.push_back(c);
                
            }
            return this -> endDec_.notificationDecode(messageContent);

        }
    
}


bool ConnectionHandler::sendData(std::string data){
    std::vector<char> dataTosend = this->endDec_.cinToMessage(data);
    dataTosend.shrink_to_fit();
    char toSend[dataTosend.size()];
    for(unsigned long i = 0 ; i <dataTosend.size(); i++){
        toSend[i] = dataTosend[i];
    }
    return sendBytes(toSend, (int)dataTosend.size());
}


