/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2012 YIXIA.COM
 * Copyright (C) 2013 Zhang Rui <bbcallen@gmail.com>

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.boss66.com.widget.video;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import java.io.IOException;

import im.boss66.com.App;
import im.boss66.com.BuildConfig;
import im.boss66.com.R;
import im.boss66.com.Utils.MycsLog;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.pragma.DebugLog;


/**
 * Displays a video file. The VideoView class can load images from various
 * sources (such as resources or content providers), takes care of computing its
 * measurement from the video so that it can be used in any layout manager, and
 * provides various display options such as scaling and tinting.
 */
public class VideoView extends SurfaceView implements
        MediaController.MediaPlayerControl {
    private static final String TAG = VideoView.class.getName();

    private Uri mUri;
    private long mDuration;
    private String mUserAgent;

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int STATE_SUSPEND = 6;
    private static final int STATE_RESUME = 7;
    private static final int STATE_SUSPEND_UNSUPPORTED = 8;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    //    private int mVideoLayout = VIDEO_LAYOUT_ZOOM;
    private int mVideoLayout = VIDEO_LAYOUT_STRETCH;
    public static final int VIDEO_LAYOUT_ORIGIN = 0;
    public static final int VIDEO_LAYOUT_SCALE = 1;
    public static final int VIDEO_LAYOUT_STRETCH = 2;
    public static final int VIDEO_LAYOUT_ZOOM = 3;

    private SurfaceHolder mSurfaceHolder = null;
    private IMediaPlayer mMediaPlayer = null;

    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoSarNum;
    private int mVideoSarDen;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private MediaController mMediaController;
    private View mMediaBufferingIndicator;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
    private int mCurrentBufferPercentage;
    private long mSeekWhenPrepared;
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;
    private Context mContext;
    private Pair<Integer, Integer> mRes;//设备的屏幕分辨率
    private boolean mIsVideoInterrupt = false;
    private boolean isShowControl = true;


    public VideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVideoView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * Set the display options
     *
     * @param layout <ul>
     *               <li>{@link #VIDEO_LAYOUT_ORIGIN}
     *               <li>{@link #VIDEO_LAYOUT_SCALE}
     *               <li>{@link #VIDEO_LAYOUT_STRETCH}
     *               <li>{@link #VIDEO_LAYOUT_ZOOM}
     *               </ul>
     */
    public void setVideoLayout(int layout) {
        LayoutParams lp = getLayoutParams();
        if (mRes == null) {
            mRes = ScreenResolution.getResolution(mContext);
        }
        int windowWidth = mRes.first.intValue(), windowHeight = mRes.second.intValue();
        float windowRatio = windowWidth / (float) windowHeight;
        int sarNum = mVideoSarNum;
        int sarDen = mVideoSarDen;
        if (mVideoHeight > 0 && mVideoWidth > 0) {
            float videoRatio = ((float) (mVideoWidth)) / mVideoHeight;
            if (sarNum > 0 && sarDen > 0)
                videoRatio = videoRatio * sarNum / sarDen;
            mSurfaceHeight = mVideoHeight;
            mSurfaceWidth = mVideoWidth;

            if (VIDEO_LAYOUT_ORIGIN == layout && mSurfaceWidth < windowWidth
                    && mSurfaceHeight < windowHeight) {
                lp.width = (int) (mSurfaceHeight * videoRatio);
                lp.height = mSurfaceHeight;
            } else if (layout == VIDEO_LAYOUT_ZOOM) {
                lp.width = windowRatio > videoRatio ? windowWidth
                        : (int) (videoRatio * windowHeight);
                lp.height = windowRatio < videoRatio ? windowHeight
                        : (int) (windowWidth / videoRatio);
            } else {
                boolean full = layout == VIDEO_LAYOUT_STRETCH;
                lp.width = (full || windowRatio < videoRatio) ? windowWidth
                        : (int) (videoRatio * windowHeight);
                lp.height = (full || windowRatio > videoRatio) ? windowHeight
                        : (int) (windowWidth / videoRatio);
            }
            setLayoutParams(lp);
            getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);
            DebugLog.dfmt(
                    TAG,
                    "VIDEO: %dx%dx%f[SAR:%d:%d], Surface: %dx%d, LP: %dx%d, Window: %dx%dx%f",
                    mVideoWidth, mVideoHeight, videoRatio, mVideoSarNum,
                    mVideoSarDen, mSurfaceWidth, mSurfaceHeight, lp.width,
                    lp.height, windowWidth, windowHeight, windowRatio);
        }
        mVideoLayout = layout;
    }

    private void initVideoView(Context ctx) {
        mContext = ctx;
        mVideoWidth = 0;
        mVideoHeight = 0;
        mVideoSarNum = 0;
        mVideoSarDen = 0;
        getHolder().addCallback(mSHCallback);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        if (ctx instanceof Activity)
            ((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public boolean isValid() {
        return (mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid());
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mUri = uri;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    private boolean isLive = false;

    public void setLiveVideoPath(IMediaPlayer player, String path) {
        setLiveVideoURI(player, Uri.parse(path));
    }

    public void setLiveVideoURI(IMediaPlayer player, Uri uri) {
//        mMediaPlayer = player;
        isLive = true;
        mUri = uri;
        mSeekWhenPrepared = 0;
//        OpenVideo();
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setUserAgent(String ua) {
        mUserAgent = ua;
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }

    private void openVideo() {

        if (mUri == null || mSurfaceHolder == null)
            return;
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        release(false);
        try {
            mDuration = -1;
            mCurrentBufferPercentage = 0;
            // mMediaPlayer = new AndroidMediaPlayer();
            IjkMediaPlayer ijkMediaPlayer = null;
            if (mUri != null) {
                ijkMediaPlayer = new IjkMediaPlayer();
                ijkMediaPlayer.native_setLogLevel(BuildConfig.DEBUG ? IjkMediaPlayer.IJK_LOG_DEBUG : IjkMediaPlayer.IJK_LOG_SILENT);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
            }
            mMediaPlayer = ijkMediaPlayer;
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            Log.i("info", "VideomUri:" + mUri);
            if (mUri != null)
                mMediaPlayer.setDataSource(mUri.toString());
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
//            Session.getInstance().showLoadingDialog();
            attachMediaController();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            DebugLog.e(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer,
                    IMediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException ex) {
            DebugLog.e(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer,
                    IMediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        }
    }

//    private void OpenVideo() {
//        if (mUri == null || mSurfaceHolder == null)
//            return;
//        Intent i = new Intent("com.android.music.musicservicecommand");
//        i.putExtra("command", "pause");
//        mContext.sendBroadcast(i);
//
//        release(false);
//        try {
//            mDuration = -1;
//            mCurrentBufferPercentage = 0;
//            IjkMediaPlayer mediaPlayer = null;
//            if (mUri != null) {
//                mediaPlayer = MediaLiveHelp.getInstance();
//            }
//            mMediaPlayer = mediaPlayer;
//            mMediaPlayer.setOnPreparedListener(mPreparedListener);
//            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
//            mMediaPlayer.setOnCompletionListener(mCompletionListener);
//            mMediaPlayer.setOnErrorListener(mErrorListener);
//            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
//            mMediaPlayer.setOnInfoListener(mInfoListener);
//            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
//            Log.i("info", "VideomUri:" + mUri);
//            if (mUri != null)
//                mMediaPlayer.setDataSource(mUri.toString());
//            mMediaPlayer.setDisplay(mSurfaceHolder);
//            mMediaPlayer.setScreenOnWhilePlaying(true);
//            if (mMediaPlayer != null) {
//                mMediaPlayer.stop();
//            }
//            mMediaPlayer.prepareAsync();
//            mCurrentState = STATE_PREPARING;
//            attachMediaController();
//        } catch (Exception e) {
//            e.printStackTrace();
//            MycsLog.i("info", "Exception:" + e.getMessage());
//        }
//    }

//    private void OpenVideo() {
//        try {
//            if (mUri == null || mSurfaceHolder == null)
//                return;
//            release(false);
//            mDuration = -1;
//            mCurrentBufferPercentage = 0;
//            if (mMediaPlayer == null) {
//                mMediaPlayer = MediaLiveHelp.getInstance();
//            }
//            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
//            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
//            mMediaPlayer.setOnInfoListener(mInfoListener);
//            mMediaPlayer.setOnErrorListener(mErrorListener);
//            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
//            if (mUri != null)
//                mMediaPlayer.setDataSource(mUri.toString());
//            mMediaPlayer.setDisplay(mSurfaceHolder);
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mMediaPlayer.setScreenOnWhilePlaying(true);
//            mMediaPlayer.prepareAsync();
//            mCurrentState = STATE_PREPARING;
//            attachMediaController();
//        } catch (Exception e) {
//            e.printStackTrace();
//            mCurrentState = STATE_ERROR;
//            mTargetState = STATE_ERROR;
//            mErrorListener.onError(mMediaPlayer,
//                    IMediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
//            return;
//        }
//    }

    public void close() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void setMediaController(MediaController controller) {
//        if (mMediaController != null) {
//            mMediaController.hide();
//        }
        mMediaController = controller;
        attachMediaController();
    }

    public void setMediaBufferingIndicator(View mediaBufferingIndicator) {
        if (mMediaBufferingIndicator != null)
            mMediaBufferingIndicator.setVisibility(View.GONE);
        mMediaBufferingIndicator = mediaBufferingIndicator;
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ? (View) this
                    .getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(isInPlaybackState());
            /*if (mUri != null) {
                List<String> paths = mUri.getPathSegments();
                String name = paths == null || paths.isEmpty() ? "null" : paths
                        .get(paths.size() - 1);
                //mMediaController.setFileName(name);
            }*/
        }
    }

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height,
                                       int sarNum, int sarDen) {
            DebugLog.dfmt(TAG, "onVideoSizeChanged: (%dx%d)", width, height);
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            mVideoSarNum = sarNum;
            mVideoSarDen = sarDen;
            if (mVideoWidth != 0 && mVideoHeight != 0)
                setVideoLayout(mVideoLayout);
        }
    };


    private boolean isContinue = true;

    public void setContinueState(boolean state) {
        this.isContinue = state;
    }

    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            DebugLog.d(TAG, "onPrepared");
            mCurrentState = STATE_PREPARED;
            mTargetState = STATE_PLAYING;

            if (mOnPreparedListener != null)
                mOnPreparedListener.onPrepared(mMediaPlayer);
            if (mMediaController != null)
                mMediaController.setEnabled(true);
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            long seekToPosition = mSeekWhenPrepared;

            if (seekToPosition != 0)
                seekTo(seekToPosition);
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                setVideoLayout(mVideoLayout);
                if (mSurfaceWidth == mVideoWidth
                        && mSurfaceHeight == mVideoHeight) {
                    if (mTargetState == STATE_PLAYING) {
                        start();
                        if (mMediaController != null && isShowControl)
                            mMediaController.show();
                    } else if (!isPlaying()
                            && (seekToPosition != 0 || getCurrentPosition() > 0)) {
                        if (mMediaController != null && isShowControl)
                            mMediaController.show(0);
                    }
                }
            } else if (mTargetState == STATE_PLAYING) {
//                Log.i("info", "PreparedListener==22222222222222222222222222222");
//                Log.i("info", "isContinue:" + isContinue);
                start();
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener = new IMediaPlayer.OnCompletionListener() {
        public void onCompletion(IMediaPlayer mp) {
            DebugLog.d(TAG, "onCompletion");
//            mCurrentState = STATE_PLAYBACK_COMPLETED;
//            mTargetState = STATE_PLAYBACK_COMPLETED;
//            if (mMediaController != null){
//                mMediaController.hide();
//            }
//            int curretnPosition = (int) mMediaPlayer.getCurrentPosition();
//            int duration = (int) mMediaPlayer.getDuration();


            int curretnPosition = getCurrentPosition();
            int duration = getDuration();

            int MinusValue = duration - curretnPosition;
            if (mOnCompletionListener != null && (MinusValue < 3000)) {
//                mMediaController.hide();
                mCurrentState = STATE_PLAYBACK_COMPLETED;
                mTargetState = STATE_PLAYBACK_COMPLETED;
                mOnCompletionListener.onCompletion(mMediaPlayer);
            } else if (mOnCompletionListener != null) {
                // 剩余时间大于3秒
                mCurrentState = STATE_PAUSED;
                mTargetState = STATE_PAUSED;

                mIsVideoInterrupt = true;
                App.getInstance().setCurrentProgress(curretnPosition);
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };

    public int getCurrentState() {
        return mCurrentState;
    }

    private IMediaPlayer.OnErrorListener mErrorListener = new IMediaPlayer.OnErrorListener() {
        public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
            DebugLog.dfmt(TAG, "Error: %d, %d", framework_err, impl_err);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
//            if (mMediaController != null)
//                mMediaController.hide();

            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err,
                        impl_err))
                    return true;
            }

            if (getWindowToken() != null) {
                int message = framework_err == IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK ? R.string.video_videoview_error_text_invalid_progressive_playback
                        : R.string.video_videoview_error_text_unknown;

                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.video_videoview_error_title)
                        .setMessage(message)
                        .setPositiveButton(
                                R.string.opera_confirm,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        if (mOnCompletionListener != null)
                                            mOnCompletionListener
                                                    .onCompletion(mMediaPlayer);
                                    }
                                }).setCancelable(false).show();
            }
            return true;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            DebugLog.d(TAG, "OnBufferingUpdateListener");
            mCurrentBufferPercentage = percent;
            if (mOnBufferingUpdateListener != null)
                mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
        }
    };

    private IMediaPlayer.OnInfoListener mInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            DebugLog.dfmt(TAG, "onInfo: (%d, %d)", what, extra);
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mp, what, extra);
            } else if (mMediaPlayer != null) {
                if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    DebugLog.dfmt(TAG, "onInfo: (MEDIA_INFO_BUFFERING_START)");
                    if (mMediaBufferingIndicator != null)
                        mMediaBufferingIndicator.setVisibility(View.VISIBLE);
                } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    DebugLog.dfmt(TAG, "onInfo: (MEDIA_INFO_BUFFERING_END)");
                    if (mMediaBufferingIndicator != null)
                        mMediaBufferingIndicator.setVisibility(View.GONE);
                }
            }
            return true;
        }
    };

    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            DebugLog.d(TAG, "onSeekComplete");
            if (mOnSeekCompleteListener != null)
                mOnSeekCompleteListener.onSeekComplete(mp);
        }
    };

    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener l) {
        mOnBufferingUpdateListener = l;
    }

    public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener l) {
        mOnSeekCompleteListener = l;
    }

    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                                   int h) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(mSurfaceHolder);
            }

            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0)
                    seekTo(mSeekWhenPrepared);
                start();
                Log.i("info", "PreparedListener==444444444444444444444444");
                if (mMediaController != null && isShowControl) {
//                    if (mMediaController.isShowing())
//                        mMediaController.hide();
                    mMediaController.show();
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND
                    && mTargetState == STATE_RESUME) {
                mMediaPlayer.setDisplay(mSurfaceHolder);
                resume();
            } else {
                openVideo();
//                if (!isLive) {
//                    openVideo();
//                } else {
//                    OpenVideo();
//                }
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = null;
            if (mMediaController != null)
//                mMediaController.hide();
                if (mCurrentState != STATE_SUSPEND)
                    release(true);
        }
    };

    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (cleartargetstate)
                mTargetState = STATE_IDLE;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        if (isInPlaybackState() && mMediaController != null && ev.getAction() == MotionEvent.ACTION_UP)
