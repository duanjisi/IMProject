package im.boss66.com.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import im.boss66.com.R;


/**
 * Created by admin on 2017/2/20.
 */
public abstract class BaseDialog {
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


}
