package im.boss66.com.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import im.boss66.com.R;
import im.boss66.com.widget.DialogFactory;

public class BaseFragment extends Fragment {
    public boolean mIsFirstin = true;

    private ProgressDialog mProgressDialog;
    private LocalBroadcastReceiver mLocalBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalBroadcastReceiver = new LocalBroadcastReceiver();
    }

    protected void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = createProgressDialog();
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
        } else {
            Activity activity = getActivity();
            if (!mProgressDialog.isShowing()
                    && activity != null
                    && !activity.isFinishing()) {
                mProgressDialog.show();
            }
        }
    }


    private ProgressDialog createProgressDialog() {
        ProgressDialog dialog = DialogFactory.createProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.loading));
        dialog.setIndeterminate(true);
        // dialog.setCanceledOnTouchOutside(false);
        // dialog.setCancelable(false);
        return dialog;
    }


    protected void cancelLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void showToast(int resId, boolean length) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), resId, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }

    protected void showToast(String msg, boolean length) {
        if (getActivity() != null && !TextUtils.isEmpty(msg)) {
            Toast.makeText(getActivity(), msg, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 注册本地广播器需要接收的Action
     *
     * @param action
     */
    protected void registerAction(String action) {
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mLocalBroadcastReceiver, new IntentFilter(action));
    }

    /**
     * 当接收到本地广播的时候调用的方法
     *
     * @param context
     * @param intent
     */
    protected void onReceiveAction(Context context, Intent intent) {
    }

    /**
     * 本地广播接收器
     */
    private class LocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            onReceiveAction(context, intent);
        }
    }

}
