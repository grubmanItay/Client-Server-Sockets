package bgu.spl.net.api.Messages;

public class PrivateMessage implements Message{
    String content;
    String username;
    String sendingDateAndTime;
    public PrivateMessage(String username, String content, String sendingDateAndTime) {
        this.content = content;
        this.username = username;
        this.sendingDateAndTime = sendingDateAndTime;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }

    public String getSendingDateAndTime() {
        return sendingDateAndTime;
    }
}
