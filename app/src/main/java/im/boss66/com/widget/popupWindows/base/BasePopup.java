package im.boss66.com.widget.popupWindows.base;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * PopupWindow的基类
 */
public class BasePopup extends PopupWindow {

    protected final Context mContext;

    public BasePopup(Context context) {
        super(context);
        mContext = context;
        setFocusable(true);
        setTouchable(true);
        setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
        setOutsideTouchable(true);
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha 透明度
     */
    protected void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity)mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity)mContext).getWindow().setAttributes(lp);
    }


    @Override
    public void dismiss() {
        super.dismiss();
        backgroundAlpha(1.0f);
    }
}
