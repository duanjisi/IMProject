package im.boss66.com.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.Session;
import im.boss66.com.Utils.Base64Utils;
import im.boss66.com.Utils.JavaCache;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.NetworkUtil;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.db.MessageDB;
import im.boss66.com.db.dao.ConversationHelper;
import im.boss66.com.entity.BaseConversation;
import im.boss66.com.entity.MessageItem;
import im.boss66.com.entity.Motion;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.util.Utils;

/**
 * Created by Johnny on 2017/1/16.
 */
public class ChatServices extends Service {
    //    private static WebSocket mConnection = new WebSocketConnection();
    private static final String socketKey = "im.boss66.com.websocket.key";
    public static ArrayList<receiveMessageCallback> callbacks = new ArrayList<>();
    private String userid;
    private MessageDB mMsgDB;// 保存消息的数据库
    private WebSocket mConnection;
    private long time1 = 0L;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    handler.removeMessages(0);
                    if (mConnection != null) {
                        Log.i("info", "======================6666666666666666666666" + "----------" + mConnection.isConnected());
//                        if (!mConnection.isConnected()) {
//                            startConnection();
//                        }
                    }
                    handler.sendEmptyMessageDelayed(0, 1500);
                    break;
                case 1:
                    handler.removeMessages(0);
                    break;
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("info", "========ChatServices中onCreate()");
        super.onCreate();
        EventBus.getDefault().register(this);
//        Session.getInstance().addObserver(this);
        mMsgDB = App.getInstance().getMessageDB();// 发送数据库
//        handler.sendEmptyMessage(0);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
//        userid = App.getInstance().getUid();
//        mConnection = App.getInstance().getWebSocket();
        startConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
//        return START_STICKY;
    }

    public interface receiveMessageCallback {
        //        void onMessageReceive(String msgType, String senderId,
//                              String senderName, String senderAvatar,
//                              String message, String chatType,
//                              String groupId, String temp);
        void onMessageReceive(MessageItem item, String fromUid);

        void onNotify(String title, String content);

        void onNetChange(boolean isNetConnected);
    }

    public static void stopChatService(Context context) {
        Intent iService = new Intent(context, ChatServices.class);
//        iService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.stopService(iService);
    }

    public static void startChatService(Context context) {
        if (App.getInstance().isLogin()) {
            Intent iService = new Intent(context, ChatServices.class);
            context.startService(iService);
        }
    }

    private void startConnection() {
        try {
            Log.i("info", "=============startConnection()");
//            if (mConnection.isConnected()) {
//                mConnection.disconnect();
//            }
            userid = App.getInstance().getUid();
//            mConnection = App.getInstance().getWebSocket();
            mConnection = JavaCache.getWebSocket(socketKey);
            mConnection.connect(HttpUrl.WS_URL, new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {
                    MycsLog.i("info", "======建立连接!");
                    logout();
                    login();
                }

                @Override
                public void onTextMessage(String payload) {
                    messageHandle(payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d("info", "=====Exception:onClose");
                    if (NetworkUtil.networkAvailable(ChatServices.this)) {
                        Log.d("info", "=====Exception:onClose" + "---NetworkUtil");
                        startConnection();
                    }
                }
            });
        } catch (Exception e) {
            Log.d("info", "=====Exception:" + e.toString());
            if (App.getInstance().isLogin()) {
                LocalBroadcastManager.getInstance(ChatServices.this).sendBroadcast(new Intent(Constants.Action.CHAT_SERVICE_CLOSE));
            }
        } catch (OutOfMemoryError e) {
            Log.d("info", "=====Exception:" + e.toString());
        }
    }


    public void sendMessage(String msg) {
        if (mConnection != null) {
            if (mConnection.isConnected()) {
                if (msg != null && !msg.equals("")) {
                    mConnection.sendTextMessage(msg);
                }
            } else {
                startConnection();
            }
        } else {
            startConnection();
        }
//        if (mConnection != null && msg != null && !msg.equals("")) {
//            Log.i("info", "===================IM发消息:" + msg);
//            mConnection.sendTextMessage(msg);
//        }
    }

