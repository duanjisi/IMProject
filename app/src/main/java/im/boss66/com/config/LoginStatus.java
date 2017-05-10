package im.boss66.com.config;

import android.content.Context;
import android.content.SharedPreferences;

import im.boss66.com.App;
import im.boss66.com.entity.AccountEntity;

/**
 * Created by Johnny on 2016/7/5.
 */
public class LoginStatus {
    public static final String FILE_NAME = "login_status.xml";
    private static final String UID = "uid";
    private static final String TOKEN = "token";
    private static final String NAME = "name";
    private static final String LAST_TIME = "last_time";
    private static final String MOBILE_PHONE = "mobile_phone";
    private static final String HUAN_XIN_ID = "huanxin_id";
    private static final String HUAN_XIN_PSW = "huanxin_psw";
    private static final String AVATAR = "avatar";
    private static final String QR_CODE_FRIEND_IM = "qrcode_friend_img";
    private static final String PROVINCE_NAME = "province_name";
    private static final String CITY_NAME = "city_name";
    private static final String BIRTHDAY = "birthday";
    private static final String DISTRICT_NAME = "district_name";
    private static final String BIRTHDAY_STR = "birthday_str";
    private static final String SEX = "sex";
    private static final String SIGNATURE = "signature";
    private static final String PROVINCE = "province";
    private static final String CITY = "city";
    private static final String DISTRICT = "district";
    private static final String DISTRICT_STR = "district_str";

    private static final String BG_IMG = "bgimg";
    private static final String SEX_STR = "sex_str";
    private static final String LOGIN_SUPPLIER_ID = "login_supplier_id";
    private static final String LOGIN_SUPPLIER_NAME = "login_supplier_name";
    private static final String LOGIN_SUPPLIER_LOGO = "login_supplier_logo";
    private static final String LOGIN_SUPPLIER_IMG = "login_supplier_img";
    private static final String LOGIN_SUPPLIER_QRCODE = "login_supplier_qrcode";
    private static final String ISSET_PAY_PASSWORD = "isset_paypassword";
    private static final String MEMBER_RANK = "member_rank";
    private static final String XINGZUO = "xingzuo";
    private static final String LOGIN_STATE = "login_state";
    private String cover_pic;
    private static LoginStatus sLoginStatus;
    private SharedPreferences mPreferences;
    private String school;

    private LoginStatus() {
        mPreferences = App.getInstance().getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static LoginStatus getInstance() {
        if (sLoginStatus == null) {
            sLoginStatus = new LoginStatus();
        }
        return sLoginStatus;
    }

    public void login(AccountEntity account, boolean isThirdParty) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(UID, account.getUser_id());
        editor.putString(TOKEN, account.getAccess_token());
        editor.putString(NAME, account.getUser_name());
        editor.putString(LAST_TIME, account.getLastlogin_time());
        editor.putString(MOBILE_PHONE, account.getMobile_phone());
        editor.putString(AVATAR, account.getAvatar());
        editor.putString(SEX, account.getSex());
        editor.putBoolean(LOGIN_STATE, true);
        editor.putString(SIGNATURE, account.getSignature());
        editor.putString(PROVINCE, account.getProvince());
        editor.putString(CITY, account.getCity());
        editor.putString(DISTRICT, account.getDistrict());
        editor.putString(DISTRICT_STR, account.getDistrict_str());
        editor.putString("cover_pic", account.getCover_pic());
        editor.putString("school", account.getSchool());
        editor.apply();
    }

