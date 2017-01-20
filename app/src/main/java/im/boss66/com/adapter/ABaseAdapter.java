package im.boss66.com.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.widget.DialogFactory;

/**
 * @param <T>
 * @ClassName: ABaseAdapter
 * @Description: adapter基类
 * @author: zyu
 * @date: 2014-12-13 下午7:11:58
 */
public abstract class ABaseAdapter<T> extends BaseAdapter {

    private static String TAG = "ABaseAdapter";
    protected List<T> mList;

    protected Context mContext;

    private LayoutInflater mInflater;
    //    public RequestQueue mRequestQueue;
//    private ProgressDialog mProgressDialog;
    private ProgressDialog mProgressDialog;

    public ABaseAdapter(Context context) {
        mContext = context;
//        mRequestQueue = Volley.newRequestQueue(context);
        mList = new ArrayList<T>();
        mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    public ABaseAdapter(Context context, List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("list can not be null");
        }
        mContext = context;
        mList = list;
        mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        if (position >= 0 && position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mList == null || mList.size() == 0) {
            return mInflater.inflate(R.layout.item_no_data, null);
        } else {
            return setConvertView(position, mList.get(position), convertView);
        }
    }

    protected Context getContext() {
        return mContext;
    }

    protected String getStr(int resId) {
        return mContext.getString(resId);
    }

    protected String getStr(int resId, Object... formatArgs) {
        return mContext.getString(resId, formatArgs);
    }

    /**
     * 添加单个数据。
     *
     * @param entity
     */
    public void addItem(T entity) {
        addItem(entity, mList.size());
    }

    public void addItem(T entity, int index) {
        if (entity != null) {
            mList.add(index, entity);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据列表
     *
     * @param list
     */
    public void addItem(List<? extends T> list) {
        if (list != null) {
            mList.addAll(list);
            this.notifyDataSetChanged();
        }
    }

    public void setData(List<T> list) {
        mList = list;
        this.notifyDataSetChanged();
    }

    public void addData(List<T> list) {
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void initData(List<T> list) {
        mList.clear();
        mList.addAll(list);
        this.notifyDataSetChanged();
    }


//    public void refreshEmpetyViews() {
//        mList.clear();
////        this.notifyDataSetChanged();
//    }

    public List<T> getCurrentList() {
        return mList;
    }

    /**
     * 清空数据
     */
    public void clear() {
        mList.clear();
        this.notifyDataSetChanged();
    }

    public void remove(T object) {
        if (mList != null) {
            mList.remove(object);
            this.notifyDataSetChanged();
        }
    }

    public List<T> getData() {
        return mList;
    }

    protected abstract View setConvertView(int position, T entity, View convertView);

    /**
     * 显示加载进度条
     *
     * @return: void
     */
    protected void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = createProgressDialog();
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        } else {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    private ProgressDialog createProgressDialog() {
        ProgressDialog dialog = DialogFactory.createProgressDialog(getContext());
        dialog.setMessage(getContext().getString(R.string.loading));
        dialog.setIndeterminate(true);
        // dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        return dialog;
    }

    /**
     * 取消加载进度条
     *
     * @return: void
     */
    protected void cancelLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void showToast(String msg, boolean length) {
        Toast.makeText(getContext(), msg, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }
}