//    private void messageHandle(String payload) {
//        for (int i = 0; i < callbacks.size(); i++)
//            ((receiveMessageCallback) callbacks.get(i)).onMessageReceive(payload);
//    }


    private void messageHandle(String str) {
        Log.i("info", "===================IM接消息");
        MycsLog.i("info", "====str:" + str);
        if (str.equals("ok")) {
            sendMessage("jokliu~~%$*()%&(*&");
            return;
        } else if (str.equals("jokliu~~%$*()%&(*&")) {
            return;
        }
        String[] datas = str.split("_");
        if (datas != null && datas.length > 6) {
            String json = Base64Utils.getFromBase64(datas[6]);
            MycsLog.i("info", "====json:" + json);
            try {
                JSONObject object = new JSONObject(json);
                String sender = object.getString("sender");
                String senderAvartar = object.getString("senderAvartar");
                String conversation = object.getString("conversation");
                String conversationAvatar = object.getString("conversationAvartar");

                saveMessageItem(datas, sender, senderAvartar);
                String fromid = "";
                BaseConversation sation = new BaseConversation();
                if (datas[3].equals("group")) {
                    sation.setUser_name(conversation);
                    sation.setAvatar(conversationAvatar);
                    sation.setConversation_id(datas[4]);
                    fromid = datas[4];
                } else {
                    sation.setUser_name(sender);
                    sation.setAvatar(senderAvartar);
                    sation.setConversation_id(datas[1]);
                    fromid = datas[1];
                }
//                boolean isForground = App.getInstance().isForground();
//                if (!isForground) {
//                    Utils.sendNotification(this, "");
//                } else {
//                    boolean isopen = PreferenceUtils.getBoolean(this, PrefKey.AVOID_DISTURB + fromid, false);
//                    if (PreferenceUtils.getBoolean(this, "system_alerts", true) && !isopen) {
//                        long time2 = System.currentTimeMillis();
//                        if (time2 - time1 > 2000) {
//                            time1 = time2;
//                            startAlarm(this);
//                        }
//                    }
//                }
                sation.setNewest_msg_type(datas[3]);
                sation.setNewest_msg_time(datas[5] + "000");
                ConversationHelper.getInstance().save(sation);
                String noticeKey = PrefKey.NEWS_NOTICE_KEY + "/" + fromid;
                String msg = "";
                String notice = "";
                if (!datas[3].equals("group")) {
                    if (datas[0].equals("emotion")) {
                        msg = "[动画表情]";
                        notice = conversation + ":" + "[动画表情]";
                    } else if (datas[0].equals("picture")) {
                        msg = "[图片]";
                        notice = conversation + ":" + "[图片]";
                    } else if (datas[0].equals("video")) {
                        msg = "[视频]";
                        notice = conversation + ":" + "[视频]";
                    } else if (datas[0].equals("audio")) {
                        msg = "[声音]";
                        notice = conversation + ":" + "[声音]";
                    } else {
                        msg = sender + ":" + datas[2];
                        notice = conversation + ":" + datas[2];
                    }
                } else {
                    if (datas[0].equals("emotion")) {
                        msg = sender + "发了一条 [动画表情]";
                        notice = conversation + ":" + "发了一条 [动画表情]";
                    } else if (datas[0].equals("picture")) {
                        msg = sender + "发了一条 [图片]";
                        notice = conversation + ":" + "发了一条 [图片]";
                    } else if (datas[0].equals("video")) {
                        msg = sender + "发了一条 [视频]";
                        notice = conversation + ":" + "发了一条 [视频]";
                    } else if (datas[0].equals("audio")) {
                        msg = sender + "发了一条 [声音]";
                        notice = conversation + ":" + "发了一条 [声音]";
                    } else {
                        msg = sender + ":" + datas[2];
                        notice = conversation + ":" + datas[2];
                    }
                }
                boolean isForground = App.getInstance().isForground();
                if (!isForground) {
                    Utils.sendNotification(this, notice);
                } else {
                    boolean isopen = PreferenceUtils.getBoolean(this, PrefKey.AVOID_DISTURB + fromid, false);
                    if (PreferenceUtils.getBoolean(this, "system_alerts", true) && !isopen) {
                        long time2 = System.currentTimeMillis();
                        if (time2 - time1 > 2000) {
                            time1 = time2;
                            startAlarm(this);
                        }
                    }
                }

                PreferenceUtils.putString(this, noticeKey, msg);
                String key = PrefKey.UN_READ_NEWS_KEY + "/" + fromid;
                int num = PreferenceUtils.getInt(this, key, 0);
                num++;
                PreferenceUtils.putInt(this, key, num);
                Session.getInstance().refreshConversationPager();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveMessageItem(String[] datas, String sender, String senderAvartar) {
//        for (int i = 0; i < callbacks.size(); i++) {
        MessageItem item = null;
        String type = datas[0];
        String time = datas[5] + "000";
        if (type.equals("emotion")) {
            item = new MessageItem(MessageItem.MESSAGE_TYPE_EMOTION,
                    sender, 0,
                    datas[2], 0, true, 0,
                    0, time,
                    sender, datas[1],
                    senderAvartar);
        } else if (type.equals("picture")) {
            item = new MessageItem(MessageItem.MESSAGE_TYPE_IMG,
                    sender, 0,
                    datas[2], 0, true, 0,
                    0, time,
                    sender, datas[1],
                    senderAvartar);
        } else if (type.equals("video")) {
            item = new MessageItem(MessageItem.MESSAGE_TYPE_VIDEO,
                    sender, 0,
                    datas[2], 0, true, 0,
                    0, time,
                    sender, datas[1],
                    senderAvartar);
        } else if (type.equals("text")) {
            item = new MessageItem(MessageItem.MESSAGE_TYPE_TXT,
                    sender, 0,
                    datas[2], 0, true, 0,
                    0, time,
                    sender, datas[1],
                    senderAvartar);
        } else if (type.equals("audio")) {
            item = new MessageItem(MessageItem.MESSAGE_TYPE_AUDIO,
                    sender, 0,
                    datas[2], 0, true, 0,
                    getDuration(datas[2]), time,
                    sender, datas[1],
                    senderAvartar);
        }
        MycsLog.i("info", "=====userid:" + userid);
        String fromid = "";
        if (datas[3].equals("group")) {
            fromid = datas[4];
        } else {
            fromid = datas[1];
        }
        MessageItem data = mMsgDB.saveMsg(userid + "_" + fromid, item);// 保存数据库
        for (int i = 0; i < callbacks.size(); i++)
            ((receiveMessageCallback) callbacks.get(i)).onMessageReceive(data, fromid);
    }

    private int getDuration(String url) {
        Log.i("info", "===================url:" + url);
        String str = url.substring(url.indexOf("-"), url.length());
        String duration = str.substring(str.indexOf("-") + 1, str.indexOf("."));
        Log.i("info", "=======duration:" + duration);
        return Integer.parseInt(duration);
    }

    private void login() {
        if (mConnection != null && !TextUtils.isEmpty(userid)) {
            mConnection.sendTextMessage("login_" + userid);
        } else {
            startConnection();
        }
    }

    private void logout() {
        if (mConnection != null && !TextUtils.isEmpty(userid)) {
            Log.i("info", "==================mConnection:" + mConnection + "\n" + "userid:" + userid);
            mConnection.sendTextMessage("logout_" + userid);
        } else {
            startConnection();
        }
    }

    @Subscribe
    public void onMessageEvent(Motion event) {
        if (event != null) {
            int action = event.getAction();
            switch (action) {
                case Session.ACTION_SEND_IM_MESSAGE:
                    String msg = (String) event.getData();
                    if (msg != null && !msg.equals("")) {
                        sendMessage(msg);
                    }
                    break;
                case Session.ACTION_STOP_CHAT_SERVICE:
                    stopChatService(this);
                    break;
                case Session.ACTION_RE_CONNECT_WEBSOCKET:
                    if (mConnection != null) {
                        if (!mConnection.isConnected()) {
                            startConnection();
                        }
                    } else {
                        startConnection();
                    }
                    break;
            }
        }
    }

//    @Override
//    public void update(Observable observable, Object data) {
//        SessionInfo sin = (SessionInfo) data;
//        if (sin != null) {
//            if (sin.getAction() == Session.ACTION_SEND_IM_MESSAGE) {
//                String msg = (String) sin.getData();
//                if (msg != null && !msg.equals("")) {
//                    Log.i("info", "===================url:" + msg);
//                    sendMessage(msg);
//                }
//            } else if (sin.getAction() == Session.ACTION_STOP_CHAT_SERVICE) {
//                stopChatService(this);
//            } else if (sin.getAction() == Session.ACTION_RE_CONNECT_WEBSOCKET) {
//                if (mConnection != null) {
//                    if (!mConnection.isConnected()) {
//                        startConnection();
//                    }
//                } else {
//                    startConnection();
//                }
//            }
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        handler.sendEmptyMessage(1);
        EventBus.getDefault().unregister(this);
        if (mConnection.isConnected()) {
            logout();
            mConnection.disconnect();
        }
        if (App.getInstance().isLogin()) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Action.CHAT_SERVICE_CLOSE));
        }
        Log.i("info", "========ChatServices中onDestroy()");
    }

    //提示音
    private void startAlarm(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (notification == null) return;
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }


}
