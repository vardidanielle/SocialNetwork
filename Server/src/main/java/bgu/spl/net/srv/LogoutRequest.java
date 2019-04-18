package bgu.spl.net.srv;

public class LogoutRequest extends Requests{


    public LogoutRequest(){}

    public String toString() {
        return "LOGOUT";
    }

    public LogoutRequest(short op){
        super(op);
    }
}
