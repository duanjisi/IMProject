package im.boss66.com.db;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/10.
 */
public class EmoLoveColumn extends DatabaseColumn {
    public static final String TABLE_NAME = "emoLoveTable";
    public static final String EMO_LOVE_ICON = "emoLoveIcon";
    private static final Map<String, String> mColumnMap = new HashMap<String, String>();

    static {
        mColumnMap.put(_ID, "integer primary key autoincrement");
        mColumnMap.put(EMO_LOVE_ICON, "text");
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
