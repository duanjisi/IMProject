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
    private static String userId;

    public EmoLoveHelper() {
        mContext = App.getInstance().getApplicationContext();
    }

    public static EmoLoveHelper getInstance() {
        userId = App.getInstance().getUid();
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
        values.put(EmoLoveColumn.USER_ID, userId);
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
        Collections.reverse(list);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                this.save(list.get(i));
            }
        }
    }

    @Override
    public void save(EmoLove entity) {
        String[] args = new String[]{entity.getCollect_id(), userId};
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                getSelectSql(EmoLoveColumn.TABLE_NAME, new String[]{EmoLoveColumn.EMO_LOVE_COLL_ID, EmoLoveColumn.USER_ID}), args);
        if (exist(c)) {
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

    public ArrayList<EmoLove> qureList() {
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EmoLoveColumn.TABLE_NAME +
                        " WHERE " + EmoLoveColumn.USER_ID
                        + " = ? ", new String[]{userId});
        ArrayList<EmoLove> bos = new ArrayList<EmoLove>();
        if (exist(c)) {
            c.moveToLast();
            do {
                bos.add(getBean(c));
            } while (c.moveToPrevious());
        }
        c.close();
        return bos;
    }

    public String qureEmoByUrl(String url) {
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EmoLoveColumn.TABLE_NAME +
                        " WHERE " + EmoLoveColumn.USER_ID
                        + " = ? " +
                        " and " + EmoLoveColumn.EMO_LOVE_URL + " =?", new String[]{userId, url});
        String str;
        if (exist(c)) {
            c.moveToFirst();
            str = getBean(c).getEmo_url();
        } else {
            str = "";
        }
        c.close();
        return str;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void delete(String str) {//删除某一组
        DBHelper.getInstance(mContext).delete(EmoLoveColumn.TABLE_NAME, EmoLoveColumn.EMO_LOVE_URL + " = ?" +
                        " and " + EmoLoveColumn.USER_ID + " =?",
                new String[]{str, userId});
    }

    public void delete(EmoLove love) {//删除某一组
        DBHelper.getInstance(mContext).delete(EmoLoveColumn.TABLE_NAME, EmoLoveColumn.EMO_LOVE_COLL_ID + " = ?" +
                        " and " + EmoLoveColumn.USER_ID + " =?",
                new String[]{love.getCollect_id(), userId});
    }

    /**
     * 删除表内所有数据
     */
    public void deleteAllDatas() {
        DBHelper.getInstance(mContext).delete(EmoLoveColumn.TABLE_NAME, EmoLoveColumn.USER_ID + " = ?", new String[]{userId});
    }

    @Override
    public void update(EmoLove entity) {

    }
}
