package im.boss66.com.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.db.DBHelper;
import im.boss66.com.db.EmoLoveColumn;
import im.boss66.com.db.helper.ColumnHelper;
import im.boss66.com.entity.EmoLove;

/**
 * Created by Johnny on 2017/2/9.
 */
public class EmoLoveHelper extends ColumnHelper<EmoLove> {
    private static EmoLoveHelper helper;
    private Context mContext;

    public EmoLoveHelper() {
        mContext = App.getInstance().getApplicationContext();
    }

    public static EmoLoveHelper getInstance() {
        if (helper == null) {
            synchronized (EmoLoveHelper.class) {
                if (helper == null) {
                    helper = new EmoLoveHelper();
                }
            }
        }
        return helper;
    }

    @Override
    protected ContentValues getValues(EmoLove bean) {
        ContentValues values = new ContentValues();
//        values.put(EmoLoveColumn.EMO_LOVE_ICON, bean.getIcon());
        values.put(EmoLoveColumn.EMO_LOVE_COLL_ID, bean.getCollect_id());
        values.put(EmoLoveColumn.EMO_LOVE_DESC, bean.getEmo_desc());
        values.put(EmoLoveColumn.EMO_LOVE_ID, bean.getEmo_id());
        values.put(EmoLoveColumn.EMO_LOVE_NAME, bean.getEmo_name());
        values.put(EmoLoveColumn.EMO_LOVE_URL, bean.getEmo_url());
        values.put(EmoLoveColumn.EMO_LOVE_USER_ID, bean.getUser_id());
        return values;
    }

    @Override
    protected EmoLove getBean(Cursor c) {
        EmoLove cate = new EmoLove();
//        cate.setIcon(getString(c, EmoLoveColumn.EMO_LOVE_ICON));
        cate.setCollect_id(getString(c, EmoLoveColumn.EMO_LOVE_COLL_ID));
        cate.setEmo_desc(getString(c, EmoLoveColumn.EMO_LOVE_DESC));
        cate.setEmo_id(getString(c, EmoLoveColumn.EMO_LOVE_ID));
        cate.setEmo_name(getString(c, EmoLoveColumn.EMO_LOVE_NAME));
        cate.setEmo_url(getString(c, EmoLoveColumn.EMO_LOVE_URL));
        cate.setUser_id(getString(c, EmoLoveColumn.EMO_LOVE_USER_ID));
        return cate;
    }

    @Override
    public void save(List<EmoLove> list) {
        deleteAllDatas();
        Collections.reverse(list);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                this.save(list.get(i));
            }
        }
    }

    @Override
    public void save(EmoLove entity) {
        String[] args = new String[]{entity.getEmo_url()};
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                getSelectSql(EmoLoveColumn.TABLE_NAME, new String[]{EmoLoveColumn.EMO_LOVE_URL}), args);
        if (exist(c)) {
//            c.moveToFirst();
//            this.delete(entity.getUser_name());
            this.update(entity);
        } else {
            DBHelper.getInstance(mContext).insert(EmoLoveColumn.TABLE_NAME, getValues(entity));
        }
        c.close();
    }

    @Override
    public EmoLove query(int id) {
        return null;
    }

    @Override
    public List<EmoLove> query() {
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EmoLoveColumn.TABLE_NAME, null);
        List<EmoLove> bos = new ArrayList<EmoLove>();
        if (exist(c)) {
            c.moveToLast();
            do {
                bos.add(getBean(c));
            } while (c.moveToPrevious());
        }
        c.close();
        return bos;
    }

    public ArrayList<String> qureList() {
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EmoLoveColumn.TABLE_NAME, null);
        ArrayList<String> bos = new ArrayList<String>();
        if (exist(c)) {
            c.moveToLast();
            do {
                bos.add(getBean(c).getEmo_url());
            } while (c.moveToPrevious());
        }
        c.close();
        return bos;
    }

//    public List<EmoLove> queryByCateId(String cateId) {//根据分类id查询组
//        Cursor c = DBHelper.getInstance(mContext).rawQuery(
//                "SELECT * FROM " + EmoLoveColumn.TABLE_NAME + " WHERE " + EmoLoveColumn.CATE_ID
//                        + " = ? ", new String[]{cateId});
//        List<EmoLove> bos = new ArrayList<EmoLove>();
//        if (exist(c, mContext)) {
//            c.moveToFirst();
//            do {
//                bos.add(getBean(c));
//            } while (c.moveToNext());
//        }
//        c.close();
//        return bos;
//    }

//    public void deleteByCateId(String cateId) {//根据分类id删除组
//        DBHelper.getInstance(mContext).delete(EmoLoveColumn.TABLE_NAME, EmoLoveColumn.CATE_ID + " = ?",
//                new String[]{cateId});
//    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void delete(String str) {//删除某一组
        DBHelper.getInstance(mContext).delete(EmoLoveColumn.TABLE_NAME, EmoLoveColumn.EMO_LOVE_URL + " = ?",
                new String[]{str});
    }

    /**
     * 删除表内所有数据
     */
    public void deleteAllDatas() {
        DBHelper.getInstance(mContext).delete(EmoLoveColumn.TABLE_NAME, null, null);
    }

    @Override
    public void update(EmoLove entity) {

    }
}
