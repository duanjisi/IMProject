package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/20.
 */
public class BaseEmoStore {
    private String message = "";
    private ArrayList<EmoStore> result = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<EmoStore> getResult() {
        return result;
    }

    public void setResult(ArrayList<EmoStore> result) {
        this.result = result;
    }
}
