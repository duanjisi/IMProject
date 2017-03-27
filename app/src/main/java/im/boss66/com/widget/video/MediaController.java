/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2012 YIXIA.COM
 * Copyright (C) 2013 Zhang Rui <bbcallen@gmail.com>
 *
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.Locale;

import im.boss66.com.R;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.TimeUtil;

/**
 * A view containing controls for a MediaPlayer. Typically contains the buttons
 * like "Play/Pause" and a progress slider. It takes care of synchronizing the
 * controls with the state of the MediaPlayer.
 * <p/>
 * The way to use this class is to a) instantiate it programatically or b)
 * create it in your xml layout.
 * <p/>
 * a) The MediaController will create a default set of controls and put them in
 * a window floating above your application. Specifically, the controls will
 * float above the view specified with setAnchorView(). By default, the window
 * will disappear if left idle for three seconds and reappear when the user
 * touches the anchor view. To customize the MediaController's style, layout and
 * controls you should extend MediaController and override the {#link
 * {@link #makeControllerView()} method.
 * <p/>
 * b) The MediaController is a FrameLayout, you can put it in your layout xml
 * and get it through {@link #findViewById(int)}.
 * <p/>
 * NOTES: In each way, if you want customize the MediaController, the SeekBar's
 * id must be mediacontroller_progress, the Play/Pause's must be
 * mediacontroller_pause, current time's must be mediacontroller_time_current,
 * total time's must be mediacontroller_time_total, file name's must be
 * mediacontroller_file_name. And your resources must have a pause_button
 * drawable and a play_button drawable.
 * <p/>
 * Functions like show() and hide() have no effect when MediaController is
 * created in an xml layout.
 */
public class MediaController extends FrameLayout {
    private static final String TAG = MediaController.class.getSimpleName();

    public MediaPlayerControl mPlayer;
    private Context mContext;
    private PopupWindow mWindow;
    private int mAnimStyle;
    private View mAnchor;
    private View mRoot;
    private DragSeekBar mProgress;
    private TextView mEndTime, mCurrentTime;
    //    private TextView mCurrentTime;
    //private TextView mFileName;
    //private OutlineTextView mInfoView;
    private String mTitle;
    private long mDuration;
    private boolean mShowing;
    private boolean mDragging;
    private boolean mInstantSeeking = true;
    private static final int sDefaultTimeout = 5000;
    private static final int FADE_OUT = 1;

    private static final int SHOW_PROGRESS = 2;
    private boolean mFromXml = false;
    private ImageButton mPauseButton;
    private AudioManager mAM;
    //    private TextView mChaptersTv;
//    private ImageButton mPlayNextBtn;
    private VideoControllerListener mControllerListener;


    private LiveControlImpl mediaLiveControl;
    private RelativeLayout relativeLayout;
    private ImageView mExpandImg;// 最大化播放按钮
    private ImageView mShrinkImg;// 缩放播放按钮

    public void setMediaLiveControl(LiveControlImpl mediaLiveControl) {
        this.mediaLiveControl = mediaLiveControl;
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        //mRoot = this;
        mContext = context;
        mFromXml = true;
        mRoot = makeControllerView();
        initController(context);
    }

    public MediaController(Context context) {
        super(context);
        if (!mFromXml && initController(context))
            initFloatingWindow();
    }

    private boolean initController(Context context) {
        mContext = context;
        mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return true;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        if (mRoot != null)
            initControllerView(mRoot);
    }

    private void initFloatingWindow() {
        mWindow = new PopupWindow(mContext);
        mWindow.setFocusable(false);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
        mAnimStyle = android.R.style.Animation;
    }

