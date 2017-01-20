package im.boss66.com.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Code;
import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.Utils.Base64Utils;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.SharePreferenceUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.MessageAdapter;
import im.boss66.com.db.MessageDB;
import im.boss66.com.db.RecentDB;
import im.boss66.com.entity.MessageItem;
import im.boss66.com.entity.RecentItem;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.services.ChatServices;
import im.boss66.com.xlistview.MsgListView;

/**
 * Created by Johnny on 2017/1/10.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener, ChatServices.receiveMessageCallback,
        View.OnTouchListener, MsgListView.IXListViewListener {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final int MESSAGE_TYPE_EMOTION = 0x011;
    private static final int MESSAGE_TYPE_IMG = 0x012;
    private static final int MESSAGE_TYPE_VIDEO = 0x013;
    public final static String CHATPHOTO_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator;
    private String[] codes = {"[拥抱]", "[鄙视]", "[困]"};
    public static final int NEW_MESSAGE = 0x001;// 收到消息
    public static int MSGPAGERNUM;
    private LinearLayout ll_emotion, ll_picture;
    private ImageView iv00, iv01, iv02;
    public static String defaulgUserName = "在飞";
    public static String defaulgIcon = "4";
    public static int defaultCount = 0;
    private SharePreferenceUtil mSpUtil;
    private WindowManager.LayoutParams mParams;
    //    private LinearLayout mllFace;// 表情显示的布局
    private boolean isFaceShow = false;
    private InputMethodManager mInputMethodManager;
    //    private EditText mEtMsg;
    private App mApplication;
    private Button mBtnAffix;
    private Button mBtnSend;// 发送消息按钮
    private TextView tvImg, tvVideo, tvPhoto, tvImgs;
    private static MessageAdapter adapter;// 发送消息展示的adapter
    private MsgListView mMsgListView;// 展示消息的
    private MessageDB mMsgDB;// 保存消息的数据库
    private RecentDB mRecentDB;
    private String userid, uid, title;
    private boolean isGroupChat = false;
    private TextView tvBack, tvTitle;

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
            mMsgListView.setSelection(adapter.getCount() + 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
    }

    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userid = bundle.getString("uid1", "");
            uid = bundle.getString("uid2", "");
            title = bundle.getString("title", "");
            isGroupChat = bundle.getBoolean("isgroup", false);
        }
        MSGPAGERNUM = 0;
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mSpUtil = App.getInstance().getSpUtil();


        tvBack = (TextView) findViewById(R.id.tv_back);
        tvTitle = (TextView) findViewById(R.id.tv_chat_title);

        tvBack.setOnClickListener(this);
        tvTitle.setText(title);

        ll_emotion = (LinearLayout) findViewById(R.id.ll_emotion_test);
        ll_picture = (LinearLayout) findViewById(R.id.ll_chatmain_affix);

        tvImg = (TextView) findViewById(R.id.tv_picture);
        tvVideo = (TextView) findViewById(R.id.tv_video);

        tvPhoto = (TextView) findViewById(R.id.tv_take_photo);
        tvImgs = (TextView) findViewById(R.id.tv_take_img);

        tvPhoto.setOnClickListener(this);
        tvImgs.setOnClickListener(this);
        tvImg.setOnClickListener(this);
        tvVideo.setOnClickListener(this);

//        mllFace = (LinearLayout) findViewById(R.id.face_ll);
        iv00 = (ImageView) findViewById(R.id.iv_gif_00);
        iv01 = (ImageView) findViewById(R.id.iv_gif_01);
        iv02 = (ImageView) findViewById(R.id.iv_gif_02);

        mBtnAffix = (Button) findViewById(R.id.btn_chat_affix);
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

        // 消息
        mApplication = App.getInstance();
        mMsgDB = mApplication.getMessageDB();// 发送数据库
        mRecentDB = mApplication.getRecentDB();// 接收消息数据库
        adapter = new MessageAdapter(this, initMsgData());
        mMsgListView = (MsgListView) findViewById(R.id.msg_listView);
        // 触摸ListView隐藏表情和输入法
        mMsgListView.setOnTouchListener(this);
        mMsgListView.setPullLoadEnable(false);
        mMsgListView.setXListViewListener(this);
        mMsgListView.setAdapter(adapter);
        mMsgListView.setSelection(adapter.getCount() - 1);
//        mTitleRightBtn.setOnClickListener(this);
//        mEtMsgOnKeyListener();
    }


    /**
     * 加载消息历史，从数据库中读出
     */
    private List<MessageItem> initMsgData() {
//        List<MessageItem> list = mMsgDB
//                .getMsg(mSpUtil.getUserId(), MSGPAGERNUM);
        List<MessageItem> list = mMsgDB
                .getMsg(userid, MSGPAGERNUM);
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
//    private void mEtMsgOnKeyListener() {
//        mEtMsg.setOnKeyListener(new OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                // TODO Auto-generated method stub
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    if (mParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
//                            || isFaceShow) {
////                        mllFace.setVisibility(View.GONE);
//                        isFaceShow = false;
//                        // imm.showSoftInput(msgEt, 0);
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });

