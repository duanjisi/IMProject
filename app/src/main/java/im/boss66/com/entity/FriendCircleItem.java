package im.boss66.com.entity;

import java.io.Serializable;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public class FriendCircleItem implements Serializable {

    private String id;
    private User user;
    private User toReplyUser;
    private String content;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public User getToReplyUser() {
        return toReplyUser;
    }
    public void setToReplyUser(User toReplyUser) {
        this.toReplyUser = toReplyUser;
    }

}
