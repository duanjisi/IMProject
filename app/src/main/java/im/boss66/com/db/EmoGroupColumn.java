package im.boss66.com.db;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/10.
 */
public class EmoGroupColumn extends DatabaseColumn {

    public static final String TABLE_NAME = "emoGroupTable";
    public static final String GROUP_ID = "groupId";
    public static final String CATE_ID = "cate_id";
    public static final String GROUP_NAME = "group_name";
    public static final String GROUP_DESC = "group_desc";
    public static final String GROUP_COUNT = "group_count";
    public static final String GROUP_COVER = "group_cover";
    public static final String GROUP_ICON = "group_icon";
    public static final String GROUP_FORMAT = "group_format";
    public static final String USER_ID = "user_id";
    private static final Map<String, String> mColumnMap = new HashMap<String, String>();

    static {
        mColumnMap.put(_ID, "integer primary key autoincrement");
        mColumnMap.put(GROUP_ID, "text");
        mColumnMap.put(CATE_ID, "text");
        mColumnMap.put(GROUP_NAME, "text");
        mColumnMap.put(GROUP_DESC, "text");
        mColumnMap.put(GROUP_COUNT, "text");
        mColumnMap.put(GROUP_COVER, "text");
        mColumnMap.put(GROUP_ICON, "text");
        mColumnMap.put(GROUP_FORMAT, "text");
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
