package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
@SuppressWarnings("unchecked")
public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler> hash;

    public ConnectionsImpl(){
        hash=new ConcurrentHashMap<>();
    }
    @Override
    public boolean send(int connectionId, T msg) {
        ConnectionHandler connect= hash.get(connectionId);
        if(connect!=null) {
            connect.send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for (Entry<Integer,ConnectionHandler> element:hash.entrySet()) {
            send(element.getKey(),msg);
        }

    }

    @Override
    public void disconnect(int connectionId) {
        ConnectionHandler removed= hash.remove(connectionId);
    }

    public ConcurrentHashMap<Integer, ConnectionHandler> getHash() {
        return hash;
    }


    public void addConnectionHandler(int id,ConnectionHandler handler){
        hash.put(id,handler);
    }

}
