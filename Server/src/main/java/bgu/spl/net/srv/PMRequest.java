package bgu.spl.net.srv;

public class PMRequest extends Requests{
    private String username;
    private String content;

    public PMRequest(){}

    public PMRequest(short op, String str1,String str2){
        super(op);
        username = str1;
        content = str2;
    }

    public String toString() {
        return "PM "+username+" "+content;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}
