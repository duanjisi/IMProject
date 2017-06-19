package im.boss66.com.http;

/**
 * Created by Johnny on 2017/1/12.
 */
public class HttpUrl {
    public static final String MAIN_URL = "https://api.66boss.com/";
    //    public static final String BASE_URL = "http://live.66boss.com/";
    //    public static final String BASE_IM_URL = "http://live.66boss.com:6060/";
    public static final String BASE_URL = "http://wsim.hmg66.com/";
    public static final String BASE_IM_URL = "http://wsim.hmg66.com:8080/";
    //    public static final String WS_URL = "ws://live.66boss.com:6060/entry";
    public static final String WS_URL = "ws://wsim.hmg66.com:8080/entry";

    public static final String NOTIFICATION_LINK = "http://live.66boss.com/umeng/send?";
    public static final String UPLOAD_AUDIO_URL = BASE_URL + "upload/writev1?";
    public static final String UPLOAD_IMAGE_URL = BASE_URL + "upload/writev2?";
    public static final String UPLOAD_VIDEO_URL = BASE_URL + "upload/writev3?";

    public static final String[] BASE_FUWA = {"http://wsimali.66boss.com:9090/",
            "http://fuwa2.66boss.com:9090/",
            "http://fuwa.hmg66.com/"};
    public static int FUWA_TAG = 2;
    /**
     * android版本更新
     */
    public static final String VERSION_UPDATE = MAIN_URL + "api/update";
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
    public static final String FRIENDSHIP_URL = MAIN_URL + "ucenter/friends/isfriend";
    //通讯录请求
    public static final String CONTACTS_URL = MAIN_URL + "ucenter/friends/index";
    //新朋友列表请求
    public static final String NEW_FRIENDS_URL = MAIN_URL + "ucenter/friendslog/index";
    //查找用户
    public static final String QURE_ACCOUNT = MAIN_URL + "ucenter/userinfo/findman";
    //精选表情首页数据请求
    public static final String EMO_WELL_URL = MAIN_URL + "api/v1/store";
    //表情包详细信息请求
    public static final String EMO_INFORM_DETAILS_URL = MAIN_URL + "api/v1/store/viewpack";
    //搜索热门词
    public static final String HOT_WORDS_URL = MAIN_URL + "api/v1/store/searchhot";
    //表情商店搜索
//    public static final String EMO_STORE_SEARCH_URL = MAIN_URL + "api/v1/store/search";
    public static final String EMO_STORE_SEARCH_URL = MAIN_URL + "api/v1/search/store";

