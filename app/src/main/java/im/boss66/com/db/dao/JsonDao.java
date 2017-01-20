package im.boss66.com.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.db.DBHelper;
import im.boss66.com.db.JsonEntityColumn;
import im.boss66.com.db.helper.ColumnHelper;
import im.boss66.com.entity.JsonEntity;


/**
 * Created by Administrator on 2015/7/30.
 */
public class JsonDao extends ColumnHelper<JsonEntity> {
    private static JsonDao sAccountDao;
    private Context mContext;

    private JsonDao() {
        mContext = App.getInstance().getApplicationContext();
    }

    public static JsonDao getInstance() {
        if (sAccountDao == null) {
            synchronized (JsonDao.class) {
                if (sAccountDao == null) {
                    sAccountDao = new JsonDao();
                }
            }
        }
        return sAccountDao;
    }


    @Override
    protected ContentValues getValues(JsonEntity bean) {
        ContentValues values = new ContentValues();
        values.put(JsonEntityColumn.USER_ID, bean.getUserId());
        values.put(JsonEntityColumn.AUTH_KEY, bean.getAuthKey());
        values.put(JsonEntityColumn.JSON_TEXT, bean.getJson());
        return values;
    }

    @Override
    protected JsonEntity getBean(Cursor c) {
        JsonEntity entity = new JsonEntity();
        entity.setUserId(getString(c, JsonEntityColumn.USER_ID));
        entity.setAuthKey(getString(c, JsonEntityColumn.AUTH_KEY));
        entity.setJson(getString(c, JsonEntityColumn.JSON_TEXT));
        return entity;
    }


    @Override
    public void save(List<JsonEntity> list) {

    }

    @Override
    public void save(JsonEntity entity) {
        String[] args = new String[]{entity.getAuthKey(), entity.getUserId()};
//        Cursor c = DBHelper.getInstance(mContext).rawQuery(
//                getSelectSql(JsonEntityColumn.TABLE_NAME,
//                        new String[]{JsonEntityColumn.AUTH_KEY
//
//                                , JsonEntityColumn.USER_ID}), args);
//        if (exist(c)) {
//            c.moveToFirst();
//            this.delete(entity.getAuthKey(), entity.getUserId());
//        }
        this.delete(entity.getAuthKey(), entity.getUserId());
        DBHelper.getInstance(mContext).insert(JsonEntityColumn.TABLE_NAME, getValues(entity));
//        MycsLog.i(entity.toString());
//        if (c != null) {
//            c.close();
//        }
    }

    @Override
    public JsonEntity query(int id) {
        return null;
    }

    public JsonEntity QureJson(String authKey, String userId) {
        JsonEntity user = null;
        String[] args = new String[]{authKey, userId};
        Cursor cursor = null;
        try {
            cursor = DBHelper.getInstance(mContext).rawQuery(
                    getSelectSql(JsonEntityColumn.TABLE_NAME, new String[]{JsonEntityColumn.AUTH_KEY
                            , JsonEntityColumn.USER_ID}), args);
            if (cursor != null && !cursor.isClosed() && exist(cursor)) {
                cursor.moveToFirst();
                user = getBean(cursor);
            }
//            cursor.close();
//            DBHelper.getInstance(mContext).closeDb();
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            DBHelper.getInstance(mContext).closeDb();
            return user;
        }
//        Cursor c = DBHelper.getInstance(mContext).rawQuery(
//                getSelectSql(JsonEntityColumn.TABLE_NAME, new String[]{JsonEntityColumn.AUTH_KEY
//                        , JsonEntityColumn.USER_ID}), args);
//        if (exist(c)) {
//            c.moveToFirst();
//            user = getBean(c);
//        }
//        c.close();
//        DBHelper.getInstance(mContext).closeDb();
//        return user;
    }

    @Override
    public List<JsonEntity> query() {
        return null;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void delete(String str) {

    }

    private void delete(String authKey, String userId) {
        DBHelper.getInstance(mContext).delete(JsonEntityColumn.TABLE_NAME, JsonEntityColumn.USER_ID + " = ?" + " and " + JsonEntityColumn.AUTH_KEY + " =?",
                new String[]{userId, authKey});
    }

    @Override
    public void update(JsonEntity entity) {
        this.delete(entity.getAuthKey(), entity.getUserId());
        DBHelper.getInstance(mContext).insert(JsonEntityColumn.TABLE_NAME, getValues(entity));
    }

    /**
     * 删除表内所有数据
     */
    public void deleteAllDatas() {
        DBHelper.getInstance(mContext).delete(JsonEntityColumn.TABLE_NAME, null, null);
    }
}
