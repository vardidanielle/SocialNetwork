//
// Created by vardidan@wincs.cs.bgu.ac.il on 01/01/19.
//

#include "connectionHandler.h"
#ifndef BOOST_ECHO_CLIENT_READFROMKEYBOARD_H
#define BOOST_ECHO_CLIENT_READFROMKEYBOARD_H

class ReadFromKeyboard {
private:
    ConnectionHandler& connectionHandler;

public:
    ReadFromKeyboard(ConnectionHandler& connectionHandler);
    void operator()();
};


#endif //BOOST_ECHO_CLIENT_READFROMKEYBOARD_H
