package bgu.spl.net.srv;


public class PostRequest extends Requests{
    private String content;

    public PostRequest(){}

    public PostRequest(short op, String content){
        super(op);
        this.content=content;
    }

    public String toString() {
        return ("POST " + content);
    }

    public String getContent() {
        return content;
    }
}
