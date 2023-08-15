package bgu.spl.net.api;

import bgu.spl.net.api.Messages.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message>{
    private int counter = 0;
    private byte[] opcodeBytes = new byte[2];
    private int opcode;
    private int numOfWord = 0;
    private String username = "";
    private String password = "";
    private String birthDay = "";
    private String content = "";
    private Byte captcha = null;
    private Byte isFollowing = null;
    private String sendingDateAndTime = "";
    private List<String> usernameList;
    private Byte notificationType = null;
    private String postUser = "";
    private int messageOpcode = 0;
    private String optional = "";
    private String currentUsername = "";

    public MessageEncoderDecoderImpl(){
        this.opcode = 0;
        this.usernameList = new LinkedList<>();
    }

    public Message decodeNextByte(byte nextByte) {
        if (counter < 2) {
            opcodeBytes[counter] = nextByte;
            counter++;
            if (counter == 2) {
                this.opcode = (opcodeBytes[0] << 8 | opcodeBytes[1]);
                numOfWord++;
            }
        } else {
            if (opcode == 1) {
                if (new String(new byte[]{nextByte}).equals(";")) {
                    Message m = new Register(this.username, this.password, this.birthDay);
                    resetEncoderDecoder();
                    return m;
                }
                    if (nextByte == 0)
                        numOfWord++;
                    else if (numOfWord == 1)
                        this.username = this.username + new String(new byte[]{nextByte});
                    else if (numOfWord == 2)
                        this.password = this.password + new String(new byte[]{nextByte});
                    else if (numOfWord == 3)
                        this.birthDay = this.birthDay + new String(new byte[]{nextByte});
                }
                if (opcode == 2) {
                    if (new String(new byte[]{nextByte}).equals(";")){
                        Message m = new Login(this.username, this.password, this.captcha);
                        resetEncoderDecoder();
                        return m;
                    }
                    if (nextByte == 0)
                        numOfWord++;
                    else if (numOfWord == 1)
                        this.username = this.username + new String(new byte[]{nextByte});
                    else if (numOfWord == 2)
                        this.password = this.password + new String(new byte[]{nextByte});
                    else if (numOfWord == 3)
                        this.captcha = nextByte;
                }

                if (opcode == 3) {
                    Message m = new Logout();
                    resetEncoderDecoder();
                    return m;
                }

                if (opcode == 4) {
                    if (new String(new byte[]{nextByte}).equals(";")){
                        Message m = new Follow(this.isFollowing, this.username);
                        resetEncoderDecoder();
                        return m;
                    }

                    if (numOfWord == 1) {
                        this.isFollowing = nextByte;
                        numOfWord++;
                    } else if (nextByte != 0)
                        this.username = this.username + new String(new byte[]{nextByte});
                }

                if (opcode == 5) {
                    if (new String(new byte[]{nextByte}).equals(";")){
                        Message m = new Post(this.content);
                        resetEncoderDecoder();
                        return m;
                    }
                    if (nextByte != 0)
                        this.content = content + new String(new byte[]{nextByte});
                }

                if (opcode == 6) {
                    if (new String(new byte[]{nextByte}).equals(";")){
                        Message m = new PrivateMessage(this.username, this.content, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                        resetEncoderDecoder();
                        return m;
                    }
                    if (nextByte == 0)
                        numOfWord++;
                    else if (numOfWord == 1)
                        this.username = this.username + new String(new byte[]{nextByte});
                    else if (numOfWord == 2)
                        this.content = this.content + new String(new byte[]{nextByte});
//                    else if (numOfWord == 3)
//                        this.sendingDateAndTime = this.sendingDateAndTime + new String(new byte[]{nextByte});
                }

                if (opcode == 7) {
                    if (new String(new byte[]{nextByte}).equals(";")) {
                        Message m = new LogStat();
                        resetEncoderDecoder();
                        return m;
                    }
                }
                if (opcode == 8) {
                    if (new String(new byte[]{nextByte}).equals(";")){
                        this.usernameList.add(currentUsername);
                        Message m = new Stat(this.usernameList);
                        resetEncoderDecoder();
                        return m;
                    }
                    if (new String(new byte[]{nextByte}).equals("|")) {
                        this.usernameList.add(currentUsername);
                        currentUsername = "";
                    } else if (nextByte != 0)
                        currentUsername += new String(new byte[]{nextByte});
                }
                if (opcode == 9) {
                    if (new String(new byte[]{nextByte}).equals(";")){
                        Message m = new NotificationMessage(notificationType, postUser, content);
                        resetEncoderDecoder();
                        return m;
                    }
                    if (nextByte == 0 & numOfWord != 1)
                        numOfWord++;
                    if (numOfWord == 1) {
                        this.notificationType = nextByte;
                        numOfWord++;
                    }
                    if (numOfWord == 2)
                        this.postUser += new String(new byte[]{nextByte});
                    if (numOfWord == 3)
                        this.content += new String(new byte[]{nextByte});
                }

//                if (opcode == 10) {
//                    if (new String(new byte[]{nextByte}).equals(";")){
//                        Message m = new ACKMessage(messageOpcode, optional);
//                        resetEncoderDecoder();
//                        return m;
//                    }
//                    if (counter < 4) {
//                        this.opcodeBytes[counter - 2] = nextByte;
//                        counter++;
//                        if (counter == 4)
//                            this.messageOpcode = opcodeBytes[0] << 8 | opcodeBytes[1];
//                    } else {
//                        this.optional += new String(new byte[]{nextByte});
//                    }
//                }
//
//                if (opcode == 11) {
//                    if (new String(new byte[]{nextByte}).equals(";")){
//                        Message m = new ErrorMessage(messageOpcode);
//                        resetEncoderDecoder();
//                        return m;
//                    }
//                    this.opcodeBytes[counter - 2] = nextByte;
//                    counter++;
//                    if (counter == 4)
//                        this.messageOpcode = opcodeBytes[0] << 8 | opcodeBytes[1];
//                }
//
                if (opcode == 12) {
                    if (new String(new byte[]{nextByte}).equals(";")){
                        Message m = new BlockMessage(this.username);
                        resetEncoderDecoder();
                        return m;
                    }
                    if (nextByte != 0)
                        this.username = username + new String(new byte[]{nextByte});
                }
            }
        return null;
    }

    private void resetEncoderDecoder(){
        counter = 0;
        byte[] opcodeBytes = new byte[2];
        opcode = 0;
        numOfWord = 0;
        username = "";
        password = "";
        birthDay = "";
        content = "";
        captcha = null;
        isFollowing = null;
        sendingDateAndTime = "";
        usernameList = new LinkedList<>();
        notificationType = null;
        postUser = "";
        messageOpcode = 0;
        optional = "";
        currentUsername = "";
    }

    /**
     * encodes the given message to bytes array
     *
     * @param message the message to encode
     * @return the encoded bytes
     */
    public byte[] encode(Message message){
        byte[] bytes = null;
        if (message instanceof Register){
            int index = 2;
            byte[] username = ((Register) message).getUserName().getBytes(StandardCharsets.UTF_8);
            byte[] password = ((Register) message).getPassword().getBytes(StandardCharsets.UTF_8);
            byte[] birthday = ((Register) message).getBirthday().getBytes(StandardCharsets.UTF_8);
            bytes = new byte[username.length+password.length+birthday.length+5];
            bytes[0] = 1 >> 8;
            bytes[1] = 1;
            for (int i = 0; i<username.length; i++,index++)
                bytes[index] = username[i];
            bytes[index] = 0;
            index++;
            for (int i = 0; i<password.length; i++,index++)
                bytes[index] = password[i];
            bytes[index] = 0;
            index++;
            for (int i = 0; i<birthday.length; i++,index++)
                bytes[index] = birthday[i];
            bytes[index] = 0;
        }

        if (message instanceof Login){
            int index = 2;
            byte[] username = ((Login) message).getUsername().getBytes(StandardCharsets.UTF_8);
            byte[] password = ((Login) message).getPassword().getBytes(StandardCharsets.UTF_8);
            byte captcha = ((Login) message).getCaptcha();
            bytes = new byte[username.length+password.length + 5];
            bytes[0] = 2 >> 8;
            bytes[1] = 2;
            for (int i = 0; i<username.length; i++,index++)
                bytes[index] = username[i];
            bytes[index] = 0;
            index++;
            for (int i = 0; i<password.length; i++,index++)
                bytes[index] = password[i];
            bytes[index] = 0;
            index++;
            bytes[index] = captcha;
        }
        if (message instanceof Logout){
            bytes = new byte[2];
            bytes[0] = 3 >> 8;
            bytes[1] = 3;
        }
        if (message instanceof Follow){
            int index = 3;
            byte[] username = ((Follow) message).getUsername().getBytes(StandardCharsets.UTF_8);
            byte followUnfollow = ((Follow) message).getFollowUnfollow();
            bytes = new byte[username.length + 3];
            bytes[0] = 4 >> 8;
            bytes[1] = 4;
            bytes[2] = followUnfollow;
            for (int i = 0; i<username.length; i++,index++)
                bytes[index] = username[i];
        }
        if (message instanceof Post){
            int index = 2;
            byte[] content = ((Post) message).getContent().getBytes(StandardCharsets.UTF_8);
            bytes = new byte[content.length + 3];
            bytes[0] = 5 >> 8;
            bytes[1] = 5;
            for (int i = 0; i<content.length; i++,index++)
                bytes[index] = content[i];
            bytes[index] = 0;
        }
        if (message instanceof PrivateMessage){
            int index = 2;
            byte[] username = ((PrivateMessage) message).getUsername().getBytes(StandardCharsets.UTF_8);
            byte[] content = ((PrivateMessage) message).getContent().getBytes(StandardCharsets.UTF_8);
            byte[] time = ((PrivateMessage) message).getSendingDateAndTime().getBytes(StandardCharsets.UTF_8);
            bytes = new byte[username.length + content.length + time.length + 5];
            bytes[0] = 6 >> 8;
            bytes[1] = 6;
            for (int i = 0; i<username.length; i++,index++)
                bytes[index] = username[i];
            bytes[index] = 0;
            index++;
            for (int i = 0; i<content.length; i++,index++)
                bytes[index] = content[i];
            bytes[index] = 0;
            index++;
            for (int i = 0; i<time.length; i++,index++)
                bytes[index] = time[i];
            bytes[index] = 0;
        }
        if (message instanceof LogStat){
            bytes = new byte[2];
            bytes[0] = 7 >> 8;
            bytes[1] = 7;
        }
        if (message instanceof Stat){
            int index = 2;
            bytes = new byte[2];
            bytes[0] = 8 >> 8;
            bytes[1] = 8;
            LinkedList<String> usernamesList = (LinkedList<String>) ((Stat) message).getUsernameList();
            String usernamesString = "";
            for (int i = 0; i<usernamesList.size(); i++) {
                usernamesString += usernamesList.get(i);
                if (i != usernamesList.size())
                    usernamesString += '|';
            }
            byte[] usernames = usernamesString.getBytes(StandardCharsets.UTF_8);

            for (int i = 0; i<usernames.length; i++,index++)
                bytes[index] = usernames[i];
            bytes[index] = 0;

        }
        if (message instanceof NotificationMessage){
            int index = 3;
            byte notificationType = ((NotificationMessage) message).getNotificationType();
            byte[] postingUser = ((NotificationMessage) message).getPostUser().getBytes(StandardCharsets.UTF_8);
            byte[] content = ((NotificationMessage) message).getContent().getBytes(StandardCharsets.UTF_8);
            bytes = new byte[postingUser.length + content.length + 5];
            bytes[0] = 9 >> 8;
            bytes[1] = 9;
            bytes[2] = notificationType;
            for (int i = 0; i<postingUser.length; i++,index++)
                bytes[index] = postingUser[i];
            bytes[index] = 0;
            index++;
            for (int i = 0; i<content.length; i++,index++)
                bytes[index] = content[i];
            bytes[index] = 0;
        }

        if (message instanceof ACKMessage){
            int index = 4;
            LinkedList<Short> optional = ((ACKMessage) message).getOptional();
            String followUsername = ((ACKMessage) message).getFollowUsername();
            int mOpcode = ((ACKMessage) message).getOpcode();
            if (optional != null) {
                bytes = new byte[optional.size() * 2 + 4];
                bytes[0] = 10 >> 8;
                bytes[1] = 10;
                bytes[2] = (byte) (mOpcode >> 8);
                bytes[3] = (byte) (mOpcode);
                for (int i = 0; i < optional.size(); i++, index += 2) {
                    bytes[index] = (byte) (optional.get(i) << 8);
                    bytes[index + 1] = (byte) (optional.get(i) << 0);
                }
            }
            else
            {
                bytes = new byte[followUsername.length() + 4];
                bytes[0] = 10 >> 8;
                bytes[1] = 10;
                bytes[2] = (byte) (mOpcode >> 8);
                bytes[3] = (byte) (mOpcode);
                for (int i = 0; i < followUsername.length(); i++, index ++) {
                    bytes[index] = (byte) followUsername.charAt(i);
                }
            }
        }

        if (message instanceof ErrorMessage){
            bytes = new byte[4];
            bytes[0] = 11 >> 8;
            bytes[1] = 11;
            int mOpcode = ((ErrorMessage) message).getMessageOpcode();
            bytes[2] = (byte)(mOpcode >> 8);
            bytes[3] = (byte)(mOpcode);
        }

        if (message instanceof BlockMessage){
            int index = 2;
            byte[] username = ((BlockMessage) message).getUsername().getBytes(StandardCharsets.UTF_8);
            bytes = new byte[username.length + 3];
            bytes[0] = 12 >> 8;
            bytes[1] = 12;
            for (int i = 0; i<username.length; i++,index++)
                bytes[index] = username[i];
            bytes[index] = 0;
        }
        byte[] newBytes = new byte[bytes.length+1];
        for (int i = 0; i<bytes.length; i++)
            newBytes[i] = bytes[i];
        newBytes[bytes.length] = ';';
        return newBytes;
    }
}
