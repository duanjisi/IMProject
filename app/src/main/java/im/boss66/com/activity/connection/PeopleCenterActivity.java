package im.boss66.com.activity.connection;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.activity.discover.CircleMessageListActivity;
import im.boss66.com.activity.discover.CirclePresenter;
import im.boss66.com.adapter.CommunityListAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.entity.CircleCommentListEntity;
import im.boss66.com.entity.CircleItem;
import im.boss66.com.entity.CircleNewest;
import im.boss66.com.entity.CircleNewestEntity;
import im.boss66.com.entity.CirclePraiseListEntity;
import im.boss66.com.entity.CommentConfig;
import im.boss66.com.entity.FriendCircle;
import im.boss66.com.entity.FriendCircleCommentEntity;
import im.boss66.com.entity.FriendCircleEntity;
import im.boss66.com.entity.FriendCircleItem;
import im.boss66.com.entity.FriendCirclePraiseEntity;
import im.boss66.com.entity.FriendState;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.http.request.AddFriendRequest;
import im.boss66.com.http.request.CommunityCreateCommentRequest;
import im.boss66.com.http.request.FriendShipRequest;
import im.boss66.com.listener.CircleContractListener;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.widget.ActionSheet;
import im.boss66.com.widget.RoundImageView;

/**
 * 人脉中心
 * Created by liw on 2017/2/21.
 */
public class PeopleCenterActivity extends ABaseActivity implements View.OnClickListener, CircleContractListener.View, ActionSheet.OnSheetItemClickListener {
    private final static String TAG = PeopleCenterActivity.class.getSimpleName();
    private LRecyclerView rcv_news;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;

    private String access_token;

    private CommunityListAdapter adapter;
    private int feedId;
    private int curPostion;
    private String commentId;
    private Dialog dialog;
    private int sceenW;
    private LinearLayout ll_edit_text;
    private EditText et_send;
    private Button bt_send;
    private boolean isReply;
    private String commentFromId, commentPid;
    private CirclePresenter presenter;
    private String CuiUid, name, address, avatar;
    private RoundImageView user_head;
    private TextView tv_name;
    private TextView tv_address;
    private Button bt_add;
    private LinearLayout rl_msg;
    private ImageView iv_msg, user_bg_icon;
    private ImageLoader imageLoader;
    private boolean isCurrentUser;
    private String curLoginUid;
    private boolean isChange = false;

