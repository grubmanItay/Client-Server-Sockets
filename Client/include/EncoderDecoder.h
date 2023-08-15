#ifndef BOOST_ECHO_CLIENT_ENCODERDECODER_H
#define BOOST_ECHO_CLIENT_ENCODERDECODER_H
#include <vector>
#include <string>
#include <map>
#include <iostream>

class EncoderDecoder{

    public:
    EncoderDecoder();
    ~EncoderDecoder() = default;
    short bytesToShort(char* bytesArr);
    void shortToBytes(short num, char bytesArr[]);
    std::vector<char> cinToMessage(std::string cin);
    std::map<std::string,short> opCodes;
    std::string ackDecode(short ackOp ,std::vector<char> messageContent);
    std::string notificationDecode(std::vector<char> message);
    
};
#endif
