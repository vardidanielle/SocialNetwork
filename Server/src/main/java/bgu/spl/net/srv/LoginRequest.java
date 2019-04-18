package bgu.spl.net.srv;

public class LoginRequest extends Requests {
    private String username;
    private String password;

    public LoginRequest(){}

    public LoginRequest(short op, String str1,String str2){
        super(op);
        username = str1;
        password = str2;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String toString() {
        return "LOGIN " + username + " " + password;
    }
}
