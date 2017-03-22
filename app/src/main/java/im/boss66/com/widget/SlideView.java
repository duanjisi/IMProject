package im.boss66.com.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by GMARUnity on 2017/3/20.
 */
public class SlideView extends LinearLayout implements View.OnTouchListener {

    private float x;//item被触摸时x起点
    private int l;//x轴上滑动的距离
    private boolean isShow = false;//底部
    private boolean left = false;
    private boolean right = false;
    private android.widget.RelativeLayout.LayoutParams layoutParams;  //父窗体为RelativeLayout可根据需要修改
    private int longth = 100;//滑动结束时滑动的距离
    private int min = 10;//屏蔽滑动小于10的情况算为点击事件
    private int max = 30;//滑动结束时滑动的距离大于该值滑动完成否则取消滑动

    public SlideView(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    public SlideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlideView(Context context, AttributeSet attrs, int defStyleAttr,
                     int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        layoutParams = (android.widget.RelativeLayout.LayoutParams) getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                l = (int) (x - event.getX());
                if (l > 0 && !isShow) {
                    layoutParams.setMargins(-l, 0, l, 0);
                    requestLayout();
                    left = true;
                } else if (l < 0 && isShow && l > -longth) {
                    layoutParams.setMargins(-longth - l, 0, longth + l, 0);
                    requestLayout();
                    right = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isShow && left) {
                    if (l < min) {
                        return false;
                    }
                    if (l > max) {
                        layoutParams.setMargins(-longth, 0, longth, 0);
                        requestLayout();
                        isShow = true;
                        left = false;
                    } else {
                        layoutParams.setMargins(0, 0, 0, 0);
                        requestLayout();
                    }
                    return true;
                }
                if (isShow && right) {
                    if (l > -min) {
                        return false;
                    }
                    if (l < -max) {
                        layoutParams.setMargins(0, 0, 0, 0);
                        requestLayout();
                        isShow = false;
                        right = false;
                    } else {
                        layoutParams.setMargins(-longth, 0, longth, 0);
                        requestLayout();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!isShow && left) {
                    if (l > max) {
                        layoutParams.setMargins(-longth, 0, longth, 0);
                        requestLayout();
                        isShow = true;
                        left = false;
                    } else {
                        layoutParams.setMargins(0, 0, 0, 0);
                        requestLayout();
                    }
                } else if (isShow && right) {
                    if (l < -max) {
                        layoutParams.setMargins(0, 0, 0, 0);
                        requestLayout();
                        isShow = false;
                        right = false;
                    } else {
                        layoutParams.setMargins(-longth, 0, longth, 0);
                        requestLayout();
                    }
                }
                break;

            default:
                break;
        }
        return false;
    }

}
