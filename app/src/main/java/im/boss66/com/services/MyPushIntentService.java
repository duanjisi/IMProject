package im.boss66.com.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.umeng.common.message.Log;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.client.BaseConstants;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import im.boss66.com.Constants;
import im.boss66.com.activity.book.NewFriendsActivity;
import im.boss66.com.entity.MessageEvent;
import im.boss66.com.entity.MessageEvent2;

/**
 * Developer defined push intent service.
 * Remember to call {@link com.umeng.message.PushAgent#setPushIntentServiceClass(Class)}.
 *
 * @author lucas
 */
//完全自定义处理类
//参考文档的1.6.5
//http://dev.umeng.com/push/android/integration#1_6_5
public class MyPushIntentService extends UmengBaseIntentService {
    private static final String TAG = MyPushIntentService.class.getName();
    private static final int notifyID = 1002;
    private static final int notifyID_FRIEND_ADD = 1003;
    private static final int notifyID_FRIEND_REPLY = 1004;
    private static final int notifyID_ADERTISING = 1005;
    private static final int notifyID_REDPACKET = 1006;


    private static MyPushIntentService pushIntentService;

    public static MyPushIntentService getInstance() {
        if (pushIntentService == null) {
            pushIntentService = new MyPushIntentService();
        }
        return pushIntentService;
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        // 需要调用父类的函数，否则无法统计到消息送达
        super.onMessage(context, intent);
        try {
            android.util.Log.i("info", "=======bundle:" + printBundle(intent.getExtras()));
            String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
            sendNotification(context, message);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void printIntent(Intent intent) {
        String sourse = intent.getStringExtra(BaseConstants.MESSAGE_SOURCE);
        String type = intent.getStringExtra(BaseConstants.MESSAGE_TYPE);
        String notification = intent.getStringExtra(BaseConstants.MESSAGE_NOTIFICATION);
        android.util.Log.i("info", "---------sourse:" + sourse);
        android.util.Log.i("info", "---------type:" + type);
        android.util.Log.i("info", "---------notification:" + notification);
    }

    // 打印所有的 intent extra 数据.
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
        }
        return sb.toString();
    }

    private void sendNotification(Context appContext, String message) {
        try {
            String packageName = appContext.getApplicationInfo().packageName;
            UMessage msg = new UMessage(new JSONObject(message));
            NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
            PackageManager packageManager = appContext.getPackageManager();
            String appname = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                    .setSmallIcon(appContext.getApplicationInfo().icon)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true);
            int notificationId = 0;
            String msgType = msg.custom;
            String notice = msg.text;
            Intent intent = null;
            if (msgType.equals("chat")) {
//                Intent it = null;
                if (notice.contains("已成为你的好友!")) {
                    intent = new Intent();
                    android.util.Log.i("info", "=============receiver()");

//                    if (pushCallback != null) {
//                        pushCallback.onMessageReceiver(Constants.Action.CHAT_AGREE_FRIENDSHIP);
//                    }
//                    Session.getInstance().refreshContacts(null, Constants.Action.CHAT_AGREE_FRIENDSHIP);
//                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Constants.Action.CHAT_AGREE_FRIENDSHIP));
//                    senBroadCast(appContext, Constants.Action.CHAT_AGREE_FRIENDSHIP);
                    notificationId = notifyID_FRIEND_REPLY;
                } else {
                    notificationId = notifyID_FRIEND_ADD;
//                    it = new Intent(Constants.Action.CHAT_NEW_MESSAGE_NOTICE);
                    android.util.Log.i("info", "=====8888888========add()");
                    intent = new Intent(appContext, NewFriendsActivity.class);
//                    EventBus.getDefault().post(new MessageEvent(Constants.Action.CHAT_NEW_MESSAGE_NOTICE));

                    EventBus.getDefault().post(new MessageEvent2("哈哈"));
                    android.util.Log.i("liwya","EventBus.getDefault()");
//                    ContactBooksFragment.onMessage(Constants.Action.CHAT_NEW_MESSAGE_NOTICE);
//                    App.getInstance().getFragment().onChatMessageReceiver(Constants.Action.CHAT_NEW_MESSAGE_NOTICE);

//                    if (pushCallback != null) {
//                        android.util.Log.i("info", "=====6666666========add()");
//                        pushCallback.onMessageReceiver(Constants.Action.CHAT_NEW_MESSAGE_NOTICE);
//                    }
//                    Session.getInstance().refreshContacts(null, Constants.Action.CHAT_NEW_MESSAGE_NOTICE);
                    senBroadCast(appContext, Constants.Action.CHAT_NEW_MESSAGE_NOTICE);
//                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(Constants.Action.CHAT_NEW_MESSAGE_NOTICE));
                }
//                if (it != null) {
//                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(it);
//                }
                mBuilder.setContentTitle(msg.title);
                mBuilder.setTicker(notice);
                mBuilder.setContentText(notice);
            } else if (msgType.equals("cheer")) {
//                intent = new Intent(appContext, WebPagerActivity.class);
//                intent.putExtra("title", msg.title);
//                intent.putExtra("url", msg.url);
//                notificationId = notifyID_ADERTISING;
//                mBuilder.setContentTitle(msg.title);
//                mBuilder.setTicker(notice);
//                mBuilder.setContentText(notice);
            } else if (msgType.equals("redpacket")) {
//                intent = new Intent(appContext, RedPacketRecordActivity.class);
////                intent.putExtra("title", msg.title);
////                intent.putExtra("url", msg.url);
//                notificationId = notifyID_REDPACKET;
//                mBuilder.setContentTitle(msg.title);
//                mBuilder.setTicker(notice);
//                mBuilder.setContentText(notice);
            } else if (msgType.equals("info")) {
                intent = new Intent();
                notificationId = notifyID;
            } else {
                intent = new Intent();
                notificationId = notifyID;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            Notification notification = mBuilder.build();
            notification.defaults = Notification.DEFAULT_SOUND;
            notificationManager.notify(notificationId, notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private pushCallback pushCallback;

    public void setPushCallback(MyPushIntentService.pushCallback pushCallback) {
        this.pushCallback = pushCallback;
    }

    public interface pushCallback {
        void onMessageReceiver(String action);
    }

    private void senBroadCast(Context context, String action) {
        Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }
}
