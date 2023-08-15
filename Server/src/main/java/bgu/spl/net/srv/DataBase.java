package bgu.spl.net.srv;

import bgu.spl.net.api.Messages.User;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    private LinkedList<User> registered;
    private ConcurrentHashMap<Integer, User> connectionToLogedinUsers;
    private int registerIndex = 0;
    private ConcurrentHashMap<Integer, String> messagesHistory;
    private String[] filteredWords = {"war", "sap"};

    public DataBase(){
        registered = new LinkedList<>();
        connectionToLogedinUsers = new ConcurrentHashMap<>();
        this.messagesHistory = new ConcurrentHashMap<>();
//        this.filteredWords = new LinkedList<>();
    }

    public String[] getFilteredWords() {
        return filteredWords;
    }

    public User getLoggedIn(int index) {
        return this.connectionToLogedinUsers.get(index);
    }

    public LinkedList<User> getRegistered() {
        return registered;
    }

    public Collection<User> getLoggedList(){
        return this.connectionToLogedinUsers.values();
    }

    public void register(String userName, String password, String birthday) {
        this.registered.push(new User(userName,password,birthday));
//        registerIndex++;
    }

    public User findLoggedUserByName(String username){
        User user = null;
        Iterator<User> iterator = connectionToLogedinUsers.values().iterator();
        boolean hasFound = false;
        while(iterator.hasNext() & hasFound == false) {
            User currentUser = iterator.next();
            if(currentUser.getUsername().equals(username)){
                hasFound = true;
                user = currentUser;
            }
        }
        return user;
    }

    public User findRegisteredUserByName(String username) {
        User user = null;
        boolean hasFound = false;
        synchronized (registered) {
            for (int i = 0; i < this.registered.size(); i++) {
                if (registered.get(i).getUsername().equals(username)) {
                    hasFound = true;
                    user = registered.get(i);
                }
            }
        }
            return user;

    }

//    public void loginUser(User user,int id){
//        this.connectionToLogedinUsers.put(id, user);
//        user.login();
//    }

    public void loginUser(String username, int id){
        User user;
        boolean isFound = false;
        synchronized (registered) {
            for (int i = 0; i < this.registered.size() & isFound == false; i++) {
                if (this.registered.get(i).getUsername().equals(username)) {
                    user = this.registered.get(i);
                    this.connectionToLogedinUsers.put(id, user);
                    user.login(id);
                }
            }
        }
    }

    public void logoutUser(int connectionId) {
        synchronized (connectionToLogedinUsers) {
            User user = getLoggedIn(connectionId);
            user.logout();
            this.connectionToLogedinUsers.remove(connectionId);
        }
    }

    public void addMessage(int i, String content) {
        synchronized (messagesHistory) {
            this.messagesHistory.put(i, content);
        }
    }
}
