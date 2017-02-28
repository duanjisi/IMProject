package im.boss66.com.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import im.boss66.com.R;
import im.boss66.com.widget.DialogFactory;

/**
 * Created by Johnny on 2017/2/25.
 */
public class BasePicAdapter extends BaseAdapter {
    protected Context mContext;

    private LayoutInflater mInflater;
    //    public RequestQueue mRequestQueue;
//    private ProgressDialog mProgressDialog;
    private ProgressDialog mProgressDialog;

    public BasePicAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

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

    protected Context getContext() {
        return mContext;
    }
}
