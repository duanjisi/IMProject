package im.boss66.com.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.db.DBHelper;
import im.boss66.com.db.EmoCateColumn;
import im.boss66.com.db.helper.ColumnHelper;
import im.boss66.com.entity.EmoCate;
import im.boss66.com.entity.EmoGroup;

/**
 * Created by Johnny on 2017/2/9.
 */
public class EmoCateHelper extends ColumnHelper<EmoCate> {
    private static EmoCateHelper helper;
    private Context mContext;
    private static String userId;

    public EmoCateHelper() {
        mContext = App.getInstance().getApplicationContext();
    }

    public static EmoCateHelper getInstance() {
        userId = App.getInstance().getUid();
        if (helper == null) {
            synchronized (EmoCateHelper.class) {
                if (helper == null) {
                    helper = new EmoCateHelper();
                }
            }
        }
        return helper;
    }

    @Override
    protected ContentValues getValues(EmoCate bean) {
        ContentValues values = new ContentValues();
        values.put(EmoCateColumn.EMO_CATE_ID, bean.getCate_id());
        values.put(EmoCateColumn.EMO_CATE_NAME, bean.getCate_name());
        values.put(EmoCateColumn.EMO_CATE_DESC, bean.getCate_desc());
        values.put(EmoCateColumn.USER_ID, userId);
        return values;
    }

    @Override
    protected EmoCate getBean(Cursor c) {
        EmoCate cate = new EmoCate();
        cate.setCate_id(getString(c, EmoCateColumn.EMO_CATE_ID));
        cate.setCate_name(getString(c, EmoCateColumn.EMO_CATE_NAME));
        cate.setCate_desc(getString(c, EmoCateColumn.EMO_CATE_DESC));
        return cate;
    }

    @Override
    public void save(List<EmoCate> list) {
        Collections.reverse(list);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                EmoCate cate = list.get(i);
                Log.i("info", "=====cateName:" + cate.getCate_name());
                this.save(cate);
                saveGroup(cate);
            }
        }
    }


    private void saveGroup(EmoCate cate) {
        ArrayList<EmoGroup> groups = cate.getGroup();
        if (groups.size() != 0) {
            EmoGroupHelper.getInstance().save(groups);
        }
    }

    @Override
    public void save(EmoCate entity) {
        String[] args = new String[]{entity.getCate_id(), userId};
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                getSelectSql(EmoCateColumn.TABLE_NAME, new String[]{EmoCateColumn.EMO_CATE_ID,
                        EmoCateColumn.USER_ID}), args);
//        if (exist(c)) {
////            c.moveToFirst();
////            this.delete(entity.getUser_name());
//            this.update(entity);
//        } else {
//            DBHelper.getInstance(mContext).insert(EmoCateColumn.TABLE_NAME, getValues(entity));
//        }
        if (!exist(c)) {
            DBHelper.getInstance(mContext).insert(EmoCateColumn.TABLE_NAME, getValues(entity));
        }
        c.close();
    }

    @Override
    public EmoCate query(int id) {
        return null;
    }

    @Override
    public List<EmoCate> query() {
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EmoCateColumn.TABLE_NAME + " WHERE " + EmoCateColumn.USER_ID
                        + " = ? ", new String[]{userId});
        List<EmoCate> bos = new ArrayList<EmoCate>();
        if (exist(c)) {
            c.moveToLast();
            do {
                bos.add(getBean(c));
            } while (c.moveToPrevious());
        }
        c.close();
        return bos;
    }

    /**
     * 删除表内所有数据
     */
    public void deleteAllDatas() {
        DBHelper.getInstance(mContext).delete(EmoCateColumn.TABLE_NAME, null, null);
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void delete(String str) {//根据cateid删除分类
        DBHelper.getInstance(mContext).delete(EmoCateColumn.TABLE_NAME, EmoCateColumn.EMO_CATE_ID + " = ?"
                        + " and " + EmoCateColumn.USER_ID + " =?",
                new String[]{str, userId});
    }

    @Override
    public void update(EmoCate entity) {

    }
}
