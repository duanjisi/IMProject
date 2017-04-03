package im.boss66.com.entity;

import java.io.Serializable;

/**
 * Created by GMARUnity on 2017/2/17.
 */
public class NearByChildEntity implements Serializable {

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    private String user_id;
    private String user_name;
    private String avatar;
    private int sex;
    private int distance;

}
