package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/3/15.
 */
public class BaseChildren {
    private String message = "";
    private ArrayList<ChildEntity> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ChildEntity> getData() {
        return data;
    }

    public void setData(ArrayList<ChildEntity> data) {
        this.data = data;
    }
}
