package bgu.spl.net.srv;

import java.nio.charset.StandardCharsets;

public class Notifications extends Requests{
    private byte notificationType;
    private String postingUser;
    private String content;


    public Notifications(short op,byte type, String postingUser, String content){
        super(op);
        this.notificationType=type;
        this.postingUser=postingUser;
        this.content=content;

    }


    public byte[] getByteArray(){
        byte[] postingUserByteArray = getByteArrayFromString(postingUser);
        byte[] contentByteArray = getByteArrayFromString(content);

        byte[] bytes = new byte[5+postingUserByteArray.length+contentByteArray.length];
        bytes[0] = (byte)((getOp() >> 8) & 0xFF);
        bytes[1] = (byte)(getOp() & 0xFF);
        bytes[2] = notificationType;
        for (int i = 0; i<postingUserByteArray.length;i++)
            bytes[i+3] = postingUserByteArray[i];
        bytes[postingUserByteArray.length+3] = (byte) '\0';
        for (int i=0 ; i<contentByteArray.length;i++) {
            bytes[i + postingUserByteArray.length + 4] = contentByteArray[i];
        }
        bytes[bytes.length-1] = (byte) '\0';
        return bytes;
    }

    private byte[] getByteArrayFromString(String str){
        return str.getBytes(StandardCharsets.UTF_8);
    }
}
