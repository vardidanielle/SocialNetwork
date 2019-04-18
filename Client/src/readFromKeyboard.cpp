//
// Created by vardidan@wincs.cs.bgu.ac.il on 04/01/19.
//

#include "connectionHandler.h"
#include "readFromKeyboard.h"
#include <vector>
#include <string>
#include "../include/encoderDecoder.h"
#include <queue>

ReadFromKeyboard::ReadFromKeyboard(ConnectionHandler& connectionHandler): connectionHandler(connectionHandler){}

//a thread is "taking" this action, and keeps on reading from the keyboard while the connection between the server and client is open.

void ReadFromKeyboard::operator()(){
    while(connectionHandler.isOpen()) {
        EncoderDecoder encdec(connectionHandler);
        std::string input;
        getline(std::cin, input);
        std::queue<std::string> strVec;
        size_t space = input.find(' ');
        while (space != std::string::npos) { //while message is not done.
            strVec.push(input.substr(0, space ));
            input = input.substr(space + 1);
            space = input.find(' ');
        }
        strVec.push(input);
        encdec.encode(strVec);
    }
}