    /**
     * Set the view that acts as the anchor for the control view. This can for
     * example be a VideoView, or your Activity's main view.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(View view) {
        mAnchor = view;
        if (!mFromXml) {
            removeAllViews();
            mRoot = makeControllerView();
            mWindow.setContentView(mRoot);
            mWindow.setWidth(LayoutParams.MATCH_PARENT);
            mWindow.setHeight(LayoutParams.WRAP_CONTENT);
        }
        initControllerView(mRoot);
    }

    /**
     * Create the view that holds the widgets that control playback. Derived
     * classes can override this to create their own.
     *
     * @return The controller view.
     */
    protected View makeControllerView() {
        return ((LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.mediacontroller, this);
    }

    public void hideRootView() {
        setVisibility(View.GONE);
    }

    public void showRootView() {
        setVisibility(View.VISIBLE);
    }

    private void initControllerView(View v) {
        mPauseButton = (ImageButton) v
                .findViewById(R.id.mediacontroller_play_pause);
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }

        relativeLayout = (RelativeLayout) findViewById(R.id.rl_expand);
        mExpandImg = (ImageView) findViewById(R.id.expand);
        mShrinkImg = (ImageView) findViewById(R.id.shrink);
        mExpandImg.setOnClickListener(mExpandListener);
        mShrinkImg.setOnClickListener(mShrinkListener);

        mProgress = (DragSeekBar) v.findViewById(R.id.mediacontroller_seekbar);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
                seeker.setThumbOffset(1);
            }
            mProgress.setMax(1000);
        }

        mEndTime = (TextView) v.findViewById(R.id.mediacontroller_time_total);
        mCurrentTime = (TextView) v
                .findViewById(R.id.mediacontroller_time_current);
        /*mFileName = (TextView) v.findViewById(R.id.mediacontroller_file_name);
        if (mFileName != null)
            mFileName.setText(mTitle);*/
//        mChaptersTv = (TextView) v.findViewById(R.id.video_controller_chapters_tv);
//        mChaptersTv.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mControllerListener != null) {
//                    mControllerListener.chapterOnClicked();
//                }
//            }
//        });

//        mPlayNextBtn = (ImageButton) v.findViewById(R.id.video_controller_next_btn);
//        mPlayNextBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean canPlayNext = (boolean) v.getTag();
//                if (canPlayNext) {
//                    if (mControllerListener != null) {
//                        mControllerListener.playNext();
//                    }
//                }
//            }
//        });
    }

    public void setVideoControllerDraggable(boolean draggable) {
        mProgress.setDragable(draggable);
    }

    public void setOnEnable(boolean enable) {
        mProgress.setFocusableInTouchMode(enable);
    }

