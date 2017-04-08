package im.boss66.com.activity.player;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.video.MediaHelp;
import im.boss66.com.widget.video.VideoMediaController;
import im.boss66.com.widget.video.VideoSuperPlayer;

/**
 * Created by Johnny on 2017/4/8.
 */
public class VideoPlayerActivity extends BaseActivity {
    private VideoSuperPlayer mVideo;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 横屏
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player_details);
        mVideo = (VideoSuperPlayer) findViewById(R.id.video);
        videoPath = getIntent().getExtras().getString("videoPath", "");
        if (!TextUtils.isEmpty(videoPath)) {
            mVideo.loadAndPlay(MediaHelp.getInstance(), videoPath, getIntent()
                    .getExtras().getInt("position"), true);
            mVideo.setPageType(VideoMediaController.PageType.EXPAND);
            mVideo.setVideoPlayCallback(new VideoSuperPlayer.VideoPlayCallbackImpl() {
                @Override
                public void onSwitchPageType() {
                    finish();
                }

                @Override
                public void onPlayFinish() {
                    finish();
                }

                @Override
                public void onCloseVideo() {
                    finish();
                }
            });
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("position", mVideo.getCurrentPosition());
        intent.putExtra("playType", 1);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaHelp.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaHelp.resume();
    }
}
