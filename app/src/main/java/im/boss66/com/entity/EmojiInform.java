package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/18.
 * 表情包详细信息
 */
public class EmojiInform {
    private String group_id = "";
    private String cate_id = "";
    private String group_name = "";
    private String group_count = "";
    private String group_desc = "";
    private String group_cover = "";
    private String group_icon = "";
    private String group_down = "";
    private String group_attachment = "";
    private String group_format = "";
    private String group_custom = "";
    private String group_copyright = "";
    private String group_system = "";
    private String url = "";

    private ArrayList<EmoEntity> emos = new ArrayList<>();

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getCate_id() {
        return cate_id;
    }

    public void setCate_id(String cate_id) {
        this.cate_id = cate_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_count() {
        return group_count;
    }

    public void setGroup_count(String group_count) {
        this.group_count = group_count;
    }

    public String getGroup_desc() {
        return group_desc;
    }

    public void setGroup_desc(String group_desc) {
        this.group_desc = group_desc;
    }

    public String getGroup_cover() {
        return group_cover;
    }

    public void setGroup_cover(String group_cover) {
        this.group_cover = group_cover;
    }

    public String getGroup_icon() {
        return group_icon;
    }

    public void setGroup_icon(String group_icon) {
        this.group_icon = group_icon;
    }

    public String getGroup_down() {
        return group_down;
    }

    public void setGroup_down(String group_down) {
        this.group_down = group_down;
    }

    public String getGroup_attachment() {
        return group_attachment;
    }

    public void setGroup_attachment(String group_attachment) {
        this.group_attachment = group_attachment;
    }

    public String getGroup_format() {
        return group_format;
    }

    public void setGroup_format(String group_format) {
        this.group_format = group_format;
    }

    public String getGroup_custom() {
        return group_custom;
    }

    public void setGroup_custom(String group_custom) {
        this.group_custom = group_custom;
    }

    public String getGroup_copyright() {
        return group_copyright;
    }

    public void setGroup_copyright(String group_copyright) {
        this.group_copyright = group_copyright;
    }

    public String getGroup_system() {
        return group_system;
    }

    public void setGroup_system(String group_system) {
        this.group_system = group_system;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<EmoEntity> getEmos() {
        return emos;
    }

    public void setEmos(ArrayList<EmoEntity> emos) {
        this.emos = emos;
    }
}
