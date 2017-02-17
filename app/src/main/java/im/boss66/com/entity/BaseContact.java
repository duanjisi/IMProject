package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/16.
 */
public class BaseContact {

    private String message = "";

    private ArrayList<ContactEntity> result = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ContactEntity> getResult() {
        return result;
    }

    public void setResult(ArrayList<ContactEntity> result) {
        this.result = result;
    }
}
