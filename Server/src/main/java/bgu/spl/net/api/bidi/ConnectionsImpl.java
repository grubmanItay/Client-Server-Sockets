package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T>{
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connections;

    public ConnectionsImpl() {
        this.connections = new ConcurrentHashMap<>();
    }

    public void addConnection(int currentId,ConnectionHandler<T> connectionHandler) {
        synchronized (connections) {
            if (!this.connections.values().contains(connectionHandler)) {
                this.connections.put(currentId, connectionHandler);
            }
        }
    }

    public boolean send(int conId, T msg) {
        synchronized (connections) {
            ConnectionHandler<T> sender = this.connections.get(conId);
            if (sender == null) {
                return false;
            } else {
                sender.send(msg);
                return true;
            }
        }
    }

    public void broadcast(T msg){

    }

    public void disconnect(int connectionId) {
        synchronized (connections) {
            this.connections.remove(connectionId);
        }
    }
}