    //表情编码解析
    public static final String EMO_PARSE_URL = MAIN_URL + "api/v1/pack/topath";
    //获取表情收藏
    public static final String EMO_COLLECTIONS_URL = MAIN_URL + "api/v1/collect";
    //添加收藏表情
    public static final String EMO_COLLECTION_ADD_URL = MAIN_URL + "api/v1/collect/add";
    //删除收藏表情
    public static final String EMO_COLLECTIONS_DELETE_URL = MAIN_URL + "api/v1/collect/delete";
    //获取用户信息接口
    public static final String PERSON_INFORM_URL = MAIN_URL + "ucenter/userinfo/index";
    //匹配手机通讯录接口
    public static final String MATCH_PHONE_BOOK_URL = MAIN_URL + "ucenter/matchphone";
    //创建群接口
    public static final String CREATE_GROUP_URL = BASE_IM_URL + "creategrp";
    //增加群成员
    public static final String ADD_GROUP_MEMBERS = BASE_IM_URL + "addmembers";
    //踢出群成员
    public static final String DELETE_GROUP_MEMBERS = BASE_IM_URL + "delmembers";
    //更改群资料
    public static final String UPDATE_GROUP_INFORMS = BASE_IM_URL + "editgrp";
    //查询我的群
    public static final String QURE_MY_GROUP = BASE_IM_URL + "querymygrps";
    //查询群成員
    public static final String QURE_GROUP_MEMBERS = BASE_IM_URL + "grpinfo";
    //踢出群成員
    public static final String REMOVE_GROUP_MEMBERS = BASE_IM_URL + "delmembers";
    //获取所有分类
    public static final String ALL_CATEGROIES = MAIN_URL + "api/v1/emo/cates";
    //获取所有表情
    public static final String EMOS_ALL = MAIN_URL + "api/v1/emo";
    //按分类获取表情
    public static final String EMOS_BY_CATE = MAIN_URL + "api/v1/emo/cate";
    //表情搜索
    public static final String EMOS_SEARCH = MAIN_URL + "api/v1/search/emo";


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
    //发布图文朋友圈
    public static final String CREATE_CIRCLE_PHOTO_TX = MAIN_URL + "api/v1/cofriends/create";
    //发布视频朋友圈
    public static final String CREATE_CIRCLE_VIDEO_TX = MAIN_URL + "api/v1/cofriends/createvideo";
    //朋友圈列表
    public static final String FRIEND_CIRCLE_LIST = MAIN_URL + "api/v1/cofriends";
    //点赞or取消点赞
    public static final String FRIEND_CIRCLE_PRAISE = MAIN_URL + "api/v1/praise/create";
    //删除朋友圈
    public static final String FRIEND_CIRCLE_DELETE = MAIN_URL + "api/v1/cofriends/delete";
    //获取某条朋友圈的赞列表
    public static final String FRIEND_CIRCLE_GET_PRAISE_LIST = MAIN_URL + "api/v1/praise";
    //发表/回复评论
    public static final String FRIEND_CIRCLE_COMMET_CREATE = MAIN_URL + "api/v1/comment/create";
    //删除评论
    public static final String FRIEND_CIRCLE_COMMET_DELETE = MAIN_URL + "api/v1/comment/delete";
    //获取某条朋友圈的评论列表
    public static final String FRIEND_CIRCLE_GET_COMMENT_LIST = MAIN_URL + "api/v1/comment";
    //获取行业列表
    public static final String CHOOSE_JOB_LIST = MAIN_URL + "api/v1/industry";
    //获取兴趣列表
    public static final String CHOOSE_LIKE_LIST = MAIN_URL + "api/v1/interest";
    //获取个人相册
    public static final String GET_PERSONAL_GALLERY = MAIN_URL + "api/v1/gallery";
    //保存学校信息
    public static final String SAVE_SCHOOL_INFO = MAIN_URL + "api/v1/userschool/create";
    //获取学校列表
    public static final String GET_SCHOOL_LIST = MAIN_URL + "api/v1/userschool";
    //编辑学校信息
    public static final String EDIT_SCHOOL_INFO = MAIN_URL + "api/v1/userschool/update";
    //删除学校信息
    public static final String DELETE_SCHOOL_INFO = MAIN_URL + "api/v1/userschool/delete";
    //保存用户信息
    public static final String SAVE_USER_INFO = MAIN_URL + "ucenter/userinfo/update";

    //获取某条朋友圈详情
    public static final String GET_FRIEND_CIRCLE_DETAIL = MAIN_URL + "api/v1/cofriends/view";
    //全网搜索(添加好友、朋友圈、表情)不包含人脉
    public static final String SEARCH_BY_ALL_NET = MAIN_URL + "api/v1/search";
    //修改用户名(姓名)
    public static final String CHANGE_USER_NAME = MAIN_URL + "ucenter/userinfo/updateuname";
    //人脉首页
    public static final String CONNECTION_MY_INFO = MAIN_URL + "api/v1/contacts";
    //学校简介
    public static final String SCHOOL_INFO = MAIN_URL + "web/school";
    //家乡简介
    public static final String HOMETOWN_INFO = MAIN_URL + "web/hometown";
    //社团列表
    public static final String SCHOOL_CLUB_LIST = MAIN_URL + "api/v1/school/league";
    //商会列表
    public static final String HOMETOWN_CLUB_LIST = MAIN_URL + "api/v1/hometown/cofc";
    //学校名人
    public static final String SCHOOL_FAMOUS_PEOPLE = MAIN_URL + "api/v1/school/celebrity";
    //商会名人
    public static final String BUSINESS_FAMOUS_PEOPLE = MAIN_URL + "api/v1/hometown/celebrity";

