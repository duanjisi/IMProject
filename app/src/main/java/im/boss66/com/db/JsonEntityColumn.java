package im.boss66.com.db;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/10.
 */
public class JsonEntityColumn extends DatabaseColumn {

    public static final String TABLE_NAME = "jsonTable";
    public static final String USER_ID = "userId";
    public static final String JSON_TEXT = "json";
    public static final String AUTH_KEY = "authKey";

    private static final Map<String, String> mColumnMap = new HashMap<String, String>();

    static {
        mColumnMap.put(_ID, "integer primary key autoincrement");
        mColumnMap.put(USER_ID, "text");
        mColumnMap.put(JSON_TEXT, "text");
        mColumnMap.put(AUTH_KEY, "text");
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
