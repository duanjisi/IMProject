package im.boss66.com.entity;

/**
 * Created by Johnny on 2017/2/18.
 * 精选表情广告
 */
public class EmoAdEntity {
    private String group_id = "";
    private String group_name = "";
    private String cate_id = "";
    private String group_format = "";
    private String group_cover = "";
    private String url = "";

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getCate_id() {
        return cate_id;
    }

    public void setCate_id(String cate_id) {
        this.cate_id = cate_id;
    }

    public String getGroup_format() {
        return group_format;
    }

    public void setGroup_format(String group_format) {
        this.group_format = group_format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGroup_cover() {
        return group_cover;
    }

    public void setGroup_cover(String group_cover) {
        this.group_cover = group_cover;
    }
}
