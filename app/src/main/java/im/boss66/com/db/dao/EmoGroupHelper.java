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
import im.boss66.com.db.EmoGroupColumn;
import im.boss66.com.db.helper.ColumnHelper;
import im.boss66.com.entity.EmoEntity;
import im.boss66.com.entity.EmoGroup;

/**
 * Created by Johnny on 2017/2/9.
 */
public class EmoGroupHelper extends ColumnHelper<EmoGroup> {
    private static EmoGroupHelper helper;
    private Context mContext;
    private static String userId;

    public EmoGroupHelper() {
        mContext = App.getInstance().getApplicationContext();
    }

    public static EmoGroupHelper getInstance() {
        userId = App.getInstance().getUid();
        if (helper == null) {
            synchronized (EmoGroupHelper.class) {
                if (helper == null) {
                    helper = new EmoGroupHelper();
                }
            }
        }
        return helper;
    }

    @Override
    protected ContentValues getValues(EmoGroup bean) {
        ContentValues values = new ContentValues();
        values.put(EmoGroupColumn.CATE_ID, bean.getCate_id());
        values.put(EmoGroupColumn.GROUP_COUNT, bean.getGroup_count());
        values.put(EmoGroupColumn.GROUP_COVER, bean.getGroup_cover());
        values.put(EmoGroupColumn.GROUP_DESC, bean.getGroup_desc());
        values.put(EmoGroupColumn.GROUP_FORMAT, bean.getGroup_format());
        values.put(EmoGroupColumn.GROUP_ICON, bean.getGroup_icon());
        values.put(EmoGroupColumn.GROUP_ID, bean.getGroup_id());
        values.put(EmoGroupColumn.GROUP_NAME, bean.getGroup_name());
        values.put(EmoGroupColumn.USER_ID, userId);
        return values;
    }

    @Override
    protected EmoGroup getBean(Cursor c) {
        EmoGroup cate = new EmoGroup();
        cate.setCate_id(getString(c, EmoGroupColumn.CATE_ID));
        cate.setGroup_desc(getString(c, EmoGroupColumn.GROUP_DESC));
        cate.setGroup_count(getString(c, EmoGroupColumn.GROUP_COUNT));
        cate.setGroup_format(getString(c, EmoGroupColumn.GROUP_FORMAT));
        cate.setGroup_cover(getString(c, EmoGroupColumn.GROUP_COVER));
        cate.setGroup_icon(getString(c, EmoGroupColumn.GROUP_ICON));
        cate.setGroup_id(getString(c, EmoGroupColumn.GROUP_ID));
        cate.setGroup_name(getString(c, EmoGroupColumn.GROUP_NAME));
        return cate;
    }

    @Override
    public void save(List<EmoGroup> list) {
        Collections.reverse(list);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                EmoGroup group = list.get(i);
                this.save(group);
                saveEmo(group);
            }
        }
    }

    public void saveSortList(List<EmoGroup> list) {
        deleteAllDatas();
//        Collections.reverse(list);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                EmoGroup group = list.get(i);
                this.save(group);
                saveEmo(group);
            }
        }
    }

    private void saveEmo(EmoGroup group) {
        ArrayList<EmoEntity> emos = group.getEmo();
        if (emos.size() != 0) {
            EmoHelper.getInstance().save(emos);
        }
    }

    @Override
    public void save(EmoGroup entity) {
        String[] args = new String[]{entity.getGroup_id(), userId};
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                getSelectSql(EmoGroupColumn.TABLE_NAME, new String[]{EmoGroupColumn.GROUP_ID,
                        EmoGroupColumn.USER_ID}), args);
        if (exist(c)) {
//            c.moveToFirst();
//            this.delete(entity.getUser_name());
            Log.i("info", "====exist:");
            this.update(entity);
        } else {
            Log.i("info", "========插入:" + entity.getGroup_name());
            DBHelper.getInstance(mContext).insert(EmoGroupColumn.TABLE_NAME, getValues(entity));
        }
        c.close();
    }

    @Override
    public EmoGroup query(int id) {
        return null;
    }

    @Override
    public List<EmoGroup> query() {
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EmoGroupColumn.TABLE_NAME + " WHERE " + EmoGroupColumn.USER_ID
                        + " = ? ", new String[]{userId});
        List<EmoGroup> bos = new ArrayList<EmoGroup>();
        if (exist(c)) {
            c.moveToLast();
            do {
                bos.add(getBean(c));
            } while (c.moveToPrevious());
        }
        c.close();
        return bos;
    }

    public List<EmoGroup> queryByCateId(String cateId) {//根据分类id查询组
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EmoGroupColumn.TABLE_NAME + " WHERE " + EmoGroupColumn.CATE_ID
                        + " = ? " + " and " + EmoGroupColumn.USER_ID + " =?", new String[]{cateId, userId});
        List<EmoGroup> bos = new ArrayList<EmoGroup>();
        if (exist(c, mContext)) {
            Log.i("info", "====count:" + c.getCount());
            c.moveToFirst();
            do {
                bos.add(getBean(c));
            } while (c.moveToNext());
        }
        c.close();
        return bos;
    }

    public void deleteByCateId(String cateId) {//根据分类id删除组
        DBHelper.getInstance(mContext).delete(EmoGroupColumn.TABLE_NAME, EmoGroupColumn.CATE_ID + " = ?",
                new String[]{cateId});
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void delete(String str) {

    }

    public void delete(String cateid, String str) {//删除某一组
        DBHelper.getInstance(mContext).delete(EmoGroupColumn.TABLE_NAME, EmoGroupColumn.GROUP_ID + " = ?"
                        + " and " + EmoGroupColumn.USER_ID + " =?",
                new String[]{str, userId});
        EmoHelper.getInstance().deleteByGroupId(str);
        deleteCate(cateid);
    }

    private void deleteCate(String cateid) {//根据groupid查询有多少组，如果只有一组，删除该父级
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EmoGroupColumn.TABLE_NAME + " WHERE " + EmoGroupColumn.CATE_ID
                        + " = ? " + " and " + EmoGroupColumn.USER_ID + " =?", new String[]{cateid, userId});
        if (exist(c, mContext)) {
            int count = c.getCount();
            Log.i("info", "======count:" + count);
            if (count == 0) {
                EmoCateHelper.getInstance().delete(cateid);
            }
        } else {
            EmoCateHelper.getInstance().delete(cateid);
        }
        c.close();
    }


    private void deleteEmos() {//删除某一组下的所有表情
        EmoHelper.getInstance().deleteAllDatas();
    }

    /**
     * 删除表内所有数据
     */
    public void deleteAllDatas() {
        DBHelper.getInstance(mContext).delete(EmoGroupColumn.TABLE_NAME, EmoGroupColumn.USER_ID + " =?", new String[]{userId});
    }

    @Override
    public void update(EmoGroup entity) {

    }
}
