package bgu.spl.net.api.Messages;

public class Register implements Message{
    String userName;
    String password;
    String birthday;
    public Register(String userName, String password, String birthday){
        this.userName = userName;
        this.password = password;
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
}
