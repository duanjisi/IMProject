package im.boss66.com.widget.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import im.boss66.com.R;
import im.boss66.com.widget.DialogFactory;


/**
 * Created by admin on 2017/2/20.
 */
public abstract class BaseDialog {

    // 进度条
    private ProgressDialog mProgressDialog;

    protected Context context;
    protected Dialog dialog;

    protected abstract int getView();

    protected abstract int getDialogStyleId();


    public BaseDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, getDialogStyleId());
        dialog.setContentView(getView());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        //设置点击空白，弹出层也不会消失
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //设置背景透明度
        LinearLayout view1 = (LinearLayout) dialog.findViewById(R.id.ll_people_data);
        view1.getBackground().setAlpha(150);
        //隐藏系统输入盘
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
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
        ProgressDialog dialog = DialogFactory.createProgressDialog(context);
        dialog.setMessage(context.getResources().getString(R.string.loading));
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
        Toast.makeText(context, resId, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
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
        Toast.makeText(context, msg, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }
}
