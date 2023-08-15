#include <EncoderDecoder.h>

using namespace std;

EncoderDecoder::EncoderDecoder(){
    this->opCodes.insert(std::pair<string,short>("REGISTER",1));
    this->opCodes.insert(std::pair<string,short>("LOGIN",2));
    this->opCodes.insert(std::pair<string,short>("LOGOUT",3));
    this->opCodes.insert(std::pair<string,short>("FOLLOW",4));
    this->opCodes.insert(std::pair<string,short>("POST",5));
    this->opCodes.insert(std::pair<string,short>("PM",6));
    this->opCodes.insert(std::pair<string,short>("LOGSTAT",7));
    this->opCodes.insert(std::pair<string,short>("STAT",8));
    this->opCodes.insert(std::pair<string,short>("NOTFICATION",9));
    this->opCodes.insert(std::pair<string,short>("ACK",10));
    this->opCodes.insert(std::pair<string,short>("ERORR",11));
    this->opCodes.insert(std::pair<string,short>("BLOCK",12));
    
}
void EncoderDecoder::shortToBytes(short s, char bytes[]){
    bytes[0] = ((s >> 8) & 0xFF);
    bytes[1] = (s & 0xFF);
}
short EncoderDecoder::bytesToShort(char *bytes) {
    short out = (short)((bytes[0] & 0xff) << 8);
    out += (short)(bytes[1] & 0xff);
    return out;
}


std::vector<char> EncoderDecoder::cinToMessage(std::string cin ){
    std::vector<char> messageToSend;
    char op[2];
    std::string opCodeS=cin.substr(0,cin.find_first_of(" "));//takes the op
    cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
    short opcode=this->opCodes.at(opCodeS);
    this->shortToBytes(opcode,op);
    messageToSend.push_back(op[0]);
    messageToSend.push_back(op[1]);
    if (opcode==1){
        std::string userName(cin.substr(0,cin.find_first_of(" "))); 
        cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
        std::string password(cin.substr(0,cin.find_first_of(" ")));
        cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
        std::string birthday(cin.substr(0,cin.find_first_of(";")));
        //cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
        for (char c : userName) 
            messageToSend.push_back(c);
        messageToSend.push_back(char(0)); 
        //messageToSend.push_back('0');   
        for (char c : password) 
            messageToSend.push_back(c);
        messageToSend.push_back(char(0));
        //messageToSend.push_back('0');   
        for (char c : birthday) 
            messageToSend.push_back(c);
        messageToSend.push_back(char(0));
        //messageToSend.push_back('0'); 
    }
    if (opcode==2){
        std::string userName(cin.substr(0,cin.find_first_of(" "))); 
        cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
        std::string password(cin.substr(0,cin.find_first_of(" ")));
        cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
        std::string captcha(cin.substr(0,cin.find_first_of(";")));
        //cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
        for (char c : userName) 
            messageToSend.push_back(c);
        messageToSend.push_back(char(0));
        //messageToSend.push_back('0'); 
        for (char c : password) 
            messageToSend.push_back(c);
        messageToSend.push_back(char(0));    
        for (char c : captcha) 
            messageToSend.push_back(c);
        messageToSend.push_back(char(0));    
    }
    if (opcode==3){}
    if (opcode==4){
        char f;
        std::string fOrU(cin.substr(0,cin.find_first_of(" "))); 
        cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
        if (fOrU == "0")
           f=0;
        else
           f=1;
        std::string userToFollow(cin.substr(0,cin.find_first_of(" "))); 
        //cin=cin.substr(cin.find_first_of(" ")+1);//update the cin        
        messageToSend.push_back(f);
         for (char c : userToFollow) 
            messageToSend.push_back(c);
        messageToSend.push_back(char(0));
        //messageToSend.push_back('0'); 
    }
    if (opcode==5){
        std::string content=cin; 
        //cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
        for (char c : content) 
            messageToSend.push_back(c);
        messageToSend.push_back(char(0));    
        //messageToSend.push_back('0');
         
    }
    if (opcode==6){
        std::string userName(cin.substr(0,cin.find_first_of(" ")));
        cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
        std::string content=cin; 
        //cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
        for (char c : userName) 
            messageToSend.push_back(c);
        messageToSend.push_back(char(0));
        //messageToSend.push_back('0'); 
        for (char c : content) 
            messageToSend.push_back(c);
        messageToSend.push_back(char(0));       
    }
    if (opcode==7){}
    if (opcode==8){
        for (char c : cin) {
            if(c != ' '){
                messageToSend.push_back(c);
            }
            else{
                messageToSend.push_back('|');
            }
    }
    messageToSend.push_back('|');
    messageToSend.push_back(char(0)); 
    }
    if (opcode==12){
        std::string userName(cin.substr(0,cin.find_first_of(" ")));
        //cin=cin.substr(cin.find_first_of(" ")+1);//update the cin
        for (char c : userName) 
            messageToSend.push_back(c);
        messageToSend.push_back(char(0));        
    }
    messageToSend.push_back(';');
    return messageToSend;
}
std::string EncoderDecoder::ackDecode(short ackOp ,std::vector<char> messageContent){
    std::string output= "ACK " + std::to_string(ackOp) + " ";
    if(messageContent.size() > 0){
        if(ackOp == 4){
            for (int i =0 ; i < messageContent.size()-2 ; i++){
                output += messageContent.at(i);
            }

        }
        if(ackOp == 7 || ackOp == 8){
            int j=0;
            for (int i = messageContent.size()-2 ; i >= 0 ; i-=2){
                char opArr[2] = {messageContent.at(i-1),messageContent.at(i)};
                short temp = bytesToShort(opArr);
                output += std::to_string(temp);
                output += " ";
                j++;
                if (j%4==0 && i>1){
                    j=0;
                    output+= "\n" "ACK " + std::to_string(ackOp)+" "  ;
                }
                
            }
        
    }
    return output;
}
}
std::string EncoderDecoder::notificationDecode(std::vector<char> messageContent){
    int counter = 1;
    char c = '$';
    std::string userName = "";
    std::string content = "";
    int space_c =0; 
    for (int i=1; i<messageContent.size()-1; i++){
        if(space_c == 0)
            userName = userName+messageContent.at(i);
        if(space_c == 1)
            content = content + messageContent.at(i);
        if(space_c == 0 && messageContent.at(i) == char(0))
            space_c=1;
    }
    return userName +" "+ content;

}

 