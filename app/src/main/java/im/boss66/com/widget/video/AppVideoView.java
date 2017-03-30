package im.boss66.com.widget.video;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.widget.popupWindows.LoadingPop;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class AppVideoView extends FrameLayout implements View.OnTouchListener {

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private int mVideoState = STATE_IDLE;

    private int mTargetState = STATE_IDLE;

    private int mScreenWidth, mScreenHeight;

    private int mVideoWidth, mVideoHeight;

    private int mSeekWhenPrepared = 0;

    private int mThreshold = 20;

//    private boolean mBrightnessViewShow = false;

    private int ViewShowType = 0;//1:亮度显示，2；声音显示，3：进度显示

    private boolean mIsControllerViewShowing = true; // 上边条与控制条的显示

    private boolean mHasTestPaper = false;

    private String mVideoPath = "";//m3u8文件路径

    private String mp4UrlPath = "";//mp4文件路径

//    private GestureDetector mGestureDetector;

    private MediaController mMediaController;

    private boolean draggable = true;

    private MediaControllerListener mMediaControllerListener;

//    private VideoTopbarView mTopbarView;

    private VideoBrightnessView mBrightnessView;

    private VideoVolumeView mVolumeView;

    //private VideoHandler mVideoHandler = new VideoHandler(this);
    private OnBackBtnListener mBackBtnListener;

    private IMediaPlayer.OnCompletionListener mOnCompletionListener;

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener;

    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;

    private IMediaPlayer.OnErrorListener mOnErrorListener;

    private IMediaPlayer.OnInfoListener mOnInfoListener;

    private IMediaPlayer.OnPreparedListener mOnPreparedListener;

    private ChaptersOnClickListener mVideoListListener;

    private OnVideoStateListener mOnVideoStateListener;
    private LivePlayCallbackImpl mVideoPlayCallback;
    private VideoView mVideoView;
    private Context mContext;

    public AppVideoView(Context context) {
        super(context);
        init(context);
    }

    public AppVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mIsShowOnTouch)
            mMediaController.show();
        return true;
    }

    /**
     * 播放视频
     *
     * @param videoPath 视频地址
     */
    public void PlayVideo(String videoPath) {
        mVideoPath = videoPath;
        mIsFromCaseCenter = true;
        mp4UrlPath = "";
        playVideo();
    }

    public void PlayLiveVideo(IMediaPlayer player, String videoPath) {
        mVideoPath = videoPath;
        mIsFromCaseCenter = true;
        mp4UrlPath = "";
        playLiveVideo(player);
    }

    /**
     * 播放视频
     *
     * @param videoPath 视频地址
     */
    public void playVideo(String mp4Url, String videoPath) {
        mVideoPath = videoPath;
        mp4UrlPath = mp4Url;
        MycsLog.i("mp4UrlPath:" + mp4UrlPath);
        MycsLog.i("mVideoPath:" + mVideoPath);
        playVideo();
    }

    public void playVideo(String videoPath) {
        mVideoPath = videoPath;
        playVideo();
    }

    private boolean mIsFromCaseCenter;

    public void playVideo(String videoPath, boolean isFromCaseCenter) {
        mIsFromCaseCenter = isFromCaseCenter;
        mVideoPath = videoPath;
        playVideo();
    }


//    /**
//     * 播放临时的m3u8文件
//     */
//    public void playTempM3u8() {
//        mVideoView.setVideoPath(Constants.Paths.VIDEO_TEMP_PATH + "temp.m3u8");
//        mVideoView.start();
//    }

    public String getVideoUrl() {
        return mVideoPath;
    }

    public void setVideoUrl(String videoUrl) {
        mVideoPath = videoUrl;
    }

    /**
     * 设置视频时长
     *
     * @param duration
     */
    public void setDuration(int duration) {
        mMediaController.setDuration(duration);
    }

    /**
     * 设置视频时长
     *
     * @param duration 00:00:00格式
     */
    public void setDuration(String duration) {
        mMediaController.setDuration(duration);
    }

    /**
     * 隐藏视频底部控制栏中的“选集”按钮
     */
//    public void hideControllerChaptersBtn() {
//        mMediaController.setChapterTvVisible(false);
//    }
    public void hideController() {
//        mTopbarView.setVisibility(View.GONE);
        mVideoView.showControl(false);
        mMediaController.hideRootView();
    }


    public void showController() {
//        mTopbarView.setVisibility(View.VISIBLE);
        mVideoView.showControl(true);
        mMediaController.showRootView();
    }

//    public void showControllerChaptersBtn() {
//        mMediaController.setChapterTvVisible(true);
//    }

    public void setChaptersOnClickListener(ChaptersOnClickListener listener) {
        mVideoListListener = listener;
    }

