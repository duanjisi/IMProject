package im.boss66.com.widget.video;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.App;
import im.boss66.com.R;


/**
 * 播放界面的音量显示
 *
 * @author wzz
 */
public class VideoVolumeView extends BaseCenterView {

    private TextView mProgressTv;
    private static final int DELAY = 1 * 1000;
    private static final int FADE = 22;
    private AudioManager mAudioManager;

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

    public VideoVolumeView(Context context) {
        super(context);
    }

    public VideoVolumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoVolumeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void registerListener() {
        mProgressTv = (TextView) findViewById(R.id.player_volume_tv);
    }

    private int setProgress(double progress) {
        if (progress <= 0) {
            progress = 0;
        }
        if (progress >= 100) {
            progress = 100;
        }

        mProgressTv.setText(((int) progress) + "%");
        setVolume(progress);
//		Log.i("TAG", progress + "");
        return (int) progress;
    }

    public int show(int increment) {
        this.setVisibility(View.VISIBLE);
        int cur = setProgress(PlayerUtil.getCentumVolumeProgress() + increment);

//        int cur = setProgress(PlayerUtil.getCentumVolumeProgress() + 1);
//        MycsLog.i("show: " + PlayerUtil.getCentumVolumeProgress() + ":" + increment);
//        Toast.makeText(getContext(), "show: " + PlayerUtil.getCentumVolumeProgress() + "\n" + increment, Toast.LENGTH_SHORT).show();
        Message msg = mHandler.obtainMessage(FADE);
        mHandler.removeMessages(FADE);
        mHandler.sendMessageDelayed(msg, DELAY);
        return cur;
    }

    public int showMins(int mins) {
        this.setVisibility(View.VISIBLE);
        int cur = 0;

        if (mins > 0) {
            double a = PlayerUtil.getCentumVolumeProgress() - mins;
            cur = setProgress(a);
        } else {
            double b = PlayerUtil.getCentumVolumeProgress() - 1;
            cur = setProgress(b);
        }

//        double b = PlayerUtil.getCentumVolumeProgress() - 1;
//        cur = setProgress(b);

//        MycsLog.i("show: " + PlayerUtil.getCentumVolumeProgress() + ":" + increment);
//        Toast.makeText(getContext(), "show: " + PlayerUtil.getCentumVolumeProgress() + "\n" + increment, Toast.LENGTH_SHORT).show();
        Message msg = mHandler.obtainMessage(FADE);
        mHandler.removeMessages(FADE);
        mHandler.sendMessageDelayed(msg, DELAY);
        return cur;
    }

    public void hide() {
        this.setVisibility(View.GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    private void setVolume(double progress) {
        double d = PlayerUtil.computeCurrentVolume(progress);
//		Intent it = new Intent(Constants.Player.VOLUME_ACTION);
//		it.putExtra(Constants.Player.VOLUME_BROADCAST, curVolume);
//		mContext.sendBroadcast(it);
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) App.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) d,
                AudioManager.FX_KEY_CLICK);

        App.getInstance().setDoubleVolume(d);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_player_volume;
    }
}
