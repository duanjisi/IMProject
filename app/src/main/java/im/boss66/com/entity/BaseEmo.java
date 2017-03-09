package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/3/7.
 */
public class BaseEmo {
    private String message = "";
    private ArrayList<EmoEntity> result = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<EmoEntity> getResult() {
        return result;
    }

    public void setResult(ArrayList<EmoEntity> result) {
        this.result = result;
    }
}
