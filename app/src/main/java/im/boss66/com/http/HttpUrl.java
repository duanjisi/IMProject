package im.boss66.com.http;

/**
 * Created by Johnny on 2017/1/12.
 */
public class HttpUrl {
    public static final String MAIN_URL = "https://api.66boss.com/";
    public static final String BASE_URL = "http://live.66boss.com/";
    public static final String WS_URL = "ws://live.66boss.com:6060/entry";

    public static final String UPLOAD_IMAGE_URL = BASE_URL + "upload/writev2?";
    public static final String UPLOAD_VIDEO_URL = BASE_URL + "upload/writev3?";

    //手机号登录
    public static final String LOGIN_URL = MAIN_URL + "ucenter/login/index";
    //QQ登录
    public static final String LOGIN_QQ_URL = MAIN_URL + "ucenter/login/qqoath";
    //微信登录
    public static final String LOGIN_WX_URL = MAIN_URL + "ucenter/login/wxoath";
    //手机号注册
    public static final String REGIST_URL = MAIN_URL + "ucenter/reg/index";
    //发送短信验证码
    public static final String CODE_URL = MAIN_URL + "ucenter/sms/index";
    //通用验证手机短信验证
    public static final String SMS_URL = MAIN_URL + "ucenter/sms/validatecode";
    //手机号找回密码
    public static final String FIND_PWS_URL = MAIN_URL + "ucenter/psw/find";
    //添加好友
    public static final String ADD_FRIEND_URL = MAIN_URL + "ucenter/friendslog/create";
    //删除好友
    public static final String DELETE_FRIEND_URL = MAIN_URL + "ucenter/friends/delete";
    //处理好友请求
    public static final String HANDLE_FRIEND_URL = MAIN_URL + "ucenter/friendslog/update";
    //新朋友请求数
    public static final String NEW_FRIEND_NUM_URL = MAIN_URL + "ucenter/friendslog/count";
    //好友关系请求
    public static final String FRIENDSHIP_URL = MAIN_URL + "ucenter/friends/isfriends";
    //通讯录请求
    public static final String CONTACTS_URL = MAIN_URL + "ucenter/friends/index";
    //新朋友列表请求
    public static final String NEW_FRIENDS_URL = MAIN_URL + "ucenter/friendslog/index";

    //附近的人
    public static final String PEOPLE_NEARBY = MAIN_URL + "ucenter/nearuser/index";
    //更新位置
    public static final String UPDATE_LOCATION = MAIN_URL + "ucenter/nearuser/update";
    //摇一摇
    public static final String SHAKE_IT_OFF = MAIN_URL + "ucenter/shakeuser/index";
    //修改用户头像
    public static final String CHANGE_AVAYAR = MAIN_URL + "ucenter/userinfo/changeavatar";
    //获取用户信息
    public static final String GET_UER_INFO = MAIN_URL + "ucenter/userinfo/index";
    //修改用户名/性别/地区
    public static final String CHANE_NAME_SEX_AREA = MAIN_URL + "ucenter/userinfo/update";
    //修改个性签名
    public static final String CHANE_SIGNATURE = MAIN_URL + "ucenter/userinfo/updatesignature";
    //更换相册封面
    public static final String CHANE_ALBUM_COVER = MAIN_URL + "ucenter/userinfo/uploadcoverpic";
    //更换手机号码
    public static final String CHANE_PHONE_NUM = MAIN_URL + "ucenter/userinfo/bindmobile";
}
