package bgu.spl.net.api.bidi;

import java.io.IOException;

public interface Connections<T> { //uses the send function from connectionHandler.

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);
}