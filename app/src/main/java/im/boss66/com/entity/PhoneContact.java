package im.boss66.com.entity;

/**
 * Created by Johnny on 2017/2/27.
 */
public class PhoneContact {
    private String user_id = "";
    private String user_name = "";
    private String avatar = "";
    private String is_friends = "";//1;已添加为好友，0；未添加为好友
    private String mobile_phone = "";

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

    public String getIs_friends() {
        return is_friends;
    }

    public void setIs_friends(String is_friends) {
        this.is_friends = is_friends;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }
}
