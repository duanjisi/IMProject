package im.boss66.com.entity;

import java.io.Serializable;

/**
 * 朋友圈评论
 */
public class FriendCircleCommentEntity implements Serializable{

    private String comm_id;//评论ID
    private String feed_id;//朋友圈ID
    private String content;//评论内容
    private String uid_from;//评论者ID
    private String uid_from_name;//评论者用户名称
    private String uid_to;//被评论者ID
    private String uid_to_name;//被评论者的ID
    private String pid;//回复的评论ID，此值大于0，则表示是回复

    public String getComm_id() {
        return comm_id;
    }

    public void setComm_id(String comm_id) {
        this.comm_id = comm_id;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid_from() {
        return uid_from;
    }

    public void setUid_from(String uid_from) {
        this.uid_from = uid_from;
    }

    public String getUid_from_name() {
        return uid_from_name;
    }

    public void setUid_from_name(String uid_from_name) {
        this.uid_from_name = uid_from_name;
    }

    public String getUid_to() {
        return uid_to;
    }

    public void setUid_to(String uid_to) {
        this.uid_to = uid_to;
    }

    public String getUid_to_name() {
        return uid_to_name;
    }

    public void setUid_to_name(String uid_to_name) {
        this.uid_to_name = uid_to_name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

}
