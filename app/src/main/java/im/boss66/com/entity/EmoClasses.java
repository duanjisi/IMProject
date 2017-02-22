package im.boss66.com.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import im.boss66.com.http.HttpUtil;

/**
 * Created by Johnny on 2017/2/9.
 */
public class EmoClasses {
    private String createtime = "";
    private String version = "";
    private ArrayList<EmoCate> category = new ArrayList<>();

    public static EmoClasses parseEntity(JSONObject jsonObject) throws JSONException {
        EmoClasses emoClasses = null;
        JSONArray array = jsonObject.getJSONArray("category");
        if (array != null && array.length() != 0) {
            emoClasses = new EmoClasses();
            ArrayList<EmoCate> cates = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                EmoCate cate = new EmoCate();
                cate.setCate_desc(HttpUtil.getString(object, "cate_id"));
                cate.setCate_name(HttpUtil.getString(object, "cate_name"));
                cate.setCate_desc(HttpUtil.getString(object, "cate_desc"));
                ArrayList<EmoGroup> groups = null;
                JSONArray array1 = object.getJSONArray("group");
                if (array1 != null && array1.length() != 0) {
                    groups = new ArrayList<>();
                    for (int j = 0; j < array1.length(); j++) {
                        JSONObject object1 = array1.getJSONObject(j);
                        EmoGroup group = new EmoGroup();
                        group.setCate_id(HttpUtil.getString(object1, "cate_id"));
                        group.setGroup_id(HttpUtil.getString(object1, "group_id"));
                        group.setGroup_name(HttpUtil.getString(object1, "group_name"));
                        group.setGroup_icon(HttpUtil.getString(object1, "group_icon"));
                        group.setGroup_count(HttpUtil.getString(object1, "group_count"));
                        group.setGroup_cover(HttpUtil.getString(object1, "group_cover"));
                        group.setGroup_format(HttpUtil.getString(object1, "group_format"));
                        group.setGroup_desc(HttpUtil.getString(object1, "group_desc"));
                        ArrayList<EmoEntity> emos = null;
                        JSONArray array2 = object1.getJSONArray("");
                        if (array2 != null && array2.length() != 0) {
                            emos = new ArrayList<>();
                            for (int k = 0; k < array2.length(); k++) {
                                JSONObject object2 = array2.getJSONObject(k);
                                EmoEntity entity = new EmoEntity();
                                entity.setEmo_cate_id(HttpUtil.getString(object2, "emo_cate_id"));
                                entity.setEmo_code(HttpUtil.getString(object2, "emo_code"));
                                entity.setEmo_desc(HttpUtil.getString(object2, "emo_desc"));
                                entity.setEmo_format(HttpUtil.getString(object2, "emo_format"));
                                entity.setEmo_group_id(HttpUtil.getString(object2, "emo_group_id"));
                                entity.setEmo_id(HttpUtil.getString(object2, "emo_id"));
                                entity.setEmo_name(HttpUtil.getString(object2, "emo_name"));
                                emos.add(entity);
                            }
                            group.setEmo(emos);
                        }
                        groups.add(group);
                    }
                    cate.setGroup(groups);
                }
                cates.add(cate);
            }
            emoClasses.setCategory(cates);
        }
        return emoClasses;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<EmoCate> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<EmoCate> category) {
        this.category = category;
    }
}
