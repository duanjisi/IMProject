package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/27.
 */
public class BasePhoneContact {
    private String message = "";

    private ArrayList<PhoneContact> result = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<PhoneContact> getResult() {
        return result;
    }

    public void setResult(ArrayList<PhoneContact> result) {
        this.result = result;
    }
}