//            toggleMediaControlsVisibility();
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
//        if (isInPlaybackState() && mMediaController != null)
//            toggleMediaControlsVisibility();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP
                && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                && keyCode != KeyEvent.KEYCODE_MENU
                && keyCode != KeyEvent.KEYCODE_CALL
                && keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported
                && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    || keyCode == KeyEvent.KEYCODE_SPACE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
//                    mMediaController.show();
                } else {
                    start();
//                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    && mMediaPlayer.isPlaying()) {
                pause();
//                mMediaController.show();
            } else {
//                toggleMediaControlsVisibility();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void toggleMediaControlsVisibility() {
        MycsLog.i("info", "toggle11111:" + mMediaController.isShowing());
        MycsLog.i("info", "toggle22222:" + isShowControl);
        if (mMediaController.isShowing()) {
            MycsLog.i("info", "mMediaController.hide()");
//            mMediaController.hide();
        } else {
            if (isShowControl) {
                MycsLog.i("info", "mMediaController.show()");
                mMediaController.show();
            }
        }
    }

    public void showControl(boolean isShow) {
        this.isShowControl = isShow;
    }

    //    @Override
//    public void start() {
//        if (isInPlaybackState()) {
//            mMediaPlayer.start();
//            mCurrentState = STATE_PLAYING;
//        }
//        mTargetState = STATE_PLAYING;
//    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
//            mMediaPlayer.start();
//            mCurrentState = STATE_PLAYING;
            if (mIsVideoInterrupt) {
                mIsVideoInterrupt = false;
                seekTo(App.getInstance().getCurrentProgress());
                mMediaPlayer.start();
                mCurrentState = STATE_PLAYING;
            } else {
                mMediaPlayer.start();
                mCurrentState = STATE_PLAYING;
            }
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                MycsLog.w("pauseVideo3");
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void resume() {
        if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND) {
            mTargetState = STATE_RESUME;
        } else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED) {
            openVideo();
//            if (!isLive) {
//                openVideo();
//            } else {
//                OpenVideo();
//            }
        } else if (mCurrentState == 0 && mSurfaceHolder != null) {
            openVideo();
//            if (!isLive) {
//                openVideo();
//            } else {
//                OpenVideo();
//            }
        }
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            if (mDuration > 0)
                return (int) mDuration;
            mDuration = mMediaPlayer.getDuration();
            return (int) mDuration;
        }
        mDuration = -1;
        return (int) mDuration;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            long position = mMediaPlayer.getCurrentPosition();
            return (int) position;
        }
        return 0;
    }

//    public RecordProgressListener progressListener;
//
//    public void setProgressListener(RecordProgressListener progressListener) {
//        this.progressListener = progressListener;
//    }

    public interface RecordProgressListener {
        void recordProgress();
    }

    public void resetNewPlayer() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
    }


    @Override
    public void seekTo(long msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null)
            return mCurrentBufferPercentage;
        return 0;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    protected boolean isInPlaybackState() {
        return (mMediaPlayer != null && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }

    public boolean canPause() {
        return mCanPause;
    }

    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    public boolean canSeekForward() {
        return mCanSeekForward;
    }
}
