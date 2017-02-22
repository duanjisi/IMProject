package im.boss66.com.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import im.boss66.com.App;
import im.boss66.com.Session;
import im.boss66.com.Utils.Base64Utils;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.db.MessageDB;
import im.boss66.com.db.dao.ConversationHelper;
import im.boss66.com.entity.BaseConversation;
import im.boss66.com.entity.MessageItem;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/1/16.
 */
public class ChatServices extends Service {
    private static WebSocket mConnection = new WebSocketConnection();
    public static ArrayList<receiveMessageCallback> callbacks = new ArrayList<>();
    private String userid;
    private MessageDB mMsgDB;// 保存消息的数据库

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMsgDB = App.getInstance().getMessageDB();// 发送数据库
        startConnection();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        userid = intent.getExtras().getString("userid", "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public interface receiveMessageCallback {
        //        void onMessageReceive(String msgType, String senderId,
//                              String senderName, String senderAvatar,
//                              String message, String chatType,
//                              String groupId, String temp);
        void onMessageReceive(MessageItem item);

        void onNotify(String title, String content);

        void onNetChange(boolean isNetConnected);
    }

    private void startConnection() {
        try {
            mConnection.connect(HttpUrl.WS_URL, new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {
                    MycsLog.i("info", "======建立连接!");
                    login();
                }

                @Override
                public void onTextMessage(String payload) {
                    messageHandle(payload);
                }

                @Override
                public void onClose(int code, String reason) {
                    MycsLog.i("info", "======reason:" + reason);
                    for (int i = 0; i < callbacks.size(); i++)
                        ((receiveMessageCallback) callbacks.get(i)).onNotify("", reason);
                }
            });
        } catch (Exception e) {
            Log.d("info", "=====Exception:" + e.toString());
        }
    }


    public static void sendMessage(String msg) {
        if (mConnection != null && msg != null && !msg.equals("")) {
            mConnection.sendTextMessage(msg);
        }
    }

//    private void messageHandle(String payload) {
//        for (int i = 0; i < callbacks.size(); i++)
//            ((receiveMessageCallback) callbacks.get(i)).onMessageReceive(payload);
//    }


    private void messageHandle(String str) {
        MycsLog.i("info", "====str:" + str);
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
//                for (int i = 0; i < callbacks.size(); i++)
//                    ((receiveMessageCallback) callbacks.get(i)).onMessageReceive(
//                            datas[0], datas[1],
//                            sender, senderAvartar,
//                            datas[2], datas[3],
//                            datas[4], datas[5]);
                saveMessageItem(datas, sender, senderAvartar);
                BaseConversation sation = new BaseConversation();
                sation.setUser_name(conversation);
                sation.setAvatar(conversationAvatar);
                sation.setUser_id(datas[4]);
                sation.setNewest_msg_type(datas[3]);
                sation.setNewest_msg_time(datas[5] + "000");
                ConversationHelper.getInstance().save(sation);
                String noticeKey = PrefKey.NEWS_NOTICE_KEY + "/" + conversationAvatar;
                String msg = "";
                if (!datas[3].equals("group")) {
                    if (datas[0].equals("emotion")) {
                        msg = "[动画表情]";
                    } else if (datas[0].equals("picture")) {
                        msg = "[图片]";
                    } else if (datas[0].equals("video")) {
                        msg = "[视频]";
                    } else {
                        msg = "";
                    }
                } else {
                    if (datas[0].equals("emotion")) {
                        msg = sender + "发了一条 [动画表情]";
                    } else if (datas[0].equals("picture")) {
                        msg = sender + "发了一条 [图片]";
                    } else if (datas[0].equals("video")) {
                        msg = sender + "发了一条 [视频]";
                    } else {
                        msg = "";
                    }
                }
                PreferenceUtils.putString(this, noticeKey, msg);
                String key = PrefKey.UN_READ_NEWS_KEY + "/" + conversationAvatar;
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
                    0, time,
                    sender, datas[1],
                    senderAvartar);
        }
        MycsLog.i("info", "=====userid:" + userid);
        mMsgDB.saveMsg(userid, item);// 保存数据库
        for (int i = 0; i < callbacks.size(); i++)
            ((receiveMessageCallback) callbacks.get(i)).onMessageReceive(item);
    }

    private void login() {
        mConnection.sendTextMessage("login_" + userid);
    }

    private void logout() {
        mConnection.sendTextMessage("logout_" + userid);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConnection.isConnected()) {
            logout();
            mConnection.disconnect();
        }
        Log.i("info", "========ChatServices中onDestroy()");
    }
}