//    public void hidePlayNextBtn() {
//        mMediaController.hidePlayNextBtn();
//    }
//
//    public void showPlayNextBtn() {
//        mMediaController.showPlayNextBtn();
//    }

    /**
     * 视频处于播放或暂停状态
     *
     * @return
     */
    public boolean isInPlaybackState() {
        return (mVideoView != null && mVideoState != STATE_ERROR && mVideoState != STATE_IDLE && mVideoState != STATE_PREPARING);
    }

    /**
     * 设置视频是否可拖动进度条
     *
     * @param draggable
     */
    public void setVideoControllerDraggable(boolean draggable) {
        this.draggable = draggable;
        mMediaController.setVideoControllerDraggable(draggable);
    }

    public void setVideoControllerEnable(boolean enable) {
        mMediaController.setOnEnable(enable);
    }

    /**
     * 设置视频显示标题
     *
     * @param
     */
//    public void setVideoTitle(String title) {
//        mTopbarView.setVideoTitle(title);
//    }

//    public void startTopScroll() {
////        mTopbarView.startScroll();
//    }
    public void hasTestPaper(boolean hasTestPaper) {
        mHasTestPaper = hasTestPaper;
    }

    /**
     * 获取视频播放的当前进度
     *
     * @return
     */
    public int getCurrentPosition() {
        return mVideoView == null ? 0 : mVideoView.getCurrentPosition();
    }

    public void resetPlayer() {
        mVideoView.resetNewPlayer();
    }


//    public void initMediaDuration() {
//        mMediaController.setDuration(0);
//    }

    /**
     * 记录视频播放进度
     *
     * @param position
     */
    public void recordVideoProgress(int position) {
        mSeekWhenPrepared = position;
        App.getInstance().setCurrentProgress(mSeekWhenPrepared);
    }


    public int getVideoProgress() {
        return App.getInstance().getCurrentProgress();
    }


    public void recordCurrentVideoProgress() {
        recordVideoProgress(getCurrentPosition());
    }

    public void pause() {
        if (mMediaControllerListener != null) {
            mMediaControllerListener.pause();
//            mMediaController.setPlayBtnTag(true);
        }
    }


    public void pauseVideo() {
        isTestPaper = true;
        if (mVideoView.isPlaying()) {
            MycsLog.w("pauseVideo2");
            mVideoView.pause();
            mVideoState = STATE_PAUSED;
        }

        mTargetState = STATE_PAUSED;
        if (mOnVideoStateListener != null) {
            mOnVideoStateListener.onPause();
        }
    }

    public VideoView getRealVideoView() {
        return mVideoView;
    }

//    private boolean isPerson() {
//        return (mVideoView != null && mVideoState != STATE_ERROR
//                && mVideoState != STATE_IDLE
//                && mVideoState != STATE_PREPARING);
//    }

    public void stopPlay() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            //mVideoView.release();
//            mVideoView = null;
            mVideoState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }

    public void close() {
        mVideoView.close();
        setVisibility(GONE);
    }


    /**
     * 隐藏视频控制界面
     */
    public void hideControllerView() {
        //showControllerView(false);
        mMediaController.show();
    }

    public void controlVolume(int position) {
        mVolumeView.show(position);
    }

    public void UpVolume(int position) {
        mVolumeView.show(position);
    }

    public void MinsVolume(int position) {
        mVolumeView.showMins(position);
    }

    /**
     * Register a callback to be invoked when the media file is loaded and ready
     * to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file has been
     * reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener l) {
        mOnBufferingUpdateListener = l;
    }

    public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener l) {
        mOnSeekCompleteListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs during playback or
     * setup. If no listener is specified, or if the listener returned false,
     * VideoView will inform the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * Register a callback to be invoked when an informational event occurs
     * during playback or setup.
     *
     * @param l The callback that will be run
     */
    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }

    public void setOnBackBtnListener(OnBackBtnListener l) {
        mBackBtnListener = l;
    }

    public void setOnVideoStateListener(OnVideoStateListener l) {
        mOnVideoStateListener = l;
    }

    private void init(Context context) {
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_play_video, this, true);
        mScreenWidth = UIUtils.getScreenWidth(context);
        mScreenHeight = UIUtils.getScreenHeight(context);
