//
// Created by vardidan@wincs.cs.bgu.ac.il on 04/01/19.
//
#include "connectionHandler.h"
#include "readFromSocket.h"
#include "../include/encoderDecoder.h"


ReadFromSocket::ReadFromSocket(ConnectionHandler& connectionHandler): connectionHandler(connectionHandler){}

//a thread is "taking" this action, and keeps on reading from the socket the while the connection between the server and client is open.

void ReadFromSocket:: operator()(){
    EncoderDecoder encdec(connectionHandler);
    while(connectionHandler.isOpen()) {
        char ch[2];
        if(connectionHandler.getBytes(&ch[0],2)){ //sends 2 byts every round.
            encdec.decoder(ch);
            ch[0] = 0;
            ch[1] = 0;
        }
    }
}


short ReadFromSocket::bytesToShort(char* bytesArr)
{
    auto result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}