//        mEtMsg.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,
//                                      int count) {
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.length() > 0) {
//                    mBtnSend.setEnabled(true);
//                    mBtnAffix.setVisibility(View.GONE);
//                    mBtnSend.setVisibility(View.VISIBLE);
//                } else {
//                    mBtnSend.setEnabled(false);
//                    mBtnAffix.setVisibility(View.VISIBLE);
//                    mBtnSend.setVisibility(View.GONE);
//                }
//            }
//        });
//    }
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
            case R.id.iv_gif_01:
                sendEmotionMessage(codes[1]);
                break;
            case R.id.iv_gif_02:
                sendEmotionMessage(codes[2]);
                break;
            case R.id.tv_picture:
                UIUtils.hindView(ll_emotion);
                UIUtils.showView(ll_picture);
                break;
            case R.id.tv_take_img:
                takeImg();
                break;
            case R.id.tv_take_photo:
                takePhoto();
                break;
            case R.id.tv_video:
                Intent intent = new Intent(context, ImageGridActivity.class);
                startActivityForResult(intent, 0);
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
//                mInputMethodManager.hideSoftInputFromWindow(
//                        mEtMsg.getWindowToken(), 0);
//                mllFace.setVisibility(View.GONE);
//                isFaceShow = false;
                break;
//            case R.id.msg_et:
//                mInputMethodManager.showSoftInput(mEtMsg, 0);
////                mllFace.setVisibility(View.GONE);
//                isFaceShow = false;
//                break;
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
                false, 0, 0, "" + System.currentTimeMillis());
        adapter.upDateMsg(item);
        mMsgListView.setSelection(adapter.getCount() - 1);
        mMsgDB.saveMsg(userid, item);// 消息保存数据库
//        mEtMsg.setText("");
//        if ("".equals(mSpUtil.getUserId())) {
//            showToast("发送者未登录!", true);
//            return;
//        }
        ChatServices.sendMessage(getString(MESSAGE_TYPE_EMOTION, faceCode));
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
                str = "emotion_" + uid + "_" + code + "_" + tag + "_" + getExtension();
                break;
            case MESSAGE_TYPE_IMG:
                str = "picture_" + uid + "_" + code + "_" + tag + "_" + getExtension();
                break;
            case MESSAGE_TYPE_VIDEO:
                str = "video_" + uid + "_" + code + "_" + tag + "_" + getExtension();
                break;
        }
        return str;
    }

    private String getExtension() {
        JSONObject object = new JSONObject();
        try {
            object.put("sender", userid);
            object.put("senderAvartar", "http://img17.3lian.com/d/file/201701/14/2106887296f7dd4d5b583f0af822967e.jpg");
            object.put("conversation", userid);
            object.put("conversationAvartar", "http://img17.3lian.com/d/file/201701/14/2106887296f7dd4d5b583f0af822967e.jpg");
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
                false, 0, 0, "" + System.currentTimeMillis());
        adapter.upDateMsg(item);
        mMsgListView.setSelection(adapter.getCount() - 1);
        mMsgDB.saveMsg(userid, item);// 消息保存数据库
//        mEtMsg.setText("");
//        if ("".equals(mSpUtil.getUserId())) {
//            showToast("发送者未登录!", true);
//            return;
//        }

        ChatServices.sendMessage(getString(MESSAGE_TYPE_IMG, photoPath));
        // ===保存近期的消息
        RecentItem recentItem = new RecentItem(
                MessageItem.MESSAGE_TYPE_IMG, mSpUtil.getUserId(),
                defaultCount, defaulgUserName, photoPath, 0,
                System.currentTimeMillis(), 0, "" + System.currentTimeMillis());
        mRecentDB.saveRecent(recentItem);
    }

    private void sendVideoMessage(String faceCode) {
        // 发送消息
        MessageItem item = new MessageItem(
                MessageItem.MESSAGE_TYPE_VIDEO, mSpUtil.getNick(),
                System.currentTimeMillis(), faceCode, mSpUtil.getHeadIcon(),
                false, 0, 0, "" + System.currentTimeMillis());
        adapter.upDateMsg(item);
        mMsgListView.setSelection(adapter.getCount() - 1);
        mMsgDB.saveMsg(mSpUtil.getUserId(), item);// 消息保存数据库
//        mEtMsg.setText("");
//        if ("".equals(mSpUtil.getUserId())) {
//            showToast("发送者未登录!", true);
//            return;
//        }
        ChatServices.sendMessage(getString(MESSAGE_TYPE_VIDEO, faceCode));
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
    public void onMessageReceive(MessageItem item) {
        if (item != null) {
            android.os.Message msg = new android.os.Message();
            msg.what = NEW_MESSAGE;
            msg.obj = item;
            handler.sendMessage(msg);
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
        ChatServices.callbacks.add(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatServices.callbacks.remove(this);
    }


    private void uploadImageFile(final String path) {
        String main = HttpUrl.UPLOAD_IMAGE_URL;
//        showLoadingDialog();
        final HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        final com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        try {
            String fileName = FileUtils.getFileNameFromPath(path);
            String compressPath = FileUtils.compressImage(path, CHATPHOTO_PATH + fileName, 30);
            File file = new File(compressPath);
            if (file.exists() && file.length() > 0) {
                params.addBodyParameter("file", file);
            }
            MycsLog.i("info", "AbsolutePath:" + file.getAbsolutePath());
            httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> responseInfo) {
                    Log.i("info", "responseInfo:" + responseInfo.result);
//                    cancelLoadingDialog();
                    sendImageMessage(parsePath(responseInfo.result));
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
                            Log.i("info", "responseInfo:" + responseInfo.result);
                            cancelLoadingDialog();
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
                uploadImageFile(picPath);
            }
        } else if (requestCode == Code.Request.TAKE_PHOTO) {
            uploadImageFile(photoPath);
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


    @Override
    protected void onDestroy() {
        Session.getInstance().refreshConversationPager();
        super.onDestroy();
    }
}
