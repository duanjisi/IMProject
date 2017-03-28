package im.boss66.com.activity.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import im.boss66.com.R;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.NetworkUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.PhoneStateReceiver;
import im.boss66.com.widget.video.AppVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Johnny on 2016/6/28.
 */
public class videoPlayerActivity extends BaseActivity implements
        View.OnClickListener {
    private FrameLayout fl_close;
    private AppVideoView mVideoView;
    private ProgressBar mLoadingProgressBar;
    private RelativeLayout mPlayLoadingLayout;
    private ScreenBroadcastReceiver mScreenReceiver;
    private String videoPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MycsLog.i("info", "===========onCreate()===========");
        setContentView(R.layout.activity_player_video);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViews() {
        fl_close = (FrameLayout) findViewById(R.id.fl_close);
        fl_close.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            videoPath = bundle.getString("videoPath", "");
        }
        initLoadingLayout();
        initVideoView();
        mScreenReceiver = new ScreenBroadcastReceiver();
        registerScreenReceiver();
        if (videoPath != null && !videoPath.equals("")) {
            playerDirectVideo(videoPath);
        }
    }

    private void initLoadingLayout() {
        mPlayLoadingLayout = (RelativeLayout) findViewById(R.id.rl_loading);
//        mLoadingTitleTv = (TextView) findViewById(R.id.tv_title);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.loading_pb);
    }

    private void initVideoView() {
        mVideoView = (AppVideoView) findViewById(R.id.play_video_layout);
        mVideoView.setOnBackBtnListener(new MyOnBackBtnListener());
        mVideoView.setOnPreparedListener(new MyOnPreparedListener());
        mVideoView.setOnInfoListener(new MyOnInfoListener());
        mVideoView.setOnErrorListener(new MyOnErrorListener());
        mVideoView.setOnCompletionListener(new MyOnCompletionListener());
        mVideoView.setOnVideoStateListener(new MyOnVideoStateListener());
        mVideoView.setOnBufferingUpdateListener(new MyBufferingUpdateListener());
        mVideoView.setOnSeekCompleteListener(new MySeekCompleteListener());
        mVideoView.setVideoControllerDraggable(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        recordCurrentVideoPosition();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_close:
                finish();
                break;
        }
    }


    private Drawable getDrawableFromRes(int resId) {
        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resId);
        return new BitmapDrawable(bmp);
    }

    /**
     * 启动screen状态广播接收器
     */
    private void registerScreenReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, filter);
    }

    /**
     * 如果播放的是本地视频，播放前先解密
     */

    private class MyOnBackBtnListener implements AppVideoView.OnBackBtnListener {

        @Override
        public void onClicked() {
            //当用户要关闭视频播放界面时，通知Service停止下载
            LocalBroadcastManager.getInstance(videoPlayerActivity.this)
                    .sendBroadcast(new Intent("local.CLEAN_DOWNLOAD"));

            finish();
        }
    }

    private class MyOnPreparedListener implements IMediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            showLoadingLayout(false); // 加载页面消失
            Log.i("info", "======onPrepared中====checkIfTime");
//            checkIfTimeToShowPaper();
//            checkTwoMinuteLimit();
            onMediaPlayerPrepared();
        }
    }

    /**
     * onPrepared回调 , 留待子类继承用
     */
    protected void onMediaPlayerPrepared() {

    }

    private class MyOnErrorListener implements IMediaPlayer.OnErrorListener {

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    break;
                default:
                    break;
            }

            MycsLog.e("play video error: what:" + what + ", extra:" + extra);
            if (NetworkUtil.networkAvailable(videoPlayerActivity.this)) {
                showToast(R.string.error_video_play_error, true);
            } else {
                showToast(R.string.error_network_unavailable, false);
            }
            return true;
        }
    }

    private class MyOnInfoListener implements IMediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            if (i == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            } else if (i == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            }
            return false;
        }
    }

    private class MyOnCompletionListener implements IMediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
//            onVideoComplete();

//            FileUtil.deleteVideoFiles();
//            FileUtil.recoverLocalFiles(mVideoPath);
        }
    }

    private class MyOnVideoStateListener implements AppVideoView.OnVideoStateListener {
        @Override
        public void onStart() {
            showLoadingLayout(false);
            Log.i("info", "======onStart()中====checkIfTime");
//            checkIfTimeToShowPaper();
//            checkTwoMinuteLimit();
        }

        @Override
        public void onPause() {
//            cancelTestPaperShowDelayed();
            Log.i("info", "==================视频播放停止=============");
        }

        @Override
        public void onPlayNext() {
//            onVideoComplete();
        }

        @Override
        public void onProgressChange(int progress) {
            showLoadingLayout(false);
        }
    }

    private class MyBufferingUpdateListener implements IMediaPlayer.OnBufferingUpdateListener {

        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
//            Toast.makeText(context, "onBufferingUpdate()", Toast.LENGTH_LONG).show();
//            Log.i("info", "第onBufferingUpdate()");
        }
    }

    private class MySeekCompleteListener implements IMediaPlayer.OnSeekCompleteListener {
        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
//            Log.i("info", "第onSeekComplete()");
//            Toast.makeText(context, "onSeekComplete()", Toast.LENGTH_LONG).show();
        }
    }

    private class MyPhoneListener implements PhoneStateReceiver.PhoneListener {

        @Override
        public void idle() {
        }

        @Override
        public void calling() {
            if (mVideoView.isInPlaybackState()) { // 手机来电：停止播放
                recordCurrentVideoPosition();
                mVideoView.pause();
            }
        }
    }

    /**
     * screen状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                Log.i("info", "sadas");
                mVideoView.getmVideoView().resume();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
//                recordCurrentVideoPosition();
//                mVideoView.pause();
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
//                mVideoView.restart();
            }
        }
    }

//    protected void setVideoTitle(String title) {
////        mLoadingTitleTv.setText(title);
//        mVideoView.setVideoTitle(title);
//    }

    protected void hideControllerView() {
        mVideoView.hideController();
    }

    protected void playerDirectVideo(String videoPath) {
        mVideoView.PlayVideo(videoPath);
    }

//    /**
//     * 视频播放结束
//     */
//    protected abstract void onVideoComplete();

    private void showLoadingLayout(final boolean show) {
        mPlayLoadingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoadingProgressBar.setIndeterminate(show);
    }

    private void recordCurrentVideoPosition() {
        mVideoView.recordVideoProgress(mVideoView.getCurrentPosition());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScreenReceiver);
    }
}