//        mGestureDetector = new GestureDetector(context, new GestureListener());
//        initTopBar();
        initMediaController();
        initMediaPlayer();
        mBrightnessView = (VideoBrightnessView) findViewById(R.id.video_view_light);
        mVolumeView = (VideoVolumeView) findViewById(R.id.video_view_volume);
    }


    private void initMediaPlayer() {
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setLongClickable(true);
        mVideoView.setOnTouchListener(this);
        mVideoView.setMediaBufferingIndicator(findViewById(R.id.buffering_indicator));
        setMediaPlayerListener();
    }

    public VideoView getmVideoView() {
        return mVideoView;
    }

    private void initMediaController() {
        mMediaController = (MediaController) findViewById(R.id.video_view_mc);
        mMediaControllerListener = new MediaControllerListener();
        mMediaController.setVideoControllerListener(mMediaControllerListener);
        mMediaController.setMediaLiveControl(new mediaLiveControlImpl());
//      mMediaController.setOnTouchListener(this);
//        mMediaController.setOnHiddenListener(new MediaController.OnHiddenListener() {
//            @Override
//            public void onHidden() {
//                mTopbarView.setVisibility(GONE);
//            }
//        });
//        mMediaController.setOnShownListener(new MediaController.OnShownListener() {
//            @Override
//            public void onShown() {
//                mTopbarView.setVisibility(VISIBLE);
//            }
//        });
    }

    private LiveControlCallback liveControlCallback;

    public void setLiveControlCallback(LiveControlCallback liveControlCallback) {
        this.liveControlCallback = liveControlCallback;
    }

    public interface LiveControlCallback {
        void onExpand();

        void onShrink();
    }

    public void setPageType(MediaController.PageType pageType) {
        if (mMediaController != null) {
            mMediaController.setPageType(pageType);
        }
    }

    public void setExpand(boolean flag) {
        if (mMediaController != null) {
            mMediaController.setExpand(flag);
        }
    }

    private class mediaLiveControlImpl implements MediaController.LiveControlImpl {
        @Override
        public void onExpand() {
            if (liveControlCallback != null) {
                liveControlCallback.onExpand();
            }
            if (mVideoPlayCallback != null) {
                mVideoPlayCallback.onSwitchPageType();
            }
        }

        @Override
        public void onShrink() {
            if (liveControlCallback != null) {
                liveControlCallback.onShrink();
            }
            if (mVideoPlayCallback != null) {
                mVideoPlayCallback.onSwitchPageType();
            }
        }
    }

//    private void initTopBar() {
//        mTopbarView = (VideoTopbarView) findViewById(R.id.video_view_topbar);
//        mTopbarView.setOnTouchListener(this);
//        mTopbarView.setOnBackBtnClickListener(new VideoTopbarView.OnBackBtnClickListener() {
//
//            @Override
//            public void onClicked() {
//                //MycsLog.d("video topbar OnBackBtnClicked");
//                if (mBackBtnListener != null) {
//                    mBackBtnListener.onClicked();
//                }
//            }
//        });
//    }

    private void seekVideoTo(int position) {
        mVideoView.seekTo(position);
    }

    private void playVideo() {

//        if (TextUtils.isEmpty(mVideoPath)
//                && TextUtils.isEmpty(mp4UrlPath)) {
//            return;
//        }
        if (TextUtils.isEmpty(mVideoPath)) {
            return;
        }
        /**
         * 如果已经开发播放，先停止播放
         */
        if (mVideoState != STATE_IDLE) {
            mVideoView.stopPlayback();
        }
        initDataSource();
//        InitDataSourcePrepareAsync4();
    }

    private void playLiveVideo(IMediaPlayer player) {
        if (TextUtils.isEmpty(mVideoPath)
                && TextUtils.isEmpty(mp4UrlPath)) {
            return;
        }

        /**
         * 如果已经开发播放，先停止播放
         */
        if (mVideoState != STATE_IDLE) {
            mVideoView.stopPlayback();
        }
        initLiveDataSource(player);
    }

    /**
     * 视频是否正在播放
     *
     * @return true 视频正在播放
     */
    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    /**
     * 设置MediaPlayer的监听器
     */
    private void setMediaPlayerListener() {
        mVideoView.setOnPreparedListener(new PreparedListener());
        mVideoView.setOnCompletionListener(new CompletionListener());
        mVideoView.setOnErrorListener(new ErrorListener());
        mVideoView.setOnInfoListener(new InfoListener());
        mVideoView.setOnBufferingUpdateListener(new BufferingUpdateListener());
        mVideoView.setOnSeekCompleteListener(new SeekCompleteListener());
    }


//    private boolean isVideoFileFull() {
//        String cachePath = Constants.Paths.VIDEO_CACHE_PATH;//缓存文件路径
//        final String filePath = cachePath + AppUtil.getM3u8FileNameFromUrl(mVideoPath);//m3u8文件路径
//        MycsLog.i("文件夹是否存在：" + FileUtil.isDirExist(cachePath));
//        MycsLog.i("m3u8文件是否存在：" + AppUtil.getM3u8FileNameFromUrl(mVideoPath) + ":" + FileUtil.isDirExist(filePath));
//
//        if (FileUtil.isDirExist(cachePath) && FileUtil.isDirExist(filePath)) {
//            final ArrayList<DownloadVideoService.DownloadInfo> downloadInfos =
//                    AppUtil.getDownloadListFromM3u8(filePath);//从M3u8文件中获取下载信息的列表
//            return isAllTsExist(downloadInfos);
//        } else {
//            return false;
//        }
//    }

