package im.boss66.com;

import android.content.Context;

import java.util.Observable;

/**
 * Created by Johnny on 2017/1/17.
 */
public class Session extends Observable {
    private static Session session = null;
    public static final int ACTION_REFRSH_CONVERSATION_PAGE = 1011;
    public static final int ACTION_DELETE_CURRENT_EMOJI_GROUP = 1012;
    public static final int ACTION_APPLICATION_EXIT = 1013;//退出当前所有activity
    public static final int ACTION_REFRESH_VIEWS = 1014;//刷新视图
    public static final int ACTION_SEND_IM_MESSAGE = 1015;//发送信息
    public static final int ACTION_STOP_CHAT_SERVICE = 1016;//发送信息
    public static final int ACTION_PREV_CLOSE_ACTIVITY = 1017;//关闭上一个activity
    public static final int ACTION_CONTACTS_REFRESH_PAGER = 1018;//刷新通讯录数据
//    public static final int ACTION_STOP_PUSH_SERVICE = 1019;//关闭推送服务

    public Session() {

    }

    public static Session getInstance() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    public void refreshConversationPager() {
        SessionInfo sin = new SessionInfo();
        sin.setAction(ACTION_REFRSH_CONVERSATION_PAGE);
        this.setChanged();
        this.notifyObservers(sin);
    }

    public void deleteEmojiGroup(String groupid) {
        SessionInfo sin = new SessionInfo();
        sin.setAction(ACTION_DELETE_CURRENT_EMOJI_GROUP);
        sin.setData(groupid);
        this.setChanged();
        this.notifyObservers(sin);
    }

    public void exitActivitys() {
        SessionInfo sin = new SessionInfo();
        sin.setAction(ACTION_APPLICATION_EXIT);
        this.setChanged();
        this.notifyObservers(sin);
    }

    public void actionRefreshViews(String userid, int tag) {
        SessionInfo sin = new SessionInfo();
        sin.setAction(ACTION_REFRESH_VIEWS);
        sin.setData(userid);
        sin.setTag(tag);
        this.setChanged();
        this.notifyObservers(sin);
    }

    public void sendImMessage(String msg) {
        SessionInfo sin = new SessionInfo();
        sin.setAction(ACTION_SEND_IM_MESSAGE);
        sin.setData(msg);
        this.setChanged();
        this.notifyObservers(sin);
    }

    public void stopChatService() {
        SessionInfo sin = new SessionInfo();
        sin.setAction(ACTION_STOP_CHAT_SERVICE);
        this.setChanged();
        this.notifyObservers(sin);
    }

//    public void stopPushService() {
//        SessionInfo sin = new SessionInfo();
//        sin.setAction(ACTION_STOP_PUSH_SERVICE);
//        this.setChanged();
//        this.notifyObservers(sin);
//    }

    public void closePreActivity(String imagePath) {
        SessionInfo sin = new SessionInfo();
        sin.setAction(ACTION_PREV_CLOSE_ACTIVITY);
        sin.setData(imagePath);
        this.setChanged();
        this.notifyObservers(sin);
    }

    public void refreshContacts(Context context, String action) {
        SessionInfo sin = new SessionInfo();
        sin.setAction(ACTION_CONTACTS_REFRESH_PAGER);
        sin.setData(action);
        this.setChanged();
        this.notifyObservers(sin);
//        Log.i("info", "============================refreshContacts()");

//        Log.i("info", "=========context:" + App.getInstance().getActivity());
//        if (context != null) {
//            Log.i("info", "=========action:" + action);
//            LocalBroadcastManager.getInstance(App.getInstance().getActivity()).sendBroadcast(new Intent(action));
//        }

    }
}
