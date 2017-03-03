package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/28.
 */
public class BaseUserInform {
    private String message = "";
    private ArrayList<UserInform> result = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<UserInform> getResult() {
        return result;
    }

    public void setResult(ArrayList<UserInform> result) {
        this.result = result;
    }
}