//    private boolean isAllTsExist(ArrayList<DownloadVideoService.DownloadInfo> list) {
//        int sum = 0;
//        for (DownloadVideoService.DownloadInfo info : list) {
//            if (FileUtil.isDirExist(info.getSavePath())) {
//                sum++;
//            }
//        }
//
//        MycsLog.i("是否sum:" + sum);
//        MycsLog.i("是否size:" + list.size());
//        if (sum != list.size()) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    private String getEncryptFilePath(String url) {
        int index = url.lastIndexOf('.');
        return url.substring(0, index);
    }

    private LoadingPop loadingPop = new LoadingPop(getContext());
    private int currentPosition = 0;

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity activity = (Activity) getContext();
            switch (msg.what) {
                case 0:
                    if (activity != null && !activity.isFinishing()) {
                        loadingPop.dismissPop();
                    }
                    break;
                case 1:
                    if (activity != null && !activity.isFinishing()) {
                        loadingPop.show(getRootView());
                    }
                    break;
            }
        }
    };

    private boolean isRunning = false;
    private boolean isSelectChapter = false;
    private boolean isPauseBtn = false;

    private stateThread stateThread;

    public void stopListenePlayState() {
        isRunning = false;
        isContinue = false;
        isSelectChapter = true;
        stateThread = null;
    }

//    private void InitDataSourcePrepareAsync() {//初始化数据
//        mVideoView.setMediaController(mMediaController);
//        mVideoState = STATE_PREPARING;
//        MycsLog.i("是否mVideoPath:" + mVideoPath);
//        if (getContext() instanceof PlayTaskActivity) {
//            MycsLog.i("是否本地播放：" + isVideoFileFull());
//            if (!isVideoFileFull()) {//如果媒体文件完整，则边下载，边播放
////                mVideoView.setVideoPath(mVideoPath);
//                loadDataSources();
//            } else {//本地已经存在文件，播放本地m3u8文件
//                String cachePath = Constants.Paths.VIDEO_CACHE_PATH;//缓存文件路径
//                final String filePath = cachePath + AppUtil.getM3u8FileNameFromUrl(mVideoPath);//m3u8文件路径
//
//                mVideoView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mVideoView.setVideoPath(filePath);
//                        mVideoView.start();
//                    }
//                });
//            }
//        } else {
//            mVideoView.setVideoPath(mVideoPath);
//            mVideoView.start();
//        }
//    }

//    @Override
//    public void update(Observable observable, Object data) {
//        SessionInfo sin = (SessionInfo) data;
//        if (sin.getAction() == Session.ACTION_VIDEO_START_PLAY) {
//            String cachePath = Constants.Paths.VIDEO_CACHE_PATH;//缓存文件路径
//            final String filePath = cachePath + AppUtil.getM3u8FileNameFromUrl(mVideoPath);//m3u8文件路径
//            mVideoView.post(new Runnable() {
//                @Override
//                public void run() {
//                    mVideoView.setVideoPath(filePath);
//                    mVideoView.start();
//
//                    if (stateThread == null) {
//                        isRunning = true;
//                        isSelectChapter = false;
//                        isContinue = true;
//                        count = 0;
//
//                        stateThread = new stateThread();
//                        stateThread.start();
//                    }
//                }
//            });
//        }
//    }


    public void setContinue(boolean state) {
        mVideoView.setContinueState(state);
    }

    private void initDataSource() {
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.start();
    }

    private void initLiveDataSource(IMediaPlayer player) {
        mVideoView.setLiveVideoPath(player, mVideoPath);
        mVideoView.start();
    }
