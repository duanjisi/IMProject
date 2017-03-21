package im.boss66.com.db;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/10.
 */
public class EmoColumn extends DatabaseColumn {
    public static final String TABLE_NAME = "emoTable";
    public static final String EMO_ID = "emo_id";
    public static final String EMO_NAME = "emo_name";
    public static final String EMO_DESC = "emo_desc";
    public static final String EMO_CATE_ID = "emo_cate_id";
    public static final String EMO_GROUP_ID = "emo_group_id";
    public static final String EMO_FORMAT = "emo_format";
    public static final String EMO_CODE = "emo_code";
    public static final String EMO_WIDTH = "emo_width";
    public static final String EMO_HEIGHT = "emo_height";
    public static final String USER_ID = "user_id";
    private static final Map<String, String> mColumnMap = new HashMap<String, String>();

    static {
        mColumnMap.put(_ID, "integer primary key autoincrement");
        mColumnMap.put(EMO_ID, "text");
        mColumnMap.put(EMO_NAME, "text");
        mColumnMap.put(EMO_DESC, "text");
        mColumnMap.put(EMO_CATE_ID, "text");
        mColumnMap.put(EMO_GROUP_ID, "text");
        mColumnMap.put(EMO_FORMAT, "text");
        mColumnMap.put(EMO_CODE, "text");
        mColumnMap.put(EMO_WIDTH, "text");
        mColumnMap.put(EMO_HEIGHT, "text");
        mColumnMap.put(USER_ID, "text");
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Map<String, String> getTableMap() {
        return mColumnMap;
    }
}