    private RelativeLayout rl_edit_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_center);
        initViews();
    }

    protected void initViews() {
        imageLoader = ImageLoaderUtils.createImageLoader(this);
        sceenW = UIUtils.getScreenWidth(context);
        AccountEntity sAccount = App.getInstance().getAccount();
        access_token = sAccount.getAccess_token();
        curLoginUid = sAccount.getUser_id();
        ll_edit_text = (LinearLayout) findViewById(R.id.ll_edit_text);

        et_send = (EditText) findViewById(R.id.et_send);
        bt_send = (Button) findViewById(R.id.bt_send);
        bt_send.setOnClickListener(this);

        //rcv 设置
        rcv_news = (LRecyclerView) findViewById(R.id.rcv_news);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rcv_news.setLayoutManager(layoutManager);
        ((DefaultItemAnimator) rcv_news.getItemAnimator()).setSupportsChangeAnimations(false);
        rcv_news.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (ll_edit_text.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
                return false;
            }
        });
        // 头部view
        View header = LayoutInflater.from(this).inflate(R.layout.headview_people_center,
                (ViewGroup) findViewById(android.R.id.content), false);
        rl_edit_info = (RelativeLayout) header.findViewById(R.id.rl_edit_info);
        user_bg_icon = (ImageView) header.findViewById(R.id.user_bg_icon);
        TextView tv_back = (TextView) header.findViewById(R.id.tv_back);
        RelativeLayout rl_head = (RelativeLayout) header.findViewById(R.id.rl_head);
        user_head = (RoundImageView) header.findViewById(R.id.user_head);
        tv_name = (TextView) header.findViewById(R.id.tv_name);
        tv_address = (TextView) header.findViewById(R.id.tv_address);
        bt_add = (Button) header.findViewById(R.id.bt_add);
        rl_msg = (LinearLayout) header.findViewById(R.id.rl_msg);
        iv_msg = (ImageView) header.findViewById(R.id.iv_msg);
        bt_add.setOnClickListener(this);
        rl_msg.setOnClickListener(this);
        rl_edit_info.setOnClickListener(this);

        FrameLayout.LayoutParams headParam = (FrameLayout.LayoutParams) rl_head.getLayoutParams();
        headParam.height = sceenW / 3 * 2;
        rl_head.setLayoutParams(headParam);
        RelativeLayout.LayoutParams headIconParam = (RelativeLayout.LayoutParams) user_head.getLayoutParams();
        headIconParam.height = sceenW / 10*3;
        headIconParam.width = sceenW / 10*3;
        user_head.setLayoutParams(headIconParam);

        RelativeLayout.LayoutParams headMsgParam = (RelativeLayout.LayoutParams) rl_msg.getLayoutParams();
        headMsgParam.width = sceenW / 5;
        rl_msg.setLayoutParams(headMsgParam);
        tv_back.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            CuiUid = intent.getStringExtra("uid");
            if (!TextUtils.isEmpty(CuiUid)) {
                isCurrentUser = false;
                name = intent.getStringExtra("name");
                address = intent.getStringExtra("address");
                avatar = intent.getStringExtra("avatar");
                bt_add.setVisibility(View.VISIBLE);
                rl_msg.setVisibility(View.GONE);
                requestFriendShip();
                getNoreadMsg();
            } else {
                isCurrentUser = true;
            }
        } else {
            isCurrentUser = true;
        }
        if (!TextUtils.isEmpty(CuiUid)) {
            String uid = sAccount.getUser_id();
            if (uid.equals(CuiUid)) {
                bt_add.setVisibility(View.GONE);
                rl_msg.setVisibility(View.VISIBLE);
            }
        }
        if (isCurrentUser) {
            bt_add.setVisibility(View.GONE);
            rl_msg.setVisibility(View.VISIBLE);
            CuiUid = sAccount.getUser_id();
            avatar = sAccount.getAvatar();
            name = sAccount.getUser_name();
        }
        if (!TextUtils.isEmpty(name)) {
            tv_name.setText(name);
        } else {
            tv_name.setText(CuiUid);
        }
        if (!TextUtils.isEmpty(address)) {
            tv_address.setText(address);
        }
        imageLoader.displayImage(avatar, user_head, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                user_bg_icon.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
        // 回调接口和adapter设置
        presenter = new CirclePresenter(this);
        adapter = new CommunityListAdapter(this);
        adapter.getCurUserId(curLoginUid);
        adapter.setCirclePresenter(presenter);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerViewAdapter.addHeaderView(header);
        //rcv设置adapter以及刷新
        rcv_news.setAdapter(mLRecyclerViewAdapter);
        rcv_news.setLoadMoreEnabled(true);
        rcv_news.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rcv_news.refreshComplete(20);
                        ToastUtil.showShort(PeopleCenterActivity.this, "刷新完成");
                        getCommunityList();
                    }
                }, 1000);
            }
        });
        getCommunityList();
    }

    private void addFriendRequest(String userid) {
        showLoadingDialog();
        AddFriendRequest request = new AddFriendRequest(TAG, userid, "通过一下呗");
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                cancelLoadingDialog();
                showToast("已发送好友请求!", true);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
                cancelLoadingDialog();
            }
        });
    }

    private void requestFriendShip() {
        if (!CuiUid.equals("")) {
            FriendShipRequest request = new FriendShipRequest(TAG, CuiUid);
            request.send(new BaseDataRequest.RequestCallback<FriendState>() {
                @Override
                public void onSuccess(FriendState pojo) {
                    String isFriend = pojo.getIs_friend();
                    if ("1".equals(isFriend)) {
                        bt_add.setText("已添加");
                    } else {
                        bt_add.setText("+ 添加");
                    }
                }

                @Override
                public void onFailure(String msg) {
                    showToast(msg, true);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                if (isChange) {
                    setResult(RESULT_OK);
                }
                finish();
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
                        String feed_uid = CuiUid;
                        String pid = "0";
                        if (isReply) {
                            pid = commentId;
                            feed_uid = commentFromId;
                        }
                        createComment(content, pid, feed_uid);
                    }
                }
                break;
            case R.id.bt_add://添加好友
                addFriendRequest(CuiUid);
                break;
            case R.id.rl_msg://我的消息
                Bundle bundle = new Bundle();
                bundle.putString("classType", "SchoolHometownActivity");
                openActivity(CircleMessageListActivity.class, bundle);
                break;
            case R.id.rl_edit_info: // 修改资料
                Intent intent = new Intent(this, PersonalDataActivity.class);
                Intent intent1 = intent.putExtra("isEdit", true);
                startActivity(intent1);

                break;
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

    private void getCommunityList() {
        showLoadingDialog();
        String url = HttpUrl.COMMUNITY_GET_USER_DETAIL + "?user_id=" + CuiUid;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (result != null) {
                    FriendCircleEntity data = JSON.parseObject(result, FriendCircleEntity.class);
                    if (data != null) {
                        if (data.getCode() == 1) {
                            List<FriendCircle> list = data.getResult();
                            if (list != null && list.size() > 0) {
                                Collections.reverse(list);
                                showData(list);
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
                cancelLoadingDialog();
                showToast(e.getMessage(), false);
            }
        });
    }

    private void showData(List<FriendCircle> list) {
        adapter.setDatas(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(int which) {
        deleteComment(commentId);
    }

    //获取点赞列表
    private void getServerPraiseList(int feedId, final int isPraise) {
        //showLoadingDialog();
        String main = HttpUrl.GET_COMMUNITY_PRAISE_LIST + "?feed_id=" + feedId;
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

            @Override
            public void onFailure(HttpException e, String s) {
                //cancelLoadingDialog();
                showToast(s, false);
            }
        });
    }

    //删除朋友圈item
    private void deleteFriendCircleItem(int id) {
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
                            if (code == 1) {
                                adapter.remove(curPostion);
                                showToast("删除成功", false);
                                isChange = true;
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
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                showToast("删除失败", false);
            }
        });
    }

    //点or取消赞
    private void doPraise(final int id, final int isPraise) {
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
                        if (code == 1) {
                            isChange = true;
                            getServerPraiseList(id, isPraise);
                        } else {
                            showToast(msg, false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });
    }

    //发表评论
    private void createComment(String content, String pid, String uid_to) {
        et_send.setText("");
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
                                isChange = true;
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
        String main = HttpUrl.GET_COMMUNITY_COMMENT_LIST + "?feed_id=" + feedId;
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

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(s, false);
            }
        });
    }

    private void deleteComment(String comm_id) {
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
                        int code = data.getCode();
                        String msg = data.getMessage();
                        if (code == 1) {
                            getServerCommentList(feedId);
                        } else {
                            showToast(msg, false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });
    }

    private void showActionSheet() {
        updateEditTextBodyVisible(View.GONE, null);
        ActionSheet actionSheet = new ActionSheet(PeopleCenterActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.setTitle("删除我的评论");
        actionSheet.addSheetItem("删除", ActionSheet.SheetItemColor.Red, PeopleCenterActivity.this);
        actionSheet.show();
    }

    @Override
    public void update2DeleteCircle(int circleId, int postion) {
        feedId = circleId;
        curPostion = postion;
        showDialog();
    }

    @Override
    public void update2AddFavorite(int circlePosition, int favortId) {
        curPostion = circlePosition;
        doPraise(favortId, 1);
    }

    @Override
    public void update2DeleteFavort(int circlePosition, int favortId) {
        curPostion = circlePosition;
        doPraise(favortId, 0);
    }

    @Override
    public void update2AddComment(int circlePosition, FriendCircleItem addItem) {

    }

    @Override
    public void update2DeleteComment(int circlePosition, String commentId, boolean isLong) {
        FriendCircle item = (FriendCircle) adapter.getDatas().get(curPostion);
        if (item != null) {
            feedId = item.getFeed_id();
        }
        this.commentId = commentId;
        if (isLong) {
            deleteComment(commentId);
        } else {
            showActionSheet();
        }
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        ll_edit_text.setVisibility(visibility);
        if (commentConfig != null) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (ll_edit_text != null && ll_edit_text.getVisibility() == View.VISIBLE) {
                updateEditTextBodyVisible(View.GONE, null);
                return true;
            }
            setResult(RESULT_OK);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getNoreadMsg() {
        String url = HttpUrl.COMMUNITY_GET_NEW_MSG;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    BaseResult res = BaseResult.parse(result);
                    if (res != null) {
                        if (res.getCode() == 1) {
                            iv_msg.setImageResource(R.drawable.news_have);
                        } else {
                            iv_msg.setImageResource(R.drawable.news_no);
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

}
