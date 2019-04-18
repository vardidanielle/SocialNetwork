package bgu.spl.net.srv;

import java.io.IOException;

public class ACKStats extends Requests {
    private short statOpcode;
    private short numOfPostes;
    private short numOfFollowers;
    private short numOfFollowing;


    public ACKStats(){}

    public ACKStats(short op,short statOpcode,short numOfPostes,short numOfFollowers,short numOfFollowing){
        super(op);
        this.statOpcode=statOpcode;
        this.numOfPostes=numOfPostes;
        this.numOfFollowers=numOfFollowers;
        this.numOfFollowing=numOfFollowing;
    }

    public byte[] getByteArray(){
        byte[] bytes = new byte[10];
        bytes[0] = (byte)((getOp() >> 8) & 0xFF);
        bytes[1] = (byte)(getOp() & 0xFF);
        bytes[2] = (byte)((statOpcode >> 8) & 0xFF);
        bytes[3] = (byte)(statOpcode & 0xFF);
        bytes[4] = (byte)((numOfPostes >> 8) & 0xFF);
        bytes[5] = (byte)(numOfPostes & 0xFF);
        bytes[6] = (byte)((numOfFollowers >> 8) & 0xFF);
        bytes[7] = (byte)(numOfFollowers & 0xFF);
        bytes[8] = (byte)((numOfFollowing >> 8) & 0xFF);
        bytes[9] = (byte)(numOfFollowing & 0xFF);
        return bytes;
    }
}