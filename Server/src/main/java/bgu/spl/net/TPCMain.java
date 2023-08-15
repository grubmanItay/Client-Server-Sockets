package bgu.spl.net;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        DataBase dataBase = new DataBase();
        Server<Message> threadPerClientServer = Server.threadPerClient(7777,
                () -> new BidiMessagingProtocolImpl(dataBase),
                () -> new MessageEncoderDecoderImpl());

        threadPerClientServer.serve();
    }
}
