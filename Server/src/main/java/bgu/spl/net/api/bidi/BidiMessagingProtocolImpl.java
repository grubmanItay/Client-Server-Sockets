package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Messages.*;
import bgu.spl.net.srv.DataBase;

import java.util.LinkedList;
import java.util.List;

public class BidiMessagingProtocolImpl implements  BidiMessagingProtocol<Message> {
    private DataBase db;
    private int connectionId;
    private Connections<Message> connections;
    private boolean should_terminate = false;

    public BidiMessagingProtocolImpl(DataBase dataBase) {
        this.db = dataBase;
    }



    /**
     * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     **/
    public void start(int connectionId, Connections<Message> connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }

    public void process(Message message) {
        if (message instanceof Register) {
            boolean foundUsername = false;
            LinkedList<User> registered = this.db.getRegistered();
            for (int i = 0; i < registered.size() & foundUsername == false; i++) {
                if (registered.get(i).getUsername().equals(((Register) message).getUserName()))
                    foundUsername = true;
            }
            if (foundUsername)
                connections.send(this.connectionId, new ErrorMessage(1));
            else {
                this.db.register(((Register) message).getUserName(), ((Register) message).getPassword(), ((Register) message).getBirthday());
                connections.send(this.connectionId, new ACKMessage(1, ""));
            }
        }

        if (message instanceof Login) {
            boolean foundUsername = false;
            boolean validLogin = false;
            LinkedList<User> registered = this.db.getRegistered();
//            for (int i = 0; i < registered.size() & foundUsername == false; i++) {
//                if (registered.get(i).getUsername().equals(((Login) message).getUsername()))
//                    foundUsername = true;
            User foundUser = this.db.findRegisteredUserByName(((Login) message).getUsername());
//            if (foundUser != null){
            if (foundUser != null && (foundUser.getPassword().equals(((Login) message).getPassword()) & foundUser.islogged() == false & (new String(new byte[]{((Login) message).getCaptcha()}).equals("1"))))
                validLogin = true;

//            }
            if (!validLogin)
                connections.send(this.connectionId, new ErrorMessage(2));
            else {
                this.db.loginUser(((Login) message).getUsername(), this.connectionId);
                connections.send(this.connectionId, new ACKMessage(2, ""));
                for (NotificationMessage notification : foundUser.getWaitingMessages()) {
                    connections.send(this.connectionId, notification);
                }
                foundUser.resetWaitingMessages();
            }
        }

        if (message instanceof Logout) {
            boolean foundLogged = false;

            if (this.db.getLoggedIn(this.connectionId) != null)
                foundLogged = true;
            if (!foundLogged)
                connections.send(this.connectionId, new ErrorMessage(3));
            else {
                this.db.logoutUser(this.connectionId);
                connections.send(this.connectionId, new ACKMessage(3, ""));
//                this.should_terminate = true;
                this.connections.disconnect(this.connectionId);
            }
        }

        if (message instanceof Follow) {
            boolean validFollow = false;
            User sender = this.db.getLoggedIn(this.connectionId);
            User foundUser = null;
            if (sender != null) {
//                boolean foundUsername = false;
//                for (int i = 0; i < connections.length & foundUsername == false; i++) {
//                    if (connections[i].getusername == ((Follow) message).getUsername())
//                        foundUsername = true;
//                }
                foundUser = this.db.findRegisteredUserByName(((Follow) message).getUsername());
                if (foundUser != null && ((((Follow) message).getFollowUnfollow() == 1 & sender.isFollowing(((Follow) message).getUsername())) | (((Follow) message).getFollowUnfollow() == 0 & !sender.isFollowing(((Follow) message).getUsername()))) & !sender.isBlocked(foundUser))
                    validFollow = true;
            }

            if (!validFollow)
                connections.send(this.connectionId, new ErrorMessage(4));
            else {
                if (((Follow) message).getFollowUnfollow() == 0) {
                    sender.follow(foundUser);
                    foundUser.addFollower(sender);
                } else {
                    sender.unfollow(foundUser);
                    foundUser.removeFollower(sender);
                }
                connections.send(this.connectionId, new ACKMessage(4, ((Follow) message).getUsername() + 0));
            }
        }

        if (message instanceof Post) {
            boolean readingUsername = false;
            String username = "";
            List<String> usernameList = new LinkedList<>();
            String content = ((Post) message).getContent();
            User sender = this.db.getLoggedIn(this.connectionId);

            if (sender != null) {
                for (int i = 0; i < content.length(); i++) {
                    if (content.charAt(i) == '@')
                        readingUsername = true;
                    else if (readingUsername & content.charAt(i) != ' ')
                        username += content.charAt(i);
                    else if (readingUsername & content.charAt(i) == ' ') {
                        usernameList.add(username);
                        username = "";
                        readingUsername = false;
                    }
                }
                NotificationMessage notification = new NotificationMessage((byte) 1, sender.getUsername(), ((Post) message).getContent());
                for (int i = 0; i < sender.getFollowing().size(); i++) {
                    User receiver = sender.getFollowing().get(i);
                    if (receiver.islogged())
                        connections.send(receiver.getConId(), notification);
                    else
                        receiver.addWaitingMessage(notification);

                }
                boolean foundUsername = false;
                for (int i = 0; i < usernameList.size(); i++) {
                    User receiver = this.db.findRegisteredUserByName(usernameList.get(i));
                    if (receiver != null) {
                        if (receiver.islogged())
                            connections.send(receiver.getConId(), notification);
                        else
                            receiver.addWaitingMessage(notification);
                    }
                }
//                    for (int j = 0; j < connections.length & foundUsername == false; j++) {
//                        if (connections[j].getusername == usernameList.get(i)) {
//                            foundUsername = true;
//                            connections.send(j, new NotificationMessage(1,connections[this.connectionId],((Post) message).getContent()));
//                        }
//                    }
                sender.addPost();
                this.db.addMessage(1, ((Post) message).getContent());
                connections.send(this.connectionId, new ACKMessage(5, ""));
            } else
                connections.send(this.connectionId, new ErrorMessage(5));
        }

        if (message instanceof PrivateMessage) {
            //filter words from content
            boolean validPM = false;
            User sender = this.db.getLoggedIn(this.connectionId);
            String recUsername = ((PrivateMessage) message).getUsername();
            User receiver = this.db.findRegisteredUserByName(recUsername);
            if (sender != null) {
                if (receiver != null && sender.isFollowing(recUsername)) {
                    validPM = true;
                }
//                for (int i = 0; i < connections.length & validPM == false; i++) {
//                    if (connections[i].getusername == recUsername) {
//                        validPM = true;
//                        connections.send(i,new NotificationMessage(0,connections[this.connectionId],((PrivateMessage) message).getContent()));
//                    }
//                }

            }
            if (validPM) {
                String content = ((PrivateMessage) message).getContent();
                String[] filteredWords = db.getFilteredWords();
                for (int i = 0; i < filteredWords.length; i++) {
                    content = content.replace(" " + filteredWords[i] + " ", " <filtered> ");
                    if (content.substring(0, filteredWords[i].length() + 1).equals(filteredWords[i] + " "))
                        content = "<filtered> " + content.substring(filteredWords[i].length());
                    if (content.substring(content.length() - 1 - filteredWords[i].length()).equals(" " + filteredWords[i]))
                        content = content.substring(0, content.length() - filteredWords[i].length() - 1) + " <filtered>";
                }
                NotificationMessage notification = new NotificationMessage((byte) 0, sender.getUsername(), content + " " + ((PrivateMessage) message).getSendingDateAndTime());
                if (receiver.islogged())
                    connections.send(receiver.getConId(), notification);
                else
                    receiver.addWaitingMessage(notification);
                this.db.addMessage(0, content);
                connections.send(this.connectionId, new ACKMessage(6, ""));
            } else
                connections.send(this.connectionId, new ErrorMessage(6));
        }

        if (message instanceof LogStat) {
            User sender = this.db.getLoggedIn(this.connectionId);
            if (sender != null) {
                LinkedList<Short> ages = new LinkedList<>();
                LinkedList<Short> numPosts = new LinkedList<>();
                LinkedList<Short> numFollowers = new LinkedList<>();
                LinkedList<Short> numFollowing = new LinkedList<>();
                for (User user : this.db.getLoggedList()) {
                    if (!sender.isBlocked(user)) {
                        ages.push(user.getAge());
                        numPosts.push(user.getPostsNum());
                        numFollowers.push(user.getFollowersNum());
                        numFollowing.push(user.getFollowingNum());
                    }
                }
                LinkedList<Short> optional = new LinkedList<>();
                for (int i = 0; i < ages.size(); i++) {
                    optional.push(ages.get(i));
                    optional.push(numPosts.get(i));
                    optional.push(numFollowers.get(i));
                    optional.push(numFollowing.get(i));
                }
                connections.send(this.connectionId, new ACKMessage(7, optional));
            } else
                connections.send(this.connectionId, new ErrorMessage(7));
        }

        if (message instanceof Stat) {
            User sender = this.db.getLoggedIn(this.connectionId);
            if (sender != null) {
                LinkedList<Short> ages = new LinkedList<>();
                LinkedList<Short> numPosts = new LinkedList<>();
                LinkedList<Short> numFollowers = new LinkedList<>();
                LinkedList<Short> numFollowing = new LinkedList<>();
                for (String username : ((Stat) message).getUsernameList()) {
                    User user = this.db.findRegisteredUserByName(username);
                    if (user != null && !sender.isBlocked(user)) {
                        ages.push(user.getAge());
                        numPosts.push(user.getPostsNum());
                        numFollowers.push(user.getFollowersNum());
                        numFollowing.push(user.getFollowingNum());
                    }
                }
                LinkedList<Short> optional = new LinkedList<>();
                for (int i = 0; i < ages.size(); i++) {
                    optional.push(ages.get(i));
                    optional.push(numPosts.get(i));
                    optional.push(numFollowers.get(i));
                    optional.push(numFollowing.get(i));
                }
                connections.send(this.connectionId, new ACKMessage(8, optional));
            } else
                connections.send(this.connectionId, new ErrorMessage(8));
        }

        if (message instanceof BlockMessage) {
            User sender = this.db.getLoggedIn(this.connectionId);
            User blockedUser = this.db.findRegisteredUserByName(((BlockMessage) message).getUsername());
            if (sender != null & blockedUser != null) {
                sender.unfollow(blockedUser);
                blockedUser.removeFollower(sender);
                blockedUser.unfollow(sender);
                sender.removeFollower(blockedUser);
                sender.blockUser(blockedUser);
                blockedUser.blockUser(sender);
                connections.send(this.connectionId, new ACKMessage(12, ""));
            } else
                connections.send(this.connectionId, new ErrorMessage(12));
        }
    }
    /**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){
        return should_terminate;
    }

}
