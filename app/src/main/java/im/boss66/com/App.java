package im.boss66.com;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.Utils.SharePreferenceUtil;
import im.boss66.com.Utils.SharedPreferencesMgr;
import im.boss66.com.config.LoginStatus;
import im.boss66.com.db.MessageDB;
import im.boss66.com.db.RecentDB;
import im.boss66.com.db.UserDB;
import im.boss66.com.domain.EaseUser;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.LocalAddressEntity;
import im.boss66.com.fragment.ContactBooksFragment;
import im.boss66.com.services.ChatServices;

public class App extends Application {
    public final static String API_KEY = "fiWrR2Ki8NkR6r5GHdM2lY7j";
    public final static String SECRIT_KEY = "PTtP7I1EfBnRMu0LimV8fRSIso0dZtgA";
    public static final String SP_FILE_NAME = "push_msg_sp";
    public static final int PAGER_NUM = 8;
    // ======头像===
    public static final int[] heads = {R.drawable.h0};
    public static final int NUM_PAGE = 6;// 总共有多少页
    public static int NUM = 20;// 每页20个表情,还有最后一个删除button
    private static App mApplication;
    //    private BaiduPush mBaiduPushServer;
    private Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
    private SharePreferenceUtil mSpUtil;
    private UserDB mUserDB;
    private MessageDB mMsgDB;
    private RecentDB mRecentDB;
    // private List<User> mUserList;
    private MediaPlayer mMediaPlayer;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    //    private Gson mGson;
    private AccountEntity sAccount;
    private WebSocket webSocket;
    private List<Activity> tempActivityList;
    private PushAgent mPushAgent;
    //    private LocalAddressEntity.SecondChild localAddress;
    private static int sSeekPos;
    private static int sMaxVolume;
    private static double dVolume;

    private int count = 0;
    public boolean isForground = true;
    private List<String> uidList;

    private LocalAddressEntity.SecondChild loacalAddress;

    public synchronized static App getInstance() {
        return mApplication;
    }


    private ContactBooksFragment contactBooksFragment;

    public void setFragment(ContactBooksFragment fragment) {
        this.contactBooksFragment = fragment;
    }

    public ContactBooksFragment getFragment() {
        return contactBooksFragment;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        switchBackground();
        mApplication = this;
        initFaceMap();
        initData();
        initUmengPush();
//        Fresco.initialize(getApplicationContext());//注册，在setContentView之前。
        CrashReport.initCrashReport(getApplicationContext(), "3a7059fedc", false);
    }

    private void initData() {
//        mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
//                SECRIT_KEY, API_KEY);
//        // 不转换没有 @Expose 注解的字段
//        mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
//                .create();
        mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
        mUserDB = new UserDB(this);
        mMsgDB = new MessageDB(this);
        mRecentDB = new RecentDB(this);
//        mMediaPlayer = MediaPlayer.create(this, R.raw.office);
//        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        SharedPreferencesMgr.init(this, "liw");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initUmengPush() {
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 参考集成文档的1.6.3
             * http://dev.umeng.com/push/android/integration#1_6_3
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

            /**
             * 参考集成文档的1.6.4
             * http://dev.umeng.com/push/android/integration#1_6_4
             * */
            @Override
            public Notification getNotification(Context context,
                                                UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);
                        return builder.build();
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);
        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 参考集成文档的1.6.2
         * http://dev.umeng.com/push/android/integration#1_6_2
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
        //参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }


    public synchronized WebSocket getWebSocket() {
        if (webSocket == null) {
            webSocket = new WebSocketConnection();
        }
        return webSocket;
    }


//    public WebSocket getWebSocket() {
//        if (webSocket != null) {
////            if (webSocket.isConnected()){
////                webSocket.disconnect();
////            }
//            webSocket = null;
//        }
//        webSocket = new WebSocketConnection();
//        return webSocket;
//    }

    public synchronized UserDB getUserDB() {
        if (mUserDB == null)
            mUserDB = new UserDB(this);
        return mUserDB;
    }

    public void setCurrentProgress(int pos) {
        sSeekPos = pos;
    }

