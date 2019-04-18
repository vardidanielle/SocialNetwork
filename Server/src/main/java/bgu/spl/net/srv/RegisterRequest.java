package bgu.spl.net.srv;

public class RegisterRequest extends Requests{
    private String username;
    private String password;

    public RegisterRequest(){}

    public RegisterRequest(short op, String str1,String str2){
        super(op);
        username = str1;
        password = str2;
    }

    public String toString() {
        return "REGISTER " + username + " " + password;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
