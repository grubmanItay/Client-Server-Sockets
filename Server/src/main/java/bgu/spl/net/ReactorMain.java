package bgu.spl.net;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.Messages.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;

public class ReactorMain {
    public static void main(String[] args) {
        DataBase dataManager = new DataBase();
//        Server<Message> reactorServer = Server.reactor(Integer.parseInt(args[1]),Integer.parseInt(args[0]),
        Server<Message> reactorServer = Server.reactor(10,7777,
                () -> new BidiMessagingProtocolImpl(dataManager),
                MessageEncoderDecoderImpl::new);
        reactorServer.serve();
    }
}
