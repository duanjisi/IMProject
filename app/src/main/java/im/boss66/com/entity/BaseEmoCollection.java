package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/20.
 * 表情收藏实体类
 */
public class BaseEmoCollection {
    private String message = "";
    private ArrayList<EmoLove> result = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<EmoLove> getResult() {
        return result;
    }

    public void setResult(ArrayList<EmoLove> result) {
        this.result = result;
    }
}
