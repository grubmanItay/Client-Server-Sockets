package bgu.spl.net.api.Messages;

import java.sql.Time;
import java.util.Calendar;
import java.util.LinkedList;

public class User {
    private String username;
    private String password;
    private String birthday;
    private int conId = -1;
    private LinkedList<User> following;
    private LinkedList<User> followers;
    private boolean logged = false;
    private short postsNum = 0;
    private LinkedList<User> blocking;
    private LinkedList<NotificationMessage> waitingMessages;


    public User(String username, String password, String birthday){
        this.username = username;
        this.password = password;
        this.birthday = birthday;
        following = new LinkedList<>();
        followers = new LinkedList<>();
        blocking = new LinkedList<>();
        waitingMessages = new LinkedList<>();
    }

    public LinkedList<NotificationMessage> getWaitingMessages() {
        return waitingMessages;
    }

    public void addWaitingMessage(NotificationMessage message){
        waitingMessages.addLast(message);
    }

    public void setConId(int conId){
        this.conId = conId;
    }

    public void blockUser(User user){
        this.blocking.push(user);
    }

//    public void gotBlocked(User user){
//        this.blockedBy.push(user);
//    }

    public short getAge(){
        short age;
        int day = Integer.parseInt(this.birthday.substring(0,2));
        int month = Integer.parseInt(this.birthday.substring(3,5));
        int year = Integer.parseInt(this.birthday.substring(6,10));
        Calendar cal = Calendar.getInstance();
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);
        if (month < currentMonth | (month == currentMonth & day <= currentDay))
            age = (short) (currentYear-year);
        else
            age = (short) (currentYear-year - 1);
        return age;
    }

    public short getFollowingNum(){
        return (short) this.following.size();
    }

    public short getFollowersNum(){
        return (short) this.followers.size();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LinkedList<User> getFollowers() {
        return followers;
    }

    public LinkedList<User> getFollowing() {
        return following;
    }

    public boolean islogged() {
        return logged;
    }

    public void login(int conId){
        this.logged = true;
        this.conId = conId;
    }

    public boolean isFollowing(String username) {
        boolean hasFound = false;
        for (int i = 0; i < this.following.size() & hasFound == false; i++){
            if (this.following.get(i).getUsername().equals(username))
                hasFound = true;
        }
        return hasFound;
    }

//    public void Follow(User)

    public boolean isFollower(String username) {
        boolean hasFound = false;
        for (int i = 0; i < this.followers.size() & hasFound == false; i++){
            if (this.followers.get(i).getUsername() == username)
                hasFound = true;
        }
        return hasFound;
    }

    public void follow(User foundUser)
    {
        this.following.push(foundUser);
    }

    public void unfollow(User foundUser){
        if (this.getFollowing().contains(foundUser))
            this.following.remove(foundUser);
    }

    public int getConId() {
        return this.conId;
    }

    public void addPost() {
        this.postsNum++;
    }

    public boolean isBlocked(User foundUser) {
        boolean hasFound = false;
        for (int i = 0; i < this.blocking.size() & hasFound == false; i++){
            if (this.blocking.get(i).getUsername() == username)
                hasFound = true;
        }
        return hasFound;
    }

    public short getPostsNum() {
        return postsNum;
    }

    public void resetWaitingMessages() {
        this.waitingMessages.clear();
    }

    public void logout() {
        this.logged = false;
    }

    public void addFollower(User sender) {
        this.followers.push(sender);
    }

    public void removeFollower(User sender) {
        if (this.followers.contains(sender))
            this.followers.remove(sender);
    }
}
