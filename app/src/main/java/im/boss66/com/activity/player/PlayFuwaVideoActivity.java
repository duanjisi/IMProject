package im.boss66.com.activity.player;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.MD5Util;
import im.boss66.com.Utils.NetworkUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.treasure.FindTreasureChildrenActivity;
import im.boss66.com.entity.ActionEntity;
import im.boss66.com.entity.FuwaVideoEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.services.VideoCacheService;
import im.boss66.com.widget.ActionSheet;

/**
 * Created by GMARUnity on 2017/3/27.
 */
public class PlayFuwaVideoActivity extends BaseActivity implements ActionSheet.OnSheetItemClickListener, View.OnClickListener {
    private View view;
    private PLVideoTextureView mVideoView;
    private String mVideoPath, videoPath;
    private ActionSheet actionSheet;
    private boolean isCacheVideo = false;
    private int mIsLiveStreaming = 1;
    private MediaController mMediaController;
    private ImageView iv_coverView, iv_close;
    private int mVideoRotation;
    private TextView tvNext, tvGo;
    private int currentIndex;
    private List<FuwaVideoEntity.DataBean> datas;
    private FuwaVideoEntity.DataBean dataBean;
    private String classid, position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        view = findViewById(R.id.parent);
        tvNext = (TextView) findViewById(R.id.tv_next);
        tvGo = (TextView) findViewById(R.id.tv_go);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_coverView = (ImageView) findViewById(R.id.iv_coverView);
        mVideoView = (PLVideoTextureView) findViewById(R.id.videoView);
        View loadingView = findViewById(R.id.loading);
        mVideoView.setBufferingIndicator(loadingView);
        mVideoView.setScreenOnWhilePlaying(true);
        int codec = getIntent().getIntExtra("mediaCodec", AVOptions.MEDIA_CODEC_SW_DECODE);
        tvNext.setOnClickListener(this);
        tvGo.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentIndex = bundle.getInt("position", 0);
            classid = bundle.getString("classid", "");
            position = bundle.getString("pos", "");
            String result = bundle.getString("result", "");
            if (!TextUtils.isEmpty(result)) {
                FuwaVideoEntity videoEntity = JSON.parseObject(result, FuwaVideoEntity.class);
                if (videoEntity.getCode() == 0) {
                    datas = videoEntity.getData();
                }
            }
        }

//        videoPath = getIntent().getStringExtra("videoPath");
//        mVideoPath = getVideoPath(videoPath);
        mMediaController = new MediaController(this, false, mIsLiveStreaming == 1);
        mMediaController.setMediaPlayer(mVideoView);
        mVideoView.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_FIT_PARENT);
        mVideoView.setMediaController(mMediaController);
        setOptions(codec);
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        mVideoView.setOnErrorListener(mOnErrorListener);
        mVideoView.setOnInfoListener(mOnInfoListener);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isCacheVideo = true;
                showActionSheet();
                return true;
            }
        });
        String imgurl = getIntent().getStringExtra("imgurl");
        if (!TextUtils.isEmpty(imgurl)) {
            iv_coverView.setVisibility(View.VISIBLE);
            Glide.with(this).load(imgurl).into(iv_coverView);
            mVideoView.setCoverView(iv_coverView);
        }
//        mVideoView.setVideoPath(mVideoPath);
        mVideoView.setOnVideoSizeChangedListener(new PLMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height, int videoSar, int videoDen) {
                Log.i("onVideoSizeChanged:", "width:" + width + "  " + "height:" + height + "  mVideoRotation:" + mVideoRotation);
                if (mVideoRotation == 270) {
                    //旋转视频
                    mVideoView.setDisplayOrientation(90);
                } else {
                    if (width > height) {
                        mVideoView.setDisplayOrientation(270);
                    } else {
                        mVideoView.setDisplayOrientation(mVideoRotation);
                    }
                }
            }
        });
        initPlayer();