    //获取消息列表
    public static final String GET_CIRCLE_MSG_LIST = MAIN_URL + "api/v1/message";
    //清空消息列表
    public static final String CLEAR_CIRCLE_MSG_LIST = MAIN_URL + "api/v1/message/flushall";
    //获取最新朋友圈消息通知
    public static final String GET_CIRCLE_NEWEST_MSG = MAIN_URL + "api/v1/message/getnewest";
    //获取帖子列表
    public static final String GET_COMMUNITY_LIST = MAIN_URL + "api/v1/contactsfeed";
    //删除帖子
    public static final String DELETE_COMMUNITY = MAIN_URL + "api/v1/contactsfeed/delete";
    //获取帖子的赞列表
    public static final String GET_COMMUNITY_PRAISE_LIST = MAIN_URL + "api/v1/contactspraise";
    //帖子-赞/取消赞
    public static final String COMMUNITY_DO_PRAISE = MAIN_URL + "api/v1/contactspraise/create";
    //获取帖子评论列表
    public static final String GET_COMMUNITY_COMMENT_LIST = MAIN_URL + "api/v1/contactscomment";
    //帖子-发表/回复评论
    public static final String COMMUNITY_CREATE_COMMENT = MAIN_URL + "api/v1/contactscomment/create";
    //帖子-删除评论
    public static final String COMMUNITY_DELETE_COMMENT = MAIN_URL + "api/v1/contactscomment/delete";
    //帖子-发布帖子
    public static final String COMMUNITY_CREATE = MAIN_URL + "api/v1/contactsfeed/create";
    //帖子-获取我的消息列表
    public static final String COMMUNITY_GET_MY_MSG = MAIN_URL + "api/v1/contactsmsg";
    //帖子-获取我的消息列表
    public static final String COMMUNITY_CLEAR_MY_MSG = MAIN_URL + "api/v1/contactsmsg/flushall";
    //帖子-获取帖子详情
    public static final String COMMUNITY_GET_DETAIL = MAIN_URL + "api/v1/contactsfeed/view";
    //帖子-获取某个用户的帖子列表
    public static final String COMMUNITY_GET_USER_DETAIL = MAIN_URL + "api/v1/contactsc";
    //帖子-获取某个用户的帖子列表
    public static final String COMMUNITY_GET_NEW_MSG = MAIN_URL + "api/v1/contactsmsg/getnewest";

    //商会详情页
//    public static final String CLUB_DETATIL = MAIN_URL + "hometown/cofc";
    public static final String CLUB_DETATIL = MAIN_URL + "web/hometown/cofc";
    //学校动态列表
    public static final String SCHOOL_NEWS = MAIN_URL + "api/v1/school/message";
    //家乡动态列表
    public static final String HOMETOWN_NEWS = MAIN_URL + "api/v1/hometown/message";
    //搜索学校
    public static final String SEARCH_SCHOOL = MAIN_URL + "api/v1/search/local-school";
    //获取用户信息
    public static final String SEARCH_USER_INFO = MAIN_URL + "ucenter/userinfo/index";
    //查询周围的福娃
    public static final String FIND_AROUND_CHILDREN = BASE_FUWA[FUWA_TAG] + "api/queryv2";
    //查询周围缘分
    public static final String FIND_AROUND_FATE = BASE_FUWA[FUWA_TAG] + "api/querystrangerv2";
    //查询特定商家的福娃
    public static final String FIND_MERCHANTS_BABY = BASE_FUWA[FUWA_TAG] + "api/queryv3";
    //查询特定用户的福娃
    public static final String FIND_USER_BABY = BASE_FUWA[FUWA_TAG] + "api/querystrangerv3";

    //同学推荐
    public static final String SCHOOLMATE_LIST = MAIN_URL + "api/v1/search/same-school";
    //同乡推荐
    public static final String COUNTRYMAN_LIST = MAIN_URL + "api/v1/search/same-home";
    //随便看看(自定义)
    public static final String RANDOWM_LOOK = MAIN_URL + "api/v1/search/rand";
    //搜索人脉用户
    public static final String SEARCH_PEOPLE = MAIN_URL + "api/v1/search/user";
    //查询所有的福娃出售
    public static final String SEARCH_FUWA_SELL = BASE_FUWA[FUWA_TAG] + "msg/querysell";

