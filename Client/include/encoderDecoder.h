//
// Created by vardidan@wincs.cs.bgu.ac.il on 01/01/19.
//

#ifndef BOOST_ECHO_CLIENT_ENCODERDECODER_H
#define BOOST_ECHO_CLIENT_ENCODERDECODER_H
#include <vector>
#include <string>
#include <queue>
#include "connectionHandler.h"


using namespace std;

class EncoderDecoder {
private:
    ConnectionHandler& connectionHandler;
    void printAck(int messageOpcode);
    short bytesToShort(char* bytesArr);

public:
    EncoderDecoder(ConnectionHandler& connectionHandler);

    //---ENCODER---//
    void encode(char* input);
    void encode(queue<std::string>& input);
    void shortToBytes(short num, char* bytesArr);
    void sendTwoBytes(short number);
    void sendRegister(queue<string>& strVec);
    void sendLogin(queue<string>& strVec);
    void sendFollow(queue<string>& strVec);
    void sendPost(queue<string>& strVec);
    void sendPm(queue<string>& strVec);
    void sendStat(queue<string>& strVec);
    void sendContent(queue<string>& strVec);

    //---DECODER---//
    void decoder(char* ch);
    void ack();
    void followUnfollowACK(int messageOpcode);
    void statsACK(int messageOpcode);
    void userListACK(int messageOpcode);
    void notification();
    void error();
};


#endif //BOOST_ECHO_CLIENT_ENCODERDECODER_H
