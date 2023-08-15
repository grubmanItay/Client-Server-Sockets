package bgu.spl.net.api.Messages;

public class Login implements Message{
    String username;
    String password;
    Byte captcha;
    public Login(String username, String password, Byte captcha) {
        this.username = username;
        this.password = password;
        this.captcha = captcha;
    }

    public String getPassword() {
        return password;
    }

    public Byte getCaptcha() {
        return captcha;
    }

    public String getUsername() {
        return username;
    }
}
