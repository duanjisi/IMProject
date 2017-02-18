package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/17.
 */
public class BaseFriends {
    private String message = "";
    private ArrayList<NewFriend> result = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<NewFriend> getResult() {
        return result;
    }

    public void setResult(ArrayList<NewFriend> result) {
        this.result = result;
    }
}
