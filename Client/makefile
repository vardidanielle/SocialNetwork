CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread -lboost_filesystem -lboost_thread

all: BGSclient
	g++ -o bin/BGSclient bin/connectionHandler.o bin/echoClient.o bin/encoderDecoder.o bin/readFromSocket.o bin/readFromKeyboard.o $(LDFLAGS)

BGSclient: bin/connectionHandler.o bin/echoClient.o bin/encoderDecoder.o bin/readFromKeyboard.o bin/readFromSocket.o

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/encoderDecoder.o: src/encoderDecoder.cpp
	g++ $(CFLAGS) -o bin/encoderDecoder.o src/encoderDecoder.cpp

bin/readFromSocket.o: src/readFromSocket.cpp
	g++ $(CFLAGS) -o bin/readFromSocket.o src/readFromSocket.cpp

bin/readFromKeyboard.o: src/readFromKeyboard.cpp
	g++ $(CFLAGS) -o bin/readFromKeyboard.o src/readFromKeyboard.cpp

.PHONY: clean
clean:
	rm -f bin/*
