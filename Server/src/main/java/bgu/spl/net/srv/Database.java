package bgu.spl.net.srv;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Database {
    private ConcurrentHashMap<String,ClientInfo> data ; // String represents the username.
    private ConcurrentHashMap<Integer,String> login ;
    private Object PMLock;
    private Object PostLock;
    private Object LogoutLock;
    private Object RegisterLock;


//holds all of the networks clients data.
    public Database() {
        data= new ConcurrentHashMap<>();
        login= new ConcurrentHashMap<>();
        PMLock = new Object();
        PostLock = new Object();
        LogoutLock = new Object();
        RegisterLock = new Object();
    }

    public ConcurrentHashMap<String, ClientInfo> getData() {
        return data;
    }

    public ConcurrentHashMap<Integer, String> getLogin() {
        return login;
    }

    public Object getPMLock() {
        return PMLock;
    }

    public Object getPostLock() {
        return PostLock;
    }

    public Object getLogoutLock() {
        return LogoutLock;
    }

    public Object getRegisterLock() {
        return RegisterLock;
    }

    public void setRegisterLock(Object registerLock) {
        RegisterLock = registerLock;
    }
}
