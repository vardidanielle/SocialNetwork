//
// Created by vardidan@wincs.cs.bgu.ac.il on 04/01/19.
//

#include <stdlib.h>
#include <connectionHandler.h>
#include <thread>
#include "../include/readFromSocket.h"
#include "../include/readFromKeyboard.h"

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    //creating a thread per task.

    ReadFromSocket socket(connectionHandler);
    ReadFromKeyboard keyboard(connectionHandler);

    std::thread th1(socket); // we use std::ref to avoid creating a copy of the Task object
    std::thread th2(keyboard);

    th1.join();
    th2.join();

    connectionHandler.close();
    return 0;
}