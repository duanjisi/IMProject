package im.boss66.com.activity.discover;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.TimeUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.player.VideoPlayerNewActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.ActionItem;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.entity.CircleCommentListEntity;
import im.boss66.com.entity.FriendCircle;
import im.boss66.com.entity.FriendCircleCommentEntity;
import im.boss66.com.entity.FriendCirclePraiseEntity;
import im.boss66.com.entity.PhotoAlbumDetailEntity;
import im.boss66.com.entity.PhotoInfo;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.http.request.CircleCommentCreateRequest;
import im.boss66.com.http.request.CircleCommentDeleteRequest;
import im.boss66.com.http.request.CommunityCreateCommentRequest;
import im.boss66.com.http.request.DoPraiseRequest;
import im.boss66.com.widget.ActionSheet;
import im.boss66.com.widget.CommentListView;
import im.boss66.com.widget.MultiImageView;
import im.boss66.com.widget.PraiseListView;
import im.boss66.com.widget.SnsPopupWindow;

/**
 * 相册详情
 */
public class PhotoAlbumDetailActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {
    private final static String TAG = FriendCircleActivity.class.getSimpleName();
    private TextView tv_back, tv_title;
    private ImageLoader imageLoader;
    //图文字
    private LinearLayout ll_midden;
    private RelativeLayout rl_text;
    private ImageView iv_head, iv_sns;
    private TextView tv_name, tv_content, tv_time, tv_delete;
    private PraiseListView praiseListView;
    private CommentListView commentView;
    private EditText et_send;
    private Button bt_send;
    private MultiImageView multiImagView;
    private View lin_dig;

    //视频
    private FrameLayout fl_video;
    private ImageView iv_video_bg, iv_video_play;

