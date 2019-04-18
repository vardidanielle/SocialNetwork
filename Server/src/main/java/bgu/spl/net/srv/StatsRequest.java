package bgu.spl.net.srv;

public class StatsRequest extends Requests{
    private String username;

    public StatsRequest(){}

    public StatsRequest(short op, String string){
        super(op);
        this.username = string;
    }

    public String getUsername() {
        return username;
    }

    public String toString() {
        return "STAT " + username;
    }
}