//    private void InitDataSourcePrepareAsync4() {//初始化数据
//        mVideoView.setMediaController(mMediaController);
//        mVideoState = STATE_PREPARING;
//        MycsLog.i("是否mVideoPath:" + mVideoPath);
//        MycsLog.i("是否mmp4UrlPath:" + mp4UrlPath);
//
//        String cachePath = Constants.Paths.VIDEO_CACHE_PATH;//缓存文件路径
//        String mp4FilePath = cachePath + App.getInstance().getUid() + "_" + AppUtil.getFileNameFromPath(mp4UrlPath);
//        File file = new File(mp4FilePath);
//
//        if (getContext() instanceof PlayTaskActivity
//                || getContext() instanceof PlayDetailsTaskActivity) {
//            MycsLog.i("是否本地播放：" + isVideoFileFull());
////            isExistMp4(mp4UrlPath);
//            if (file.exists() && isLoaded(mp4UrlPath)) {
//                MycsLog.i("播放mp4文件");
////                String cachePath = Constants.Paths.VIDEO_CACHE_PATH;//缓存文件路径
////                String filePath = cachePath + App.getInstance().getUid() + "_" + AppUtil.getFileNameFromPath(mp4UrlPath);
////                File file = new File(filePath);
////                mVideoView.setVideoPath(filePath);
////                mVideoView.start();
//                VideoEncryptionUtil.decryptFileMp4(file);
//                PreferenceUtils.putString(getContext(), KeyUtil.KEY_ENTRY_DENTRY, "mp4");
//                mVideoView.setVideoPath(mp4FilePath);
//                mVideoView.start();
//            } else {
//                PreferenceUtils.putString(getContext(), KeyUtil.KEY_ENTRY_DENTRY, "online");
//                mVideoView.setVideoPath(mVideoPath);
////                mVideoView.setVideoPath(mp4UrlPath);
////                mVideoView.setVideoPath("http://v1.mycs.cn/91/91/500125/Pzs8PDw6bCUj.mp4");
////                if (!isVideoFileFull()) {//如果不是本地播放，则在线播放
////                    PreferenceUtils.putString(getContext(), KeyUtil.KEY_ENTRY_DENTRY, "online");
////                    mVideoView.setVideoPath(mVideoPath);
////                    loadDataSources();
////                } else {//本地已经存在文件，播放本地m3u8文件
//////                    String cachePath = Constants.Paths.VIDEO_CACHE_PATH;//缓存文件路径
////                    final String filePath = cachePath + AppUtil.getM3u8FileNameFromUrl(mVideoPath);//m3u8文件路径
////                    stopListenePlayState();
////                    DownloadSourcesService.stopCurrentService(getContext());
////                    VideoEncryptionUtil.decryptTsByPath(filePath);
////                    PreferenceUtils.putString(getContext(), KeyUtil.KEY_ENTRY_DENTRY, "m3u8");
////                    mVideoView.post(new Runnable() {
////                        @Override
////                        public void run() {
////                            mVideoView.setVideoPath(filePath);
////                            mVideoView.start();
////                        }
////                    });
////                }
//            }
//        } else {
////            isExistMp4(mp4UrlPath)
//            if (file.exists() && isLoaded(mp4UrlPath)) {
////                String cachePath = Constants.Paths.VIDEO_CACHE_PATH;//缓存文件路径
////                String filePath = cachePath + App.getInstance().getUid() + "_" + AppUtil.getFileNameFromPath(mp4UrlPath);
////                mVideoView.setVideoPath(filePath);
////                mVideoView.start();
////                String filePath = cachePath + App.getInstance().getUid() + "_" + AppUtil.getFileNameFromPath(mp4UrlPath);
//                VideoEncryptionUtil.decryptFileMp4(file);
//                PreferenceUtils.putString(getContext(), KeyUtil.KEY_ENTRY_DENTRY, "mp4");
//                mVideoView.setVideoPath(mp4FilePath);
//                mVideoView.start();
//            } else {
//                PreferenceUtils.putString(getContext(), KeyUtil.KEY_ENTRY_DENTRY, "online");
//                mVideoView.setVideoPath(mVideoPath);
////                mVideoView.setVideoPath(mp4UrlPath);
////                mVideoView.setVideoPath("http://v1.mycs.cn/91/91/500125/Pzs8PDw6bCUj.mp4");
//                mVideoView.start();
//            }
//        }
//    }


    private int count = 0;

    private boolean isContinue = true;

    private boolean isTestPaper = false;

    private class stateThread extends Thread {//监听播放是否暂停

        @Override
        public void run() {
            super.run();
            while (isRunning) {
                boolean flag = isPlaying();
                MycsLog.i("是否正在播放：" + flag);
                if (!flag && count > 1 && !isSelectChapter) {//处于播放暂停状态
                    if (isContinue && !isPauseBtn) {
                        count = 0;
                        isContinue = false;
                        mhandler.sendMessage(mhandler.obtainMessage(1));
                    }
                } else {
                    count++;
                    isContinue = true;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 向文件追加内容
     *
     * @param path    文件的路径
     * @param content 要追加的内容
     */

    public void appendContentToFile(String path, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(path, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*private void downloadM3u8(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String savePath = Constants.Paths.VIDEO_CACHE_PATH + getM3u8FileNameFromUrl(mVideoPath);

                    new Downloader().downloadFile(
                            path,
                            new RandomAccessFile(new File(savePath), "rwd"),
                            0L,
                            3,
                            new Downloader.OnProgressUpdateListener() {
                                @Override
                                public void onProgressUpdate(long downloadedBytes, long fileLength) {

                                }

                                @Override
                                public void onComplete() {
                                    String[] contents = getContentFromM3u8(savePath);
                                    createTempM3u8(contents[0]);

                                    if(contents[0].contains(".ts")) {
                                        mVideoView.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mVideoView.setVideoPath(Constants.Paths.VIDEO_TEMP_PATH + "temp.m3u8");
                                                mVideoView.start();
                                            }
                                        });
                                    }

                                    Intent intent = new Intent(getContext(), DownloadVideoService.class);
                                    intent.putExtra(DownloadVideoService.TASK_INFOS, getDownloadListFromM3u8(savePath));
                                    intent.putExtra(DownloadVideoService.M3U8_PATH, savePath);
                                    getContext().startService(intent);


                                }

                                @Override
                                public void onError(Exception e) {

                                }
                            });
                } catch (FileNotFoundException e) {
                    MycsLog.e("file not found:" + e.getMessage());
                }
            }
        }).start();
    }*/