    //查自己的福娃
    public static final String QUERY_MY_FUWA = BASE_FUWA[FUWA_TAG] + "api/querymy?user=";
    //福娃详情
    public static final String FUWA_DETAIL = BASE_FUWA[FUWA_TAG] + "api/querydetail?fuwagid=";
    //出售福娃
    public static final String SELL_FUWA = BASE_FUWA[FUWA_TAG] + "msg/sell";
    //赠送福娃
    public static final String GIVE_FUWA = BASE_FUWA[FUWA_TAG] + "api/donate";
    //福娃排行榜
    public static final String FUWA_TOPLIST = "http://wsim.66boss.com/toplist/top.html?user=";
    //查自己出售的福娃
    public static final String SEARY_MY_SELL_FUWA = BASE_FUWA[FUWA_TAG] + "/msg/querymysell?userid=";

    //藏福娃
    public static final String HIDE_MY_FUWA = BASE_FUWA[FUWA_TAG] + "api/hidev2?owner=";
    //捉福娃
    public static final String CATCH_MY_FUWA = BASE_FUWA[FUWA_TAG] + "api/capturev2?user=";
    //福娃消息
    public static final String FUWA_MSG = BASE_FUWA[FUWA_TAG] + "msg/myinfo?userid=";
    //微信支付购买福娃
    public static final String FUWA_PAY_WX = MAIN_URL + "api/pay/weixin";
    //支付宝购买福娃
    public static final String FUWA_PAY_ALIPAY = MAIN_URL + "api/pay/alipay";
    //支付系统通知购买成功
    public static final String SYSTEM_PAY_NOTICE = BASE_FUWA[FUWA_TAG] + "msg/notice?";

    //名人详情页
    public static final String FAMOUSPERSON_DETAIL = MAIN_URL + "web/hometown/celebrity";
    //学校名人页
    public static final String SCHOOL_PERSON_DETAIL = MAIN_URL + "web/school/celebrity";
    //学校社团详情页
    public static final String SCHOOL_CLUB_DETATIL = MAIN_URL + "web/school/league";
    //家乡动态详情页
    public static final String HOMETONW_NEWS_DETATIL = MAIN_URL + "web/hometown/message";
    //学校动态详情页
    public static final String SCHOOL_NEWS_DETATIL = MAIN_URL + "/web/school/message";
    //申请福娃
    public static final String APPLY_FUWA = BASE_FUWA[FUWA_TAG] + "msg/apply";
    //修改密码
    public static final String CHANGE_USER_PW = MAIN_URL + "/ucenter/psw/index";
    //创建宗亲
    public static final String CREATE_CLAN = MAIN_URL + "api/v1/clan/create";
    //创建商会
    public static final String CREATE_CLUB = MAIN_URL + "api/v1/cofc/create";
    //删除宗亲
    public static final String DELETE_CLAN = MAIN_URL + "api/v1/clan/delete";
    //删除商会
    public static final String DELETE_CLUB = MAIN_URL + "api/v1/cofc/delete";
    //宗亲web页
    public static final String CLAN_DETAIL = MAIN_URL + "web/clan";
    //商会web页
    public static final String COFC_DETAIL = MAIN_URL + "web/cofc";
    //获取宗亲详情
    public static final String CLAN_INFO = MAIN_URL + "api/v1/clan";
    //获取商会详情
    public static final String COFC_INFO = MAIN_URL + "api/v1/cofc";
    //关注宗亲
    public static final String FOLLOW_CLAN = MAIN_URL + "api/v1/clan/follow";
    //取消关注宗亲
    public static final String CANCEL_FOLLOW_CLAN = MAIN_URL + "api/v1/clan/unfollow";
    //关注商会
    public static final String FOLLOW_COFC = MAIN_URL + "api/v1/cofc/follow";
    //取消关注商会
    public static final String CANCEL_FOLLOW_COFC = MAIN_URL + "api/v1/cofc/unfollow";
    //修改宗亲
    public static final String EDIT_CLAN = MAIN_URL + "api/v1/clan/update";
    //修改商会
    public static final String EDIT_COFC = MAIN_URL + "api/v1/cofc/update";
    //验证福娃
    public static final String SCAN_CODE = BASE_FUWA[FUWA_TAG] + "api/award";
    //宗亲名人列表
    public static final String CLAN_PERSON_LIST = MAIN_URL + "api/v1/clan/getcelebrity";
    //商会名人列表
    public static final String COFC_PERSON_LIST = MAIN_URL + "api/v1/cofc/getcelebrity";
    //添加宗亲名人
    public static final String ADD_CLAN_PERSON = MAIN_URL + "api/v1/clan/addcelebrity";
    //添加商会名人
    public static final String ADD_COFC_PERSON = MAIN_URL + "api/v1/cofc/addcelebrity";
    //宗亲名人web
    public static final String CLAN_PERSON_WEB = MAIN_URL + "web/clan/celebrity";
    //商会名人web
    public static final String COFC_PERSON_WEB = MAIN_URL + "web/cofc/celebrity";
    //删除宗亲名人
    public static final String DELETE_CLAN_PERSON = MAIN_URL + "api/v1/clan/delcelebrity";
    //删除商会名人
    public static final String DELETE_COFC_PERSON = MAIN_URL + "api/v1/cofc/delcelebrity";
    //编辑宗亲名人
    public static final String UPDATA_CLAN_PERSON = MAIN_URL + "api/v1/clan/updatecelebrity";
    //编辑商会名人
    public static final String UPDATA_COFC_PERSON = MAIN_URL + "api/v1/cofc/updatecelebrity";

