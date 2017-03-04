package im.boss66.com.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GMARUnity on 2017/3/3.
 */
public class SearchCofriendEntity {

    public String feed_id;
    public String feed_uid;
    public String feed_uname;
    public String feed_avatar;
    public String content;
    public String add_time;
    public String feed_type;
    public List<PhotoInfo> fileList = new ArrayList<>();

    public String getFeed_type() {
        return feed_type;
    }

    public void setFeed_type(String feed_type) {
        this.feed_type = feed_type;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFeed_avatar() {
        return feed_avatar;
    }

    public void setFeed_avatar(String feed_avatar) {
        this.feed_avatar = feed_avatar;
    }

    public String getFeed_uid() {
        return feed_uid;
    }

    public void setFeed_uid(String feed_uid) {
        this.feed_uid = feed_uid;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }

    public String getFeed_uname() {
        return feed_uname;
    }

    public void setFeed_uname(String feed_uname) {
        this.feed_uname = feed_uname;
    }

    public List<String> files;

}
