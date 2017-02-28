package im.boss66.com.entity;

/**
 * Created by Johnny on 2017/2/20.
 * 表情搜索实体类
 */
public class EmoStore {
    private String group_id = "";
    private String cate_id = "";
    private String emo_id = "";
    private String sname = "";
    private String sdesc = "";
    private String url = "";
    private String icon = "";

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getEmo_id() {
        return emo_id;
    }

    public void setEmo_id(String emo_id) {
        this.emo_id = emo_id;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getSdesc() {
        return sdesc;
    }

    public void setSdesc(String sdesc) {
        this.sdesc = sdesc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCate_id() {
        return cate_id;
    }

    public void setCate_id(String cate_id) {
        this.cate_id = cate_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
