package im.boss66.com.widget.video;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.Utils.MycsLog;


/**
 * 播放界面的亮度显示
 *
 * @author wzz
 */
public class VideoBrightnessView extends BaseCenterView {

    private TextView mProgressTv;
    private static final int DELAY = 1 * 500;
    private static final int FADE = 21;
    private static final float MAX_BRIGHT = 100.0f;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FADE:
                    hide();
                    break;

                default:
                    break;
            }
        }

        ;
    };

    public VideoBrightnessView(Context context) {
        super(context);
    }

    public VideoBrightnessView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoBrightnessView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void registerListener() {
        mProgressTv = (TextView) findViewById(R.id.player_light_tv);
    }

    private void setProgress(int progress) {
        MycsLog.i("setProgress = " + progress);
        if (progress <= 2) {//亮度为0.01时，会黑屏
            progress = 2;
        }
        if (progress >= 100) {
            progress = 100;
        }
        mProgressTv.setText(progress + "%");
        setScreenBrightness(progress / 100.0f * MAX_BRIGHT);
    }

    public void show(int progress) {
        MycsLog.i("show = " + progress);
        this.setVisibility(View.VISIBLE);
        setProgress(getScreenBrightness() + progress);

        Message msg = mHandler.obtainMessage(FADE);
        mHandler.removeMessages(FADE);
        mHandler.sendMessageDelayed(msg, DELAY);
    }

    /**
     * 获得当前屏幕亮度值 0--100
     */
    private int getScreenBrightness() {
        float screenBrightness = MAX_BRIGHT;
        try {
            screenBrightness = Settings.System.getFloat(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return (int) screenBrightness;
    }

    /**
     * 保存亮度值
     *
     * @param brightness
     */
    private void saveScreenBrightness(float brightness) {
        try {
            Settings.System.putFloat(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                    brightness);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public void hide() {
        this.setVisibility(View.GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    /**
     * 设置屏幕亮度[0,1]
     *
     * @param bright
     */
    private void setScreenBrightness(float bright) {
        Window localWindow = ((Activity) mContext).getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
        float f = bright / MAX_BRIGHT;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);

        saveScreenBrightness(f * 100f);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_player_brightness;
    }
}