    private int sceenW;
    private List<FriendCirclePraiseEntity> praiseList;
    private List<FriendCircleCommentEntity> commentList;
    private String userName = "";
    private SnsPopupWindow snsPopupWindow;
    private Dialog dialog;
    private String access_token;
    private int feedId, isPrase;
    private FriendCircle friendCircle;
    private String videoUrl, videoImgUrl, userId, commonId;
    private boolean isReply = false;
    private String commentFromId, commentPid;
    private List<PhotoInfo> files;
    private String classType;
    private ScrollView sv_content;
    private LinearLayout ll_p;
    private boolean isShowkeyboad = false;
    private String replyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album_detail);
        initView();
        initData();
    }

    private void initData() {
        AccountEntity sAccount = App.getInstance().getAccount();
        access_token = sAccount.getAccess_token();
        userId = sAccount.getUser_id();
        userName = sAccount.getUser_name();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                classType = bundle.getString("classType");
                feedId = bundle.getInt("feedId");
                if (!TextUtils.isEmpty(classType) && "PhotoAlbumLookPicActivity".equals(classType)) {
                    friendCircle = (FriendCircle) bundle.getSerializable("data");
                    if (friendCircle != null) {
                        feedId = friendCircle.getFeed_id();
                        int feedType = friendCircle.getFeed_type();
                        if (feedType == 1 || feedType == 2) {
                            showSigleTxData(friendCircle);
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(classType) && "SchoolHometownActivity".equals(classType)) {
                        getCommunityDetail();
                    } else {
                        getAlbumDetail();
                    }
                }
            }
        }
    }

    private void initView() {
        sceenW = UIUtils.getScreenWidth(this);
        imageLoader = ImageLoaderUtils.createImageLoader(this);
        ll_p = (LinearLayout) findViewById(R.id.ll_p);
        sv_content = (ScrollView) findViewById(R.id.sv_content);
        //视频
        fl_video = (FrameLayout) findViewById(R.id.fl_video);
        iv_video_play = (ImageView) findViewById(R.id.iv_video_play);
        iv_video_bg = (ImageView) findViewById(R.id.iv_video_bg);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iv_video_bg.getLayoutParams();
        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) iv_video_play.getLayoutParams();
        params.width = sceenW / 3;
        params.height = (int) (sceenW / 3 * 1.2);
        iv_video_bg.setLayoutParams(params);
        params1.width = sceenW / 7;
        params1.height = sceenW / 7;
        iv_video_play.setLayoutParams(params1);
        fl_video.setOnClickListener(this);
        //图文字
        ll_midden = (LinearLayout) findViewById(R.id.ll_midden);
        lin_dig = findViewById(R.id.lin_dig);
        multiImagView = (MultiImageView) findViewById(R.id.multiImagView);
        rl_text = (RelativeLayout) findViewById(R.id.rl_text);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        iv_sns = (ImageView) findViewById(R.id.iv_sns);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        praiseListView = (PraiseListView) findViewById(R.id.praiseListView);
        commentView = (CommentListView) findViewById(R.id.commentList);
        et_send = (EditText) findViewById(R.id.et_send);
        bt_send = (Button) findViewById(R.id.bt_send);
        tv_delete.setOnClickListener(this);
        iv_sns.setOnClickListener(this);
        bt_send.setOnClickListener(this);

        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_back.setOnClickListener(this);
        tv_title.setText("详情");
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) iv_head.getLayoutParams();
        params2.width = sceenW / 8;
        params2.height = sceenW / 8;
        iv_head.setLayoutParams(params2);
        multiImagView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());

                List<String> photoUrls = new ArrayList<String>();
                for (PhotoInfo photoInfo : files) {
                    photoUrls.add(photoInfo.file_url);
                }
                ImagePagerActivity.startImagePagerActivity(context, photoUrls, position, imageSize, false);
            }

            @Override
            public void onItemLongClick(View view, int postion) {

            }
        });

        ll_p.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = ll_p.getRootView().getHeight() - ll_p.getHeight();
                if (heightDiff > 100) {
                    //高度变小100像素则认为键盘弹出
                    isShowkeyboad = true;
                    sv_content.fullScroll(ScrollView.FOCUS_DOWN);
                } else {
                    isShowkeyboad = false;
                }
            }
        });
        sv_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isShowkeyboad) {
                    et_send.setHint("评论");
                    UIUtils.hideSoftInput(et_send, context);
                    return true;
                }
                return false;
            }
        });
    }

    private void showSigleTxData(FriendCircle item) {
        String curDetailUid = item.getFeed_uid();
        if (!TextUtils.isEmpty(curDetailUid) && !TextUtils.isEmpty(userId)) {
            if (userId.equals(curDetailUid)) {
                tv_delete.setVisibility(View.VISIBLE);
            } else {
                tv_delete.setVisibility(View.GONE);
            }
        }
        int prase = item.getIs_praise();
        if (prase == 0) {
            isPrase = 1;
        } else {
            isPrase = 0;
        }
        int feedType = item.getFeed_type();
        if (feedType == 1 || feedType == 2) {
            files = item.getFiles();
            if (files != null && files.size() > 0) {
                rl_text.setVisibility(View.VISIBLE);
                if (feedType == 1) {
                    multiImagView.setVisibility(View.VISIBLE);
                    String file = files.get(0).file_url;
                    if (TextUtils.isEmpty(file)) {
                        multiImagView.setVisibility(View.GONE);
                    } else if (!file.contains(".jpg") && !file.contains(".png") && !file.contains(".jpeg")) {
                        multiImagView.setVisibility(View.GONE);
                    } else {
                        multiImagView.setSceenW(sceenW);
                        multiImagView.setList(files);
                    }
                } else {
                    fl_video.setVisibility(View.VISIBLE);
                    videoUrl = files.get(0).file_url;
                    videoImgUrl = files.get(0).file_thumb;
                    Glide.with(this).load(videoImgUrl).into(iv_video_bg);
                }
            }
        } else if (feedType == 3) {

        }
        String feedName;
        if (!TextUtils.isEmpty(classType) && "SchoolHometownActivity".equals(classType)) {
            feedName = item.getUser_name();
        } else {
            feedName = item.getFeed_username();
        }
        tv_name.setText("" + feedName);
        String head = item.getFeed_avatar();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_head.getLayoutParams();
        params.width = sceenW / 8;
        params.height = sceenW / 8;
        iv_head.setLayoutParams(params);
        imageLoader.displayImage(head, iv_head,
                ImageLoaderUtils.getDisplayImageOptions());
        tv_content.setText("" + item.getContent());

        praiseList = item.getPraise_list();
        if (praiseList != null && praiseList.size() > 0) {
            ll_midden.setVisibility(View.VISIBLE);
            praiseListView.setVisibility(View.VISIBLE);
            praiseListView.setDatas(praiseList);
            praiseListView.setOnItemClickListener(new PraiseListView.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    FriendCirclePraiseEntity item = praiseList.get(position);
                }
            });
        } else {
            praiseListView.setVisibility(View.GONE);
        }

        commentList = item.getComment_list();
        if (commentList != null && commentList.size() > 0) {
            ll_midden.setVisibility(View.VISIBLE);
            commentView.setVisibility(View.VISIBLE);
            commentView.getCurLoginUserId(userId);
            commentView.setDatas(commentList);
            commentView.setOnItemClickListener(new CommentListView.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    FriendCircleCommentEntity item = commentList.get(position);
                    if (item != null) {
                        replyName = item.getUid_from_name();
                        commonId = item.getComm_id();
                        commentFromId = item.getUid_from();
                        commentPid = item.getPid();
                        if (!TextUtils.isEmpty(commentFromId) && !TextUtils.isEmpty(userId)) {
                            if (commentFromId.equals(userId)) {
                                isReply = false;
                                showActionSheet();
                            } else {
                                isReply = true;
                                et_send.setHint("回复" + replyName);
                                UIUtils.showSoftInput(et_send, PhotoAlbumDetailActivity.this);
                            }
                        }
                    }
                }
            });
        } else {
            lin_dig.setVisibility(View.GONE);
            commentView.setVisibility(View.GONE);
        }

        if (commentList != null && praiseList != null) {
            if (commentList.size() == 0 && praiseList.size() == 0) {
                ll_midden.setVisibility(View.GONE);
            } else if (commentList.size() > 0 && praiseList.size() > 0) {
                ll_midden.setVisibility(View.VISIBLE);
                lin_dig.setVisibility(View.VISIBLE);
            }
        } else if (commentList == null && praiseList == null) {
            ll_midden.setVisibility(View.GONE);
        } else {
            ll_midden.setVisibility(View.VISIBLE);
        }
        String createTime = item.getAdd_time();
        if (!TextUtils.isEmpty(createTime)) {
            try {
                long time = Long.parseLong(createTime);
                String timestr = TimeUtil.getChatTime(time);
                tv_time.setText("" + timestr);
            } catch (Exception e) {
                tv_time.setText(createTime);
            }
        }
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(PhotoAlbumDetailActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.setTitle("删除我的评论");
        actionSheet.addSheetItem("删除", ActionSheet.SheetItemColor.Red, PhotoAlbumDetailActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_delete:
                showDeleteDialog();
                break;
            case R.id.iv_sns:
                showSnsPopupWindow(view);
                break;
            case R.id.bt_send:
                String content = et_send.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    showToast("评论不能为空", false);
                    return;
                } else {
                    UIUtils.hideSoftInput(et_send, this);
                    String pid = "0", feed_uid = "0";
                    if (isReply) {
                        pid = commentPid;
                        feed_uid = commentFromId;
                    } else {
                        pid = "0";
                        feed_uid = "0";
                    }
                    et_send.setText("");
                    if (!TextUtils.isEmpty(classType) && "SchoolHometownActivity".equals(classType)) {
                        createCommunityComment(content, pid, feed_uid);
                    } else {
                        createComment(content, pid, feed_uid);
                    }
                }
                break;
            case R.id.bt_close://删除朋友圈item
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (!TextUtils.isEmpty(classType) && "SchoolHometownActivity".equals(classType)) {
                    deleteCommunityItem(feedId);
                } else {
                    deleteFriendCircleItem(feedId);
                }
                break;
            case R.id.bt_ok:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;
            case R.id.fl_video:
                Log.i("info", "=============videoUrl:" + videoUrl);
                Intent intent = new Intent(this, VideoPlayerNewActivity.class);
                intent.putExtra("videoPath", videoUrl);
                intent.putExtra("imgurl", videoImgUrl);
                intent.putExtra("isDelete", true);
                startActivity(intent);
                break;
        }
    }

    private void showDeleteDialog() {
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

    private void showSnsPopupWindow(View view) {
        if (snsPopupWindow == null) {
            snsPopupWindow = new SnsPopupWindow(this);
            snsPopupWindow.setmItemClickListener(new PopupItemClickListener());
        }
        //判断是否已点赞
        if (isPrase == 0) {
            snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
        } else {
            snsPopupWindow.getmActionItems().get(0).mTitle = "点赞";
        }
        snsPopupWindow.showPopupWindow(view);
    }

    @Override
    public void onClick(int which) {
        if (!TextUtils.isEmpty(classType) && "SchoolHometownActivity".equals(classType)) {
            deleteCommunityComment(commonId);
        } else {
            deleteComment(commonId);
        }
    }

    private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener {
        private long mLasttime = 0;

        public PopupItemClickListener() {
        }

        @Override
        public void onItemClick(ActionItem actionitem, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    if (System.currentTimeMillis() - mLasttime < 700)//防止快速点击操作
                        return;
                    mLasttime = System.currentTimeMillis();
                    if (!TextUtils.isEmpty(classType) && "SchoolHometownActivity".equals(classType)) {
                        doCommunityPraise(feedId, isPrase);
                    } else {
                        doPraise();
                    }
                    break;
                case 1://发布评论
                    et_send.setHint("评论");
                    et_send.requestFocus();
                    //弹出键盘
                    UIUtils.showSoftInput(et_send, PhotoAlbumDetailActivity.this);
                    isReply = false;
                    break;
                default:
                    break;
            }
        }
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
                                goLogin();
                            } else {
                                if (code == 1) {
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
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    cancelLoadingDialog();
                    showToast("删除失败", false);
                }
            }
        });
    }

    //点or取消赞
    private void doPraise() {
        DoPraiseRequest request = new DoPraiseRequest(TAG, String.valueOf(feedId), String.valueOf(isPrase));
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
                                if (isPrase == 0) {
                                    isPrase = 1;
                                    editPraseList(false);
                                } else {
                                    isPrase = 0;
                                    editPraseList(true);
                                }
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

    private void editPraseList(boolean isAdd) {
        if (praiseList != null && friendCircle != null) {
            if (isAdd) {
                FriendCirclePraiseEntity item = new FriendCirclePraiseEntity();
                item.setUser_id(userId);
                if (!TextUtils.isEmpty(userName)) {
                    item.setUser_name(userName);
                } else {
                    item.setUser_name(userId);
                }
                praiseList.add(item);
                friendCircle.setPraise_list(praiseList);
            } else {
                for (int i = 0; i < praiseList.size(); i++) {
                    FriendCirclePraiseEntity item = praiseList.get(i);
                    if (item.getUser_id().equals(userId)) {
                        praiseList.remove(i);
                        friendCircle.setPraise_list(praiseList);
                    }
                }
            }
            if (praiseList.size() > 0) {
                ll_midden.setVisibility(View.VISIBLE);
                lin_dig.setVisibility(View.VISIBLE);
                praiseListView.setVisibility(View.VISIBLE);
                if (commentView.getVisibility() == View.GONE) {
                    lin_dig.setVisibility(View.GONE);
                }
            } else {
                if (commentView.getVisibility() == View.GONE) {
                    ll_midden.setVisibility(View.GONE);
                }
                lin_dig.setVisibility(View.GONE);
                praiseListView.setVisibility(View.GONE);
            }
            praiseListView.setDatas(praiseList);
        }
    }

    //删除评论
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

    //发表评论
    private void createComment(String content, String pid, String uid_to) {
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

    //获取某个相册详情
    private void getAlbumDetail() {
        showLoadingDialog();
        String main = HttpUrl.GET_FRIEND_CIRCLE_DETAIL + "?feed_id=" + feedId;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    PhotoAlbumDetailEntity data = JSON.parseObject(result, PhotoAlbumDetailEntity.class);
                    if (data != null) {
                        if (data.getStatus() == 401) {
                            goLogin();
                        } else {
                            String msg = data.getMessage();
                            int code = data.getCode();
                            if (code == 1) {
                                friendCircle = data.getResult();
                                if (friendCircle != null) {
                                    feedId = friendCircle.getFeed_id();
                                    int feedType = friendCircle.getFeed_type();
                                    if (feedType == 1) {
                                        rl_text.setVisibility(View.VISIBLE);
                                        showSigleTxData(friendCircle);
                                    }
                                }
                            } else {
                                showToast(msg, false);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    cancelLoadingDialog();
                    showToast("获取失败", false);
                }
            }
        });
    }

    //获取评论列表
    private void getServerCommentList(int feedId) {
        String main = HttpUrl.FRIEND_CIRCLE_GET_COMMENT_LIST + "?feed_id=" + feedId + "&page=0&size=1024";
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
                            goLogin();
                        } else {
                            List<FriendCircleCommentEntity> list = entity.getResult();
                            if (list != null && list.size() > 0) {
                                friendCircle.setComment_list(list);
                                showSigleTxData(friendCircle);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    showToast(s, false);
                }
            }
        });
    }

    /* 帖子详情 */
    private void getCommunityDetail() {
        showLoadingDialog();
        String main = HttpUrl.COMMUNITY_GET_DETAIL + "?feed_id=" + feedId;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    BaseResult result1 = JSON.parseObject(result, BaseResult.class);
                    if (result1 != null) {
                        int code = result1.getCode();
                        String errorMsg = result1.getMessage();
                        if (code == 1) {
                            PhotoAlbumDetailEntity data = JSON.parseObject(result, PhotoAlbumDetailEntity.class);
                            if (data != null) {
                                if (data.getStatus() == 401) {
                                    goLogin();
                                } else {
                                    String msg = data.getMessage();
                                    int code1 = data.getCode();
                                    if (code1 == 1) {
                                        friendCircle = data.getResult();
                                        if (friendCircle != null) {
                                            feedId = friendCircle.getFeed_id();
                                            int feedType = friendCircle.getFeed_type();
                                            if (feedType == 1) {
                                                rl_text.setVisibility(View.VISIBLE);
                                                showSigleTxData(friendCircle);
                                            }
                                        }
                                    } else {
                                        showToast(msg, false);
                                    }
                                }
                            }
                        } else {
                            showToast(errorMsg, false);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    cancelLoadingDialog();
                    showToast(s, false);
                }
            }
        });
    }

    //删除帖子item
    private void deleteCommunityItem(int id) {
        showLoadingDialog();
        String main = HttpUrl.DELETE_COMMUNITY + "?feed_id=" + id;
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
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    cancelLoadingDialog();
                    showToast("删除失败", false);
                }
            }
        });
    }

    //点or取消赞
    private void doCommunityPraise(final int id, final int isPraise) {
        String url = HttpUrl.COMMUNITY_DO_PRAISE + "?feed_id=" + id + "&type=" + isPraise;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    BaseResult data = JSON.parseObject(result, BaseResult.class);
                    if (data != null) {
                        int code = data.getCode();
                        String msg = data.getMessage();
                        if (data.getStatus() == 401) {
                            goLogin();
                        } else {
                            if (code == 1) {
                                if (isPrase == 0) {
                                    isPrase = 1;
                                    editPraseList(false);
                                } else {
                                    isPrase = 0;
                                    editPraseList(true);
                                }
                            } else {
                                showToast(msg, false);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    showToast(s, false);
                }
            }
        });
    }

    //发表评论
    private void createCommunityComment(String content, String pid, String uid_to) {
        CommunityCreateCommentRequest request = new CommunityCreateCommentRequest(TAG, String.valueOf(feedId), content, pid, uid_to);
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
                                getCommunityCommentList(feedId);
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
    private void getCommunityCommentList(int feedId) {
        String main = HttpUrl.GET_COMMUNITY_COMMENT_LIST + "?feed_id=" + feedId + "&page=0&size=1024";
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
                            if (list != null && list.size() > 0) {
                                friendCircle.setComment_list(list);
                                showSigleTxData(friendCircle);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    cancelLoadingDialog();
                    showToast(s, false);
                }
            }
        });
    }

    private void deleteCommunityComment(String comm_id) {
        String url = HttpUrl.COMMUNITY_DELETE_COMMENT + "?comm_id=" + comm_id;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    BaseResult data = JSON.parseObject(result, BaseResult.class);
                    if (data != null) {
                        if (data.getStatus() == 401) {
                            goLogin();
                        } else {
                            int code = data.getCode();
                            String msg = data.getMessage();
                            if (code == 1) {
                                getCommunityCommentList(feedId);
                            } else {
                                showToast(msg, false);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    goLogin();
                } else {
                    showToast(s, false);
                }
            }
        });
    }

    private void goLogin() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGOUT_RESETING);
        App.getInstance().sendBroadcast(intent);
    }
}