    public void logout() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(LOGIN_STATE, false);
        editor.apply();
    }

    public void setUser_name(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(NAME, name);
        editor.apply();
    }

    public void setBg_image(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(BG_IMG, name);
        editor.apply();
    }

    public void setAvatar(String avatar) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(AVATAR, avatar);
        editor.apply();
    }


    public void setSex_str(String sex) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SEX, sex);
        editor.apply();
    }

    public void setBirthday_str(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(BIRTHDAY_STR, name);
        editor.apply();
    }

    public void setXingzuo(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(XINGZUO, name);
        editor.apply();
    }

    public void setLogin_supplier_id(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(LOGIN_SUPPLIER_ID, name);
        editor.apply();
    }

    public void setLogin_supplier_name(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(LOGIN_SUPPLIER_NAME, name);
        editor.apply();
    }

    public void setLogin_supplier_img(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(LOGIN_SUPPLIER_IMG, name);
        editor.apply();
    }

    public void setLogin_supplier_qrcode(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(LOGIN_SUPPLIER_QRCODE, name);
        editor.apply();
    }

    public void setIsset_pay_password(String state) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(ISSET_PAY_PASSWORD, state);
        editor.apply();
    }

    public void setLogin_supplier_logo(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(LOGIN_SUPPLIER_LOGO, name);
        editor.apply();
    }

    public void setLastTime(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(LAST_TIME, name);
        editor.apply();
    }

    public void setMobilePhone(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(MOBILE_PHONE, name);
        editor.apply();
    }

    public void setSignature(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(SIGNATURE, name);
        editor.apply();
    }

    public void setProvince(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PROVINCE, name);
        editor.apply();
    }

    public void setCity(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(CITY, name);
        editor.apply();
    }

    public void setDistrict(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(DISTRICT, name);
        editor.apply();
    }

    public void setDistrict_str(String name) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(DISTRICT_STR, name);
        editor.apply();
    }

    public String getSignature() {
        return mPreferences.getString(SIGNATURE, "");
    }

    public String getProvince() {
        return mPreferences.getString(PROVINCE, "");
    }

    public String getCity() {
        return mPreferences.getString(CITY, "");
    }

    public String getDistrict() {
        return mPreferences.getString(DISTRICT, "");
    }

    public String getDistrict_str() {
        return mPreferences.getString(DISTRICT_STR, "");
    }

    public String getLastTime() {
        return mPreferences.getString(LAST_TIME, "");
    }

    public String getMobilePhone() {
        return mPreferences.getString(MOBILE_PHONE, "");
    }

    public String getXingzuo() {
        return mPreferences.getString(XINGZUO, "");
    }

    public String getUser_id() {
        return mPreferences.getString(UID, "");
    }

    public String getToken() {
        return mPreferences.getString(TOKEN, "");
    }

    public String getUser_name() {
        return mPreferences.getString(NAME, "");
    }

    public String getIsset_pay_password() {
        return mPreferences.getString(ISSET_PAY_PASSWORD, "");
    }

    public String getHuanxin_id() {
        return mPreferences.getString(HUAN_XIN_ID, "");
    }

    public String getHuanxin_psw() {
        return mPreferences.getString(HUAN_XIN_PSW, "");
    }

    public String getAvatar() {
        return mPreferences.getString(AVATAR, "");
    }

    public String getQrcode_friend_img() {
        return mPreferences.getString(QR_CODE_FRIEND_IM, "");
    }

    public String getProvince_name() {
        return mPreferences.getString(PROVINCE_NAME, "");
    }

    public String getCity_name() {
        return mPreferences.getString(CITY_NAME, "");
    }

    public String getBirthday() {
        return mPreferences.getString(BIRTHDAY, "");
    }

    public String getDistrict_name() {
        return mPreferences.getString(DISTRICT_NAME, "");
    }

    public String getBirthday_str() {
        return mPreferences.getString(BIRTHDAY_STR, "");
    }

    public String getSex() {
        return mPreferences.getString(SEX, "");
    }

    public String getBgimg() {
        return mPreferences.getString(BG_IMG, "");
    }

    public String getSex_str() {
        return mPreferences.getString(SEX_STR, "");
    }

    public String getLogin_supplier_id() {
        return mPreferences.getString(LOGIN_SUPPLIER_ID, "");
    }

    public String getLogin_supplier_name() {
        return mPreferences.getString(LOGIN_SUPPLIER_NAME, "");
    }

    public String getLogin_supplier_logo() {
        return mPreferences.getString(LOGIN_SUPPLIER_LOGO, "");
    }

    public String getLogin_supplier_img() {
        return mPreferences.getString(LOGIN_SUPPLIER_IMG, "");
    }

    public String getLogin_supplier_qrcode() {
        return mPreferences.getString(LOGIN_SUPPLIER_QRCODE, "");
    }

    public String getCoverPic() {
        return mPreferences.getString("cover_pic", "");
    }

    public String getMember_rank() {
        return mPreferences.getString(MEMBER_RANK, "");
    }

    public boolean hadLogged() {
        return mPreferences.getBoolean(LOGIN_STATE, false);
    }

    public void clear() {
        mPreferences.edit().clear().apply();
    }

    public void setCover_pic(String cover_pic) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("cover_pic", cover_pic);
        editor.apply();
    }

    public String getSchool() {
        return mPreferences.getString("school", "");
    }

    public void setSchool(String school) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("school", school);
        editor.apply();
    }
}
