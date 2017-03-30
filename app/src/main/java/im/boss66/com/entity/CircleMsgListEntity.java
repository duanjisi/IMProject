package im.boss66.com.entity;

import java.util.List;

/**
 * 朋友圈消息
 */
public class CircleMsgListEntity {
    private String name;
    private String version;

    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    private int code;
    private String type;
    private List<CircleMsgItem> result;

    public List<CircleMsgItem> getResult() {
        return result;
    }

    public void setResult(List<CircleMsgItem> result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    private int status;

    public class CircleMsgItem {
        public int getMsg_id() {
            return msg_id;
        }

        public void setMsg_id(int msg_id) {
            this.msg_id = msg_id;
        }

        public String getAdd_time() {
            return add_time;
        }

        public void setAdd_time(String add_time) {
            this.add_time = add_time;
        }

        public String getFeed_file() {
            return feed_file;
        }

        public void setFeed_file(String feed_file) {
            this.feed_file = feed_file;
        }

        public int getFeed_id() {
            return feed_id;
        }

        public void setFeed_id(int feed_id) {
            this.feed_id = feed_id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getMsg_content() {
            return msg_content;
        }

        public void setMsg_content(String msg_content) {
            this.msg_content = msg_content;
        }

        public String getFeed_content() {
            return feed_content;
        }

        int msg_id;
        String msg_content;
        String user_id;
        String user_name;
        String avatar;
        int feed_id;
        String feed_file;
        String add_time;
        String feed_content;
    }
}