//    public void hidePlayNextBtn() {
////        mPlayNextBtn.setVisibility(GONE);
//        mPlayNextBtn.setTag(false);
//        mPlayNextBtn.setImageResource(R.drawable.play_next_undo);
//    }
//
//    public void showPlayNextBtn() {
////        mPlayNextBtn.setVisibility(VISIBLE);
//        mPlayNextBtn.setTag(true);
//        mPlayNextBtn.setImageResource(R.drawable.play_next_cando);
//    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    public void setPageType(PageType pageType) {
        mExpandImg.setVisibility(pageType.equals(PageType.EXPAND) ? GONE
                : VISIBLE);
        mShrinkImg.setVisibility(pageType.equals(PageType.SHRINK) ? GONE
                : VISIBLE);
    }

    public void setExpand(boolean flag) {
        relativeLayout.setVisibility((!flag) ? GONE
                : VISIBLE);
    }

    /**
     * 播放样式 展开、缩放
     */
    public enum PageType {
        EXPAND, SHRINK
    }

    public void setVideoControllerListener(VideoControllerListener listener) {
        mControllerListener = listener;
    }

    /**
     * Control the action when the seekbar dragged by user
     *
     * @param seekWhenDragging True the media will seek periodically
     */
    public void setInstantSeeking(boolean seekWhenDragging) {
        mInstantSeeking = seekWhenDragging;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    private void disableUnsupportedButtons() {
        try {
            if (mPauseButton != null && !mPlayer.canPause())
                mPauseButton.setEnabled(false);
        } catch (IncompatibleClassChangeError ex) {
        }
    }

    /**
     * <p>
     * Change the animation style resource for this controller.
     * </p>
     * <p/>
     * <p>
     * If the controller is showing, calling this method will take effect only
     * the next time the controller is shown.
     * </p>
     *
     * @param animationStyle animation style to use when the controller appears and
     *                       disappears. Set to -1 for the default animation, 0 for no
     *                       animation, or a resource identifier for an explicit animation.
     */
    public void setAnimationStyle(int animationStyle) {
        mAnimStyle = animationStyle;
    }

    /**
     * Show the controller on screen. It will go away automatically after
     * 'timeout' milliseconds of inactivity.
     *
     * @param timeout in milliseconds. Use 0 to show the controller
     *                until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            disableUnsupportedButtons();

            if (mFromXml) {
                setVisibility(View.VISIBLE);
            } else {
                int[] location = new int[2];

                mAnchor.getLocationOnScreen(location);
                Rect anchorRect = new Rect(location[0], location[1],
                        location[0] + mAnchor.getWidth(), location[1]
                        + mAnchor.getHeight());

                mWindow.setAnimationStyle(mAnimStyle);
                mWindow.showAtLocation(mAnchor, Gravity.BOTTOM,
                        anchorRect.left, 0);
            }
//            mShowing = true;
//            if (mShownListener != null)
//                mShownListener.onShown();
        }

        mShowing = true;
        if (mShownListener != null)
            mShownListener.onShown();
//        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

//        if (timeout != 0) {
//            mHandler.removeMessages(FADE_OUT);
//            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
//                    timeout);
//        }
    }

//    public void setChapterTvVisible(boolean isVisible) {
//        mChaptersTv.setVisibility(isVisible ? View.VISIBLE : View.GONE);
//    }

    public boolean isShowing() {
        return mShowing;
    }

//    public void hide() {
//        if (mAnchor == null)
//            return;
//        if (mShowing) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//            }
//            try {
//                mHandler.removeMessages(SHOW_PROGRESS);
//                if (mFromXml)
//                    setVisibility(View.GONE);
//                else
//                    mWindow.dismiss();
//            } catch (IllegalArgumentException ex) {
//                DebugLog.d(TAG, "MediaController already removed");
//            }
//            mShowing = false;
//            if (mHiddenListener != null)
//                mHiddenListener.onHidden();
//        }
//    }

    public interface OnShownListener {
        public void onShown();
    }

    private OnShownListener mShownListener;

    public void setOnShownListener(OnShownListener l) {
        mShownListener = l;
    }

    public interface OnHiddenListener {
        public void onHidden();
    }

    private OnHiddenListener mHiddenListener;

    public void setOnHiddenListener(OnHiddenListener l) {
        mHiddenListener = l;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long pos;
            switch (msg.what) {
//                case FADE_OUT:
//                    hide();
//                    break;
                case SHOW_PROGRESS:
                    pos = setProgress();
//                    MycsLog.i("position:" + pos);
                    if (!mDragging && mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                        updatePausePlay();
                    }
                    break;
            }
        }
    };

    private long setProgress() {
        if (mPlayer == null || mDragging)
            return 0;
        int position = mPlayer.getCurrentPosition();
//        MycsLog.i("position_02:" + position);
//        int duration = mPlayer.getDuration() != 0 ? mPlayer.getDuration() : (int) mDuration;
        int duration = (int) mDuration;
//        MycsLog.i("info", "=======1111111111111111111111");
        if (mProgress != null) {
//            MycsLog.i("info", "=======22222222222222222222222");
            if (duration > 0) {
//                MycsLog.i("info", "=======33333333333333333333333333");
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        if (duration != 0) {
            mDuration = duration;
        }

//        if (mEndTime != null) {
//            String time = generateTime(mDuration);
//            mEndTime.setText(generateTime(mDuration));
//        }
        if (mCurrentTime != null)
            mCurrentTime.setText(generateTime(position));

        return position;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MycsLog.i("0000000000000000000onTouchEvent()");
//        show(sDefaultTimeout);
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        MycsLog.i("media=====onTrackballEvent()");
//        show(sDefaultTimeout);
        return false;
    }


    private boolean isShowOnTouch = true;

    /**
     * 设置是否在按住的时候显示MediaController
     *
     * @param isShowOnTouch
     */
    public void setIsShowOnTouch(boolean isShowOnTouch) {
        this.isShowOnTouch = isShowOnTouch;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (isShowOnTouch) show();
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isShowOnTouch) {
            show();
//            mChaptersTv.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mControllerListener != null) {
//                        mControllerListener.chapterOnClicked();
//                    }
//                }
//            });

//            mPlayNextBtn.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    boolean canPlayNext = (boolean) v.getTag();
//                    if (canPlayNext) {
//                        if (mControllerListener != null) {
//                            mControllerListener.playNext();
//                        }
//                    }
//                }
//            });
        } else {
//            mChaptersTv.setOnClickListener(null);
//            mPlayNextBtn.setOnClickListener(null);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() == 0
                && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
//            doPauseResume();
            MycsLog.i("media=====dispatchKeyEvent()1");
//            show(sDefaultTimeout);
            if (mPauseButton != null)
                mPauseButton.requestFocus();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                if (mControllerListener != null) {
                    mControllerListener.pause();
                }
//                updatePausePlay();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_MENU) {
//            hide();
            return true;
        } else {
            MycsLog.i("media=====dispatchKeyEvent()2");
//            show(sDefaultTimeout);
        }
        return super.dispatchKeyEvent(event);
    }

    private OnClickListener mExpandListener = new OnClickListener() {
        public void onClick(View v) {
            if (mediaLiveControl != null) {
                mediaLiveControl.onExpand();
            }
        }
    };

    private OnClickListener mShrinkListener = new OnClickListener() {
        public void onClick(View v) {
            if (mediaLiveControl != null) {
                mediaLiveControl.onShrink();
            }
        }
    };
    private OnClickListener mPauseListener = new OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    public interface LiveControlImpl {
        void onExpand();

        void onShrink();
    }

    private void updatePausePlay() {
        if (mRoot == null || mPauseButton == null)
            return;
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPauseButton
                        .setImageResource(R.drawable.video_controller_play);
            } else {
                mPauseButton
                        .setImageResource(R.drawable.video_controller_pause);
            }
        }
    }

    public void setDuration(int duration) {
        mDuration = duration;
        if (mEndTime != null) {
            mEndTime.setText(TimeUtil.stringForTime(duration));
        }
    }

    public void setProgress(int distanceX) {
        int pro = mProgress.getProgress() + distanceX;
        mProgress.setProgress(pro);

        long newposition = (mDuration * mProgress.getProgress()) / 1000;

        String time = generateTime(newposition);
        if (mCurrentTime != null)
            mCurrentTime.setText(time);
    }

    public void setPlayerProgress(int progress) {
        mPlayer.seekTo(progress);
        mHandler.removeMessages(SHOW_PROGRESS);
        mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
        mDragging = false;
        mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
    }


    public void setDuration(String duration) {
        mDuration = TimeUtil.timeForDuration(duration);
        if (mEndTime != null) {
            MycsLog.i("duration:" + duration);
            mEndTime.setText(duration);
            MycsLog.i("duration2:" + mEndTime.getText().toString());
        }
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            if (mControllerListener != null) {
                mControllerListener.pause();
            }
        } else {
            mPlayer.start();
            if (mControllerListener != null) {
                mControllerListener.play();
            }
        }
        updatePausePlay();
    }

    public void doStartPauseVideo(boolean isStop) {
        if (isStop) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            }
        } else {
            mPlayer.start();
        }
    }

    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
