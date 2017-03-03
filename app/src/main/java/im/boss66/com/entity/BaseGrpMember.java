package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/28.
 */
public class BaseGrpMember {
    private String message = "";
    private ArrayList<GroupEntity> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<GroupEntity> getData() {
        return data;
    }

    public void setData(ArrayList<GroupEntity> data) {
        this.data = data;
    }
}
