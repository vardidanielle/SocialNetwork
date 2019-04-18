package bgu.spl.net.srv;

public class Error extends Requests{
    private short messageOpcode;

    public Error(){}

    public Error(short op, short messageOpcode){
        super(op);
        this.messageOpcode = messageOpcode;
    }

    public byte[] getByteArray(){
        byte[] bytes = new byte[4];
        bytes[0] = (byte)((getOp() >> 8) & 0xFF);
        bytes[1] = (byte)(getOp() & 0xFF);
        bytes[2] = (byte)((messageOpcode >> 8) & 0xFF);
        bytes[3] = (byte)(messageOpcode & 0xFF);
        return bytes;
    }

}
