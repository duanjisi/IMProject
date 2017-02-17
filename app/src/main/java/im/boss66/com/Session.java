package im.boss66.com;

import java.util.Observable;

/**
 * Created by Johnny on 2017/1/17.
 */
public class Session extends Observable {
    private static Session session = null;
    public static final int ACTION_REFRSH_CONVERSATION_PAGE = 1011;
    public static final int ACTION_DELETE_CURRENT_EMOJI_GROUP = 1012;
    public static final int ACTION_APPLICATION_EXIT = 1013;//退出当前所有activity

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
}
