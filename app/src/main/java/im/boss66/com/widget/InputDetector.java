package im.boss66.com.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by dss886 on 15/9/26.
 */
public class InputDetector {

    private static final String SHARE_PREFERENCE_NAME = "com.dss886.emotioninputdetector";
    private static final String SHARE_PREFERENCE_TAG = "soft_input_height";

    private Activity mActivity;
    private InputMethodManager mInputManager;
    private SharedPreferences sp;
    private View mEmotionLayout;
    private View mMoreLayout;
    private EditText mEditText;
    private View mContentView;

    private InputDetector() {
    }

    public static InputDetector with(Activity activity) {
        InputDetector emotionInputDetector = new InputDetector();
        emotionInputDetector.mActivity = activity;
        emotionInputDetector.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        emotionInputDetector.sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return emotionInputDetector;
    }

    public InputDetector bindToContent(View contentView) {
        mContentView = contentView;
        return this;
    }

    public InputDetector bindToEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && (mEmotionLayout.isShown() || mMoreLayout.isShown())) {
                    lockContentHeight();
                    hideEmotionLayout(true);
                    hideMoreLayout(true);
                    if (!isSoftInputShown()) {
                        showSoftInput();
                    }
                    mEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unlockContentHeightDelayed();
                        }
                    }, 200L);
                }
                return false;
            }
        });
        return this;
    }

    public InputDetector bindToET(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    showSoftInput();
                    if (!isSoftInputShown()) {
                        showSoftInput();
                    }
                    mEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getSupportSoftInputHeight();
                        }
                    }, 200L);

//                    mEditText.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            unlockContentHeightDelayed();
//                        }
//                    }, 200L);
                }
                return false;
            }
        });
        return this;
    }

    public InputDetector bindToEmotionButton(View emotionButton) {
        emotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mEmotionLayout.isShown()) {
//                    lockContentHeight();
//                    hideEmotionLayout(true);
//                    unlockContentHeightDelayed();
//                } else {
//                    if (isSoftInputShown()) {
//                        lockContentHeight();
//                        showEmotionLayout();
//                        unlockContentHeightDelayed();
//                    } else {
//                        showEmotionLayout();
//                    }
//                }
                if (!mEmotionLayout.isShown()) {
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        hideMoreLayout(true);
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        hideMoreLayout(true);
                        showEmotionLayout();
                    }
                }
            }
        });
        return this;
    }

    public InputDetector bindMoreButton(View moreButton) {
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mEmotionLayout.isShown()) {
//                    lockContentHeight();
//                    hideEmotionLayout(true);
//                    unlockContentHeightDelayed();
//                } else {
//                    if (isSoftInputShown()) {
//                        lockContentHeight();
//                        showEmotionLayout();
//                        unlockContentHeightDelayed();
//                    } else {
//                        showEmotionLayout();
//                    }
//                }
                if (!mMoreLayout.isShown()) {
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        hideEmotionLayout(true);
                        showMoreLayout();
                        unlockContentHeightDelayed();
                    } else {
                        hideEmotionLayout(true);
                        showMoreLayout();
                    }
                }
            }
        });
        return this;
    }

    public InputDetector setEmotionView(View emotionView) {
        mEmotionLayout = emotionView;
        return this;
    }

    public InputDetector setMoreView(View emotionView) {
        mMoreLayout = emotionView;
        return this;
    }

    public InputDetector build() {
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hideSoftInput();
        return this;
    }

    public boolean interceptBackPress() {
        // TODO: 15/11/2 change this method's name
        if (mEmotionLayout.isShown() || mMoreLayout.isShown()) {
            hideEmotionLayout(false);
            hideMoreLayout(false);
            return true;
        }
        return false;
    }

    private void showEmotionLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
//            int height = UIUtils.getScreenHeight(mActivity) / 2 - 50;
//            Log.i("info", "=======================height:" + height);
            softInputHeight = sp.getInt(SHARE_PREFERENCE_TAG, 400);
        }
        hideSoftInput();
        mEmotionLayout.getLayoutParams().height = softInputHeight;
        mEmotionLayout.setVisibility(View.VISIBLE);
    }

    private void showMoreLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        Log.i("info", "==============softInputHeight1:" + softInputHeight);
        if (softInputHeight == 0) {
//            int height = UIUtils.getScreenHeight(mActivity) / 2 - 50;
            softInputHeight = sp.getInt(SHARE_PREFERENCE_TAG, 400);
        }
        Log.i("info", "==============softInputHeight2:" + softInputHeight);
        hideSoftInput();
        mMoreLayout.getLayoutParams().height = softInputHeight;
        mMoreLayout.setVisibility(View.VISIBLE);
    }

    private void hideEmotionLayout(boolean showSoftInput) {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
//            if (showSoftInput) {
//                showSoftInput();
//            }
        }
    }

    private void hideMoreLayout(boolean showSoftInput) {
        if (mMoreLayout.isShown()) {
            mMoreLayout.setVisibility(View.GONE);
//            if (showSoftInput) {
//                showSoftInput();
//            }
        }
    }

    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

    private void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }

    private void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }
        if (softInputHeight < 0) {
            Log.w("EmotionInputDetector", "Warning: value of softInputHeight is below zero!");
        }
        if (softInputHeight > 0) {
            sp.edit().putInt(SHARE_PREFERENCE_TAG, softInputHeight).apply();
        }
        Log.i("info", "=====softInputHeight:" + softInputHeight);
        return softInputHeight;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

}
