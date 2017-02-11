package im.boss66.com.activity.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import im.boss66.com.R;
import im.boss66.com.widget.DialogFactory;

public class BaseActivity extends FragmentActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    // 进度条
    private ProgressDialog mProgressDialog;
    private LocalBroadcastReceiver mLocalBroadcastReceiver;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        AppManager.getInstance().push(this);
//        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
        context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void onReceiveAction(Context context, Intent intent) {
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            onReceiveAction(context, intent);
        }
    }

    public void openActivity(Class<?> clazz) {
        openActivity(clazz, null);
    }

    public void openActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void openActvityForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    public void openActvityForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
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
        ProgressDialog dialog = DialogFactory.createProgressDialog(this);
        dialog.setMessage(getString(R.string.loading));
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

    /**
     * @param resId  资源ID
     * @param length true为长时间，false为短时间
     * @return: void
     */
    protected void showToast(int resId, boolean length) {
        Toast.makeText(this, resId, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    /**
     * @param msg    内容
     * @param length true为长时间，false为短时间
     * @return: void
     */
    protected void showToast(String msg, boolean length) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(this, msg, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    protected String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        AppManager.getInstance().remove(this);
    }
}