//            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
            if (mInstantSeeking)
                mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
            /*if (mInfoView != null) {
                mInfoView.setText("");
                mInfoView.setVisibility(View.VISIBLE);
            }*/
        }

        public void onProgressChanged(SeekBar bar, int progress,
                                      boolean fromuser) {
            if (!fromuser)
                return;

            long newposition = (mDuration * progress) / 1000;

            String time = generateTime(newposition);
            /* 修改为拖动结束才改变进度 linzq 2016-05-27 10:40:37
            if (mInstantSeeking)
                mPlayer.seekTo(newposition);
            */
            /*if (mInfoView != null)
                mInfoView.setText(time);*/
            if (mCurrentTime != null)
                mCurrentTime.setText(time);

            mControllerListener.onProgressChange(progress);
        }

        public void onStopTrackingTouch(SeekBar bar) {
            /* 修改为拖动结束才改变进度 linzq 2016-05-27 10:40:37
            if (!mInstantSeeking)
             */
            mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
            /*if (mInfoView != null) {
                mInfoView.setText("");
                mInfoView.setVisibility(View.GONE);
            }*/
//            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (mPauseButton != null)
            mPauseButton.setEnabled(enabled);
        if (mProgress != null)
            mProgress.setEnabled(enabled);
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    public interface playListener {
        public void playState(int state);
    }

    public interface MediaPlayerControl {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(long pos);

        boolean isPlaying();

        int getBufferPercentage();

        boolean canPause();

        boolean canSeekBackward();

        boolean canSeekForward();
    }

    public interface VideoControllerListener {
        public void play();

        public void pause();

        public void playNext();

        public void chapterOnClicked();

        void onProgressChange(int progress);
    }

}
