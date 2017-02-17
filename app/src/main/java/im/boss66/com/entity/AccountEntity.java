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
    private String avatar = "";
    private String sex = "";
    private String signature = "";
    private String province = "";
    private String city = "";
    private String district = "";
    private String district_str = "";

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDistrict_str() {
        return district_str;
    }

    public void setDistrict_str(String district_str) {
        this.district_str = district_str;
    }
}
