package im.boss66.com.entity;

import java.util.ArrayList;

/**
 * Created by Johnny on 2017/2/9.
 */
public class EmoCate {
    private String cate_id = "";//分类id
    private String cate_name = "";//分类名称
    private String cate_desc = "";//分类描述
    private ArrayList<EmoGroup> group = new ArrayList<>();

    public String getCate_id() {
        return cate_id;
    }

    public void setCate_id(String cate_id) {
        this.cate_id = cate_id;
    }

    public String getCate_name() {
        return cate_name;
    }

    public void setCate_name(String cate_name) {
        this.cate_name = cate_name;
    }

    public String getCate_desc() {
        return cate_desc;
    }

    public void setCate_desc(String cate_desc) {
        this.cate_desc = cate_desc;
    }

    public ArrayList<EmoGroup> getGroup() {
        return group;
    }

    public void setGroup(ArrayList<EmoGroup> group) {
        this.group = group;
    }
}
