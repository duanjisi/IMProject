package im.boss66.com.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.db.ConversationColumn;
import im.boss66.com.db.DBHelper;
import im.boss66.com.db.helper.ColumnHelper;
import im.boss66.com.entity.BaseConversation;

/**
 * Created by Johnny on 2017/1/17.
 */
public class ConversationHelper extends ColumnHelper<BaseConversation> {
    private static ConversationHelper helper;
    private Context mContext;
    private static String userId;

    public ConversationHelper() {
        mContext = App.getInstance().getApplicationContext();
    }

    public static ConversationHelper getInstance() {
        userId = App.getInstance().getUid();
        if (helper == null) {
            synchronized (ConversationHelper.class) {
                if (helper == null) {
                    helper = new ConversationHelper();
                }
            }
        }
        return helper;
    }

    @Override
    protected ContentValues getValues(BaseConversation bean) {
        ContentValues values = new ContentValues();
        values.put(ConversationColumn.CONVERSATION_ID, bean.getConversation_id());
        values.put(ConversationColumn.USER_NAME, bean.getUser_name());
        values.put(ConversationColumn.AVATAR, bean.getAvatar());
        values.put(ConversationColumn.UNREAD_COUNT, bean.getUnread_msg_count());
        values.put(ConversationColumn.MSG_TIME, bean.getNewest_msg_time());
        values.put(ConversationColumn.MSG_TYPE, bean.getNewest_msg_type());
        values.put(ConversationColumn.USER_ID, userId);
//        values.put(ConversationColumn.MSG_TXT, bean.getNewest_msg());
        return values;
    }

    @Override
    protected BaseConversation getBean(Cursor c) {
        BaseConversation entity = new BaseConversation();
        entity.setUser_name(getString(c, ConversationColumn.USER_NAME));
        entity.setConversation_id(getString(c, ConversationColumn.CONVERSATION_ID));
        entity.setAvatar(getString(c, ConversationColumn.AVATAR));
        entity.setUnread_msg_count(getString(c, ConversationColumn.UNREAD_COUNT));
//        entity.setNewest_msg(getString(c, ConversationColumn.MSG_TXT));
        entity.setNewest_msg_type(getString(c, ConversationColumn.MSG_TYPE));
        entity.setNewest_msg_time(getString(c, ConversationColumn.MSG_TIME));
        return entity;
    }

    @Override
    public void save(List<BaseConversation> list) {

    }

    @Override
    public void save(BaseConversation entity) {
        String[] args = new String[]{entity.getUser_name(), userId};
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                getSelectSql(ConversationColumn.TABLE_NAME, new String[]{ConversationColumn.USER_NAME,
                        ConversationColumn.USER_ID}), args);
        if (exist(c)) {
//            c.moveToFirst();
//            this.delete(entity.getUser_name());
            this.update(entity);
        } else {
            DBHelper.getInstance(mContext).insert(ConversationColumn.TABLE_NAME, getValues(entity));
        }
        c.close();
    }

    @Override
    public BaseConversation query(int id) {
        return null;
    }

    @Override
    public List<BaseConversation> query() {
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + ConversationColumn.TABLE_NAME, null);
        List<BaseConversation> bos = new ArrayList<BaseConversation>();
        if (exist(c)) {
            c.moveToLast();
            do {
                bos.add(getBean(c));
            } while (c.moveToNext());
        }
        c.close();
        return bos;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void delete(String str) {

    }

    /**
     * 删除表内所有数据
     */
    public void deleteAllDatas() {
        DBHelper.getInstance(mContext).delete(ConversationColumn.TABLE_NAME, null, null);
    }

    @Override
    public void update(BaseConversation entity) {
        String sql = ConversationColumn.USER_NAME + " = ? AND " + ConversationColumn.AVATAR + " = ?" + " and " + ConversationColumn.USER_ID + " =?";
        DBHelper.getInstance(mContext).update(ConversationColumn.TABLE_NAME, getValues(entity),
                sql, new String[]{entity.getUser_name(), entity.getAvatar(), userId});
    }
}