//        mVideoView.start();
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_next:
                playNextVideo();
                break;
            case R.id.tv_go:
                if (!TextUtils.isEmpty(classid) && dataBean != null) {
                    Intent intent = new Intent(context, FindTreasureChildrenActivity.class);
                    if (classid.equals("i")) {
                        intent.putExtra("type", 4);
                    } else {
                        intent.putExtra("type", 3);
                    }
                    intent.putExtra("pos", position);
                    intent.putExtra("distance", dataBean.getDistance());
                    intent.putExtra("userid", dataBean.getUserid());
                    startActivity(intent);
                }
                break;
        }
    }

    private void initPlayer() {
        if (datas != null && datas.size() != 0) {
            FuwaVideoEntity.DataBean bean = datas.get(currentIndex);
            if (bean != null) {
                dataBean = bean;
                String imgurl = bean.getVideo().replace(".mp4", "jpg");
                if (!TextUtils.isEmpty(imgurl)) {
                    iv_coverView.setVisibility(View.VISIBLE);
                    Glide.with(this).load(imgurl).into(iv_coverView);
                    mVideoView.setCoverView(iv_coverView);
                }
                videoPath = bean.getVideo();
                mVideoPath = getVideoPath(videoPath);
                Log.i("info", "================mVideoPath:" + mVideoPath);
                mVideoView.setVideoPath(mVideoPath);
                mVideoView.start();
            }
        }
    }

    private void playNextVideo() {
        if (datas != null && datas.size() != 0) {
            if (currentIndex != datas.size() - 1) {
                currentIndex++;
            } else {
                currentIndex = 0;
            }
            FuwaVideoEntity.DataBean bean = datas.get(currentIndex);
            if (bean != null) {
                dataBean = bean;
                videoPath = bean.getVideo();
                mVideoPath = getVideoPath(videoPath);
                Log.i("info", "================mVideoPath:" + mVideoPath);
                mVideoView.setVideoPath(mVideoPath);
                mVideoView.start();
            }
        }
    }


    private String getVideoPath(String path) {
        String localPath = Constants.VIDEO_CACHE_PATH + FileUtils.getFileNameFromPath(path);
        File file = new File(localPath);
        if (file.exists()) {
            return localPath;
        } else {
//            Intent intent = new Intent(context, VideoCacheService.class);
//            intent.putExtra("video_path", path);
//            startService(intent);
            return path;
        }
    }

    private void showActionSheet() {
        actionSheet = new ActionSheet(PlayFuwaVideoActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        if (isCacheVideo) {
            actionSheet.addSheetItem("保存视频", ActionSheet.SheetItemColor.Black,
                    PlayFuwaVideoActivity.this);
        } else {
            actionSheet.addSheetItem("播放下一个", ActionSheet.SheetItemColor.Black,
                    PlayFuwaVideoActivity.this)
                    .addSheetItem("带我去寻宝", ActionSheet.SheetItemColor.Black,
                            PlayFuwaVideoActivity.this);
        }
        actionSheet.show();
    }

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            isCacheVideo = false;
            statisticsVideoNum();
            showActionSheet();
        }
    };

    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            switch (what) {
                case 10001:
                    //保存视频角度
                    mVideoRotation = extra;
                    break;
            }
            return false;
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    showToast("Invalid URL !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    showToast("404 resource not found !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    showToast("Connection refused !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    showToast("连接超时 !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    showToast("Empty playlist !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    showToast("Stream disconnected !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    showToast("Network IO Error !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    showToast("Unauthorized Error !", false);
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    showToast("Prepare timeout !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    showToast("Read frame timeout !", false);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    setOptions(AVOptions.MEDIA_CODEC_SW_DECODE);
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    break;
                default:
                    showToast("unknown error !", false);
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            if (isNeedReconnect) {
                sendReconnectMessage();
            } else {
                finish();
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };

    private void setOptions(int codecType) {
        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        //options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        options.setInteger(AVOptions.KEY_PROBESIZE, 500000000);
        // Some optimization with buffering mechanism when be set to 1
        //options.setInteger(AVOptions.KEY_LIVE_STREAMING, mIsLiveStreaming);
        if (mIsLiveStreaming == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, codecType);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        mVideoView.setAVOptions(options);
    }

    private void sendReconnectMessage() {
        //mLoadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(0x01), 1000);
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 0x01) {
                return;
            }
            showToast("正在重连...", false);
            if (NetworkUtil.networkAvailable(PlayFuwaVideoActivity.this)) {
                sendReconnectMessage();
                return;
            }
            mVideoView.setVideoPath(mVideoPath);
            mVideoView.start();
        }
    };

    @Subscribe
    public void onMessageEvent(ActionEntity event) {
        if (event != null) {
            String action = event.getAction();
            if (action.equals(Constants.Action.VIDEO_CACHE_SUCCESSED)) {
                showToast("视频缓存成功!", true);
            } else if (action.equals(Constants.Action.VIDEO_CACHE_FAILE)) {
                showToast("视频缓存失败!", true);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mVideoView.stopPlayback();
    }

    @Override
    public void onClick(int which) {
        switch (which) {
            case 1:
                if (isCacheVideo) {
                    if (!TextUtils.isEmpty(videoPath)) {
                        String localPath = Constants.VIDEO_CACHE_PATH + FileUtils.getFileNameFromPath(videoPath);
                        File file = new File(localPath);
                        if (file.exists()) {
                            showToast("视频已缓存!", true);
                        } else {
                            Intent intent = new Intent(context, VideoCacheService.class);
                            intent.putExtra("video_path", videoPath);
                            startService(intent);
                        }
                    }
                } else {
                    playNextVideo();
                }
                break;
            case 2:
                if (!TextUtils.isEmpty(classid) && dataBean != null) {
                    Intent intent = new Intent(context, FindTreasureChildrenActivity.class);
                    if (classid.equals("i")) {
                        intent.putExtra("type", 4);
                    } else {
                        intent.putExtra("type", 3);
                    }
                    intent.putExtra("pos", position);
                    intent.putExtra("distance", dataBean.getDistance());
                    intent.putExtra("userid", dataBean.getUserid());
                    startActivity(intent);
                }
                break;
        }
    }


    //播放完统计视频播放次数
    private void statisticsVideoNum() {
        if (dataBean != null && !TextUtils.isEmpty(classid)) {
            String time = "" + System.currentTimeMillis();
            String filemd5 = dataBean.getFilemd5();
            String url = HttpUrl.PLAY_VIDEO_NUM + "?filemd5=" + filemd5 + "&class=" + classid + "&time=" + time;
            String sigh = "/hit" + "?filemd5=" + filemd5 + "&class=" + classid + "&time=" + time + "&platform=boss66";
            sigh = MD5Util.getStringMD5(sigh);
            url = url + "&sign=" + sigh;
            HttpUtils httpUtils = new HttpUtils(60 * 1000);
            httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    String res = responseInfo.result;
                    if (!TextUtils.isEmpty(res)) {
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                            if (jsonObject.getInt("code") == 0) {
                                Log.i("info", "====================已经播放了一次视频!");
                            }
//                            else {
//                                showToast(jsonObject.getString("message"), false);
//                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i("onFailure", s);
                    showToast(e.getMessage(), false);
                }
            });
        }
    }
}