    //查询余额
    public static final String QUERY_MY_MONEY = BASE_FUWA[FUWA_TAG] + "msg/querymoney?userid=";
    //提现
    public static final String WHITDRAW_MONEY = BASE_FUWA[FUWA_TAG] + "msg/money?";
    //获取个人收藏
    public static final String GET_PERSONAL_COLLECT = MAIN_URL + "api/v1/favorites";
    //刪除个人收藏
    public static final String DELETE_PERSONAL_COLLECT = MAIN_URL + "api/v1/favorites/delete";
    //增加到个人收藏
    public static final String ADD_PERSONAL_COLLECT = MAIN_URL + "api/v1/favorites/add";
    //更多宗亲
    public static final String MORE_CLAN = MAIN_URL + "api/v1/search/hot-clan";
    //更多商会
    public static final String MORE_COFC = MAIN_URL + "api/v1/search/hot-cofc";
    //取消出售
    public static final String CANCEL_FUWA_SELL = BASE_FUWA[FUWA_TAG] + "msg/cancelsell";
    //查申请的福娃
    public static final String QUERY_MY_APPLY_FUWA = BASE_FUWA[FUWA_TAG] + "api/querymyapply?user=";
    //福娃活动详情
    public static final String FUWA_ACTIVITY_INFO = BASE_FUWA[FUWA_TAG] + "api/huodong?fuwagid=";
    //创建部落
    public static final String CREATE_TRIBE = MAIN_URL + "api/v1/storetribe/create";
    //获取部落详情
    public static final String TRIBE_DETAILS = MAIN_URL + "api/v1/storetribe";
    //关注部落
    public static final String FOLLOW_TRIBE = MAIN_URL + "api/v1/storetribe/follow";
    //取消关注部落
    public static final String CANCELFOLLOW_TRIBE = MAIN_URL + "api/v1/storetribe/unfollow";
    //部落web页
    public static final String TRIBE_WEB = MAIN_URL + "web/storetribe";
    //修改部落
    public static final String EDIT_TRIBE = MAIN_URL + "api/v1/storetribe/update";
    //删除部落
    public static final String DELETE_TRIBE = MAIN_URL + "api/v1/storetribe/delete";
    //通过uid查询部落列表
    public static final String SEARCH_TRIBE_LIST = MAIN_URL + "api/v1/storetribe/createby";
    //查福娃分类
    public static final String QUERY_FUWA_CLASS = BASE_FUWA[FUWA_TAG] + "api/queryclass";
    //查询商家视频分类
    public static final String SEARCH_COMPANY_VIDEO = BASE_FUWA[FUWA_TAG] + "api/queryclass";
    //查询视频列表
    public static final String SEARCH_VIDEO_LIST = BASE_FUWA[FUWA_TAG] + "api/queryvideo";
    //播放视频次数
    public static final String PLAY_VIDEO_NUM = BASE_FUWA[FUWA_TAG] + "api/hit";
    //人脉全局搜索
    public static final String SEARCH_CONNECTION_ALL = MAIN_URL+"api/v1/search/g";
}
