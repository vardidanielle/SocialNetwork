package bgu.spl.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class ACKFollow extends Requests {
    private short followOpcode;
    private short numOfUsers;
    private LinkedList<String> userNameList;

    public ACKFollow(){}

    public ACKFollow(short op, short followOpcode, short numOfUsers, LinkedList<String> userNameList) {
        super(op);
        this.followOpcode = followOpcode;
        this.numOfUsers = numOfUsers;
        this.userNameList = userNameList;
    }

    public ACKFollow(short op){super(op);}

    public byte[] getByteArray(){
        String userListString = getStringFromList(); // turning the list into one long string with spaces.
        byte[] byteArr = userListString.getBytes(StandardCharsets.UTF_8); // get a byte array from the string.
        byte[] bytes = new byte[6+byteArr.length];
        bytes[0] = (byte)((getOp() >> 8) & 0xFF);//first two bytes- opcode
        bytes[1] = (byte)(getOp() & 0xFF);//first two bytes- opcode
        bytes[2] = (byte)((followOpcode >> 8) & 0xFF); //first two bytes- opcode
        bytes[3] = (byte)(followOpcode & 0xFF);
        bytes[4] = (byte)((numOfUsers >> 8) & 0xFF);
        bytes[5] = (byte)(numOfUsers & 0xFF);
        for (int i =6; i<bytes.length;i++)
            bytes[i] = byteArr[i - 6];
        return bytes;
    }

    private String getStringFromList(){
        String str="";
        for (String user: userNameList){
            str = str + user + "\0";
        }
        return str;
    }

}
