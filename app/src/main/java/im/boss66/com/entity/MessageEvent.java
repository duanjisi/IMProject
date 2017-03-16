package im.boss66.com.entity;

/**
 * Created by Johnny on 2017/3/11.
 */
public class MessageEvent {
    private String action;

    public MessageEvent(String action) {
        this.action = action;
    }
    public String getAction() {
        return action;
    }
}
