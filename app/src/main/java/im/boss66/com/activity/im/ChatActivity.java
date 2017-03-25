package im.boss66.com.activity.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import im.boss66.com.App;
import im.boss66.com.Code;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.Utils.Base64Utils;
import im.boss66.com.Utils.FileUtil;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.Utils.SharePreferenceUtil;
import im.boss66.com.Utils.SoundUtil;
import im.boss66.com.Utils.TimeUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.ImageGridActivity;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.CellAdapter;
import im.boss66.com.adapter.MessageAdapter;
import im.boss66.com.db.MessageDB;
import im.boss66.com.db.RecentDB;
import im.boss66.com.db.dao.ConversationHelper;
import im.boss66.com.db.dao.EmoCateHelper;
import im.boss66.com.db.dao.EmoGroupHelper;
import im.boss66.com.db.dao.EmoHelper;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseConversation;
import im.boss66.com.entity.CellEntity;
import im.boss66.com.entity.EmoCate;
import im.boss66.com.entity.EmoEntity;
import im.boss66.com.entity.EmoGroup;
import im.boss66.com.entity.MessageItem;
import im.boss66.com.entity.RecentItem;
import im.boss66.com.fragment.FaceFragment;
import im.boss66.com.fragment.FaceLoveFragment;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.services.ChatServices;
import im.boss66.com.widget.InputDetector;
import im.boss66.com.xlistview.MsgListView;

//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.view.ViewPager;

/**
 * Created by Johnny on 2017/1/10.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener, ChatServices.receiveMessageCallback,
        View.OnTouchListener, MsgListView.IXListViewListener, FaceFragment.clickCallback, FaceLoveFragment.LoveCallback {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private PermissionListener permissionListener;
    private LocalBroadcastReceiver mLocalBroadcastReceiver;
    private final int OPEN_CAMERA = 1;//相机
    private final int OPEN_ALBUM = 2;//相册
    private final int RECORD_VIDEO = 3;//视频
    private final int RECORD_AUDIO = 4;//声音
    private Map<String, EmoEntity> mEmoMap = new LinkedHashMap<String, EmoEntity>();
    private PopupWindow popupWindow;
    private static final int MESSAGE_TYPE_EMOTION = 0x011;
    private static final int MESSAGE_TYPE_IMG = 0x012;
    private static final int MESSAGE_TYPE_VIDEO = 0x013;
    private static final int MESSAGE_TYPE_TXT = 0x015;
    private static final int MESSAGE_TYPE_AUDIO = 0x016;

    private static final int EMOTION_SETING_UP = 0x014;
    //    private static final int EMOTION_STORE = 0x015;
    public final static String CHATPHOTO_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator;
    private String[] codes = {"[拥抱]", "[鄙视]", "[困]"};
    public static final int NEW_MESSAGE = 0x001;// 收到消息
    public static int MSGPAGERNUM;
    private LinearLayout ll_emotion, ll_other;
    private View viewFace;
    private ImageView iv00, iv01, iv02;
    public static String defaulgUserName = "在飞";
    public static String defaulgIcon = "4";
    public static int defaultCount = 0;
    private SharePreferenceUtil mSpUtil;
    private WindowManager.LayoutParams mParams;
    //    private LinearLayout mllFace;// 表情显示的布局
    private boolean isFaceShow = false;
    private boolean isOther = false;
    private InputMethodManager mInputMethodManager;
    //    private EditText mEtMsg;
    private App mApplication;
    //    private Button mBtnAffix;
    private Button mBtnSend;// 发送消息按钮
    private ImageButton mIbMsgBtn, ibFace, mBtnAffix;
    private TextView mTvVoiceBtn;// 语音按钮
    private ImageView mIvDelete;// 语音弹出框的差号按钮
    private LinearLayout mLLDelete;
    private ImageView mIvBigDeleteIcon;
    private View mChatPopWindow;
    private LinearLayout mLlVoiceLoading;// 加载录制loading
    private LinearLayout mLlVoiceRcding;
    private LinearLayout mLlVoiceShort;// 录制时间过短

    private static final int POLL_INTERVAL = 300;
    private static final long DELAY_VOICE = 1000;// 语音录制计时
    private static final int CAMERA_WITH_DATA = 10;

    private Handler mHandler = new Handler();
    private int flag = 1;
    private boolean isShosrt = false;

    private long mStartRecorderTime;
    private long mEndRecorderTime;

    private ImageView volume;
    private String mRecordTime;
    private TextView mTvVoiceRecorderTime;// 录制的时间
    private int mRcdStartTime = 0;// 录制的开始时间
    private int mRcdVoiceDelayTime = 1000;
    private int mRcdVoiceStartDelayTime = 300;
    private boolean isCancelVoice;// 不显示语音

    private VoiceRcdTimeTask mVoiceRcdTimeTask;
    private ScheduledExecutorService mExecutor;// 录制计时器

    //    private TextView tvImg, tvVideo, tvPhoto, tvImgs, tvCollect, tvRed, tvCard;
    private ArrayList<CellEntity> cells;
    private GridView gridMore;
    private CellAdapter cellAdapter;

    private static MessageAdapter adapter;// 发送消息展示的adapter
    private MsgListView mMsgListView;// 展示消息的
    private MessageDB mMsgDB;// 保存消息的数据库
    private String mMsgId;
    private RecentDB mRecentDB;
    private SoundUtil mSoundUtil;
    private String userid, toUid, title, toAvatar;//toUid;单聊（个人用户id）,群聊(群id)
    private AccountEntity account;
    private boolean isGroupChat = false;
    private TextView tvBack, tvTitle;
    private Resources resources;
    private ArrayList<TextView> textViews;
    private ArrayList<ImageView> imageViews;
    private LinearLayout linearLayout, llBottom;
    private ViewPager viewPager;
    private ArrayList<EmoCate> categorys = new ArrayList<>();
    private EditText mEtMsg;
    private ImageView ivSelect, ivMake, ivAddPerson, ivEditPic;
    private ImageLoader imageLoader;
    private boolean isSpeech = false;
    private LinearLayout ll_input;
    private float mImageHeight;
    private ImageView ivTips;
    private InputDetector mDetector;
    /**
     * 接收到数据，用来更新listView
     */
    private Handler handler = new Handler() {
        // 接收到消息
        public void handleMessage(android.os.Message msg) {
            if (msg.what == NEW_MESSAGE) {
                MessageItem item = (MessageItem) msg.obj;
//                Message msgItem = (Message) msg.obj;
//                String userId = msgItem.getUser_id();
////                if (!userId.equals(mSpUtil.getUserId()))// 如果不是当前正在聊天对象的消息，不处理
////                    return;
//                int headId = msgItem.getHead_id();
//                // ===接收的额数据，如果是record语音的话，用播放展示
//                MessageItem item = null;
//                RecentItem recentItem = null;
//                if (msgItem.getMessagetype() == MessageItem.MESSAGE_TYPE_EMOTION) {
//                    item = new MessageItem(MessageItem.MESSAGE_TYPE_EMOTION,
//                            msgItem.getNick(), 0,
//                            msgItem.getMessage(), headId, true, 0,
//                            msgItem.getVoiceTime(), msgItem.getTime_samp(),
//                            msgItem.getNick(), msgItem.getUser_id(),
//                            msgItem.getAvartar());
//                    recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_EMOTION,
//                            userId, headId, msgItem.getNick(),
//                            msgItem.getMessage(), 0, 0,
//                            msgItem.getVoiceTime(), msgItem.getTime_samp());
//                } else if (msgItem.getMessagetype() == MessageItem.MESSAGE_TYPE_VIDEO) {
//                    item = new MessageItem(MessageItem.MESSAGE_TYPE_VIDEO,
//                            msgItem.getNick(), 0,
//                            msgItem.getMessage(), headId, true, 0,
//                            msgItem.getVoiceTime(), msgItem.getTime_samp(),
//                            msgItem.getNick(), msgItem.getUser_id(),
//                            msgItem.getAvartar());
//                    recentItem = new RecentItem(
//                            MessageItem.MESSAGE_TYPE_VIDEO, userId, headId,
//                            msgItem.getNick(), msgItem.getMessage(), 0, 0,
//                            msgItem.getVoiceTime(), msgItem.getTime_samp());
//                } else if (msgItem.getMessagetype() == MessageItem.MESSAGE_TYPE_IMG) {
//                    item = new MessageItem(MessageItem.MESSAGE_TYPE_IMG,
//                            msgItem.getNick(), 0,
//                            msgItem.getMessage(), headId, true, 0,
//                            msgItem.getVoiceTime(), msgItem.getTime_samp(),
//                            msgItem.getNick(), msgItem.getUser_id(),
//                            msgItem.getAvartar());
//                    recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_IMG,
//                            userId, headId, msgItem.getNick(),
//                            msgItem.getMessage(), 0, 0,
//                            msgItem.getVoiceTime(), msgItem.getTime_samp());
//                }
                adapter.upDateMsg(item);// 更新界面
//                mMsgDB.saveMsg(userid, item);// 保存数据库
//                mRecentDB.saveRecent(recentItem);
                scrollToBottomListItem();
            }
        }
    };

    /**
     * @Description 滑动到列表底部
     */
    private void scrollToBottomListItem() {
        // todo eric, why use the last one index + 2 can real scroll to the
        // bottom?
        if (mMsgListView != null) {
            mMsgListView.setSelection(adapter.getCount() - 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(ChatActivity.this);//注册，在setContentView之前。
//        Session.getInstance().addObserver(this);
        setContentView(R.layout.activity_chat);
        initViews();
    }

    private void initViews() {
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        account = App.getInstance().getAccount();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userid = account.getUser_id();
            toUid = bundle.getString("toUid", "");
            title = bundle.getString("title", "");
            toAvatar = bundle.getString("toAvatar", "");
            isGroupChat = bundle.getBoolean("isgroup", false);
            mMsgId = userid + "_" + toUid;
        }
        MSGPAGERNUM = 0;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 3;
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mSpUtil = App.getInstance().getSpUtil();
        resources = getResources();
        mSoundUtil = SoundUtil.getInstance();
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvTitle = (TextView) findViewById(R.id.tv_chat_title);

        tvBack.setOnClickListener(this);
        tvTitle.setText(title);
        ll_emotion = (LinearLayout) findViewById(R.id.ll_emotion_test);
        ll_other = (LinearLayout) findViewById(R.id.ll_chatmain_affix);
        viewFace = findViewById(R.id.view_face);
//      tvImg = (TextView) findViewById(R.id.tv_picture);
//        tvVideo = (TextView) findViewById(R.id.tv_chat_video);
//        tvPhoto = (TextView) findViewById(R.id.tv_chat_photo);
//        tvImgs = (TextView) findViewById(R.id.tv_chat_img);
//        tvCollect = (TextView) findViewById(R.id.tv_chat_collect);
//        tvRed = (TextView) findViewById(R.id.tv_chat_red);
//        tvCard = (TextView) findViewById(R.id.tv_chat_card);
        gridMore = (GridView) findViewById(R.id.gridMore);
        cellAdapter = new CellAdapter(context);
        gridMore.setAdapter(cellAdapter);
        gridMore.setOnItemClickListener(new itemCellClickListener());

        ibFace = (ImageButton) findViewById(R.id.ib_chat_face);
        mEtMsg = (EditText) findViewById(R.id.msg_et);
        viewPager = (ViewPager) findViewById(R.id.pager);
        linearLayout = (LinearLayout) findViewById(R.id.ll_main);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        ivSelect = (ImageView) findViewById(R.id.iv_add_bq);

        ivMake = (ImageView) findViewById(R.id.iv_add_pic);
        ivAddPerson = (ImageView) findViewById(R.id.iv_add_person);
        ivEditPic = (ImageView) findViewById(R.id.iv_edit_pic);
        ivTips = (ImageView) findViewById(R.id.iv_icon);
        ll_input = (LinearLayout) findViewById(R.id.ll_chatmain_input);
        ivEditPic.setTag(false);
        ivSelect.setOnClickListener(this);
        ivMake.setOnClickListener(this);
        ivAddPerson.setOnClickListener(this);
        ivEditPic.setOnClickListener(this);
        if (isGroupChat) {
            ivAddPerson.setImageResource(R.drawable.hp_white_persons);
        } else {
            ivAddPerson.setImageResource(R.drawable.hp_white_person);
        }
//        tvPhoto.setOnClickListener(this);
//        tvImgs.setOnClickListener(this);
////        tvImg.setOnClickListener(this);
//        tvVideo.setOnClickListener(this);
//        tvCollect.setOnClickListener(this);
//        tvRed.setOnClickListener(this);
//        tvCard.setOnClickListener(this);

//        ibFace.setOnClickListener(this);
        mEtMsg.setOnTouchListener(this);

//        mllFace = (LinearLayout) findViewById(R.id.face_ll);
        iv00 = (ImageView) findViewById(R.id.iv_gif_00);
        iv01 = (ImageView) findViewById(R.id.iv_gif_01);
        iv02 = (ImageView) findViewById(R.id.iv_gif_02);

        mBtnAffix = (ImageButton) findViewById(R.id.btn_chat_affix);
        mParams = getWindow().getAttributes();

//        mEtMsg = (EditText) findViewById(R.id.msg_et);
        mBtnSend = (Button) findViewById(R.id.send_btn);

        iv00 = (ImageView) findViewById(R.id.iv_gif_00);
        iv01 = (ImageView) findViewById(R.id.iv_gif_01);
        iv02 = (ImageView) findViewById(R.id.iv_gif_02);

        iv00.setOnClickListener(this);
        iv01.setOnClickListener(this);
        iv02.setOnClickListener(this);

        mBtnSend.setClickable(true);
        mBtnSend.setEnabled(true);
        mBtnSend.setOnClickListener(this);
//        mBtnAffix.setOnClickListener(this);

        mLocalBroadcastReceiver = new LocalBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Action.EMOJI_EDITED_SEND);
        filter.addAction(Constants.Action.REFRSH_CHAT_PAGER_DATAS);
        filter.addAction(Constants.Action.EXIT_CURRETN_GROUP_REFRESH_DATAS);
        filter.addAction(Constants.Action.REFRSH_CHAT_PAGER);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mLocalBroadcastReceiver, filter);
        // 消息
        mApplication = App.getInstance();
        mMsgDB = mApplication.getMessageDB();// 发送数据库
        mRecentDB = mApplication.getRecentDB();// 接收消息数据库
