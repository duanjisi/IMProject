package im.boss66.com.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import im.boss66.com.R;
/**
 * 顶部导航栏
 *
 * @author zyu
 */
public class TopNavigationBar extends FrameLayout implements OnClickListener {

    private static final int DEFAULT_RIGHT_BTN_STATUS = 0;
    private final ImageButton mLeftImBtn;
    private OnRightBtnClickedListener mRightBtnClickedListener;
    private OnLeftBtnClickedListener mLeftBtnClickedListener;
    private TitleOnClickedListener mTitleOnClickedListener;
    private OnRightSecondBtnClickedListener mRightSecondClikcedListener;

    private SearchBarClickedListener mSearchBarClickedListener;

    private SearchBtnClickedListener mSearchBtnClickedListener;

    private OnCancelBtnClickedListener cancelBtnClickedListener;


    private TextView mTitleTv;
    private ImageView mArrowIm;
    private ImageView mUnreadIm;
    private ImageButton mRightImBtn;
    private EditText input_edit;

    private RelativeLayout mSearchLayout;

//  private ImageButton search_btn;

    public TopNavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopNavigationBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TopNavigationBar);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.top_navigation_bar, this, true);

        CharSequence title = a.getText(R.styleable.TopNavigationBar_topbarTitle);

        mSearchLayout = (RelativeLayout) findViewById(R.id.search_layout);
        mSearchLayout.setOnClickListener(this);
//        search_btn = (ImageButton) findViewById(R.id.search_btn);
//        search_btn.setOnClickListener(this);

        input_edit = (EditText) findViewById(R.id.input_edt);

        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mTitleTv.setText(title);
        mTitleTv.setOnClickListener(this);

        Drawable topBarIcon = a.getDrawable(R.styleable.TopNavigationBar_topbarIcon);
        if (topBarIcon != null) {
            mTitleTv.setCompoundDrawablesWithIntrinsicBounds(null, null, topBarIcon, null);
        }

        boolean showSearchBar = a.getBoolean(R.styleable.TopNavigationBar_showSearchBar, false);


        mSearchLayout.setOnClickListener(this);
        if (showSearchBar) {
            mTitleTv.setVisibility(View.GONE);
            mSearchLayout.setVisibility(View.VISIBLE);
        } else {
            mSearchLayout.setVisibility(View.GONE);
        }

        boolean isEdit = a.getBoolean(R.styleable.TopNavigationBar_isSearchBarEdit, false);

        if (isEdit) {
            input_edit.setFocusable(true);
        } else {
            input_edit.setFocusable(false);
            input_edit.setOnClickListener(this);
        }

        boolean hasSearchBtn = a.getBoolean(R.styleable.TopNavigationBar_keyboardHasSearchBtn, false);

        if (hasSearchBtn) {
            input_edit.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            input_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                        if (mSearchBtnClickedListener != null) {
                            String keyWords = input_edit.getText().toString().trim();
                            mSearchBtnClickedListener.onSearch(keyWords);
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        /**
         * 是否响应监听EditText文字变化
         */
        boolean isResponeEditor = a.getBoolean(R.styleable.TopNavigationBar_isResponseEditor, false);

        if (isResponeEditor) {

            input_edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int count = editable.length();
                    if (count != 0) {
                        setRightTv("搜索");
                    } else {
                        setRightTv("取消");
                    }
                }
            });
        }

        boolean showArrowIm = a.getBoolean(R.styleable.TopNavigationBar_showArrowIm, false);

        if (showArrowIm) {
            mArrowIm = (ImageView) findViewById(R.id.arrow_im);
            mArrowIm.setVisibility(View.VISIBLE);
        }

        boolean isHidedLeftImBtn = a.getBoolean(R.styleable.TopNavigationBar_hideLeftImBtn, false);
        mLeftImBtn = (ImageButton) findViewById(R.id.left_btn);
        if (isHidedLeftImBtn) {
            mLeftImBtn.setVisibility(View.GONE);
        }
        Drawable leftDrawable = a.getDrawable(R.styleable.TopNavigationBar_leftImBtnDrawable);
        mLeftImBtn.setImageDrawable(leftDrawable);
        mLeftImBtn.setOnClickListener(this);

        mUnreadIm = (ImageView) findViewById(R.id.unread_message_im);

        int rightBtnStatus = a.getInt(R.styleable.TopNavigationBar_rightBtnStatus, DEFAULT_RIGHT_BTN_STATUS);
        if (rightBtnStatus == 1) {
            CharSequence rightText = a.getText(R.styleable.TopNavigationBar_rightText);
            setRightTv(rightText);
        } else if (rightBtnStatus == 2) {
            Drawable rightDrawable = a.getDrawable(R.styleable.TopNavigationBar_rightImBtnDrawable);
            setRightImBtn(rightDrawable);
        } else if (rightBtnStatus == 3) {
            Drawable rightDrawable = a.getDrawable(R.styleable.TopNavigationBar_rightImBtnDrawable);
            setRightImBtn(rightDrawable);
            Drawable rightSecondDrawable = a.getDrawable(R.styleable.TopNavigationBar_rightImBtnSecondDrawable);
            setRightSecondImBtn(rightSecondDrawable);
        }

        int leftBtnStatus = a.getInt(R.styleable.TopNavigationBar_leftBtnStatus, DEFAULT_RIGHT_BTN_STATUS);
        if (leftBtnStatus == 1) {
            CharSequence leftText = a.getText(R.styleable.TopNavigationBar_leftText);
            setleftTv(leftText);
        }

        a.recycle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_tv:
                if (mTitleOnClickedListener != null) {
                    mTitleOnClickedListener.onTitleClicked();
                }
                break;
            case R.id.right_tv:
                TextView right = (TextView) findViewById(R.id.right_tv);
                String RightText = right.getText().toString().trim();
                if (RightText.equals("取消")) {
//                    Toast.makeText(getContext(), "取消事件", Toast.LENGTH_SHORT).show();
                    cancelBtnClickedListener.onCancelClicked();
                } else if (RightText.equals("搜索")) {
//                    Toast.makeText(getContext(), "搜索事件", Toast.LENGTH_SHORT).show();
                    if (mSearchBtnClickedListener != null) {
                        String keyWords = input_edit.getText().toString().trim();
                        mSearchBtnClickedListener.onSearch(keyWords);
                    }
                } else {
                    if (mRightBtnClickedListener != null) {
                        mRightBtnClickedListener.onRightBtnClicked();
                    }
                }
                break;
            case R.id.left_tv:
                if (mLeftBtnClickedListener != null) {
                    mLeftBtnClickedListener.onLeftBtnClicked();
                }
                break;
            case R.id.right_btn:
                if (mRightBtnClickedListener != null) {
                    mRightBtnClickedListener.onRightBtnClicked();
                }
                break;
            case R.id.left_btn:
                if (mLeftBtnClickedListener != null) {
                    mLeftBtnClickedListener.onLeftBtnClicked();
                }
                break;
            case R.id.right_second_btn:
                if (mRightSecondClikcedListener != null) {
                    mRightSecondClikcedListener.onRightSecondBtnClicked();
                }
                break;
