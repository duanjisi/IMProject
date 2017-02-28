package im.boss66.com.db;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/10.
 */
public class EmoLoveColumn extends DatabaseColumn {
    public static final String TABLE_NAME = "emoLoveTable";
    //    public static final String EMO_LOVE_ICON = "collect_id";
    public static final String EMO_LOVE_COLL_ID = "collect_id";
    public static final String EMO_LOVE_USER_ID = "user_id";
    public static final String EMO_LOVE_ID = "emo_id";
    public static final String EMO_LOVE_NAME = "emo_name";
    public static final String EMO_LOVE_URL = "emo_url";
    public static final String EMO_LOVE_DESC = "emo_desc";
    public static final String USER_ID = "user_id";
    private static final Map<String, String> mColumnMap = new HashMap<String, String>();

    static {
        mColumnMap.put(_ID, "integer primary key autoincrement");
//        mColumnMap.put(EMO_LOVE_ICON, "text");
        mColumnMap.put(EMO_LOVE_COLL_ID, "text");
        mColumnMap.put(EMO_LOVE_USER_ID, "text");
        mColumnMap.put(EMO_LOVE_ID, "text");
        mColumnMap.put(EMO_LOVE_NAME, "text");
        mColumnMap.put(EMO_LOVE_URL, "text");
        mColumnMap.put(EMO_LOVE_DESC, "text");
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