//        adapter = new MessageAdapter(context, initMsgData());
        adapter = new MessageAdapter(context, initMsgData(), toUid);
        mMsgListView = (MsgListView) findViewById(R.id.msg_listView);
        // 触摸ListView隐藏表情和输入法
        mMsgListView.setOnTouchListener(this);
        mMsgListView.setPullLoadEnable(false);
        mMsgListView.setXListViewListener(this);
        mMsgListView.setAdapter(adapter);
        mMsgListView.setSelection(adapter.getCount() - 1);
//        mTitleRightBtn.setOnClickListener(this);
        mAdapter = new MyFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("info", "==========onPageSelected()");
//                if (position != EMOTION_SETING_UP && position != EMOTION_STORE) {
//                    setBotBarSelector(position);
//                }
                setBotBarSelector(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mEtMsgOnKeyListener();
        // 语音
        initRecorderView();

        initCells();
        mTvVoicePreeListener();// 按住录音按钮的事件
        initDetector();
    }

    private void initDetector() {
        mDetector = InputDetector.with(this)
                .setEmotionView(viewFace)
                .bindToContent(findViewById(R.id.fl_content))
                .bindToEditText(mEtMsg)
                .bindToEmotionButton(ibFace)
                .setMoreView(ll_other)
                .bindMoreButton(mBtnAffix)
                .build();
    }


    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
    }

    private void initEmotionData() {
        initDatas();
        InItTitle1();
        setSelector(0);
    }

    private void initCells() {
        cells = new ArrayList<>();
        CellEntity cell1 = new CellEntity(R.drawable.hp_ch_photos, "图片");
        CellEntity cell2 = new CellEntity(R.drawable.hp_ch_camera, "拍摄");
        CellEntity cell3 = new CellEntity(R.drawable.hp_ch_video, "小视频");
        CellEntity cell4 = new CellEntity(R.drawable.hp_ch_collect, "收藏");
        CellEntity cell5 = new CellEntity(R.drawable.hp_ch_red, "红包");
        CellEntity cell6 = new CellEntity(R.drawable.hp_ch_card, "个人名片");

        cells.add(cell1);
        cells.add(cell2);
        cells.add(cell3);
        cells.add(cell4);
        cells.add(cell5);
        cells.add(cell6);

        cellAdapter.initData(cells);
    }


    private void initDatas() {
        categorys = (ArrayList<EmoCate>) EmoCateHelper.getInstance().query();
    }

    /**
     * 加载消息历史，从数据库中读出
     */
    private List<MessageItem> initMsgData() {
//        List<MessageItem> list = mMsgDB
//                .getMsg(mSpUtil.getUserId(), MSGPAGERNUM);
        List<MessageItem> list = mMsgDB
                .getMsg(mMsgId, MSGPAGERNUM);
        List<MessageItem> msgList = new ArrayList<MessageItem>();// 消息对象数组
        if (list.size() > 0) {
            for (MessageItem entity : list) {
                if (entity.getName().equals("")) {
                    entity.setName(defaulgUserName);
                }
                if (entity.getHeadImg() < 0) {
                    entity.setHeadImg(defaultCount);
                }
                msgList.add(entity);
            }
        }
        return msgList;
    }

    /**
     * 输入框key监听事件
     */
    private void mEtMsgOnKeyListener() {
        mEtMsg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
//                || isFaceShow
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE || isFaceShow || isOther) {
                        viewFace.setVisibility(View.GONE);
                        ll_other.setVisibility(View.GONE);
                        isFaceShow = false;
                        isOther = false;
                        // imm.showSoftInput(msgEt, 0);
                        return true;
                    }
                }
                return false;
            }
        });
        mEtMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String key = s.toString();
                Log.i("info", "========key:" + key);
                Log.i("info", "========mEmoMap:" + mEmoMap.keySet().toString());
                if (mEmoMap.containsKey(key)) {
                    EmoEntity entity = mEmoMap.get(key);
                    if (entity != null) {
                        Log.i("info", "========showCurrentEmo()");
                        showCurrentEmo(entity, ibFace);
//                        if (popupWindow == null) {
////                            ll_input
//                            showCurrentEmo(entity, ibFace);
//                        } else {
//                            if (!popupWindow.isShowing()) {
//                                showCurrentEmo(entity, ibFace);
//                            }
//                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mBtnSend.setEnabled(true);
                    mBtnAffix.setVisibility(View.GONE);
                    mBtnSend.setVisibility(View.VISIBLE);
                } else {
                    mBtnSend.setEnabled(false);
                    mBtnAffix.setVisibility(View.VISIBLE);
                    mBtnSend.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 初始化语音布局
     */
    private void initRecorderView() {
        mIbMsgBtn = (ImageButton) findViewById(R.id.ib_chat_voice);
        mIbMsgBtn.setTag(isSpeech);
        mTvVoiceBtn = (TextView) findViewById(R.id.tv_chatmain_press_voice);
        mIbMsgBtn.setOnClickListener(this);
        mTvVoiceBtn.setOnClickListener(this);

        // include包含的布局语音模块
        mIvDelete = (ImageView) this.findViewById(R.id.img1);
        mLLDelete = (LinearLayout) this.findViewById(R.id.del_re);
        mIvBigDeleteIcon = (ImageView) this.findViewById(R.id.sc_img1);
        mChatPopWindow = this.findViewById(R.id.rcChat_popup);
        mLlVoiceRcding = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_rcding);
        mLlVoiceLoading = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_loading);
        mLlVoiceShort = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_tooshort);
        volume = (ImageView) this.findViewById(R.id.volume);
        mTvVoiceRecorderTime = (TextView) this
                .findViewById(R.id.tv_voice_rcd_time);
    }

    @Override
    public void onRefresh() {
        MSGPAGERNUM++;
        List<MessageItem> msgList = initMsgData();
        int position = adapter.getCount();
        adapter.setmMsgList(msgList);
        mMsgListView.stopRefresh();
        mMsgListView.setSelection(adapter.getCount() - position - 1);
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_gif_00:
                sendEmotionMessage(codes[0]);
                break;
            case R.id.send_btn:
                String msg = getText(mEtMsg);
                sendTxtMessage(msg);
                mEtMsg.setText("");
                break;
            case R.id.iv_gif_01:
                sendEmotionMessage(codes[1]);
                break;
            case R.id.iv_gif_02:
                sendEmotionMessage(codes[2]);
                break;
//            case R.id.tv_picture:
//                UIUtils.hindView(ll_emotion);
//                UIUtils.showView(ll_other);
//                break;

//            case R.id.tv_chat_img:
//                takeImg();
//                break;
//            case R.id.tv_chat_photo:
//                takePhoto();
//                break;
//            case R.id.tv_chat_collect://收藏
//
//                break;
//            case R.id.tv_chat_red://红包
//
//                break;
//            case R.id.tv_chat_card://名片
//
//                break;
//            case R.id.tv_chat_video:
//                Intent intent = new Intent(context, ImageGridActivity.class);
//                startActivityForResult(intent, 0);
//                break;
            case R.id.ib_chat_voice://声音
//                UIUtils.hindView(ll_other);
//                isOther = false;
////                if (!isFaceShow) {
////                    mInputMethodManager.hideSoftInputFromWindow(
////                            mEtMsg.getWindowToken(), 0);
////                    try {
////                        Thread.sleep(80);// 解决此时会黑一下屏幕的问题
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                    viewFace.setVisibility(View.VISIBLE);
////                    viewFace.findViewById(R.id.tv_edit).setVisibility(View.VISIBLE);
////                    isFaceShow = true;
////                } else {
////                    viewFace.setVisibility(View.GONE);
////                    isFaceShow = false;
////                }
//                mInputMethodManager.hideSoftInputFromWindow(
//                        mEtMsg.getWindowToken(), 0);
//                try {
//                    Thread.sleep(80);// 解决此时会黑一下屏幕的问题
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                viewFace.setVisibility(View.VISIBLE);
//                viewFace.findViewById(R.id.tv_edit).setVisibility(View.VISIBLE);
//                isFaceShow = true;
                UIUtils.hindView(viewFace);
                UIUtils.hindView(ll_other);
                boolean isSpeech = (boolean) view.getTag();
                if (!isSpeech) {
                    mIbMsgBtn.setBackgroundResource(R.drawable.hp_chat_keyboard);
                    mEtMsg.setVisibility(View.GONE);
                    mTvVoiceBtn.setVisibility(View.VISIBLE);
                    mInputMethodManager.hideSoftInputFromWindow(
                            mEtMsg.getWindowToken(), 0);
                    try {
                        Thread.sleep(80);// 解决此时会黑一下屏幕的问题
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    mIbMsgBtn.setBackgroundResource(R.drawable.hp_chat_speech);
                    mEtMsg.setVisibility(View.VISIBLE);
                    mTvVoiceBtn.setVisibility(View.GONE);
                    mInputMethodManager.showSoftInput(mEtMsg, 0);
                }
                view.setTag(!isSpeech);
                break;
            case R.id.tv_chatmain_press_voice: {
                // 按住说话
                // 弹出音量框

                break;
            }
            case R.id.ib_chat_face://表情
                if (viewFace.getVisibility() != View.VISIBLE) {
                    ivEditPic.setImageResource(R.drawable.hp_sm_picture);
                    ivEditPic.setTag(false);
                    UIUtils.hindView(ll_other);
                    isOther = false;
                    mInputMethodManager.hideSoftInputFromWindow(
                            mEtMsg.getWindowToken(), 0);
                    try {
                        Thread.sleep(80);// 解决此时会黑一下屏幕的问题
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    viewFace.setVisibility(View.VISIBLE);
                    viewFace.findViewById(R.id.tv_edit).setVisibility(View.GONE);
                    isFaceShow = true;
                }
                break;
            case R.id.btn_chat_affix:
                UIUtils.hindView(viewFace);
                isFaceShow = false;
                if (!isOther) {
                    mInputMethodManager.hideSoftInputFromWindow(
                            mEtMsg.getWindowToken(), 0);
                    try {
                        Thread.sleep(80);// 解决此时会黑一下屏幕的问题
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    UIUtils.showView(ll_other);
                    isOther = true;
                } else {
                    ll_other.setVisibility(View.GONE);
                    isOther = false;
                }
                break;

            case R.id.iv_add_bq://精选表情
                Intent it = new Intent(context, EmojiSelectWellActivity.class);
                startActivity(it);
                break;
            case R.id.iv_add_pic://制作表情
                Intent it2 = new Intent(context, EmojiMakeActivity.class);
                startActivity(it2);
                break;
            case R.id.iv_edit_pic://编辑表情
                boolean isEdit = (boolean) ivEditPic.getTag();
                if (!isEdit) {
                    viewFace.findViewById(R.id.tv_edit).setVisibility(View.VISIBLE);
                    viewFace.findViewById(R.id.tv_edit).setOnClickListener(this);
                    ivEditPic.setImageResource(R.drawable.hp_sm_picture_on);
                } else {
                    viewFace.findViewById(R.id.tv_edit).setVisibility(View.GONE);
                    ivEditPic.setImageResource(R.drawable.hp_sm_picture);
                }
                ivEditPic.setTag(!isEdit);
                break;
            case R.id.tv_edit://编辑
                if (editUrl.equals("")) {
                    if (!TextUtils.isEmpty(emoCode)) {
                        Intent it0 = new Intent(context, EmojiEditActivity.class);
                        it0.putExtra("emoCode", emoCode);
                        it0.putExtra("fromChat", true);
                        startActivity(it0);
                    } else {
                        showToast("请选择所编辑的图片", true);
                    }
                } else {
                    Intent it0 = new Intent(context, EmojiEditActivity.class);
                    it0.putExtra("camera_path", editUrl);
                    it0.putExtra("fromChat", true);
                    startActivity(it0);
                }
                break;
            case R.id.iv_add_person://聊天信息
                Intent it3 = null;
                if (isGroupChat) {
                    it3 = new Intent(context, ChatGroupInformActivity.class);
                    it3.putExtra("groupid", toUid);
                } else {
                    it3 = new Intent(context, ChatInformActivity.class);
                    it3.putExtra("uid", toUid);
                    it3.putExtra("head", toAvatar);
                    it3.putExtra("name", title);
                }
                if (it3 != null) {
//                    startActivity(it3);
                    startActivityForResult(it3, 100);
                }
                break;
            default:
                String tag = (String) view.getTag();
                int id = view.getId();
                if (tag.equals("top")) {
                    setSelector(id);
                } else {
                    if (id != EMOTION_SETING_UP) {
                        setBotBarSelector(id);
                    } else {
                        openActivity(EmojiMyActivity.class);
                    }
                }
                break;
        }
    }

    private String photoPath = "";

    private void takePhoto() {
        String status = Environment.getExternalStorageState();
        // 检测手机是否有sd卡
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            // 创建存放照片的文件夹
            File dir = new File(Environment.getExternalStorageDirectory() + "/myimage/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 开启照相机
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(dir, String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            photoPath = file.getPath();
            Uri imageUri = Uri.fromFile(file);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(openCameraIntent, Code.Request.TAKE_PHOTO);
        } else {
            Toast.makeText(context, "没有储存卡", Toast.LENGTH_LONG).show();
        }
    }


    private void takeImg() {
        Intent intent;
        intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Code.Request.GET_PHOTO);
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        switch (v.getId()) {
            case R.id.msg_listView:
                viewFace.setVisibility(View.GONE);
                ll_other.setVisibility(View.GONE);
                mInputMethodManager.hideSoftInputFromWindow(
                        mEtMsg.getWindowToken(), 0);
                isFaceShow = false;
                isOther = false;
                break;
            case R.id.msg_et:
//                mInputMethodManager.showSoftInput(mEtMsg, 0);

//                viewFace.setVisibility(View.GONE);
//                ll_other.setVisibility(View.GONE);
//                isFaceShow = false;
//                isOther = false;
                break;
            default:
                break;
        }
        return false;
    }


    private void sendEmotionMessage(String faceCode) {
        // 发送消息
        MessageItem item = new MessageItem(
                MessageItem.MESSAGE_TYPE_EMOTION, mSpUtil.getNick(),
                System.currentTimeMillis(), faceCode, mSpUtil.getHeadIcon(),
                false, 0, 0, "" + System.currentTimeMillis(), account.getUser_name(), account.getUser_id(), account.getAvatar());
        adapter.upDateMsg(item);
        mMsgListView.setSelection(adapter.getCount() - 1);
        mMsgDB.saveMsg(mMsgId, item);// 消息保存数据库
//        mEtMsg.setText("");
//        if ("".equals(mSpUtil.getUserId())) {
//            showToast("发送者未登录!", true);
//            return;
//        }
//        ChatServices.sendMessage(getString(MESSAGE_TYPE_EMOTION, faceCode));
        Session.getInstance().sendImMessage(getString(MESSAGE_TYPE_EMOTION, faceCode));
        saveConversation(title, toAvatar, toUid, MESSAGE_TYPE_EMOTION, "");
        // ===保存近期的消息
        RecentItem recentItem = new RecentItem(
                MessageItem.MESSAGE_TYPE_EMOTION, mSpUtil.getUserId(),
                defaultCount, defaulgUserName, faceCode, 0,
                System.currentTimeMillis(), 0, "" + System.currentTimeMillis());
        mRecentDB.saveRecent(recentItem);
    }

    private String getString(int msgType, String code) {
        String tag = "";
        if (isGroupChat) {
            tag = "group";
        } else {
            tag = "unicast";
        }
        String str = "";
        switch (msgType) {
            case MESSAGE_TYPE_EMOTION:
                str = "emotion_" + toUid + "_" + code + "_" + tag + "_" + getExtension();
                break;
            case MESSAGE_TYPE_IMG:
                str = "picture_" + toUid + "_" + code + "_" + tag + "_" + getExtension();
                break;
            case MESSAGE_TYPE_VIDEO:
                str = "video_" + toUid + "_" + code + "_" + tag + "_" + getExtension();
                break;
            case MESSAGE_TYPE_TXT:
                str = "text_" + toUid + "_" + code + "_" + tag + "_" + getExtension();
                break;
            case MESSAGE_TYPE_AUDIO:
                str = "audio_" + toUid + "_" + code + "_" + tag + "_" + getExtension();
                break;
        }
        return str;
    }

    private String getExtension() {
        JSONObject object = new JSONObject();
        try {
            object.put("sender", account.getUser_name());
            object.put("senderID", userid);
            object.put("senderAvartar", account.getAvatar());

            object.put("conversation", title);
            object.put("conversationAvartar", toAvatar);
            return Base64Utils.encodeBase64(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sendImageMessage(String photoPath) {
        // 发送消息
        MessageItem item = new MessageItem(
                MessageItem.MESSAGE_TYPE_IMG, mSpUtil.getNick(),
                System.currentTimeMillis(), photoPath, mSpUtil.getHeadIcon(),
                false, 0, 0, "" + System.currentTimeMillis(), account.getUser_name(), account.getUser_id(), account.getAvatar());
        adapter.upDateMsg(item);
        mMsgListView.setSelection(adapter.getCount() - 1);
        mMsgDB.saveMsg(mMsgId, item);// 消息保存数据库
//        ChatServices.sendMessage(getString(MESSAGE_TYPE_IMG, photoPath));
        Session.getInstance().sendImMessage(getString(MESSAGE_TYPE_IMG, photoPath));
        saveConversation(title, toAvatar, toUid, MESSAGE_TYPE_IMG, "");
        // ===保存近期的消息
        RecentItem recentItem = new RecentItem(
                MessageItem.MESSAGE_TYPE_IMG, mSpUtil.getUserId(),
                defaultCount, defaulgUserName, photoPath, 0,
                System.currentTimeMillis(), 0, "" + System.currentTimeMillis());
        mRecentDB.saveRecent(recentItem);
    }

    private void sendTxtMessage(String msg) {//发文字
        // 发送消息
        MessageItem item = new MessageItem(
                MessageItem.MESSAGE_TYPE_TXT, mSpUtil.getNick(),
                System.currentTimeMillis(), msg, mSpUtil.getHeadIcon(),
                false, 0, 0, "" + System.currentTimeMillis(), account.getUser_name(), account.getUser_id(), account.getAvatar());
        adapter.upDateMsg(item);
        mMsgListView.setSelection(adapter.getCount() - 1);
        mMsgDB.saveMsg(mMsgId, item);// 消息保存数据库
//        mEtMsg.setText("");
//        if ("".equals(mSpUtil.getUserId())) {
//            showToast("发送者未登录!", true);
//            return;
//        }
//        ChatServices.sendMessage(getString(MESSAGE_TYPE_TXT, msg));
        Session.getInstance().sendImMessage(getString(MESSAGE_TYPE_TXT, msg));
        saveConversation(title, toAvatar, toUid, MESSAGE_TYPE_TXT, msg);
        // ===保存近期的消息
        RecentItem recentItem = new RecentItem(
                MessageItem.MESSAGE_TYPE_TXT, mSpUtil.getUserId(),
                defaultCount, defaulgUserName, msg, 0,
                System.currentTimeMillis(), 0, "" + System.currentTimeMillis());
        mRecentDB.saveRecent(recentItem);
    }

    private void sendAudioMessage(String photoPath, int voiceTime) {//发声音
        // 发送消息
        MessageItem item = new MessageItem(
                MessageItem.MESSAGE_TYPE_AUDIO, mSpUtil.getNick(),
                System.currentTimeMillis(), photoPath, mSpUtil.getHeadIcon(),
                false, 0, voiceTime, "" + System.currentTimeMillis(), account.getUser_name(), account.getUser_id(), account.getAvatar());
        adapter.upDateMsg(item);
        mMsgListView.setSelection(adapter.getCount() - 1);
        mMsgDB.saveMsg(mMsgId, item);// 消息保存数据库
//        mEtMsg.setText("");
//        if ("".equals(mSpUtil.getUserId())) {
//            showToast("发送者未登录!", true);
//            return;
//        }
//        ChatServices.sendMessage(getString(MESSAGE_TYPE_AUDIO, photoPath));
        Session.getInstance().sendImMessage(getString(MESSAGE_TYPE_AUDIO, photoPath));
        saveConversation(title, toAvatar, toUid, MESSAGE_TYPE_AUDIO, "");
        // ===保存近期的消息
        RecentItem recentItem = new RecentItem(
                MessageItem.MESSAGE_TYPE_AUDIO, mSpUtil.getUserId(),
                defaultCount, defaulgUserName, photoPath, 0,
                System.currentTimeMillis(), 0, "" + System.currentTimeMillis());
        mRecentDB.saveRecent(recentItem);
    }

    private void saveConversation(String name, String avatar, String userid, int type, String content) {
        BaseConversation sation = new BaseConversation();
        sation.setUser_name(name);
        sation.setAvatar(avatar);
        sation.setConversation_id(userid);
        if (isGroupChat) {
            sation.setNewest_msg_type("group");
        } else {
            sation.setNewest_msg_type("unicast");
        }
        sation.setNewest_msg_time("" + System.currentTimeMillis());
        ConversationHelper.getInstance().save(sation);
        String msg = "";
        switch (type) {
            case MESSAGE_TYPE_TXT:
                msg = "我：" + content;
                break;
            case MESSAGE_TYPE_EMOTION:
                if (isGroupChat) {
                    msg = "我发了一条[表情]";
                } else {
                    msg = "[表情]";
                }
                break;
            case MESSAGE_TYPE_IMG:
                if (isGroupChat) {
                    msg = "我发了一条[图片]";
                } else {
                    msg = "[图片]";
                }
                break;
            case MESSAGE_TYPE_AUDIO:
                if (isGroupChat) {
                    msg = "我发了一条[声音]";
                } else {
                    msg = "[声音]";
                }
                break;
            case MESSAGE_TYPE_VIDEO:
                if (isGroupChat) {
                    msg = "我发了一条[视频]";
                } else {
                    msg = "[视频]";
                }
                break;
        }
        String noticeKey = PrefKey.NEWS_NOTICE_KEY + "/" + userid;
        PreferenceUtils.putString(this, noticeKey, msg);
//        String key = PrefKey.UN_READ_NEWS_KEY + "/" + userid;
//        int num = PreferenceUtils.getInt(this, key, 0);
//        num++;
//        PreferenceUtils.putInt(this, key, num);
    }

    private void sendVideoMessage(String faceCode) {
        // 发送消息
        MessageItem item = new MessageItem(
                MessageItem.MESSAGE_TYPE_VIDEO, mSpUtil.getNick(),
                System.currentTimeMillis(), faceCode, mSpUtil.getHeadIcon(),
                false, 0, 0, "" + System.currentTimeMillis(),
                account.getUser_name(), account.getUser_id(),
                account.getAvatar());
        adapter.upDateMsg(item);
        mMsgListView.setSelection(adapter.getCount() - 1);
        mMsgDB.saveMsg(mMsgId, item);// 消息保存数据库
//        mEtMsg.setText("");
//        if ("".equals(mSpUtil.getUserId())) {
//            showToast("发送者未登录!", true);
//            return;
//        }
//        ChatServices.sendMessage(getString(MESSAGE_TYPE_VIDEO, faceCode));
        Session.getInstance().sendImMessage(getString(MESSAGE_TYPE_VIDEO, faceCode));
        saveConversation(title, toAvatar, toUid, MESSAGE_TYPE_VIDEO, "");
        // ===保存近期的消息
        RecentItem recentItem = new RecentItem(
                MessageItem.MESSAGE_TYPE_VIDEO, mSpUtil.getUserId(),
                defaultCount, defaulgUserName, faceCode, 0,
                System.currentTimeMillis(), 0,
                "" + System.currentTimeMillis());
        mRecentDB.saveRecent(recentItem);
    }

//    private void receiverMessage(String str) {
//        Log.i("info", "==============:" + str);
//        String[] datas = str.split("_");
//        if (datas != null) {
//            String type = datas[0];
//            long time_samp = Long.parseLong(datas[5]);
//            if (!datas[1].equals(userid)) {
//                Message message = null;
//                if (type.equals("emotion")) {
//                    message = new Message(MessageItem.MESSAGE_TYPE_EMOTION, time_samp, datas[2], "", 0);
//                } else if (type.equals("picture")) {
//                    message = new Message(MessageItem.MESSAGE_TYPE_IMG, time_samp, datas[2], "", 0, uid, "王五");
//                } else if (type.equals("video")) {
//                    message = new Message(MessageItem.MESSAGE_TYPE_VIDEO, time_samp, datas[2], "", 0);
//                }
//                android.os.Message msg = new android.os.Message();
//                msg.what = NEW_MESSAGE;
//                msg.obj = message;
//                handler.sendMessage(msg);
//            }
//        }
//    }

    /**
     * @param resId  资源ID
     * @param length true为长时间，false为短时间
     * @return: void
     */
    protected void showToast(int resId, boolean length) {
        Toast.makeText(this, resId, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    /**
     * @param msg    内容
     * @param length true为长时间，false为短时间
     * @return: void
     */
    protected void showToast(String msg, boolean length) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(this, msg, length ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onMessageReceive(String msg) {
//        receiverMessage(msg);
//    }


    @Override
    public void onMessageReceive(MessageItem item, String fromUid) {
        if (item != null) {
            if (fromUid.equals(toUid)) {//接受到的uiserid 与该聊天的对象id一样
                android.os.Message msg = new android.os.Message();
                msg.what = NEW_MESSAGE;
                msg.obj = item;
                handler.sendMessage(msg);
            }
        }
    }

//    @Override
//    public void onMessageReceive(String msgType, String senderId,
//                                 String senderName, String senderAvatar,
//                                 String mssg, String chatType,
//                                 String groupId, String temp) {
////        if (!senderId.equals(userid)) {
////            Message message = null;
////            if (msgType.equals("emotion")) {
////                message = new Message(MessageItem.MESSAGE_TYPE_EMOTION, temp, mssg, "", 0, senderId, senderName, senderAvatar);
////            } else if (msgType.equals("picture")) {
////                message = new Message(MessageItem.MESSAGE_TYPE_IMG, temp, mssg, "", 0, senderId, senderName, senderAvatar);
////            } else if (msgType.equals("video")) {
////                message = new Message(MessageItem.MESSAGE_TYPE_VIDEO, temp, mssg, "", 0, senderId, senderName, senderAvatar);
////            }
////            android.os.Message msg = new android.os.Message();
////            msg.what = NEW_MESSAGE;
////            msg.obj = message;
////            handler.sendMessage(msg);
////        }
//    }

    @Override
    public void onNotify(String title, final String content) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                showToast(content, true);
            }
        });
    }


    @Override
    public void onNetChange(boolean isNetConnected) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("info", "=======onResume()");
        initEmoMap();
        initEmotionData();
        ChatServices.callbacks.add(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mEmoMap.size() != 0) {
            mEmoMap.clear();
        }
        ChatServices.callbacks.remove(this);
    }


    private void uploadImageAudioFile(final String path, final boolean isImage, final int voiceTime) {
        String main = "";
        if (isImage) {
            main = HttpUrl.UPLOAD_IMAGE_URL;
        } else {
            main = HttpUrl.UPLOAD_AUDIO_URL;
        }
        final HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        final com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        try {

            File file = null;
            if (isImage) {
                String fileName = FileUtils.getFileNameFromPath(path);
                String compressPath = FileUtils.compressImage(path, CHATPHOTO_PATH + fileName, 30);
                file = new File(compressPath);
            } else {
                file = new File(path);
            }
            if (file.exists() && file.length() > 0) {
                params.addBodyParameter("file", file);
            }
            httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    Log.i("info", "responseInfo:" + responseInfo.result);
//                    cancelLoadingDialog();
                    String path = parsePath(responseInfo.result);
                    Log.i("info", "=====path:" + path);
                    if (isImage) {
                        sendImageMessage(path);
                    } else {
                        sendAudioMessage(path, voiceTime);
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(context, "上传失败!", Toast.LENGTH_LONG).show();
                    cancelLoadingDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String parsePath(String json) {
        try {
            JSONObject object = new JSONObject(json);
            int code = object.getInt("code");
            if (code == 0) {
                return object.getString("data");
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadVideoFile(String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            showToast("视频文件路径未找到!", true);
            return;
        }
        showLoadingDialog();
        String main = HttpUrl.UPLOAD_VIDEO_URL;
        final HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        final com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        if (videoPath != null && !videoPath.equals("")) {
            File file = new File(videoPath);
            params.addBodyParameter("file", file);
        } else {
            showToast("找不到对应的视频文件!", true);
            return;
        }
        try {
            httpUtils.send(HttpRequest.HttpMethod.POST,
                    main, params,
                    new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            cancelLoadingDialog();
                            String path = parsePath(responseInfo.result);
                            Log.i("info", "======videoPath:" + path);
                            if (path != null && !path.equals("")) {
                                sendVideoMessage(path);
                            }
                        }

                        @Override
                        public void onFailure(HttpException e, String s) {
                            Toast.makeText(context, "上传失败!", Toast.LENGTH_LONG).show();
                            cancelLoadingDialog();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Code.Request.GET_PHOTO) {
            if (data != null) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Uri originalUri = data.getData();
                if (originalUri == null) return;
                Cursor cursor = new CursorLoader(context, originalUri, proj, null, null, null).loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                final String picPath = cursor.getString(column_index);
                uploadImageAudioFile(picPath, true, 0);
            }
        } else if (requestCode == Code.Request.TAKE_PHOTO) {
            uploadImageAudioFile(photoPath, true, 0);
        } else if (requestCode == 0) {
            if (data != null) {
//              int duration = data.getIntExtra("dur", 0);
                String videoPath = data.getStringExtra("path");
                uploadVideoFile(videoPath);
//                File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
//                try {
//                    FileOutputStream fos = new FileOutputStream(file);
//                    Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
//                    ThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                    fos.close();
//                    if (ThumbBitmap != null) {
//                        imageView.setImageBitmap(ThumbBitmap);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    private void InItTitle1() {
        linearLayout.removeAllViews();
        if (textViews != null) {
            textViews.clear();
            textViews = null;
        }
        if (categorys != null && categorys.size() != 0) {
            textViews = new ArrayList<TextView>();
            int width = getWindowManager().getDefaultDisplay().getWidth() / 5;
//        int height = 70;
            int height = UIUtils.dip2px(this, 50);
            for (int i = 0; i < categorys.size(); i++) {
                TextView textView = new TextView(this);
                textView.setText(categorys.get(i).getCate_name());
                textView.setTextSize(17);
                textView.setTextColor(resources.getColor(R.color.black));
                textView.setWidth(width);
                textView.setHeight(height - 30);
                textView.setGravity(Gravity.CENTER);
                textView.setTag("top");
                textView.setId(i);
                textView.setOnClickListener(this);
                textViews.add(textView);
                // �ָ���
                View view = new View(this);
                LinearLayout.LayoutParams layoutParams = new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.width = 1;
                layoutParams.height = height - 40;
                layoutParams.gravity = Gravity.CENTER;
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(resources.getColor(R.color.gray));
                linearLayout.addView(textView);
                if (i != categorys.size() - 1) {
                    linearLayout.addView(view);
                }
            }
        }
    }

    public void setSelector(int id) {
        if (categorys != null && categorys.size() != 0) {
            for (int i = 0; i < categorys.size(); i++) {
                if (id == i) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                            R.drawable.grouplist_item_bg_normal);
                    textViews.get(id).setBackgroundDrawable(
                            new BitmapDrawable(bitmap));
                    textViews.get(id).setTextColor(Color.RED);
//                viewPager.setCurrentItem(i);
                    EmoCate cate = categorys.get(i);
                    initCatePager(cate.getCate_id());
                } else {
                    textViews.get(i).setBackgroundDrawable(new BitmapDrawable());
                    textViews.get(i).setTextColor(resources.getColor(R.color.black));
                }
            }
        } else {
            initCatePager("");
//            if (fragments != null && fragments.size() != 0) {
//                fragments.clear();
//            }
//            mAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private MyFragmentPageAdapter mAdapter;

    private void initCatePager(String cateId) {
        if (fragments != null && fragments.size() != 0) {
            fragments.clear();
        }
        ArrayList<EmoGroup> groups = (ArrayList<EmoGroup>) EmoGroupHelper.getInstance().queryByCateId(cateId);
        Log.i("info", "====cateId:" + cateId);
        Log.i("info", "====groups.size():" + groups.size());
        FaceLoveFragment frag = FaceLoveFragment.newInstance();
        frag.setLoveCallback(this);
        fragments.add(frag);
        InItBottomBar(groups);
        if (groups != null && groups.size() != 0) {
            for (int i = 0; i < groups.size(); i++) {
                EmoGroup group = groups.get(i);
                FaceFragment fragment = FaceFragment.newInstance(group.getGroup_id());
                fragment.setCallback(this);
                fragments.add(fragment);
            }
            mAdapter.setFragments(fragments);
            setBotBarSelector(1);
        } else {
            mAdapter.setFragments(fragments);
            setBotBarSelector(0);
        }
    }

    private String editUrl = "";
    private String emoCode;

    @Override
    public void onItemClick(EmoEntity entity) {//点击表情，发送表情
//        showToast("emoji_id:" + entity.getEmo_id(), true);
        boolean isEdit = (boolean) ivEditPic.getTag();
        if (!isEdit) {
            String code = entity.getEmo_code();
            if (!code.equals("")) {
                sendEmotionMessage(code);
            }
        } else {
            String formart = entity.getEmo_format();
            if (!formart.equals("gif")) {
                editUrl = entity.getUrl();
                emoCode = entity.getEmo_code();
            } else {
                showToast("不支持gif编辑", true);
            }
        }
    }

    @Override
    public void onItemLoveClick(String image) {
        boolean isEdit = (boolean) ivEditPic.getTag();
        if (!isEdit) {
            if (image != null && !image.equals("")) {
                sendImageMessage(image);
            }
        } else {
            editUrl = image;
        }
    }

    public void setBotBarSelector(int id) {
        for (int i = 0; i < fragments.size(); i++) {
            if (id == i) {
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//                        R.drawable.grouplist_item_bg_normal);
//                imageViews.get(id).setBackgroundDrawable(
//                        new BitmapDrawable(bitmap));
//                imageViews.get(id).setTextColor(Color.RED);
                imageViews.get(i).setBackgroundColor(resources.getColor(R.color.chat_bottom));
                viewPager.setCurrentItem(i);
            } else {
//                textViews.get(i).setBackgroundDrawable(new BitmapDrawable());
                imageViews.get(i).setBackgroundColor(resources.getColor(R.color.white));
//                textViews.get(i).setTextColor(resources.getColor(R.color.black));
            }
        }
    }

    private void InItBottomBar(ArrayList<EmoGroup> groups) {
        llBottom.removeAllViews();
        imageViews = new ArrayList<ImageView>();
        int width = getWindowManager().getDefaultDisplay().getWidth() / 8;
        int height = UIUtils.dip2px(this, 50);
        for (int i = 0; i <= groups.size() + 1; i++) {
            ImageView textView = new ImageView(this);
            if (i == groups.size() + 1) {
                textView.setId(EMOTION_SETING_UP);
            } else {
                textView.setId(i);
            }
            textView.setTag("bottom");
            textView.setOnClickListener(this);
//            textView.setMinimumWidth(width);
            LinearLayout.LayoutParams params = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.width = width;
            params.height = height - 32;
            params.gravity = Gravity.CENTER;
            textView.setLayoutParams(params);
            if (i == groups.size() + 1) {
//                textView.setImageResource(R.drawable.hp_ch_setup);
                textView.setImageResource(R.drawable.hp_sm_setup);
            } else if (i == 0) {
//                textView.setImageResource(R.drawable.love);
                textView.setImageResource(R.drawable.hp_o_love);
            } else {
                EmoGroup group = groups.get(i - 1);
                Log.i("info", "=====group:" + group);
                Bitmap bitmap = getBitmap(group);
                Log.i("info", "=====bitmap:" + bitmap);
                if (bitmap != null) {
                    textView.setImageBitmap(bitmap);
                }
            }
            imageViews.add(textView);
            // �ָ���
            View view = new View(this);
            LinearLayout.LayoutParams layoutParams = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.width = 1;
            layoutParams.height = height - 32;
            layoutParams.gravity = Gravity.CENTER;
            view.setLayoutParams(layoutParams);
            view.setBackgroundColor(resources.getColor(R.color.gray));
            llBottom.addView(textView);
            if (i != groups.size() - 1) {
                llBottom.addView(view);
            }
        }
    }

    private Bitmap getBitmap(EmoGroup entity) {
        String path = Constants.EMO_DIR_PATH +
                entity.getCate_id() + File.separator +
                entity.getGroup_id() + File.separator +
                entity.getGroup_icon();
        Log.i("info", "====path:" + path);
        return FileUtils.getBitmapByPath(path);
    }

    /**
     * 自定义fragment适配器
     *
     * @author ZHF
     */
//    private class MyFragmentPageAdapter extends FragmentPagerAdapter {
//        ArrayList<Fragment> fragments;
//
//        public MyFragmentPageAdapter(FragmentManager fm) {
//            super(fm);
//            fragments = new ArrayList<>();
//        }
//
//        public MyFragmentPageAdapter(FragmentManager fm,
//                                     ArrayList<Fragment> frags) {
//            super(fm);
//            fragments = frags;
//        }
//
//        public void initDatas(ArrayList<Fragment> frags) {
//            fragments.clear();
//            fragments.addAll(frags);
//            this.notifyDataSetChanged();
//        }
//
//        @Override
//        public int getCount() {
//            return fragments.size();
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return fragments.get(position);
//        }
//    }

    public class MyFragmentPageAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private FragmentManager fm;

        public MyFragmentPageAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
//            this.fragments = fragments;
            fragments = new ArrayList<>();
        }

        public MyFragmentPageAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.fragments = fragments;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.i("info", "==================instantiateItem()");
            if (true) {//根据需求添加更新标示，UI更新完成后改回false，看不懂的回家种田
                //得到缓存的fragment
                Fragment fragment = (Fragment) super.instantiateItem(container, position);
                //得到tag，这点很重要
                String fragmentTag = fragment.getTag(); //这里的tag是系统自己生产的，我们直接取就可以
//如果这个fragment需要更新
                FragmentTransaction ft = fm.beginTransaction();
//移除旧的fragment
                ft.remove(fragment);
//换成新的fragment
                fragment = fragments.get(position);
                Log.i("info", "===============isAdded:" + fragment.isAdded());
                //添加新fragment时必须用前面获得的tag，这点很重要
                ft.add(container.getId(), fragment, fragmentTag);
                Log.i("info", "===============add:");
                ft.attach(fragment);
                ft.addToBackStack(null);
                ft.commit();
                return fragment;
            } else {
                return super.instantiateItem(container, position);
            }
        }

        public void setFragments(ArrayList<Fragment> frags) {
//            if (this.fragments != null) {
//                FragmentTransaction ft = fm.beginTransaction();
//                for (Fragment f : this.fragments) {
//                    ft.remove(f);
//                }
//                ft.commit();
//                ft = null;
//                fm.executePendingTransactions();
//            }
//            this.fragments = fragments;
//            notifyDataSetChanged();
            fragments.clear();
            fragments.addAll(frags);
            this.notifyDataSetChanged();
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private class itemCellClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            CellEntity cell = (CellEntity) adapterView.getItemAtPosition(i);
            switch (cell.getResId()) {
                case R.drawable.hp_ch_photos:
//                    takeImg();
                    getPermission(PermissionUtil.PERMISSIONS_CHAT_ALBUM, OPEN_ALBUM);
                    break;
                case R.drawable.hp_ch_camera:
//                    takePhoto();
                    getPermission(PermissionUtil.PERMISSIONS_CHAT_CAMERA, OPEN_CAMERA);
                    break;
                case R.drawable.hp_ch_video:
//                    Intent intent = new Intent(context, ImageGridActivity.class);
//                    startActivityForResult(intent, 0);
                    getPermission(PermissionUtil.PERMISSIONS_CHAT_CAMERA, RECORD_VIDEO);
                    break;
                case R.drawable.hp_ch_collect:
                    showToast("暂不支持!", true);
                    break;
                case R.drawable.hp_ch_red:
                    showToast("暂不支持!", true);
                    break;
                case R.drawable.hp_ch_card:
                    showToast("暂不支持!", true);
                    break;
            }
        }
    }


    /**
     * 按住录音按钮的事件
     */
    private void mTvVoicePreeListener() {
        // 按住录音添加touch事件
        mTvVoiceBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getPermission(PermissionUtil.PERMISSIONS_CHAT_AUDIO, RECORD_AUDIO);
                if (!Environment.getExternalStorageDirectory().exists()) {
                    showToast("No SDCard", true);
                    return false;
                }
                int[] location = new int[2];
                mTvVoiceBtn.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
                int[] del_location = new int[2];
                mLLDelete.getLocationInWindow(del_location);
                int del_Y = del_location[1];
                int del_x = del_location[0];
                if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
                    if (!Environment.getExternalStorageDirectory().exists()) {
                        showToast("No SDCard", true);
                        return false;
                    }
                    // 判断手势按下的位置是否是语音录制按钮的范围内
                    mTvVoiceBtn
                            .setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
                    mChatPopWindow.setVisibility(View.VISIBLE);
                    mLlVoiceLoading.setVisibility(View.VISIBLE);
                    mLlVoiceRcding.setVisibility(View.GONE);
                    mLlVoiceShort.setVisibility(View.GONE);
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (!isShosrt) {
                                mLlVoiceLoading.setVisibility(View.GONE);
                                mLlVoiceRcding.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 300);
                    // img1.setVisibility(View.VISIBLE);
                    mLLDelete.setVisibility(View.GONE);
                    startRecord();
                    flag = 2;
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        && flag == 2) {// 松开手势时执行录制完成
                    System.out.println("4");
                    mTvVoiceBtn
                            .setBackgroundResource(R.drawable.voice_rcd_btn_nor);
                    mLlVoiceRcding.setVisibility(View.GONE);
                    // stopRecord();
                    try {
                        stopRecord();
                    } catch (IllegalStateException e) {
                        showToast("麦克风不可用", true);
                        isCancelVoice = true;
                    }
                    mEndRecorderTime = System.currentTimeMillis();
                    flag = 1;
                    int mVoiceTime = (int) ((mEndRecorderTime - mStartRecorderTime) / 1000);
                    if (mVoiceTime < 3) {
                        isShosrt = true;
                        mLlVoiceLoading.setVisibility(View.GONE);
                        mLlVoiceRcding.setVisibility(View.GONE);
                        mLlVoiceShort.setVisibility(View.VISIBLE);
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                mLlVoiceShort.setVisibility(View.GONE);
                                mChatPopWindow.setVisibility(View.GONE);
                                isShosrt = false;
                            }
                        }, 500);
                        File file = new File(mSoundUtil.getFilePath(
                                context, mRecordTime).toString());
                        if (file.exists()) {
                            file.delete();
                        }
                        return false;
                    }
                    // ===发送出去,界面展示
                    if (!isCancelVoice) {
//                        showVoice(mVoiceTime);
                        String path = mSoundUtil.getFilePath(context, mRecordTime).toString();
                        uploadImageAudioFile(path, false, mVoiceTime);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 语音界面展示
     *
     * @param mVoiceTime
     */
    protected void showVoice(int mVoiceTime) {
        if (mRecordTime == null || "".equals(mRecordTime)) {
            return;
        }
//        MessageItem item = new MessageItem(MessageItem.MESSAGE_TYPE_AUDIO,
//                mSpUtil.getNick(), System.currentTimeMillis(), mRecordTime,
//                mSpUtil.getHeadIcon(), false, 0, mVoiceTime);
        MessageItem item = new MessageItem(
                MessageItem.MESSAGE_TYPE_AUDIO, mSpUtil.getNick(),
                System.currentTimeMillis(), photoPath, mSpUtil.getHeadIcon(),
                false, 0, 0, "" + System.currentTimeMillis(), account.getUser_name(), account.getUser_id(), account.getAvatar());
        adapter.upDateMsg(item);
        mMsgListView.setSelection(adapter.getCount() - 1);
        mMsgDB.saveMsg(mMsgId, item);// 消息保存数据库
        // ===发送消息到服务器
//        com.pzf.liaotian.bean.Message msgItem = new com.pzf.liaotian.bean.Message(
//                MessageItem.MESSAGE_TYPE_RECORD, System.currentTimeMillis(),
//                item.getMessage(), "", item.getVoiceTime());
//        if ("".equals(mSpUtil.getUserId())) {
//            Log.e("fff", "用户id为空3");
//            return;
//        }
//        new SendMsgAsyncTask(mGson.toJson(msgItem), mSpUtil.getUserId()).send();// push发送消息到服务器
        // ===保存近期的消息
//        RecentItem recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_AUDIO,
//                mSpUtil.getUserId(), defaultCount, defaulgUserName, mSoundUtil
//                .getFilePath(context, item.getMessage())
//                .toString(), 0, System.currentTimeMillis(),
//                item.getVoiceTime());
        RecentItem recentItem = new RecentItem(
                MessageItem.MESSAGE_TYPE_AUDIO, mSpUtil.getUserId(),
                defaultCount, defaulgUserName, mSoundUtil
                .getFilePath(context, item.getMessage())
                .toString(), 0,
                System.currentTimeMillis(), item.getVoiceTime(),
                "" + System.currentTimeMillis());
        mRecentDB.saveRecent(recentItem);
    }

    /**
     * 开始录音
     */
    private void startRecord() {
        // ===录音格式：用户id_时间戳_send_sound
        // SoundUtil.getInstance().startRecord(MainActivity.this,
        // id_time_send_sound);
        mStartRecorderTime = System.currentTimeMillis();
        if (mSoundUtil != null) {
            mRecordTime = mSoundUtil.getRecordFileName();
            mSoundUtil.startRecord(context, mRecordTime);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

            mVoiceRcdTimeTask = new VoiceRcdTimeTask(mRcdStartTime);

            if (mExecutor == null) {
                mExecutor = Executors.newSingleThreadScheduledExecutor();
                mExecutor.scheduleAtFixedRate(mVoiceRcdTimeTask,
                        mRcdVoiceStartDelayTime, mRcdVoiceDelayTime,
                        TimeUnit.MILLISECONDS);
            }
        }

    }

    /**
     * 结束录音
     */
    private void stopRecord() throws IllegalStateException {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        volume.setImageResource(R.drawable.amp1);
        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.shutdown();
            mExecutor = null;
        }
        if (mSoundUtil != null) {
            mSoundUtil.stopRecord();
        }
    }

    /**
     * 录制语音计时器
     *
     * @desc:
     * @author: pangzf
     * @date: 2014年11月10日 下午3:46:46
     */
    private class VoiceRcdTimeTask implements Runnable {
        int time = 0;

        public VoiceRcdTimeTask(int startTime) {
            time = startTime;
        }

        @Override
        public void run() {
            time++;
            updateTimes(time);
        }
    }

    /**
     * 更新文本内容
     *
     * @param time
     */
    public void updateTimes(final int time) {
        Log.e("fff", "时间:" + time);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mTvVoiceRecorderTime.setText(TimeUtil
                        .getVoiceRecorderTime(time));
            }
        });

    }

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stopRecord();
        }
    };

    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSoundUtil.getAmplitude();
            Log.e("fff", "音量:" + amp);
            updateDisplay(amp);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };

    /**
     * 变换语音量的图片
     *
     * @param signalEMA
     */
    private void updateDisplay(double signalEMA) {
        switch ((int) signalEMA) {
            case 0:
            case 1:
                volume.setImageResource(R.drawable.amp1);
                break;
            case 2:
            case 3:
                volume.setImageResource(R.drawable.amp2);
                break;
            case 4:
            case 5:
                volume.setImageResource(R.drawable.amp3);
                break;
            case 6:
            case 7:
                volume.setImageResource(R.drawable.amp4);
                break;
            case 8:
            case 9:
                volume.setImageResource(R.drawable.amp5);
                break;
            case 10:
            case 11:
                volume.setImageResource(R.drawable.amp6);
                break;
            default:
                volume.setImageResource(R.drawable.amp7);
                break;
        }
    }


    private void initEmoMap() {
        ArrayList<EmoEntity> list = (ArrayList<EmoEntity>) EmoHelper.getInstance().query();
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                EmoEntity entity = list.get(i);
                String key = entity.getEmo_name();
                if (!mEmoMap.containsKey(key)) {
                    mEmoMap.put(key, entity);
                }
            }
        }
    }

    private void showCurrentEmo(EmoEntity entity, View v) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.popwindows_item_emo, null);
