package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/9.
 */
public class EmoGroup {
    private String group_id = "";//组id
    private String cate_id = "";//分类id
    private String group_name = "";//组名
    private String group_desc = "";//组描述
    private String group_count = "";//组下表情个数
    private String group_cover = "";//组下封面图
    private String group_icon = "";//组图标
    private String group_format = "";//组图标格式
    private ArrayList<EmoEntity> emo = new ArrayList<>();

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

    public String getGroup_desc() {
        return group_desc;
    }

    public void setGroup_desc(String group_desc) {
        this.group_desc = group_desc;
    }

    public String getGroup_count() {
        return group_count;
    }

    public void setGroup_count(String group_count) {
        this.group_count = group_count;
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

    public String getGroup_format() {
        return group_format;
    }

    public void setGroup_format(String group_format) {
        this.group_format = group_format;
    }

    public ArrayList<EmoEntity> getEmo() {
        return emo;
    }

    public void setEmo(ArrayList<EmoEntity> emo) {
        this.emo = emo;
    }
}
