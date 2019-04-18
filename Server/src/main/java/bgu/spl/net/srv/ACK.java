package bgu.spl.net.srv;


public class ACK extends Requests {

    private short numOfAction;


    public ACK(){}

    public ACK(short op,short numOfAction){
        super(op);
        this.numOfAction=numOfAction;
    }

    public byte[] getByteArray() {
        byte[] bytes = new byte[4];
        bytes[0] = (byte)((getOp() >> 8) & 0xFF); //first two bytes- opcode
        bytes[1] = (byte)(getOp() & 0xFF); //first two bytes- opcode
        bytes[2] = (byte)((numOfAction >> 8) & 0xFF); //next two bytes- for what action do we send this ACK to.
        bytes[3] = (byte)(numOfAction & 0xFF);

        return bytes;
    }
}
