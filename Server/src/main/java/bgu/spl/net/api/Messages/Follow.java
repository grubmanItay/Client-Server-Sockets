package bgu.spl.net.api.Messages;

public class Follow implements Message{
    Byte followUnfollow;
    String username;

    public Follow(Byte isFollowing, String username) {
        this.followUnfollow = isFollowing;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Byte getFollowUnfollow() {
        return followUnfollow;
    }
}