    public int getCurrentProgress() {
        return sSeekPos;
    }

    public void setMaxVolume(int maxVolume) {
        sMaxVolume = maxVolume;
    }

    public int getMaxVolume() {
        return sMaxVolume;
    }

    public void setDoubleVolume(double currentVolume) {
        dVolume = currentVolume;
    }

    public double getDoubleVolume() {
        return dVolume;
    }

//    public synchronized BaiduPush getBaiduPush() {
//        if (mBaiduPushServer == null)
//            mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
//                    SECRIT_KEY, API_KEY);
//        return mBaiduPushServer;
//
//    }
//
//    public synchronized Gson getGson() {
//        if (mGson == null)
//            // 不转换没有 @Expose 注解的字段
//            mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
//                    .create();
//        return mGson;
//    }

    public synchronized MessageDB getMessageDB() {
        if (mMsgDB == null)
            mMsgDB = new MessageDB(this);
        return mMsgDB;
    }

    public synchronized RecentDB getRecentDB() {
        if (mRecentDB == null)
            mRecentDB = new RecentDB(this);
        return mRecentDB;
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

//    public synchronized MediaPlayer getMediaPlayer() {
//        if (mMediaPlayer == null)
//            mMediaPlayer = MediaPlayer.create(this, R.raw.office);
//        return mMediaPlayer;
//    }

    public synchronized SharePreferenceUtil getSpUtil() {
        if (mSpUtil == null)
            mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
        return mSpUtil;
    }

    public Map<String, Integer> getFaceMap() {
        if (!mFaceMap.isEmpty())
            return mFaceMap;
        return null;
    }

    /**
     * 创建挂机图标
     */
    @SuppressWarnings("deprecation")
//    public void showNotification() {
//        if (!mSpUtil.getMsgNotify())// 如果用户设置不显示挂机图标，直接返回
//            return;
//
//        int icon = R.drawable.notify_general;
//        CharSequence tickerText = getResources().getString(
//                R.string.app_is_run_background);
//        long when = System.currentTimeMillis();
//        mNotification = new Notification(icon, tickerText, when);
//
//        // 放置在"正在运行"栏目中
//        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
//
//        RemoteViews contentView = new RemoteViews(getPackageName(),
//                R.layout.notify_status_bar_latest_event_view);
//        contentView.setImageViewResource(R.id.icon,
//                heads[mSpUtil.getHeadIcon()]);
//        contentView.setTextViewText(R.id.title, mSpUtil.getNick());
//        contentView.setTextViewText(R.id.text, tickerText);
//        contentView.setLong(R.id.time, "setTime", when);
//        // 指定个性化视图
//        mNotification.contentView = contentView;
//
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // 指定内容意图
//        mNotification.contentIntent = contentIntent;
//        // 下面是4.0notify
//        // Bitmap icon = BitmapFactory.decodeResource(getResources(),
//        // heads[mSpUtil.getHeadIcon()]);
//        // Notification.Builder notificationBuilder = new Notification.Builder(
//        // this).setContentTitle(mSpUtil.getNick())
//        // .setContentText(tickerText)
//        // .setSmallIcon(R.drawable.notify_general)
//        // .setWhen(System.currentTimeMillis())
//        // .setContentIntent(contentIntent).setLargeIcon(icon);
//        // Notification n = notificationBuilder.getNotification();
//        // n.flags |= Notification.FLAG_NO_CLEAR;
//
//        mNotificationManager.notify(PushMessageReceiver.NOTIFY_ID,
//                mNotification);
//    }

    public String getUid() {
        if (sAccount == null) {
            return LoginStatus.getInstance().getUser_id();
        }
        return sAccount.getUser_id();
    }

    public AccountEntity getAccountEntity() {
        if (sAccount == null) {
            LoginStatus loginStatus = LoginStatus.getInstance();
            sAccount = new AccountEntity();
            sAccount.setUser_name(loginStatus.getUser_name());
            sAccount.setUser_id(loginStatus.getUser_id());
            sAccount.setAccess_token(loginStatus.getToken());
            sAccount.setLastlogin_time(loginStatus.getLastTime());
            sAccount.setMobile_phone(loginStatus.getMobilePhone());
            sAccount.setAvatar(loginStatus.getAvatar());
            sAccount.setSex(loginStatus.getSex());
            sAccount.setSignature(loginStatus.getSignature());
            sAccount.setProvince(loginStatus.getProvince());
            sAccount.setCity(loginStatus.getCity());
            sAccount.setDistrict(loginStatus.getDistrict());
            sAccount.setDistrict_str(loginStatus.getDistrict_str());
            sAccount.setCover_pic(loginStatus.getCoverPic());
            sAccount.setSchool(loginStatus.getSchool());
        }
        return sAccount;
    }


    public AccountEntity getAccount() {
        if (sAccount != null) {
            sAccount = null;
        }
        LoginStatus loginStatus = LoginStatus.getInstance();
        sAccount = new AccountEntity();
        if (sAccount != null) {
            sAccount.setUser_name(loginStatus.getUser_name());
            sAccount.setUser_id(loginStatus.getUser_id());
            sAccount.setAccess_token(loginStatus.getToken());
            sAccount.setLastlogin_time(loginStatus.getLastTime());
            sAccount.setMobile_phone(loginStatus.getMobilePhone());
            sAccount.setAvatar(loginStatus.getAvatar());
            sAccount.setSex(loginStatus.getSex());
            sAccount.setSignature(loginStatus.getSignature());
            sAccount.setProvince(loginStatus.getProvince());
            sAccount.setCity(loginStatus.getCity());
            sAccount.setDistrict(loginStatus.getDistrict());
            sAccount.setDistrict_str(loginStatus.getDistrict_str());
            sAccount.setCover_pic(loginStatus.getCoverPic());
            sAccount.setSchool(loginStatus.getSchool());
        }
        return sAccount;
    }

    public boolean isForground() {
        return isForground;
    }

    public boolean isLogin() {
        return LoginStatus.getInstance().hadLogged();
    }

    public void initUser(AccountEntity account) {
        sAccount = account;
        LoginStatus.getInstance().login(account, false);
    }

    public void logout() {
        sAccount = null;
        LoginStatus.getInstance().clear();
    }


    private void initFaceMap() {
        // TODO Auto-generated method stub
        mFaceMap.put("[呲牙]", R.drawable.f000);
        mFaceMap.put("[调皮]", R.drawable.f001);
        mFaceMap.put("[流汗]", R.drawable.f002);
        mFaceMap.put("[偷笑]", R.drawable.f003);
        mFaceMap.put("[再见]", R.drawable.f004);
        mFaceMap.put("[敲打]", R.drawable.f005);
        mFaceMap.put("[擦汗]", R.drawable.f006);
        mFaceMap.put("[猪头]", R.drawable.f007);
        mFaceMap.put("[玫瑰]", R.drawable.f008);
        mFaceMap.put("[流泪]", R.drawable.f009);
        mFaceMap.put("[大哭]", R.drawable.f010);
        mFaceMap.put("[嘘]", R.drawable.f011);
        mFaceMap.put("[酷]", R.drawable.f012);
        mFaceMap.put("[抓狂]", R.drawable.f013);
        mFaceMap.put("[委屈]", R.drawable.f014);
        mFaceMap.put("[便便]", R.drawable.f015);
        mFaceMap.put("[炸弹]", R.drawable.f016);
        mFaceMap.put("[菜刀]", R.drawable.f017);
        mFaceMap.put("[可爱]", R.drawable.f018);
        mFaceMap.put("[色]", R.drawable.f019);
        mFaceMap.put("[害羞]", R.drawable.f020);

        mFaceMap.put("[得意]", R.drawable.f021);
        mFaceMap.put("[吐]", R.drawable.f022);
        mFaceMap.put("[微笑]", R.drawable.f023);
        mFaceMap.put("[发怒]", R.drawable.f024);
        mFaceMap.put("[尴尬]", R.drawable.f025);
        mFaceMap.put("[惊恐]", R.drawable.f026);
        mFaceMap.put("[冷汗]", R.drawable.f027);
        mFaceMap.put("[爱心]", R.drawable.love);
        mFaceMap.put("[示爱]", R.drawable.f029);
        mFaceMap.put("[白眼]", R.drawable.f030);
        mFaceMap.put("[傲慢]", R.drawable.f031);
        mFaceMap.put("[难过]", R.drawable.f032);
        mFaceMap.put("[惊讶]", R.drawable.f033);
        mFaceMap.put("[疑问]", R.drawable.f034);
        mFaceMap.put("[睡]", R.drawable.f035);
        mFaceMap.put("[亲亲]", R.drawable.f036);
        mFaceMap.put("[憨笑]", R.drawable.f037);
        mFaceMap.put("[爱情]", R.drawable.f038);
        mFaceMap.put("[衰]", R.drawable.f039);
        mFaceMap.put("[撇嘴]", R.drawable.f040);
        mFaceMap.put("[阴险]", R.drawable.f041);

        mFaceMap.put("[奋斗]", R.drawable.f042);
        mFaceMap.put("[发呆]", R.drawable.f043);
        mFaceMap.put("[右哼哼]", R.drawable.f044);
        mFaceMap.put("[拥抱]", R.drawable.f045);
        mFaceMap.put("[坏笑]", R.drawable.f046);
        mFaceMap.put("[飞吻]", R.drawable.f047);
        mFaceMap.put("[鄙视]", R.drawable.f048);
        mFaceMap.put("[晕]", R.drawable.f049);
        mFaceMap.put("[大兵]", R.drawable.f050);
        mFaceMap.put("[可怜]", R.drawable.f051);
        mFaceMap.put("[强]", R.drawable.f052);
        mFaceMap.put("[弱]", R.drawable.f053);
        mFaceMap.put("[握手]", R.drawable.f054);
        mFaceMap.put("[胜利]", R.drawable.f055);
        mFaceMap.put("[抱拳]", R.drawable.f056);
        mFaceMap.put("[凋谢]", R.drawable.f057);
        mFaceMap.put("[饭]", R.drawable.f058);
        mFaceMap.put("[蛋糕]", R.drawable.f059);
        mFaceMap.put("[西瓜]", R.drawable.f060);
        mFaceMap.put("[啤酒]", R.drawable.f061);
        mFaceMap.put("[飘虫]", R.drawable.f062);

        mFaceMap.put("[勾引]", R.drawable.f063);
        mFaceMap.put("[OK]", R.drawable.f064);
        mFaceMap.put("[爱你]", R.drawable.f065);
        mFaceMap.put("[咖啡]", R.drawable.f066);
        mFaceMap.put("[钱]", R.drawable.f067);
        mFaceMap.put("[月亮]", R.drawable.f068);
        mFaceMap.put("[美女]", R.drawable.f069);
        mFaceMap.put("[刀]", R.drawable.f070);
        mFaceMap.put("[发抖]", R.drawable.f071);
        mFaceMap.put("[差劲]", R.drawable.f072);
        mFaceMap.put("[拳头]", R.drawable.f073);
        mFaceMap.put("[心碎]", R.drawable.f074);
        mFaceMap.put("[太阳]", R.drawable.f075);
        mFaceMap.put("[礼物]", R.drawable.f076);
        mFaceMap.put("[足球]", R.drawable.f077);
        mFaceMap.put("[骷髅]", R.drawable.f078);
        mFaceMap.put("[挥手]", R.drawable.f079);
        mFaceMap.put("[闪电]", R.drawable.f080);
        mFaceMap.put("[饥饿]", R.drawable.f081);
        mFaceMap.put("[困]", R.drawable.f082);
        mFaceMap.put("[咒骂]", R.drawable.f083);

        mFaceMap.put("[折磨]", R.drawable.f084);
        mFaceMap.put("[抠鼻]", R.drawable.f085);
        mFaceMap.put("[鼓掌]", R.drawable.f086);
        mFaceMap.put("[糗大了]", R.drawable.f087);
        mFaceMap.put("[左哼哼]", R.drawable.f088);
        mFaceMap.put("[哈欠]", R.drawable.f089);
        mFaceMap.put("[快哭了]", R.drawable.f090);
        mFaceMap.put("[吓]", R.drawable.f091);
        mFaceMap.put("[篮球]", R.drawable.f092);
        mFaceMap.put("[乒乓球]", R.drawable.f093);
        mFaceMap.put("[NO]", R.drawable.f094);
        mFaceMap.put("[跳跳]", R.drawable.f095);
        mFaceMap.put("[怄火]", R.drawable.f096);
        mFaceMap.put("[转圈]", R.drawable.f097);
        mFaceMap.put("[磕头]", R.drawable.f098);
        mFaceMap.put("[回头]", R.drawable.f099);
        mFaceMap.put("[跳绳]", R.drawable.f100);
        mFaceMap.put("[激动]", R.drawable.f101);
        mFaceMap.put("[街舞]", R.drawable.f102);
        mFaceMap.put("[献吻]", R.drawable.f103);
        mFaceMap.put("[左太极]", R.drawable.f104);

        mFaceMap.put("[右太极]", R.drawable.f105);
        mFaceMap.put("[闭嘴]", R.drawable.f106);

        mFaceMap.put("[表情一]", R.drawable.f107);
        mFaceMap.put("[表情二]", R.drawable.f108);
        mFaceMap.put("[表情三]", R.drawable.f109);
    }

    private ArrayList<EaseUser> contactList;

    public ArrayList<EaseUser> getContacts() {
        if (contactList == null) {
            contactList = new ArrayList<>();
        }
        return contactList;
    }

    public void setContacts(ArrayList<EaseUser> list) {
        this.contactList = list;
    }

    public void removeItem(String uid) {
        if (contactList != null && contactList.size() != 0) {
            Iterator<EaseUser> stringIterator = contactList.iterator();
            while (stringIterator.hasNext()) {
                EaseUser s = stringIterator.next();
                if (s.getUserid().equals(uid)) {
                    stringIterator.remove();
                }
            }
        }
    }

    public void addTempActivity(Activity activity) {
        if (tempActivityList == null) {
            tempActivityList = new ArrayList<>();
        }
        tempActivityList.add(activity);
    }

    public List<Activity> getTempActivityList() {
        return tempActivityList;
    }


    //遍历所有Activity并finish
    public void exit() {
        if (tempActivityList != null) {
            for (Activity activity : tempActivityList) {
                activity.finish();
            }
            System.exit(0);
        }
    }

    public LocalAddressEntity.SecondChild getLoacalAddress() {
        return loacalAddress;
    }

    public void setLoacalAddress(LocalAddressEntity.SecondChild loacalAddress) {
        this.loacalAddress = loacalAddress;
    }

    public boolean isThirdLogin(Context context) {
        return PreferenceUtils.getBoolean(context, "isThirdLogin", false);
    }

    public void setThirdLogin(Context context, boolean thirdLogin) {
        PreferenceUtils.putBoolean(context, "isThirdLogin", thirdLogin);
    }

    public void addUidToList(String uid, boolean isAdd) {
        if (uidList == null) {
            uidList = new ArrayList<>();
        }
        if (isAdd) {
            uidList.add(uid);
        } else if (uidList.contains(uid)) {
            uidList.remove(uid);
        }
    }

    public List<String> getUidList() {
        return uidList;
    }


    private void switchBackground() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStopped(Activity activity) {
                Log.v("viclee", activity + "onActivityStopped");
                count--;
                if (count == 0) {
                    isForground = false;
                    Log.v("viclee", ">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.v("viclee", activity + "onActivityStarted");
                if (count == 0) {
                    Log.v("viclee", ">>>>>>>>>>>>>>>>>>>切到前台  lifecycle");
                    isForground = true;
                    ChatServices.startChatService(App.this);
                }
                count++;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.v("viclee", activity + "onActivitySaveInstanceState");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.v("viclee", activity + "onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.v("viclee", activity + "onActivityPaused");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.v("viclee", activity + "onActivityDestroyed");
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.v("viclee", activity + "onActivityCreated");
            }
        });
    }
}
