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
import im.boss66.com.db.EmoColumn;
import im.boss66.com.db.helper.ColumnHelper;
import im.boss66.com.entity.EmoEntity;

/**
 * Created by Johnny on 2017/2/9.
 */
public class EmoHelper extends ColumnHelper<EmoEntity> {
    private static EmoHelper helper;
    private Context mContext;
    private static String userId;

    public EmoHelper() {
        mContext = App.getInstance().getApplicationContext();
    }

    public static EmoHelper getInstance() {
        userId = App.getInstance().getUid();
        if (helper == null) {
            synchronized (EmoHelper.class) {
                if (helper == null) {
                    helper = new EmoHelper();
                }
            }
        }
        return helper;
    }

    @Override
    protected ContentValues getValues(EmoEntity bean) {
        ContentValues values = new ContentValues();
        values.put(EmoColumn.EMO_NAME, bean.getEmo_name());
        values.put(EmoColumn.EMO_ID, bean.getEmo_id());
        values.put(EmoColumn.EMO_GROUP_ID, bean.getEmo_group_id());
        values.put(EmoColumn.EMO_FORMAT, bean.getEmo_format());
        values.put(EmoColumn.EMO_CATE_ID, bean.getEmo_cate_id());
        values.put(EmoColumn.EMO_CODE, bean.getEmo_code());
        values.put(EmoColumn.EMO_DESC, bean.getEmo_desc());
        values.put(EmoColumn.EMO_WIDTH, bean.getWidth());
        values.put(EmoColumn.EMO_HEIGHT, bean.getHeight());
        values.put(EmoColumn.USER_ID, userId);
        return values;
    }

    @Override
    protected EmoEntity getBean(Cursor c) {
        EmoEntity cate = new EmoEntity();
        cate.setEmo_name(getString(c, EmoColumn.EMO_NAME));
        cate.setEmo_id(getString(c, EmoColumn.EMO_ID));
        cate.setEmo_group_id(getString(c, EmoColumn.EMO_GROUP_ID));
        cate.setEmo_format(getString(c, EmoColumn.EMO_FORMAT));
        cate.setEmo_cate_id(getString(c, EmoColumn.EMO_CATE_ID));
        cate.setWidth(getString(c, EmoColumn.EMO_WIDTH));
        cate.setHeight(getString(c, EmoColumn.EMO_HEIGHT));
        cate.setEmo_code(getString(c, EmoColumn.EMO_CODE));
        return cate;
    }

    @Override
    public void save(List<EmoEntity> list) {
        Collections.reverse(list);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                EmoEntity entity = list.get(i);
                Log.i("info", "=====EmoName:" + entity.getEmo_name());
                this.save(entity);
            }
        }
    }

    /**
     * 删除表内所有数据
     */
    public void deleteAllDatas() {
        DBHelper.getInstance(mContext).delete(EmoColumn.TABLE_NAME, null, null);
    }

    @Override
    public void save(EmoEntity entity) {
        String[] args = new String[]{entity.getEmo_id(), userId};
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                getSelectSql(EmoColumn.TABLE_NAME, new String[]{EmoColumn.EMO_ID,
                        EmoColumn.USER_ID
                }), args);
        if (exist(c)) {
//            c.moveToFirst();
//            this.delete(entity.getUser_name());
            this.update(entity);
        } else {
            DBHelper.getInstance(mContext).insert(EmoColumn.TABLE_NAME, getValues(entity));
        }
        c.close();
    }

    @Override
    public EmoEntity query(int id) {
        return null;
    }

    @Override
    public List<EmoEntity> query() {
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EmoColumn.TABLE_NAME +
                        " WHERE " + EmoColumn.USER_ID
                        + " = ? ", new String[]{userId});
        List<EmoEntity> bos = new ArrayList<EmoEntity>();
        if (exist(c)) {
            c.moveToLast();
            do {
                bos.add(getBean(c));
            } while (c.moveToPrevious());
        }
        c.close();
        return bos;
    }

    public List<EmoEntity> queryByGroupId(String cateId) {//根据分类id查询组
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EmoColumn.TABLE_NAME +
                        " WHERE " + EmoColumn.EMO_GROUP_ID + " = ? " +
                        " and " + EmoColumn.USER_ID + " =?", new String[]{cateId, userId});
        List<EmoEntity> bos = new ArrayList<EmoEntity>();
        if (exist(c, mContext)) {
            c.moveToFirst();
            do {
                bos.add(getBean(c));
            } while (c.moveToNext());
        }
        c.close();
        return bos;
    }

    public EmoEntity queryByCode(String code) {//根据分类id查询组
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EmoColumn.TABLE_NAME +
                        " WHERE " + EmoColumn.EMO_CODE + " = ?" +
                        " and " + EmoColumn.USER_ID + " =?", new String[]{code, userId});
        EmoEntity entity = null;
        if (exist(c, mContext)) {
            c.moveToFirst();
            if (c.getCount() == 1) {
                entity = getBean(c);
            }
        }
        c.close();
        return entity;
    }

    public void deleteByCateId(String cateId) {//根据分类id删除表情
        DBHelper.getInstance(mContext).delete(EmoColumn.TABLE_NAME, EmoColumn.EMO_CATE_ID + " = ?" +
                        " and " + EmoColumn.USER_ID + " =?",
                new String[]{cateId, userId});
    }

    public void deleteByGroupId(String cateId) {//根据组id删除表情
        DBHelper.getInstance(mContext).delete(EmoColumn.TABLE_NAME, EmoColumn.EMO_GROUP_ID + " = ?" +
                        " and " + EmoColumn.USER_ID + " =?",
                new String[]{cateId, userId});
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void delete(String str) {//删除某一组
    }

    @Override
    public void update(EmoEntity entity) {

    }
}
