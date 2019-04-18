package bgu.spl.net.srv;

public abstract class Requests {
    private short op;

    public Requests(){}

    public Requests(short op){
         this.op=op;
     }

    public short getOp() {
        return op;
    }
}
