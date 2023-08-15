package bgu.spl.net.api.Messages;

import java.util.LinkedList;

public class ACKMessage implements Message{
    int opcode;
    LinkedList<Short> optional;
    String followUsername;

    public ACKMessage(int opcode, LinkedList<Short> optional) {
        this.opcode = opcode;
        this.optional = optional;
    }

    public ACKMessage(int opcode, String s) {
        this.opcode = opcode;
        this.followUsername = s;
    }

    public int getOpcode() {
        return opcode;
    }

    public LinkedList<Short> getOptional() {
        return optional;
    }

    public String getFollowUsername() {
        return followUsername;
    }
}
