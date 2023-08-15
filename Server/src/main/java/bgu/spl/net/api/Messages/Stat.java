package bgu.spl.net.api.Messages;

import java.util.List;

public class Stat implements Message{
    List<String> usernameList;
    public Stat(List<String> usernameList) {
        this.usernameList = usernameList;
    }

    public List<String> getUsernameList() {
        return usernameList;
    }
}
