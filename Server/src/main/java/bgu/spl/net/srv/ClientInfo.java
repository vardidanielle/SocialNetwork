package bgu.spl.net.srv;

import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientInfo {
    private String userName;
    private String password;
    private ConcurrentLinkedQueue<String> following;
    private ConcurrentLinkedQueue<String> followers;
    private ConcurrentLinkedQueue<Notifications> notice;
    private ConcurrentLinkedQueue<Requests> posts;
    private final Object clientLock;
    private boolean loggedIn;
    private Integer connectionId = null;

    public ClientInfo(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.following = new ConcurrentLinkedQueue<>();
        this.followers = new ConcurrentLinkedQueue<>();
        this.notice=new ConcurrentLinkedQueue<>(); //each client has a queue of messages waiting to be read once he will login.
        this.posts = new ConcurrentLinkedQueue<>();
        this.clientLock = new Object();
        loggedIn = false;
    }

    //each client has it's own database that contains all it's information.

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }


    public ConcurrentLinkedQueue<String> getFollowing() {
        return following;
    }


    public ConcurrentLinkedQueue<String> getFollowers() {
        return followers;
    }


    public ConcurrentLinkedQueue<Notifications> getNotice() {
        return notice;
    }

    public ConcurrentLinkedQueue<Requests> getPosts() {
        return posts;
    }

    public Object getClientLock() {
        return clientLock;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }
}
