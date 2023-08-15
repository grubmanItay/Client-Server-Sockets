package bgu.spl.net.api.Messages;

public class BlockMessage implements Message{
    String username;
    public BlockMessage(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