//    private void createTempM3u8(String content) {
//        String tempPath = Constants.Paths.VIDEO_TEMP_PATH;
//        File dir = new File(tempPath);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        File tempM3u8 = new File(tempPath + "temp.m3u8");
//        if (tempM3u8.exists()) {
//            tempM3u8.delete();
//        }
//        try {
//            tempM3u8.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //String content = getContentFromM3u8(originM3u8Path);
//        write(tempPath + "temp.m3u8", content);
//        //return content.contains(".ts");
//    }

    /*private ArrayList<DownloadVideoService.DownloadInfo> createDownloadInfosFromM3u8(String m3u8Path) {
        ArrayList<DownloadVideoService.DownloadInfo> resultList = null;
        try {
            resultList = new ArrayList<DownloadVideoService.DownloadInfo>();
            InputStream in = new FileInputStream(new File(m3u8Path));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (line.contains("#EXTINF")) {
                    String duration = line.substring(line.indexOf(':') + 1, line.length() - 1);

                    line = reader.readLine();
                    if(line.contains(".ts") && !line.startsWith("file://")) {
                        DownloadVideoService.DownloadInfo downloadInfo = new DownloadVideoService.DownloadInfo();
                        downloadInfo.setDuration(duration);
                        downloadInfo.setSavePath(Constants.Paths.VIDEO_CACHE_PATH + line);
                        downloadInfo.setDownUrl("http://software.swwy.com/" + line);
                        resultList.add(downloadInfo);
                    }
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }*/


    private String[] getContentFromM3u8(String m3u8Path) {
        String[] contents = new String[2];
        //需要生成的StringBuilder
        StringBuilder initContent = new StringBuilder();
        StringBuilder appendContent = new StringBuilder();
        try {
            InputStream in = new FileInputStream(new File(m3u8Path));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {

                if (line.contains(".ts") && !line.startsWith("file://")) {
                    initContent.delete(initContent.lastIndexOf("#"), initContent.length());
                    break;
                } else if (line.contains(".ts") && line.startsWith("file://")) {
                    count++;
                }

                if (count >= 3) {
                    appendContent.append(line + "\r\n");
                } else {
                    initContent.append(line + "\r\n");
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        contents[0] = initContent.toString();
        contents[1] = appendContent.toString();
        return contents;
    }

    /**
     * 将内容回写到文件中
     *
     * @param filePath
     * @param content
     */
    public void write(String filePath, String content) {
        BufferedWriter bw = null;

        try {
            // 根据文件路径创建缓冲输出流
            bw = new BufferedWriter(new FileWriter(filePath));
            // 将内容写入文件中
            bw.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    bw = null;
                }
            }
        }
    }

    /*private String getM3u8FileNameFromUrl(String url) {
        int index = url.lastIndexOf('/');
        return url.substring(index + 1);
    }*/
    public void restart() {
        isTestPaper = false;
        if (isInPlaybackState()) {
            mVideoView.start();
            mVideoState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
        if (mOnVideoStateListener != null) {
            mOnVideoStateListener.onStart();
        }
    }

    public void seekTo(long time) {
        mVideoView.seekTo(time);
    }

    public void reagainStart(int currentPosition) {
        if (isInPlaybackState()) {
            mVideoView.seekTo(currentPosition);
            mVideoView.start();
            mVideoState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
        if (mOnVideoStateListener != null) {
            mOnVideoStateListener.onStart();
        }
    }

//    public void restart() {
//        if (isInPlaybackState()) {
//            mVideoView.start();
//            mVideoState = STATE_PLAYING;
//        }
//        mTargetState = STATE_PLAYING;
//        if (mOnVideoStateListener != null) {
//            mOnVideoStateListener.onStart();
//        }
//    }

//    public void pausePlay() {
//        if (isInPlaybackState()) {
//            if (mVideoView.isPlaying()) {
//                mVideoView.pause();
//                mVideoState = STATE_PAUSED;
//            }
//        }
//        mTargetState = STATE_PAUSED;
//        if (mOnVideoStateListener != null) {
//            mOnVideoStateListener.onPause();
//        }
//    }

    private FingerListener fingerListener;

    public void setFingerListener(FingerListener listener) {
        this.fingerListener = listener;
    }

    public interface FingerListener {
        boolean onSingleTapUp(MotionEvent e);
    }

    int progress = 0;

    public void SwitchMediaControls() {
        if (mMediaController != null) {
            mVideoView.toggleMediaControlsVisibility();
        }
    }

    private boolean mIsShowOnTouch = true;// 是否在触摸的时候保持显示mMediaController

    /**
     * 设置是否在触摸的时候保持显示mMediaController
     *
     * @param isShowOnTouch
     */
    public void setShowMediaControllerOnTouch(boolean isShowOnTouch) {
        mIsShowOnTouch = isShowOnTouch;
        mMediaController.setIsShowOnTouch(isShowOnTouch);
    }

    private class GestureListener implements OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
//            mBrightnessViewShow = (e.getRawX() <= (mScreenWidth / 2));
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //MycsLog.d(e.toString());
//            boolean flag = !mIsControllerViewShowing;
//            showControllerView(flag);
//            if (mMediaController != null) {
//                mVideoView.toggleMediaControlsVisibility();
//            }

            if (fingerListener != null) {
                fingerListener.onSingleTapUp(e);
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            float e1X = e1.getRawX();
            float e2X = e2.getRawX();
            float Y = Math.abs(e1.getRawY() - e2.getY());
            float X = Math.abs(e1.getRawX() - e2.getRawX());
            int increment = (int) (distanceY / (mScreenHeight / 2.0) * 100);
            if (Y > X) {
                if (e1X < mScreenWidth / 2.0 && e2X < mScreenWidth / 2.0) {
                    ViewShowType = 1;
                } else if (e1X > mScreenWidth / 2.0 && e2X > mScreenWidth / 2.0) {
                    ViewShowType = 2;
                }
                dimissDialog();
            } else {
                ViewShowType = 3;
            }

            switch (ViewShowType) {
                case 1:
                    if (!(isShowing || mVolumeView.getVisibility() == View.VISIBLE)) {
                        dimissDialog();
                        mBrightnessView.show(increment);
                        mVolumeView.hide();
                    }
                    break;
                case 2:
                    if (!(isShowing || mBrightnessView.getVisibility() == View.VISIBLE)) {
                        dimissDialog();
                        mVolumeView.show(increment);
                        mBrightnessView.hide();
                    }
                    break;
                case 3:
                    if (!(mVolumeView.getVisibility() == View.VISIBLE ||
                            mBrightnessView.getVisibility() == View.VISIBLE) && draggable) {
                        mBrightnessView.hide();
                        mVolumeView.hide();
                        showProgressDialog(e2X - e1X);
                    }
                    break;
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // TODO Auto-generated method stub
            return false;
        }

    }

    public void doStartPauseVideo(boolean isStop) {
        mMediaController.doStartPauseVideo(isStop);
    }

    private class MediaControllerListener implements MediaController.VideoControllerListener {

        @Override
        public void play() {
            isTestPaper = false;
            if (isInPlaybackState()) {
//                mVideoView.resume();
                mVideoState = STATE_PLAYING;
            }

            mTargetState = STATE_PLAYING;
            if (mOnVideoStateListener != null) {
                isPauseBtn = false;
                mOnVideoStateListener.onStart();
            }
        }

        @Override
        public void pause() {
            if (isInPlaybackState()) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mVideoState = STATE_PAUSED;
                }
            }

            mTargetState = STATE_PAUSED;
            if (mOnVideoStateListener != null) {
                isPauseBtn = true;
                mOnVideoStateListener.onPause();
            }
        }

        @Override
        public void playNext() {
            if (mOnVideoStateListener != null) {
                mOnVideoStateListener.onPlayNext();
            }
        }

        @Override
        public void chapterOnClicked() {
            if (mVideoListListener != null) {
                hideControllerView();
                mVideoListListener.show(AppVideoView.this);
            }
        }

        @Override
        public void onProgressChange(int progress) {
            if (mOnVideoStateListener != null) {
                mOnVideoStateListener.onProgressChange(progress);
            }
        }
    }

    private class PreparedListener implements IMediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            if (mIsFromCaseCenter) mMediaController.setDuration(mVideoView.getDuration());
            mVideoState = STATE_PREPARED;
            mVideoWidth = iMediaPlayer.getVideoWidth();
            mVideoHeight = iMediaPlayer.getVideoHeight();

            if (mSeekWhenPrepared != 0) {
                mVideoView.seekTo(mSeekWhenPrepared);
            }

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(iMediaPlayer);
            }
        }
    }


    private class CompletionListener implements IMediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {

            if (mVideoView.getCurrentState() == 4) {
                if (isInPlaybackState()) {
                    if (mVideoView.isPlaying()) {
                        mVideoView.pause();
                        mVideoState = STATE_PAUSED;
                    }
                }
                mTargetState = STATE_PAUSED;
                if (mOnVideoStateListener != null) {
                    isPauseBtn = true;
                    mOnVideoStateListener.onPause();
                }
                return;
            }

            mVideoState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            recordVideoProgress(0);
            //showControllerView(false);
            MycsLog.i("=====================onCompletion()");
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(iMediaPlayer);
            }

            if (mVideoPlayCallback != null) {
                mVideoPlayCallback.onPlayFinish();
            }
        }
    }

    private class BufferingUpdateListener implements IMediaPlayer.OnBufferingUpdateListener {

        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            mOnBufferingUpdateListener.onBufferingUpdate(iMediaPlayer, i);
        }
    }

    private class SeekCompleteListener implements IMediaPlayer.OnSeekCompleteListener {

        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            mOnSeekCompleteListener.onSeekComplete(iMediaPlayer);
        }
    }

    private class InfoListener implements IMediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            if (mOnInfoListener != null) {
                doInfoThings(i);
                mOnInfoListener.onInfo(iMediaPlayer, i, i1);
            }
            return false;
        }
    }

    private void doInfoThings(int result) {
        switch (result) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START://暂停播放开始缓冲更多数据
//                if (!NetworkUtil.networkAvailable(getContext())) {
//                    Toast.makeText(getContext(), "网络不可用！", Toast.LENGTH_LONG).show();
//                } else {
                mhandler.sendMessage(mhandler.obtainMessage(1));
//                }
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END://缓冲了足够的数据重新开始播放
                mhandler.sendMessage(mhandler.obtainMessage(0));
                break;
        }
    }

    private class ErrorListener implements IMediaPlayer.OnErrorListener {

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int framework_err, int impl_err) {
            mVideoState = STATE_ERROR;
            mTargetState = STATE_ERROR;

            /* If an error handler has been supplied, use it and finish. */
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(iMediaPlayer, framework_err, impl_err)) {
                    return true;
                }
            }
            return false;
        }
    }

    public interface OnBackBtnListener {
        public void onClicked();
    }

    public interface ChaptersOnClickListener {
        public void show(View videoView);
    }

    public interface OnVideoStateListener {
        /**
         * 开始播放
         *
         * @return: void
         */
        public void onStart();

        /**
         * 暂停播放
         *
         * @return: void
         */
        public void onPause();

        /**
         * 播放下一章节
         */
        void onPlayNext();

        void onProgressChange(int progress);
    }

    ProgressBar mDialogProgressBar;
    Dialog mProgressDialog;
    ImageView mDialogIcon;
    TextView mDialogCurrentTime, mDialogTotalTime;
    int mResultTimePosition = 0;
    int prevPosition = 0;
    private int curretnPosition = 0;
    private boolean isShowing = false;

    protected void showProgressDialog(float deltaX) {
        if (mProgressDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jc_progress_dialog, null);
            mDialogProgressBar = ((ProgressBar) localView.findViewById(R.id.duration_progressbar));
            mDialogCurrentTime = ((TextView) localView.findViewById(R.id.tv_current));
            mDialogTotalTime = ((TextView) localView.findViewById(R.id.tv_duration));
            mDialogIcon = ((ImageView) localView.findViewById(R.id.duration_image_tip));
            mProgressDialog = new Dialog(getContext(), R.style.jc_style_dialog_progress);
            mProgressDialog.setContentView(localView);
            mProgressDialog.getWindow().addFlags(Window.FEATURE_ACTION_BAR);
            mProgressDialog.getWindow().addFlags(32);
            mProgressDialog.getWindow().addFlags(16);
            mProgressDialog.getWindow().setLayout(-2, -2);
            WindowManager.LayoutParams localLayoutParams = mProgressDialog.getWindow().getAttributes();
            localLayoutParams.gravity = 49;
            localLayoutParams.y = getResources().getDimensionPixelOffset(R.dimen.jc_progress_dialog_margin_top);
            mProgressDialog.getWindow().setAttributes(localLayoutParams);
        }
        if (!mProgressDialog.isShowing()) {
            isShowing = true;
            curretnPosition = getCurrentPosition();
            mProgressDialog.show();
        }
        int totalTime = mVideoView.getDuration();
        int TimePosition = (int) (getCurrentPosition() + deltaX * totalTime / mScreenWidth);
        MycsLog.i("info", "deltaX:" + deltaX);
        if (TimePosition <= totalTime) {
            mResultTimePosition = TimePosition;
            mDialogCurrentTime.setText(generateTime(TimePosition));
            mDialogTotalTime.setText(" / " + generateTime(totalTime) + "");
            mDialogProgressBar.setProgress(TimePosition * 100 / totalTime);
            int minus = TimePosition - prevPosition;
            prevPosition = TimePosition;
            if (minus > 0) {
                mDialogIcon.setBackgroundResource(R.drawable.jc_forward_icon);
            } else {
                mDialogIcon.setBackgroundResource(R.drawable.jc_backward_icon);
            }
        } else {
            mResultTimePosition = totalTime;
            mDialogCurrentTime.setText(generateTime(totalTime));
            mDialogTotalTime.setText(" / " + generateTime(totalTime) + "");
            mDialogProgressBar.setProgress(100);
            if (deltaX > 0) {
                mDialogIcon.setBackgroundResource(R.drawable.jc_forward_icon);
            } else {
                mDialogIcon.setBackgroundResource(R.drawable.jc_backward_icon);
            }
        }
    }

    private static String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    .toString();
        }
    }

    private void dimissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            isShowing = false;
            mProgressDialog.dismiss();
        }
    }

    private boolean progressIsShowing() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    public void setVideoPlayCallback(LivePlayCallbackImpl videoPlayCallback) {
        mVideoPlayCallback = videoPlayCallback;
    }

    public interface LivePlayCallbackImpl {
        void onCloseVideo();

        void onSwitchPageType();

        void onPlayFinish();
    }

//    @Override
//    public void update(Observable observable, Object data) {
//        SessionInfo sin = (SessionInfo) data;
//        if (sin.getAction() == Session.ACTION_PLAYER_CURRENT_POSITION) {
//            MycsLog.i("info", "=====AppView中======update=======");
//            boolean flag = (boolean) sin.isFlag();
//            int postion = (int) sin.getData();
//            restart();
//            if (postion != 0 && !flag) {
//                seekTo(postion);
//            }
//        }
//    }
}
