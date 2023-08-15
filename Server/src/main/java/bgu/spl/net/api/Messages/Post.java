package bgu.spl.net.api.Messages;

public class Post implements Message{
    String content;
    public Post(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
