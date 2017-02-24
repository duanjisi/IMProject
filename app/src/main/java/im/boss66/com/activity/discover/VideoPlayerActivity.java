package im.boss66.com.activity.discover;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 视频播放
 */
public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener, MediaPlayer.OnPreparedListener {

    private TextView tv_close;
    private MediaController mediaco;
    private VideoView vv_video;
    private ProgressBar pb_video;
    private ImageView iv_bg;
    private boolean fullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        initView();
    }

    private void initView() {
        pb_video = (ProgressBar) findViewById(R.id.pb_video);
        tv_close = (TextView) findViewById(R.id.tv_close);
        tv_close.setOnClickListener(this);
        int sceenH = UIUtils.getScreenHeight(this) / 10 * 3;
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra("url");
            String imgurl = intent.getStringExtra("imgurl");
            fullscreen = intent.getBooleanExtra("isFull",false);
            vv_video = (VideoView) findViewById(R.id.vv_video);

            if (!fullscreen) {//设置RelativeLayout的全屏模式
                RelativeLayout.LayoutParams layoutParams =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                vv_video.setLayoutParams(layoutParams);
                fullscreen = true;//改变全屏/窗口的标记
            } else {//设置RelativeLayout的窗口模式
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vv_video.getLayoutParams();
                params.height = sceenH;
                vv_video.setLayoutParams(params);
                fullscreen = false;//改变全屏/窗口的标记
            }

            iv_bg = (ImageView) findViewById(R.id.iv_bg);
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) iv_bg.getLayoutParams();
            params1.height = sceenH;
            iv_bg.setLayoutParams(params1);
            mediaco = new MediaController(this);
            if (!TextUtils.isEmpty(url)) {
                vv_video.setVideoPath(url);
                vv_video.setMediaController(mediaco);
                mediaco.setMediaPlayer(vv_video);
                vv_video.setOnPreparedListener(this);
                //让VideiView获取焦点
                vv_video.start();
                vv_video.requestFocus();
                if (!TextUtils.isEmpty(imgurl)) {
                    Glide.with(this).load(imgurl).into(iv_bg);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        pb_video.setVisibility(View.GONE);
        iv_bg.setVisibility(View.GONE);
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
                // 获得当前播放时间和当前视频的长度
                int currentPosition = vv_video.getCurrentPosition();
                int duration = vv_video.getDuration();
                int time = ((currentPosition * 100) / duration);
                // 设置进度条的主要进度，表示当前的播放时间
                SeekBar seekBar = new SeekBar(VideoPlayerActivity.this);
                seekBar.setProgress(time);
                // 设置进度条的次要进度，表示视频的缓冲进度
                seekBar.setSecondaryProgress(percent);
            }
        });
    }
}
