package im.boss66.com.widget.popupWindows;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import im.boss66.com.R;
import im.boss66.com.widget.popupWindows.base.BasePopup;

/**
 * Created by Administrator on 2015/12/1.
 */
public class LoadingPop extends BasePopup {

    private ProgressBar progressBar;

    public LoadingPop(Context context) {
        super(context);
//        setAnimationStyle(R.style.popwin_anim_style);
//        setAnimationStyle(R.style.popupAnimation);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        View popView = LayoutInflater.from(context).inflate(R.layout.view_video_loading, null);
        initViews(popView);
        setTouchable(false);
        setContentView(popView);
    }

    private void initViews(View view) {
        View top_bar = view.findViewById(R.id.ll_top_bar);
        progressBar = (ProgressBar) view.findViewById(R.id.loading_pb);
        top_bar.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
    }

    /**
     * 显示PopupWindow
     *
     * @param view PopupWindow依附的View
     */
    public void show(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
        backgroundAlpha(0.45f);
    }

    /**
     * 隐藏PopupWindow
     */

    public void dismissPop() {
        if (isShowing()) {
            dismiss();
        }
    }
}
