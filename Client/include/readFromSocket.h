//
// Created by vardidan@wincs.cs.bgu.ac.il on 01/01/19.
//

#include "connectionHandler.h"
#ifndef BOOST_ECHO_CLIENT_READFROMSOCKET_H
#define BOOST_ECHO_CLIENT_READFROMSOCKET_H

class ReadFromSocket {
private:
    ConnectionHandler& connectionHandler;

public:
    ReadFromSocket(ConnectionHandler& connectionHandler);
    void operator()();
    short bytesToShort(char* bytesArr);
};


#endif //BOOST_ECHO_CLIENT_READFROMSOCKET_H
