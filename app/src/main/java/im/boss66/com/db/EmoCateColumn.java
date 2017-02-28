package im.boss66.com.db;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/10.
 */
public class EmoCateColumn extends DatabaseColumn {

    public static final String TABLE_NAME = "emoCateTable";
    public static final String EMO_CATE_NAME = "emoCateName";
    public static final String EMO_CATE_ID = "emoCateId";
    public static final String EMO_CATE_DESC = "emoCateDesc";
    public static final String USER_ID = "user_id";
    private static final Map<String, String> mColumnMap = new HashMap<String, String>();

    static {
        mColumnMap.put(_ID, "integer primary key autoincrement");
        mColumnMap.put(EMO_CATE_ID, "text");
        mColumnMap.put(EMO_CATE_NAME, "text");
        mColumnMap.put(EMO_CATE_DESC, "text");
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
