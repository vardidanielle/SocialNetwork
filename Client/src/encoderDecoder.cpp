//
// Created by vardidan@wincs.cs.bgu.ac.il on 04/01/19.
//

#include "encoderDecoder.h"
#include <string>
#include <queue>

EncoderDecoder::EncoderDecoder(ConnectionHandler& connectionHandler): connectionHandler(connectionHandler){}


//------------------ENCODER-----------------//



void EncoderDecoder::encode(queue<string>& strVec) { //extracts the op code and classifies the message
    std::string request = strVec.front();
    strVec.pop();
    if (request == "REGISTER")
        sendRegister(strVec);
    else if (request == "LOGIN")
        sendLogin(strVec);
    else if(request == "LOGOUT") {
        sendTwoBytes(0x03);
        if (connectionHandler.isLoggedIn())
            connectionHandler.setOpen(false); //now the threads of reading from keyboard and socket will stop.
    }
    else if (request == "FOLLOW")
        sendFollow(strVec);
    else if (request == "POST")
        sendPost(strVec);
    else if (request == "PM")
        sendPm(strVec);
    else if(request == "USERLIST")
        sendTwoBytes(0x07);
    else if (request == "STAT")
        sendStat(strVec);
    else
        terminate();
}

//extraxt the content of the message and sends it as strings.

void EncoderDecoder::sendRegister(queue<string>& strVec){
    sendTwoBytes(0x01);
    std::string username = strVec.front();
    strVec.pop();
    std::string password = strVec.front();
    strVec.pop();
    connectionHandler.sendLine(username);
    connectionHandler.sendLine(password);
}

void EncoderDecoder::sendLogin(queue<string>& strVec){
    sendTwoBytes(0x02);
    std::string username = strVec.front();
    strVec.pop();
    std::string password = strVec.front();
    strVec.pop();
    connectionHandler.sendLine(username);
    connectionHandler.sendLine(password);
}

void EncoderDecoder::sendFollow(queue<string>& strVec){
    sendTwoBytes(0x04);
    const char* follow = strVec.front().c_str(); //to identify if its 0/1 follow/unfollow
    strVec.pop();
    connectionHandler.sendBytes(&follow[0],1);
    std::string numOfFollowers = strVec.front();
    strVec.pop();
    int num = stoi(numOfFollowers);
    sendTwoBytes(static_cast<short>(num));
    for(int i=0; i<num;i++){
        connectionHandler.sendLine(strVec.front());
        strVec.pop();
    }
}

void EncoderDecoder::sendPost(queue<string>& strVec){
    sendTwoBytes(0x05);
    sendContent(strVec);
}

void EncoderDecoder::sendPm(queue<string>& strVec){
    sendTwoBytes(0x06);
    connectionHandler.sendLine(strVec.front());
    strVec.pop();
    sendContent(strVec);
}

void EncoderDecoder::sendStat(queue<string>& strVec){
    sendTwoBytes(0x08);
    connectionHandler.sendLine(strVec.front());
    strVec.pop();
}

void EncoderDecoder::shortToBytes(short num, char* bytesArr) {
    bytesArr[0] = static_cast<char>((num >> 8) & 0xFF);
    bytesArr[1] = static_cast<char>(num & 0xFF);
}

void EncoderDecoder::sendTwoBytes(short number){
    char byteArr[2];
    shortToBytes(number,byteArr);
    connectionHandler.sendBytes(&byteArr[0],2);
}

void EncoderDecoder::sendContent(queue<string>& strVec){
    std::string content;
    while (strVec.size()!= 1){
        content += strVec.front() + " ";
        strVec.pop();
    }
    content = content + strVec.front();
    strVec.pop();
    connectionHandler.sendLine(content);
}



//------------------DECODER-----------------//



void EncoderDecoder::decoder(char* ch){ //first two bytes for opcode already decoded;
    short opcode = bytesToShort(ch);
    switch((int)opcode){
        case 9:
            notification();
            break;
        case 10:ack();
            break;
        case 11:error();
            break;
        default:
            break;
    }
}

void EncoderDecoder::ack() { //classifies the type of the ack and act according to it.
    char opcode[2];
    connectionHandler.getBytes(&opcode[0],2);
    int ACKopcode = (int) bytesToShort(opcode);
    if (ACKopcode ==2)
        connectionHandler.setLoggedIn(true);
    if (ACKopcode == 3)
        connectionHandler.setOpen(false);
    switch (ACKopcode){
        case 4:followUnfollowACK(ACKopcode);
            break;
        case 7:userListACK(ACKopcode);
            break;
        case 8:statsACK(ACKopcode);
            break;
        default:
            cout << "ACK " << ACKopcode << endl;
    }
}

void EncoderDecoder::followUnfollowACK(int messageOpcode) {
    printAck(messageOpcode);
}

void EncoderDecoder::statsACK(int messageOpcode) {
    cout << "ACK " << 8 << " "; //prints ACK opcode and message opcode
    char twoBytes[2];
    for(int i=0;i<2;i++){
        connectionHandler.getBytes(&twoBytes[0],2);
        cout << (int) bytesToShort(twoBytes) << " ";// prints by order,
    }
    connectionHandler.getBytes(&twoBytes[0],2);
    cout << (int) bytesToShort(twoBytes) << endl;//last two bytes without space.
}

void EncoderDecoder::userListACK(int messageOpcode) {
    printAck(messageOpcode);

}

void EncoderDecoder::notification() {
    char type[1];
    string postingUser;
    string content;
    connectionHandler.getBytes(&type[0],1); //if its a PM or an PUBLIC message
    connectionHandler.getLine(postingUser);
    connectionHandler.getLine(content);
    string together = postingUser +" " + content;
    std::string str;
    if(type[0] == 0){
        str = "PM";
    } else
        str = "Public";
    cout << "NOTIFICATION " << str << " " << together << endl;
}

void EncoderDecoder::error() {
    char ch[2];
    connectionHandler.getBytes(&ch[0],2);
    int opcode = (int) bytesToShort(ch);
    cout << "ERROR " << opcode << endl;
}

void EncoderDecoder::printAck(int messageOpcode){
    char ch[2];
    int i = 0;
    connectionHandler.getBytes(&ch[0],2); //to get the num of followers
    int numOfUsers= (int)bytesToShort(ch);
    string list;
    while (i < numOfUsers-1){
        connectionHandler.getLine(list);
        list += " ";
        i++;
    }
    connectionHandler.getLine(list); // the last one without space.
    cout << "ACK "  << messageOpcode << " " << numOfUsers << " " << list << endl;
}

short EncoderDecoder::bytesToShort(char* bytesArr) {
    auto result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}