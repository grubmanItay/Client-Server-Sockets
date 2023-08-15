package bgu.spl.net.api.Messages;

public class NotificationMessage implements Message{
    Byte notificationType;
    String postUser;
    String content;
    public NotificationMessage(Byte notificationType, String postUser, String content) {
        this.notificationType = notificationType;
        this.postUser = postUser;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Byte getNotificationType() {
        return notificationType;
    }

    public String getPostUser() {
        return postUser;
    }
}
