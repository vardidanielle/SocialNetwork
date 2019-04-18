package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.*;
import bgu.spl.net.srv.Error;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Requests> {
    private Database database;
    private Connections<Requests> connections;
    private int connectionId;
    public boolean shouldTerminate;
    private Boolean loggedIn;


    public BidiMessagingProtocolImpl(Database database) {
        this.database = database;
        this.shouldTerminate = false;
        this.loggedIn = null;

    }


    @Override
    public void start(int connectionId, Connections connections) {
        this.connections=(ConnectionsImpl) connections;
        this.connectionId=connectionId;

    }

    @Override
    public void process(Requests message) { //extract the op code of the message and acts according to the
        short op =message.getOp() ;
        switch (op){
            case 1:
                registerRquest((RegisterRequest)message, op);
                break;
            case 2:
                loginRequest((LoginRequest)message,op);
                break;
            case 3:
                logoutRequest(op);
                break;
            case 4:
                followRequest((FollowUnfollowRequest)message,op);
                break;
            case 5:
                postRequest((PostRequest)message,op);
                break;
            case 6:
                pmRequest((PMRequest)message,op);
                break;
            case 7:
                userListRequest((UserlistRequest)message,op);
                break;
            case 8:
                statsRequest((StatsRequest)message,op);
                break;
            default:break;
        }
    }

    private void sendACK(short op){
        ACK ack=new ACK(intToShort(10),op);
        connections.send(connectionId,ack);
    }

    private void sendError(short op){
        Error error=new Error(intToShort(11),op);
        connections.send(connectionId, error);
    }

    private short intToShort(int integer){
        Integer theInteger = integer;
        return theInteger.shortValue();
    }


    private void hashToList(LinkedList list,ConcurrentHashMap<String,ClientInfo> hashMap){ //puts the user names from the list into an hash.
        for(String user: hashMap.keySet())
            list.add(user);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    private void registerRquest(RegisterRequest msg,short op){
        String username = msg.getUsername();
        String password = msg.getPassword();
        synchronized (database.getRegisterLock()) { //so that two clients wont be able to register under the same username
            if (registered(msg.getUsername()))
                sendError(op);
            else {
                database.getData().put(username, new ClientInfo(username, password)); //updates the hash that contains usernames and all the client info.
                sendACK(op);
            }
        }
    }

    private void loginRequest(LoginRequest msg,short op){
        synchronized (getLock(msg.getUsername())) { //if there is a username is the data structure we will lock on the client info, else we will lock on an object. made to
            if (!registered(msg.getUsername())) { //if not register
                sendError(op);
            }
            else {
                ClientInfo client = database.getData().get(msg.getUsername());
                if (!client.getPassword().equals(msg.getPassword())) //if password doesn't match to the one entered while registering.
                    sendError(op);
                else if (loggedIn ==null && !client.isLoggedIn()) { //if not already logged in
                    loggedIn =true;
                    database.getData().get(msg.getUsername()).setConnectionId(connectionId); //updates the clients connection id.
                    database.getLogin().put(connectionId, msg.getUsername()); //updates the hashmap of all the users that are logged in
                    database.getData().get(client.getUserName()).setLoggedIn(true);
                    sendACK(op);
                    while (!client.getNotice().isEmpty()) { //once the client is logged in, sends him all the messages that waits to be read.
                        Notifications notifications = client.getNotice().remove();
                        connections.send(connectionId, notifications);
                    }
                } else
                    sendError(op);
            }
        }

    }

    private void logoutRequest(short op){
        String user = database.getLogin().get(connectionId);
        synchronized (database.getPMLock()) { //so that the receiver wont be able to log out while he receives a post message.
            synchronized (database.getPostLock()) { // so that the receiver wont be able to log out while he receives a private message.
                if (!loggedIn())
                    sendError(op);
                else {
                    sendACK(op);
                    database.getData().get(user).setLoggedIn(false); //in this scope we updates this client to be logged out in all relevant databases.
                    database.getLogin().remove(connectionId);
                    connections.disconnect(connectionId);
                    shouldTerminate = true;
                }
            }
        }
    }

    private void followRequest(FollowUnfollowRequest msg, short op){
        if(!loggedIn())
            sendError(op);
        else {
            int counter = 0;
            LinkedList<String> successfulUsernames = new LinkedList<>();
            ClientInfo clientInfo = database.getData().get(database.getLogin().get(connectionId)); //extract the client's info.
            String thisUsername = database.getLogin().get(connectionId);
            synchronized (database.getData().get(database.getLogin().get(connectionId)).getFollowers()) { // so that no one would be able to add or remove themselfs from the followers list while a yser is sending a message to his followers.
                if (msg.getFollowUnfollow() == 1) //if the user wants to Unfollow other users.
                    for (String user : msg.getUserNames()) {
                        if (clientInfo.getFollowing().contains(user)) { //if this target user is on it's following list.
                            counter++;
                            database.getData().get(user).getFollowers().remove(thisUsername);//remove this client from the wanted user followers list
                            clientInfo.getFollowing().remove(user); //remove the wanted user from my following list
                            successfulUsernames.add(user);
                        }
                    }
                else //user wants to follow other users.
                    for (String user : msg.getUserNames())
                        if (!clientInfo.getFollowing().contains(user)) //if the target user is not on it's following list.
                            if (registered(user)) {
                                clientInfo.getFollowing().add(user);//add the wanted user to my following list
                                counter++;
                                database.getData().get(user).getFollowers().add(thisUsername); //add this client to the wanted user followers list
                                successfulUsernames.add(user);
                            }
            }
            if (counter == 0)
                sendError(op);
            else { //preparing an ack message by the message pattern and sends it.
                Integer numOfSuccessful = successfulUsernames.size();
                short num = numOfSuccessful.shortValue();
                ACKFollow ackFollow = new ACKFollow(intToShort(10), msg.getOp(), num, successfulUsernames);
                connections.send(connectionId, ackFollow);
                msg.getUserNames().clear();
            }
        }

    }

    private void postRequest(PostRequest msg, short op){
        if (!loggedIn())
            sendError(op);
        else {
            String username = database.getLogin().get(connectionId);
            String str = msg.getContent();
            ConcurrentHashMap<String,String> tagged = new ConcurrentHashMap<>();
            String temp = str;
            Notifications notifications = new Notifications(intToShort(9),(byte)1,username,str);
            int i = temp.indexOf("@");
            //in this scope we look for @ and separate the names into smaller strings.
            while(i!=-1){
                temp = temp.substring(i);
                int space = temp.indexOf(" ");
                if (space == -1){
                    tagged.put(temp.substring(1),temp.substring(1));
                    i=-1;
                }
                else {
                    tagged.put(temp.substring(1, space),temp.substring(1, space)) ;
                    temp = temp.substring(1);
                    i = temp.indexOf("@");
                }

            }
            synchronized (database.getPostLock()) {
                for (String user: tagged.values()){
                    if (registered(user)){ //user exists
                        if (!(database.getData().get(username).getFollowers().contains(user))) { //he is not a follower
                            //if the user just logged out
                            if (database.getData().get(user).isLoggedIn()) {//if the user is logged in
                                connections.send(database.getData().get(user).getConnectionId(), notifications); // send the notification to the uer
                            } else
                                database.getData().get(user).getNotice().add(notifications);
                        }
                    }
                }
            }
            synchronized (database.getData().get(username).getFollowers()) {// so that no one would be able to add or remove themselfs from the followers list while a yser is sending a message to his followers.
                for (String user : database.getData().get(username).getFollowers()){
                    if (database.getData().get(user).isLoggedIn())
                        connections.send(database.getData().get(user).getConnectionId(), notifications);
                    else
                        database.getData().get(user).getNotice().add(notifications);
                }
            }
            sendACK(op); //confirmation to the user
            database.getData().get(username).getPosts().add(msg); // adds to the post data structure of each client
        }
    }

    private void pmRequest(PMRequest msg, short op){
        String username = database.getLogin().get(connectionId);
        String content = msg.getContent();
        Notifications notifications = new Notifications(intToShort(9),(byte)0,username,content);
        if (!loggedIn()) //if the current user is not connected
            sendError(op);
        else if (!registered(msg.getUsername())) //if the user to send the message to,  is not even registered
            sendError(op);
        else{
            synchronized (database.getPMLock()) { //so that the receiver will be logged in or logged out through out the message sending
                if (database.getData().get(msg.getUsername()).isLoggedIn())
                    connections.send(database.getData().get(msg.getUsername()).getConnectionId(), notifications); // send the notification to the uer
                else
                    database.getData().get(msg.getUsername()).getNotice().add(notifications);
            }
            sendACK(op);
            database.getData().get(username).getPosts().add(msg);// adds to the post data structure of each client
        }
    }

    private void userListRequest(UserlistRequest msg, short op){
        if (!loggedIn())
            sendError(op);
        else{
            LinkedList<String> list = new LinkedList<>();
            hashToList(list,database.getData());
            ACKUserList ackUserList = new ACKUserList(intToShort(10),op,intToShort(list.size()),list);
            connections.send(connectionId,ackUserList);
        }
    }

    private void statsRequest(StatsRequest msg, short op){
        if (!loggedIn())
            sendError(op);
        else if (!registered(msg.getUsername()))
            sendError(op);
        else{
            String user = msg.getUsername();
            ClientInfo lookIntoUser = database.getData().get(user);
            ACKStats ackStats = new ACKStats(intToShort(10),op,intToShort(lookIntoUser.getPosts().size()),intToShort(lookIntoUser.getFollowers().size()),intToShort(lookIntoUser.getFollowing().size()));
            connections.send(connectionId,ackStats);
        }
    }

    private boolean loggedIn(){
        return database.getLogin().get(connectionId)!=null;
    }

    private boolean registered(String username){
        return database.getData().get(username) != null;
    }

    private Object getLock(String username){
        ClientInfo user = database.getData().get(username);
        if (user != null){
            database.setRegisterLock(user);
        }
        else
            database.setRegisterLock(new Object());
        return database.getRegisterLock();
    }
}
