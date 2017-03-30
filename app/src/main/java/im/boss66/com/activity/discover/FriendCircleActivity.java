package im.boss66.com.activity.discover;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelector;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelectorActivity;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.personage.PersonalPhotoAlbumActivity;
import im.boss66.com.adapter.FriendCircleAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.CircleCommentListEntity;
import im.boss66.com.entity.CircleItem;
import im.boss66.com.entity.CirclePraiseListEntity;
import im.boss66.com.entity.CommentConfig;
import im.boss66.com.entity.FriendCircle;
import im.boss66.com.entity.FriendCircleCommentEntity;
import im.boss66.com.entity.FriendCircleEntity;
import im.boss66.com.entity.FriendCircleItem;
import im.boss66.com.entity.FriendCirclePraiseEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.http.request.CircleCommentCreateRequest;
import im.boss66.com.http.request.CircleCommentDeleteRequest;
import im.boss66.com.http.request.DoPraiseRequest;
import im.boss66.com.listener.CircleContractListener;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.util.Utils;
import im.boss66.com.widget.ActionSheet;

/**
 * 朋友圈
 */
public class FriendCircleActivity extends BaseActivity implements View.OnClickListener,
        CircleContractListener.View, ActionSheet.OnSheetItemClickListener {
    private final static String TAG = FriendCircleActivity.class.getSimpleName();

    private RelativeLayout rl_title;
    private LinearLayout ll_edit_text;
    private EditText et_send;
    private Button bt_send;

    private TextView tv_back;
    private ImageView iv_set;
    private LRecyclerView rv_friend;
    private LinearLayout ll_new;
    private ImageView iv_new;
    private TextView tv_new_count;
    private View v_new;

    private FriendCircleAdapter adapter;
    private CirclePresenter presenter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private int actionSheetType;

    private int SEND_TYPE_PHOTO_TX = 101;
    private int SEND_TYPE_VIDEO_TX = 102;
    private int page = 0;
    private boolean isOnRefresh = false;
    private List<FriendCircle> allList;
    private ImageLoader imageLoader;
    private boolean isAddNew = false;
    private Dialog dialog;
    private int sceenW;
    private int feedId;
    private int curPostion;
    private String access_token;
    private String commentId;
    private String savePath = Environment.getExternalStorageDirectory() + "/IMProject/";
    private final int OPEN_CAMERA = 1;//相机
    private final int OPEN_ALBUM = 2;//相册
    private Uri imageUri;
    private final int RECORD_VIDEO = 3;//视频
    private PermissionListener permissionListener;
    private int cameraType;//1:相机 2：相册 3：视频
    private boolean isReply;
    private String commentFromId, commentPid;
    private String CurUid;
    private final int CHANGE_ALBUM_COVER = 5;//封面
    private ImageView iv_bg;
    private AccountEntity sAccount;
    private long[] mHits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_circle);
        initView();
    }

    private void initView() {
        mHits = new long[2];
        sceenW = UIUtils.getScreenWidth(context);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        ll_edit_text = (LinearLayout) findViewById(R.id.ll_edit_text);
        et_send = (EditText) findViewById(R.id.et_send);
        bt_send = (Button) findViewById(R.id.bt_send);
        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_set = (ImageView) findViewById(R.id.iv_set);
        rv_friend = (LRecyclerView) findViewById(R.id.rv_friend);
        rl_title.setOnClickListener(this);
        ((DefaultItemAnimator) rv_friend.getItemAnimator()).setSupportsChangeAnimations(false);

        rv_friend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (ll_edit_text.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
                return false;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_friend.setLayoutManager(layoutManager);
        bt_send.setOnClickListener(this);
        iv_set.setOnClickListener(this);
        iv_set.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("sendType", "text");
                openActvityForResult(FriendSendNewMsgActivity.class, SEND_TYPE_PHOTO_TX, bundle);
                return true;
            }
        });
        tv_back.setOnClickListener(this);
        presenter = new CirclePresenter(this);
        adapter = new FriendCircleAdapter(this);
        sAccount = App.getInstance().getAccount();
        CurUid = sAccount.getUser_id();
        access_token = sAccount.getAccess_token();
        adapter.getCurUserId(CurUid);
        adapter.setCirclePresenter(presenter);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);

        View header = LayoutInflater.from(this).inflate(R.layout.item_friend_circle_head,
                (ViewGroup) findViewById(android.R.id.content), false);
        iv_bg = (ImageView) header.findViewById(R.id.iv_bg);
        ImageView iv_head = (ImageView) header.findViewById(R.id.iv_head);
        TextView tv_name = (TextView) header.findViewById(R.id.tv_name);
        ll_new = (LinearLayout) header.findViewById(R.id.ll_new);
        iv_new = (ImageView) header.findViewById(R.id.iv_new);
        tv_new_count = (TextView) header.findViewById(R.id.tv_new_count);
        v_new = header.findViewById(R.id.v_new);
        ll_new.setOnClickListener(this);
        iv_bg.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        String headicon = sAccount.getAvatar();
        imageLoader = ImageLoaderUtils.createImageLoader(this);
        imageLoader.displayImage(headicon, iv_head,
                ImageLoaderUtils.getDisplayImageOptions());

        SharedPreferences mPreferences = context.getSharedPreferences("albumCover", MODE_PRIVATE);
        String cover = sAccount.getCover_pic();
        if (TextUtils.isEmpty(cover)) {
            cover = mPreferences.getString("albumCover", "");
        }
        imageLoader.displayImage(cover, iv_bg,
                ImageLoaderUtils.getDisplayImageOptions());

        String user_name = sAccount.getUser_name();
        if (TextUtils.isEmpty(user_name)) {
            tv_name.setText("" + CurUid);
        } else {
            tv_name.setText("" + user_name);
        }
        FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) iv_bg.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = sceenW / 3 * 2;
        ;
        iv_bg.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        mLRecyclerViewAdapter.addHeaderView(header);
        //设置头部加载颜色
        rv_friend.setHeaderViewColor(R.color.red_fuwa, R.color.red_fuwa_alpa_stroke, android.R.color.white);
        rv_friend.setRefreshProgressStyle(ProgressStyle.Pacman); //设置下拉刷新Progress的样式
        rv_friend.setFooterViewHint("拼命加载中", "我是有底线的", "网络不给力啊，点击再试一次吧");
        rv_friend.setAdapter(mLRecyclerViewAdapter);
        rv_friend.setLoadMoreEnabled(true);
        rv_friend.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rv_friend.refreshComplete(20);
                        ToastUtil.showShort(FriendCircleActivity.this, "刷新完成");
                        isOnRefresh = true;
                        isAddNew = false;
                        getFriendCircleList();
                    }
                }, 1000);
            }
        });
        rv_friend.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rv_friend.setNoMore(true);
                        isOnRefresh = false;
                        isAddNew = false;
                        getFriendCircleList();
                    }
                }, 1000);
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String newCount = bundle.getString("newCount");
                String newIcon = bundle.getString("newIcon");
                if (!TextUtils.isEmpty(newCount)) {
                    ll_new.setVisibility(View.VISIBLE);
                    tv_new_count.setText(newCount + "条新消息");
                }
                if (!TextUtils.isEmpty(newIcon)) {
                    ll_new.setVisibility(View.VISIBLE);
                    imageLoader.displayImage(newIcon, iv_new,
                            ImageLoaderUtils.getDisplayImageOptions());
                }
            }
        }
        getFriendCircleList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_set:
                showActionSheet(1);
                break;
            case R.id.iv_bg:
                showActionSheet(2);
                break;
            case R.id.ll_new:
                showActionSheet(3);
                break;
            case R.id.bt_close://删除朋友圈item
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                deleteFriendCircleItem(feedId);
                break;
            case R.id.bt_ok:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;
            case R.id.bt_send://评论
                String content = et_send.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    showToast("评论不能为空", false);
                    return;
                } else {
                    updateEditTextBodyVisible(View.GONE, null);
                    FriendCircle item = (FriendCircle) adapter.getDatas().get(curPostion);
                    if (item != null) {
                        String feed_uid = CurUid;
                        String pid = "0";
                        if (isReply) {
                            pid = commentId;
                            feed_uid = commentFromId;
                        }
                        createComment(content, pid, feed_uid);
                    }
                }
                break;
            case R.id.iv_head:
                openActivity(PersonalPhotoAlbumActivity.class);
                break;
            case R.id.rl_title:
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();//获取手机开机时间
                if (mHits[mHits.length - 1] - mHits[0] < 500) {
                    boolean top = rv_friend.isOnTop();
                    if (!top) {
                        rv_friend.scrollToPosition(0);
                    }
                }
                break;
        }
    }

    @Override
    public void update2DeleteCircle(int circleId, int postion) {
        feedId = circleId;
        curPostion = postion;
        showDialog();
    }

    @Override
    public void update2AddFavorite(int circlePosition, int favortId) {
        //ToastUtil.showShort(this, "点赞");
        curPostion = circlePosition;
        doPraise(favortId, 1);
    }

    @Override
    public void update2DeleteFavort(int circlePosition, int favortId) {
        //ToastUtil.showShort(this, "取消点赞");
        curPostion = circlePosition;
        doPraise(favortId, 0);
    }

    @Override
    public void update2AddComment(int circlePosition, FriendCircleItem addItem) {
        ToastUtil.showShort(this, "评论");
    }

    @Override
    public void update2DeleteComment(int circlePosition, String commentId, boolean isLong) {
        //ToastUtil.showShort(this, "删除评论");
        FriendCircle item = (FriendCircle) adapter.getDatas().get(curPostion);
        if (item != null) {
            feedId = item.getFeed_id();
        }
        this.commentId = commentId;
        if (isLong) {
            deleteComment(commentId);
        } else {
            showActionSheet(4);
        }
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        ll_edit_text.setVisibility(visibility);
        if (commentConfig != null) {
            Log.i("评论--键盘--", visibility + ":" + commentConfig.toString());
            feedId = commentConfig.feedid;
            curPostion = commentConfig.circlePosition;
            commentId = commentConfig.commentId;
            isReply = commentConfig.isReply;
            commentFromId = commentConfig.commentFromId;
            commentPid = commentConfig.pid;
        }
        if (View.VISIBLE == visibility) {
            et_send.requestFocus();
            //弹出键盘
            UIUtils.showSoftInput(et_send, this);
        } else if (View.GONE == visibility) {
            //隐藏键盘
            UIUtils.hideSoftInput(et_send, this);
        }
    }

    @Override
    public void update2loadData(int loadType, List<CircleItem> datas) {

    }

    private void showActionSheet(int type) {
        updateEditTextBodyVisible(View.GONE, null);
        actionSheetType = type;
        ActionSheet actionSheet = new ActionSheet(FriendCircleActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        if (type == 1) {//发朋友圈
            actionSheet.addSheetItem(getString(R.string.small_video), ActionSheet.SheetItemColor.Black,
                    FriendCircleActivity.this)
                    .addSheetItem(getString(R.string.take_photos), ActionSheet.SheetItemColor.Black,
                            FriendCircleActivity.this)
                    .addSheetItem(getString(R.string.from_the_mobile_phone_photo_album_choice), ActionSheet.SheetItemColor.Black,
                            FriendCircleActivity.this);
        } else if (type == 2) {//更换相册封面
            actionSheet.addSheetItem(getString(R.string.replace_the_album_cover), ActionSheet.SheetItemColor.Black, FriendCircleActivity.this);
        } else if (type == 3) {//消息列表
            actionSheet.addSheetItem(getString(R.string.message_list), ActionSheet.SheetItemColor.Black, FriendCircleActivity.this);
        } else if (type == 4) {//删除评论
            actionSheet.setTitle("删除我的评论");
            actionSheet.addSheetItem("删除", ActionSheet.SheetItemColor.Red, FriendCircleActivity.this);
        }
        actionSheet.show();
    }


    @Override
    public void onClick(int which) {
        switch (which) {
            case 1://1小视频 or 2更换相册封面 or 3消息列表 or 4 删除评论
                if (actionSheetType == 1) {
                    cameraType = RECORD_VIDEO;
                    getPermission();
                } else if (actionSheetType == 2) {
                    openActvityForResult(ReplaceAlbumCoverActivity.class, CHANGE_ALBUM_COVER);
                } else if (actionSheetType == 3) {
                    ll_new.setVisibility(View.GONE);
                    openActivity(CircleMessageListActivity.class);
                } else if (actionSheetType == 4) {
                    deleteComment(commentId);
                }
                break;
            case 2://拍照
                cameraType = OPEN_CAMERA;
                getPermission();
                break;
            case 3://从手机相册选择
                cameraType = OPEN_ALBUM;
                getPermission();
                break;
        }
    }

    private void getFriendCircleList() {
        String curPage, curSize;
        String url = HttpUrl.FRIEND_CIRCLE_LIST;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        if (isOnRefresh) {
            if (page > 0) {
                curPage = String.valueOf((page - 1));
                curSize = String.valueOf(20 * page);
            } else {
                curPage = "0";
                curSize = "20";
            }
            url = url + "?page=" + curPage + "&size=" + curSize;
        } else {
            url = url + "?page=" + page + "&size=" + 20;
        }
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (result != null) {
                    FriendCircleEntity data = JSON.parseObject(result, FriendCircleEntity.class);
                    if (data != null) {
                        if (data.getCode() == 1) {
                            if (data.getStatus() == 401) {
                                Intent intent = new Intent();
                                intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                                App.getInstance().sendBroadcast(intent);
                            } else {
                                List<FriendCircle> list = data.getResult();
                                if (list != null && list.size() > 0) {
                                    showData(list);
                                }
                            }
                        } else {
                            showToast(data.getMessage(), false);
                        }
                    } else {
                        showToast("没有更多数据了", false);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });
    }

    private void showData(List<FriendCircle> list) {
        if (allList == null) {
            allList = new ArrayList<>();
        }
        if (!isOnRefresh) {
            page++;
            allList.addAll(list);
            adapter.setDatas(allList);
        } else if (isOnRefresh || isAddNew) {
            adapter.setDatas(list);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_TYPE_PHOTO_TX && resultCode == RESULT_OK) {
            isOnRefresh = true;
            isAddNew = true;
            getFriendCircleList();
        } else if (requestCode == OPEN_CAMERA && resultCode == RESULT_OK) {//打开相机
            if (imageUri != null) {
                String path = Utils.getPath(this, imageUri);
                Bundle bundle = new Bundle();
                bundle.putString("sendType", "photo");
                bundle.putInt("type", OPEN_CAMERA);
                bundle.putString("img", path);
                openActvityForResult(FriendSendNewMsgActivity.class, SEND_TYPE_PHOTO_TX, bundle);
            }
        } else if (requestCode == OPEN_ALBUM && resultCode == RESULT_OK && data != null) { //打开相册
            ArrayList<String> selectPicList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            Bundle bundle = new Bundle();
            bundle.putInt("type", OPEN_ALBUM);
            bundle.putString("sendType", "photo");
            bundle.putStringArrayList("imglist", selectPicList);
            openActvityForResult(FriendSendNewMsgActivity.class, SEND_TYPE_PHOTO_TX, bundle);
        } else if (requestCode == RECORD_VIDEO && resultCode == RESULT_OK) {
            // 录制视频完成
            try {
                AssetFileDescriptor videoAsset = getContentResolver()
                        .openAssetFileDescriptor(data.getData(), "r");
                FileInputStream fis = videoAsset.createInputStream();
                File tmpFile = new File(
                        Environment.getExternalStorageDirectory(),
                        "recordvideo.mp4");
                FileOutputStream fos = new FileOutputStream(tmpFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
                fis.close();
                fos.close();
                // 文件写完之后删除/sdcard/dcim/CAMERA/XXX.MP4

                deleteDefaultFile(data.getData());
                String videoPath = tmpFile.getAbsolutePath();

                Bundle bundle = new Bundle();
                bundle.putInt("type", RECORD_VIDEO);
                bundle.putString("sendType", "video");
                bundle.putString("videoPath", videoPath);
                openActvityForResult(FriendSendNewMsgActivity.class, SEND_TYPE_PHOTO_TX, bundle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CHANGE_ALBUM_COVER && resultCode == RESULT_OK) {
            SharedPreferences mPreferences = context.getSharedPreferences("albumCover", MODE_PRIVATE);
            String albumCover = mPreferences.getString("albumCover", "");
            imageLoader.displayImage(albumCover, iv_bg,
                    ImageLoaderUtils.getDisplayImageOptions());
        }
    }

    private void showDialog() {
        if (dialog == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_sure_phone_num, null);
            Button bt_close = (Button) view.findViewById(R.id.bt_close);
            Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
            TextView tv_dia_num = (TextView) view.findViewById(R.id.tv_dia_num);
            TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
            TextView tv_dia_title = (TextView) view.findViewById(R.id.tv_dia_title);
            tv_dia_title.setText(getString(R.string.sure_delete));
            tv_content.setVisibility(View.GONE);
            tv_dia_num.setVisibility(View.GONE);
            bt_close.setOnClickListener(this);
            bt_close.setText("删除");
            bt_ok.setText("取消");
            bt_ok.setOnClickListener(this);
            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setContentView(view);
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = sceenW / 3 * 2;
            dialogWindow.setAttributes(lp);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (ll_edit_text != null && ll_edit_text.getVisibility() == View.VISIBLE) {
                updateEditTextBodyVisible(View.GONE, null);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //删除朋友圈item
    private void deleteFriendCircleItem(int id) {
        showLoadingDialog();
        String main = HttpUrl.FRIEND_CIRCLE_DELETE + "?feed_id=" + id;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj != null) {
                            int code = obj.getInt("code");
                            String msg = obj.getString("message");
                            int status = obj.getInt("status");
                            if (status == 401) {
                                Intent intent = new Intent();
                                intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                                App.getInstance().sendBroadcast(intent);
                            } else {
                                if (code == 1) {
                                    adapter.remove(curPostion);
                                    showToast("删除成功", false);
                                } else {
                                    showToast(msg, false);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                showToast("删除失败", false);
            }
        });
    }

    //获取点赞列表
    private void getServerPraiseList(int feedId, final int isPraise) {
        //showLoadingDialog();
        String main = HttpUrl.FRIEND_CIRCLE_GET_PRAISE_LIST + "?feed_id=" + feedId;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //cancelLoadingDialog();
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    CirclePraiseListEntity entity = JSON.parseObject(result, CirclePraiseListEntity.class);
                    if (entity != null) {
                        if (entity.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            List<FriendCirclePraiseEntity> list = entity.getResult();
                            if (list != null) {
                                FriendCircle item = (FriendCircle) adapter.getDatas().get(curPostion);
                                if (item != null) {
                                    item.setPraise_list(list);
                                    item.setIs_praise(isPraise);
                                    adapter.notifyItemChanged(curPostion);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //cancelLoadingDialog();
                showToast(s, false);
            }
        });
    }

    //点or取消赞
    private void doPraise(final int id, final int isPraise) {
        DoPraiseRequest request = new DoPraiseRequest(TAG, String.valueOf(id), String.valueOf(isPraise));
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                Log.i("dopraise", pojo);
                if (!TextUtils.isEmpty(pojo)) {
                    try {
                        JSONObject obj = new JSONObject(pojo);
                        if (obj != null) {
                            int code = obj.getInt("code");
                            String msg = obj.getString("message");
                            if (code == 1) {
                                getServerPraiseList(id, isPraise);
                            } else {
                                showToast(msg, false);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, false);
            }
        });
    }

    //发表评论
    private void createComment(String content, String pid, String uid_to) {
        if (bt_send != null) {
            bt_send.setText("");
        }
        CircleCommentCreateRequest request = new CircleCommentCreateRequest(TAG, String.valueOf(feedId), content, pid, uid_to);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                if (!TextUtils.isEmpty(pojo)) {
                    try {
                        JSONObject obj = new JSONObject(pojo);
                        if (obj != null) {
                            int code = obj.getInt("code");
                            String msg = obj.getString("message");
                            if (code == 1) {
                                getServerCommentList(feedId);
                            } else {
                                showToast(msg, false);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, false);
            }
        });
    }

    //获取评论列表
    private void getServerCommentList(int feedId) {
        String main = HttpUrl.FRIEND_CIRCLE_GET_COMMENT_LIST + "?feed_id=" + feedId;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    CircleCommentListEntity entity = JSON.parseObject(result, CircleCommentListEntity.class);
                    if (entity != null) {
                        if (entity.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            List<FriendCircleCommentEntity> list = entity.getResult();
                            if (list != null) {
                                FriendCircle item = (FriendCircle) adapter.getDatas().get(curPostion);
                                if (item != null) {
                                    item.setComment_list(list);
                                    adapter.notifyItemChanged(curPostion);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(s, false);
            }
        });
    }

    private void deleteComment(String comm_id) {
        CircleCommentDeleteRequest request = new CircleCommentDeleteRequest(TAG, comm_id);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                if (!TextUtils.isEmpty(pojo)) {
                    try {
                        JSONObject obj = new JSONObject(pojo);
                        if (obj != null) {
                            int code = obj.getInt("code");
                            String msg = obj.getString("message");
                            if (code == 1) {
                                getServerCommentList(feedId);
                            } else {
                                showToast(msg, false);
                            }
                        }
                    } catch (JSONException e) {
                        showToast(e.getMessage(), false);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, false);
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    // 删除在/sdcard/dcim/Camera/默认生成的文件
    private void deleteDefaultFile(Uri uri) {
        String fileName = null;
        if (uri != null) {
            // content
            Log.d("Scheme", uri.getScheme());
            if (uri.getScheme().equals("content")) {
                Cursor cursor = getContentResolver().query(uri, null,
                        null, null, null);
                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        int columnIndex = cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                        fileName = cursor.getString(columnIndex);
                        //获取缩略图id
                        int id = cursor.getInt(cursor
                                .getColumnIndex(MediaStore.Video.VideoColumns._ID));
                        //获取缩略图
//                    Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
//                            getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND,
//                            null);

                        if (!fileName.startsWith("/mnt")) {
                            fileName = "/mnt/" + fileName;
                        }
                        Log.d("fileName", fileName);
                    }
                    cursor.close();
                }
            }
        }
        // 删除文件
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
            Log.d("delete", "删除成功");
        }
    }

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                if (cameraType == RECORD_VIDEO) {
                    Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    //mIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    mIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                    mIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 20 * 1024 * 1024L);
                    startActivityForResult(mIntent, RECORD_VIDEO);
                    //openActivity(RecordVideoActivity.class);
                } else if (cameraType == OPEN_CAMERA) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String imageName = getNowTime() + ".jpg";
                    // 指定调用相机拍照后照片的储存路径
                    File dir = new File(savePath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, imageName);
                    imageUri = Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, OPEN_CAMERA);
                    }
                } else if (cameraType == OPEN_ALBUM) {
                    MultiImageSelector.create(context).
                            showCamera(false).
                            count(9)
                            .multi() // 多选模式, 默认模式;
                            .start(FriendCircleActivity.this, OPEN_ALBUM);
                }
            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(FriendCircleActivity.this, getString(R.string.giving_camera_permissions));
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_GROUP_CAMERA //相机权限
                ).request(permissionListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
    }

}
