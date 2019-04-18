package bgu.spl.net.srv;


import java.util.Arrays;
import java.util.LinkedList;

public class FollowUnfollowRequest extends Requests {
    private int followUnFollow;
    private short numOfUsers;
    private LinkedList<String> userNames;

    public FollowUnfollowRequest(){
        this.userNames = new LinkedList<>();
    }


    public FollowUnfollowRequest(short op, int followUnFollow,short numOfUsers, LinkedList<String> userNames){
        super(op);
        if (followUnFollow== 48 || followUnFollow ==0 )
            this.followUnFollow =0;
        else
            this.followUnFollow = 1;
        this.numOfUsers = numOfUsers;
        this.userNames = userNames;
    }

    public int getFollowUnfollow() {
        return followUnFollow;
    }


    public LinkedList<String> getUserNames() {
        return userNames;
    }

    public String toString() {
        String[] arr = new String[userNames.size()];
        int i = 0;
        for (String user : userNames){
            arr[i] = user;
            i++;
        }
        return "FOLLOW " + followUnFollow + " " + numOfUsers + " " + Arrays.toString(arr);
    }


}
