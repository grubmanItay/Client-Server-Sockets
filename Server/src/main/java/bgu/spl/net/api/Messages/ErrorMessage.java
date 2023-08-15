package bgu.spl.net.api.Messages;

public class ErrorMessage implements Message{
    int messageOpcode;
    public ErrorMessage(int messageOpcode){
        this.messageOpcode = messageOpcode;
    }

    public int getMessageOpcode() {
        return messageOpcode;
    }
}
