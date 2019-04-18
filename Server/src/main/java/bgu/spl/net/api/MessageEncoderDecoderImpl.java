package bgu.spl.net.api;

import bgu.spl.net.srv.*;
import bgu.spl.net.srv.Error;

import javax.jws.soap.SOAPBinding;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Requests> {
    private short op;
    private int byteCounter=0;
    private Requests msgClass;
    private boolean done = false;
    private int decoderType;

    //FOLLOW/UNFOLLOW fields
    private byte[] numOfUsersByteArray= new byte[2];
    private LinkedList<String> userNames = new LinkedList<>();
    private int followUnFollow;
    private int numOfUsers =-1;

    //LOGIN/REGISTER/PM/Stat/post fields
    private int count = 0;
    private String string1 = "";
    private String string2 = "";
    private byte[] byteArray = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private byte[] byteArray2 = new byte[2];

    @Override
    public Requests decodeNextByte(byte nextByte) {
        if (byteCounter == 0) {
            byteArray2[0] = nextByte;
        }
        else if (byteCounter == 1) {
            byteArray2[1] = nextByte;
            op = bytesToShort(byteArray2);
            messageType(op);
        }
        else if (byteCounter > 1) {
            switch (decoderType) {
                case 1:
                    byte[] arr = new byte[1];
                    arr[0] = nextByte;
                    decode1(nextByte);
                    break;
                case 2:
                    decode2(nextByte);
                    break;
                case 3:
                    decode3(nextByte);
                    break;
                default: return null;
            }
        }
        byteCounter++;
        if (done) {
            string1="";
            string2="";
            count=0;
            byteCounter = 0;
            byteArray2 = new byte[2];
            done = false;
            numOfUsers = -1;
            numOfUsersByteArray= new byte[2];
            return msgClass;
        }
        return null;
    }

    // @Override
    public byte[] encode(Requests message) {
        if (message instanceof Error)
            return ((bgu.spl.net.srv.Error) message).getByteArray();
        else if(message instanceof Notifications)
            return ((Notifications) message).getByteArray();
        else if(message instanceof ACKUserList)
            return ((ACKUserList) message).getByteArray();
        else if(message instanceof ACK)
            return ((ACK) message).getByteArray();
        else if(message instanceof ACKStats)
            return ((ACKStats) message).getByteArray();
        else if(message instanceof ACKFollow)
            return ((ACKFollow) message).getByteArray();
        return null;
    }

    //classifies which type of a message is it.

    private void messageType(short result){
        switch (result){
            case 1:
                msgClass = new RegisterRequest();
                decoderType = 1;
                break;
            case 2:
                msgClass = new LoginRequest();
                decoderType =1;
                break;
            case 3:
                msgClass = new LogoutRequest(op);
                done = true;
                break;
            case 4:
                msgClass = new FollowUnfollowRequest();
                decoderType = 3;
                break;
            case 5:
                msgClass = new PostRequest();
                decoderType = 2;
                break;
            case 6:
                msgClass = new PMRequest();
                decoderType = 1;
                break;
            case 7:
                msgClass = new UserlistRequest(op);
                done =true;
                break;
            case 8:
                msgClass = new StatsRequest();
                decoderType = 2;
                break;
            default:
                msgClass = null;
                break;
        }
    }

    //decodes by the pattern of the message.
    public void decode1(byte b){
        if (b!=0x00) {
            pushByte(b);
        }
        else {
            if (count==0) {
                string1 = popString();
            }
            else {
                string2 = popString();
            }
            count++;
        }

        if (count == 2) {
            if (msgClass instanceof RegisterRequest)
                msgClass = new RegisterRequest(op,string1,string2);
            else if (msgClass instanceof LoginRequest)
                msgClass = new LoginRequest(op,string1,string2);
            else
                msgClass = new PMRequest(op,string1,string2);
            done = true;
        }
    }

    public void decode2(byte b){
        if (b != 0x00) {
            pushByte(b);
        }
        else {
            string1 = popString();
            if (msgClass instanceof StatsRequest)
                msgClass = new StatsRequest(op,string1);
            else
                msgClass = new PostRequest(op,string1);
            done = true;
        }
    }

    public void decode3(byte b){
        if (byteCounter == 2)
            followUnFollow = b & 0xFF;

        else if(byteCounter == 3)
            numOfUsersByteArray[0] = b;

        else if(byteCounter == 4){
            numOfUsersByteArray[1] = b;
            numOfUsers = (int)bytesToShort(numOfUsersByteArray);
        }

        if (numOfUsers != -1 && byteCounter > 4){
            if (b != '\0') {
                pushByte(b);
            }
            else {
                userNames.add(popString());
                count++;
            }
        }

        if (count == numOfUsers) {
            msgClass = new FollowUnfollowRequest(op,followUnFollow,bytesToShort(numOfUsersByteArray),userNames);
            done = true;
        }
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    private void pushByte(byte nextByte) {
        if (len >= byteArray.length && nextByte!=0) {
            byteArray = Arrays.copyOf(byteArray, len * 2);
        }

        byteArray[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(byteArray, 0, len, StandardCharsets.UTF_8);
        len = 0;
        byteArray = new byte[1<<10];
        return result;
    }
}
