package bgu.spl.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class ACKUserList extends Requests {
    private short userListOpcode;
    private short numOfUsers;
    private LinkedList<String> userNameList;

    public ACKUserList(){}

    public ACKUserList(short op, short actionOpcode, short numOfUsers, LinkedList<String> list){
        super(op);
        this.userListOpcode = actionOpcode;
        this.numOfUsers = numOfUsers;
        this.userNameList = list;
    }

    public byte[] getByteArray(){
        String userListString = getStringFromList();
        byte[] byteArr = userListString.getBytes(StandardCharsets.UTF_8);
        byte[] bytes = new byte[6+byteArr.length];
        bytes[0] = (byte)((getOp() >> 8) & 0xFF);
        bytes[1] = (byte)(getOp() & 0xFF);
        bytes[2] = (byte)((userListOpcode >> 8) & 0xFF);
        bytes[3] = (byte)(userListOpcode & 0xFF);
        bytes[4] = (byte)((numOfUsers >> 8) & 0xFF);
        bytes[5] = (byte)(numOfUsers & 0xFF);
        for (int i =6; i<bytes.length;i++)
            bytes[i] = byteArr[i - 6];
        return bytes;
    }

    private String getStringFromList(){
        String str="";
        for (String user: userNameList){
            str = str + user;
            str = str+'\0';
        }
        return str;
    }
}