//            case R.id.search_layout:
//                if (mSearchBarClickedListener != null) {
//                    mSearchBarClickedListener.onSearchBarClicked();
//                }
//            break;
            case R.id.input_edt:
                if (mSearchBarClickedListener != null) {
                    mSearchBarClickedListener.onSearchBarClicked();
                }
                break;
//            case R.id.search_btn:
//                if (mSearchBtnClickedListener != null) {
//                    String keyWords = input_edit.getText().toString().trim();
//                    mSearchBtnClickedListener.onSearch(keyWords);
//                }
//                break;
            default:
                break;
        }

    }

    public void setNotifyText(String text) {
        input_edit.setHint(text);

    }

    public void setEditText(String msg) {
        input_edit.setText(msg);
        input_edit.setSelection(msg.length());
    }

    public String getEditText() {
        return input_edit.getText().toString().trim();
    }


    public void setRightCharSequence(CharSequence text) {
        TextView rightTv = (TextView) findViewById(R.id.right_tv);
        rightTv.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(text)) {
            rightTv.setText(text);
        }
        rightTv.setOnClickListener(this);

    }

    public void setRightTv(CharSequence text) {
        TextView rightTv = (TextView) findViewById(R.id.right_tv);
        rightTv.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(text)) {
            rightTv.setText(text);
        }
        rightTv.setOnClickListener(this);
    }

    public void setRightTvGone() {
        TextView rightTv = (TextView) findViewById(R.id.right_tv);
        rightTv.setVisibility(View.GONE);
    }

    public void setRightIvGone() {
        ImageButton rightTv = (ImageButton) findViewById(R.id.right_btn);
        rightTv.setVisibility(View.GONE);
    }

    public void setRightIvSrc(int resId) {
        mRightImBtn = (ImageButton) findViewById(R.id.right_btn);
        mRightImBtn.setImageResource(resId);
        mRightImBtn.setVisibility(View.VISIBLE);
        mRightImBtn.setOnClickListener(this);
    }

    public void setleftTv(CharSequence text) {
        TextView leftTv = (TextView) findViewById(R.id.left_tv);
        leftTv.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(text)) {
            leftTv.setText(text);
        }
        leftTv.setOnClickListener(this);
    }

    public String getRightTv() {
        TextView rightTv = (TextView) findViewById(R.id.right_tv);
        String mStr = rightTv.getText().toString().trim();
        if (mStr != null && !mStr.equals("")) {
            return mStr;
        } else {
            return null;
        }
    }


    private void setRightImBtn(Drawable drawable) {
        mRightImBtn = (ImageButton) findViewById(R.id.right_btn);
        mRightImBtn.setImageDrawable(drawable);
        mRightImBtn.setVisibility(View.VISIBLE);
        mRightImBtn.setOnClickListener(this);
    }

    private void setRightSecondImBtn(Drawable drawable) {
        ImageButton rightImBtn = (ImageButton) findViewById(R.id.right_second_btn);
        rightImBtn.setImageDrawable(drawable);
        rightImBtn.setVisibility(View.VISIBLE);
        rightImBtn.setOnClickListener(this);
    }

    public void hideLeftBtn() {
        mLeftImBtn.setVisibility(GONE);
    }

    public void showLeftBtn() {
        mLeftImBtn.setVisibility(VISIBLE);
    }

    private RotateAnimation getRotateAnimation(float fromDegress, float toDegress) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegress, toDegress, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        setRotateAnimation(rotateAnimation);
        return rotateAnimation;
    }

    private void setRotateAnimation(RotateAnimation animation) {
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);
    }

    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            mTitleTv.setText(title);
        }
    }

    public void hideRightImBtn() {
        if (mRightImBtn != null) {
            mRightImBtn.setVisibility(View.GONE);
        }
    }

    public void showRightImBtn() {
        if (mRightImBtn != null) {
            mRightImBtn.setVisibility(View.VISIBLE);
        }
    }

    public RelativeLayout setLeftBtnOnClickedListener(OnLeftBtnClickedListener listener) {
        mLeftBtnClickedListener = listener;
        return null;
    }

    public void setRightBtnOnClickedListener(OnRightBtnClickedListener listener) {
        mRightBtnClickedListener = listener;
    }

    public void setRightSecondBtnOnClickedListener(OnRightSecondBtnClickedListener listener) {
        mRightSecondClikcedListener = listener;
    }

    public void setTitleOnClickedListener(TitleOnClickedListener listener) {
        mTitleOnClickedListener = listener;
    }

    public void setSearchBarOnClickedListener(SearchBarClickedListener listener) {
        mSearchBarClickedListener = listener;
    }

    public void setSearchBtnOnClickedListener(SearchBtnClickedListener listener) {
        mSearchBtnClickedListener = listener;
    }

    public void setCancelBtnClickedListener(OnCancelBtnClickedListener listener) {
        cancelBtnClickedListener = listener;
    }

    public interface OnCancelBtnClickedListener {
        public void onCancelClicked();
    }

    public interface OnRightBtnClickedListener {
        public void onRightBtnClicked();
    }

    public interface OnRightSecondBtnClickedListener {
        public void onRightSecondBtnClicked();
    }

    public interface OnLeftBtnClickedListener {
        public void onLeftBtnClicked();
    }

    public interface TitleOnClickedListener {
        public void onTitleClicked();
    }

    public interface SearchBarClickedListener {
        public void onSearchBarClicked();
    }

    public interface SearchBtnClickedListener {
        public void onSearch(String keyWords);
    }

    public void showUnreadIm(boolean show) {
        mUnreadIm.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void startRotateAmination() {
        if (mArrowIm != null) {
            mArrowIm.clearAnimation();
            mArrowIm.startAnimation(getRotateAnimation(0, -180));
        }
    }

    public void startReverseAmination() {
        if (mArrowIm != null) {
            mArrowIm.clearAnimation();
            mArrowIm.startAnimation(getRotateAnimation(-180, 0));
        }
    }
}
