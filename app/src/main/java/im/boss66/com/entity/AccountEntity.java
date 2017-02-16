package im.boss66.com.entity;

/**
 * Created by Johnny on 2017/1/17.
 */
public class AccountEntity {
    private String user_id = "";
    private String user_name = "";
    private String mobile_phone = "";
    private String lastlogin_time = "";
    private String access_token = "";

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

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public String getLastlogin_time() {
        return lastlogin_time;
    }

    public void setLastlogin_time(String lastlogin_time) {
        this.lastlogin_time = lastlogin_time;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
