package im.boss66.com.entity;

/**
 * Created by Johnny on 2017/2/9.
 */
public class EmoEntity {
    private String emo_id = "";
    private String emo_name = "";
    private String emo_desc = "";
    private String emo_cate_id = "";//分类id
    private String emo_group_id = "";//组id
    private String emo_format = "";//格式png,jpg,gif
    private String emo_code = "";
    private String url = "";
    private String emo_url = "";

    public String getEmo_id() {
        return emo_id;
    }

    public void setEmo_id(String emo_id) {
        this.emo_id = emo_id;
    }

    public String getEmo_name() {
        return emo_name;
    }

    public void setEmo_name(String emo_name) {
        this.emo_name = emo_name;
    }

    public String getEmo_desc() {
        return emo_desc;
    }

    public void setEmo_desc(String emo_desc) {
        this.emo_desc = emo_desc;
    }

    public String getEmo_cate_id() {
        return emo_cate_id;
    }

    public void setEmo_cate_id(String emo_cate_id) {
        this.emo_cate_id = emo_cate_id;
    }

    public String getEmo_group_id() {
        return emo_group_id;
    }

    public void setEmo_group_id(String emo_group_id) {
        this.emo_group_id = emo_group_id;
    }

    public String getEmo_format() {
        return emo_format;
    }

    public void setEmo_format(String emo_format) {
        this.emo_format = emo_format;
    }

    public String getEmo_code() {
        return emo_code;
    }

    public void setEmo_code(String emo_code) {
        this.emo_code = emo_code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmo_url() {
        return emo_url;
    }

    public void setEmo_url(String emo_url) {
        this.emo_url = emo_url;
    }
}