//        popupWindow = new PopupWindow(view, (int) mImageHeight,
//                (int) mImageHeight, false);
//
//        popupWindow.setAnimationStyle(R.style.PopupTitleBarAnim);
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.setTouchable(true);
//        popupWindow.setFocusable(true);
//        popupWindow.setBackgroundDrawable(getDrawableFromRes(R.drawable.bg_pop));
//        ImageView gif = (ImageView) view.findViewById(R.id.gif);
//        File file = FileUtil.getFileByPath(getPath(entity));
//        if (file != null) {
//            Glide.with(context).load(FileUtil.getBytesFromFile(file)).crossFade().into(gif);
//        }
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (popupWindow != null && popupWindow.isShowing()) {
//                    popupWindow.dismiss();
//                }
//            }
//        }, 5000);
//        int[] location = new int[2];
//        v.getLocationOnScreen(location);
//        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWindow.getWidth() / 2, location[1] - popupWindow.getHeight() - 30);

        File file = FileUtil.getFileByPath(getPath(entity));
        if (file != null) {
            Glide.with(context).load(FileUtil.getBytesFromFile(file)).crossFade().into(ivTips);
        }
        final Animation mFadeIn = AnimationUtils.loadAnimation(context, R.anim.push_top_in);
        final Animation mFadeOut = AnimationUtils.loadAnimation(context, R.anim.push_top_out);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivTips.getLayoutParams();
        params.width = (int) mImageHeight;
        params.height = (int) mImageHeight;
        ivTips.setLayoutParams(params);

        handler.post(new Runnable() {
            @Override
            public void run() {
                ivTips.setVisibility(View.VISIBLE);
                ivTips.startAnimation(mFadeIn);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ivTips.setVisibility(View.GONE);
                ivTips.startAnimation(mFadeOut);
            }
        }, 5000);
    }

    private String getPath(EmoEntity entity) {
        String path = Constants.EMO_DIR_PATH + File.separator +
                entity.getEmo_cate_id() + File.separator +
                entity.getEmo_group_id() + File.separator +
                entity.getEmo_code() + "." +
                entity.getEmo_format();
        return path;
    }

    private Drawable getDrawableFromRes(int resId) {
        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resId);
        return new BitmapDrawable(bmp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(mLocalBroadcastReceiver);
        Session.getInstance().refreshConversationPager();
    }

    private void getPermission(String[] permissions, final int cameraType) {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                if (cameraType == RECORD_VIDEO) {
                    Intent intent = new Intent(context, ImageGridActivity.class);
                    startActivityForResult(intent, 0);
                } else if (cameraType == OPEN_CAMERA) {
                    takePhoto();
                } else if (cameraType == OPEN_ALBUM) {
                    takeImg();
                }
            }

            @Override
            public void onRequestPermissionError() {
                switch (cameraType) {
                    case RECORD_VIDEO:
                        showToast(getString(R.string.giving_video_permissions), true);
                        break;
                    case OPEN_CAMERA:
                        showToast(getString(R.string.giving_camera2_permissions), true);
                        break;
                    case OPEN_ALBUM:
                        showToast(getString(R.string.giving_album_permissions), true);
                        break;
                    case RECORD_AUDIO:
                        showToast(getString(R.string.giving_audio_permissions), true);
                        break;
                }
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        permissions//相机权限
                ).request(permissionListener);
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.Action.EMOJI_EDITED_SEND.equals(action)) {
                String path = intent.getStringExtra("imagePath");
                if (path != null && !path.equals("")) {
                    uploadImageAudioFile(path, true, 0);
                }
            } else if (Constants.Action.REFRSH_CHAT_PAGER_DATAS.equals(action)) {
                MSGPAGERNUM = 0;
                List<MessageItem> msgList = initMsgData();
                int position = adapter.getCount();
                adapter.setmMsgList(msgList);
                mMsgListView.stopRefresh();
                mMsgListView.setSelection(adapter.getCount() - position - 1);
            } else if (Constants.Action.REFRSH_CHAT_PAGER.equals(action)) {
                adapter.notifyDataSetChanged();
            } else if (Constants.Action.EXIT_CURRETN_GROUP_REFRESH_DATAS.equals(action)) {
                finish();
            }
        }
    }

//    @Override
//    public void update(Observable observable, Object o) {
//        SessionInfo sin = (SessionInfo) o;
//        if (sin.getAction() == Session.ACTION_PREV_CLOSE_ACTIVITY) {
//            String path = (String) sin.getData();
//            if (path != null && !path.equals("")) {
//                uploadImageAudioFile(path, true, 0);
//            }
//        }
//    }
}
