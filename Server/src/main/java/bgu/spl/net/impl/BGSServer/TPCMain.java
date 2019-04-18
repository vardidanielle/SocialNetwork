package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        Database database = new Database();

        Server.threadPerClient(
                Integer.parseInt(args[0]),
                ()->new BidiMessagingProtocolImpl(database),
                ()->new MessageEncoderDecoderImpl()
        ).serve();

    }
}
