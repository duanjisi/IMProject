package im.boss66.com.entity;

/**
 * Created by Johnny on 2016/7/11.
 */
public class BaseConversation {
    private String user_id = "";
    private String user_name = "";//如果单聊则是用户名,群聊则是群名
    private String avatar = "";
    private String unread_msg_count = "";
    private String newest_msg_time = "";
    private String newest_msg_type = "";//unicast, group 单聊，群聊
    private String newest_msg = "";

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getUnread_msg_count() {
        return unread_msg_count;
    }

    public void setUnread_msg_count(String unread_msg_count) {
        this.unread_msg_count = unread_msg_count;
    }

    public String getNewest_msg_time() {
        return newest_msg_time;
    }

    public void setNewest_msg_time(String newest_msg_time) {
        this.newest_msg_time = newest_msg_time;
    }

    public String getNewest_msg_type() {
        return newest_msg_type;
    }

    public void setNewest_msg_type(String newest_msg_type) {
        this.newest_msg_type = newest_msg_type;
    }

    public String getNewest_msg() {
        return newest_msg;
    }

    public void setNewest_msg(String newest_msg) {
        this.newest_msg = newest_msg;
    }
}
