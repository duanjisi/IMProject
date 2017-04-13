package im.boss66.com.activity.connection;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

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
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelector;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelectorActivity;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.activity.discover.CircleMessageListActivity;
import im.boss66.com.activity.discover.CirclePresenter;
import im.boss66.com.activity.discover.FriendSendNewMsgActivity;
import im.boss66.com.activity.discover.ReplaceAlbumCoverActivity;
import im.boss66.com.adapter.CommunityListAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseResult;
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
import im.boss66.com.http.request.CommunityCreateCommentRequest;
import im.boss66.com.listener.CircleContractListener;
import im.boss66.com.listener.CommunityMsgListener;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.util.Utils;
import im.boss66.com.widget.ActionSheet;
import im.boss66.com.widget.dialog.MyNewsPop;

/**
 * 学校和家乡类
 * Created by liw on 2017/2/22.
 */
public class SchoolHometownActivity extends ABaseActivity implements View.OnClickListener,
        CircleContractListener.View, ActionSheet.OnSheetItemClickListener {

    private final static String TAG = SchoolHometownActivity.class.getSimpleName();

    private MyNewsPop myNewsPop;
    private RelativeLayout rl_top_bar;
    private boolean isSchool; // 是否是学校
    private LRecyclerView rcv_news;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private CirclePresenter presenter;

    private int SEND_TYPE_PHOTO_TX = 101;
    private String title; //标题
    private int school_id; //学校id
    private int hometown_id; //家乡id
    private String access_token;
    private int page = 0;
    private boolean isOnRefresh = false, isAddNew = false;
    private List<FriendCircle> allList;
    private CommunityListAdapter adapter;
    private int feedId;
    private int curPostion, actionSheetType;
    private int cameraType;//1:相机 2：相册 3：视频
    private final int OPEN_CAMERA = 1;//相机
    private final int OPEN_ALBUM = 2;//相册
    private Uri imageUri;
    private final int RECORD_VIDEO = 3;//视频
    private PermissionListener permissionListener;
    private String commentId;
    private String savePath = Environment.getExternalStorageDirectory() + "/IMProject/";
    private Dialog dialog;
    private int sceenW;
    private LinearLayout ll_edit_text;
    private EditText et_send;
    private Button bt_send;
    private boolean isReply;
    private String commentFromId, commentPid;
    private CommunityMsgListener communityMsgListener;
    private String CuiUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_hometown);
        initArgument();
        initViews();
    }

    private void initArgument() {
        sceenW = UIUtils.getScreenWidth(context);
        Intent intent = getIntent();
        if (intent != null) {
            isSchool = intent.getBooleanExtra("isSchool", false);
            title = intent.getStringExtra("name");
            if (isSchool) {
                school_id = intent.getIntExtra("school_id", -1);
            } else {
                hometown_id = intent.getIntExtra("hometown_id", -1);
            }
        }
    }

    private void initViews() {
        ll_edit_text = (LinearLayout) findViewById(R.id.ll_edit_text);
        et_send = (EditText) findViewById(R.id.et_send);
        bt_send = (Button) findViewById(R.id.bt_send);
        bt_send.setOnClickListener(this);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText(title);

        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);
        iv_headright_view = (ImageView) findViewById(R.id.iv_headright_view);

        iv_headright_view.setVisibility(View.VISIBLE);
        iv_headright_view.setOnClickListener(this);
        iv_headright_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("sendType", "text");
                bundle.putString("feedType", "1");
                if (isSchool) {
                    bundle.putString("id_value", "1");
                    bundle.putInt("id_value_ext", school_id);
                } else {
                    bundle.putString("id_value", "2");
                    bundle.putInt("id_value_ext", hometown_id);
                }
                bundle.putString("classType", "SchoolHometownActivity");
                openActvityForResult(FriendSendNewMsgActivity.class, SEND_TYPE_PHOTO_TX, bundle);
                return true;
            }
        });
        rl_top_bar = (RelativeLayout) findViewById(R.id.rl_top_bar);

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
        View header = LayoutInflater.from(this).inflate(R.layout.item_people_news_head,
                (ViewGroup) findViewById(android.R.id.content), false);
        ImageView iv_bg = (ImageView) header.findViewById(R.id.iv_bg);
        TextView tv_introduce = (TextView) header.findViewById(R.id.tv_introduce);
        TextView tv_famous_person = (TextView) header.findViewById(R.id.tv_famous_person);
        TextView tv_club = (TextView) header.findViewById(R.id.tv_club);

        if (!isSchool) {
            tv_club.setText("商会");
        }
        TextView tv_news = (TextView) header.findViewById(R.id.tv_news);
        tv_introduce.setOnClickListener(this);
        tv_famous_person.setOnClickListener(this);
        tv_club.setOnClickListener(this);
        tv_news.setOnClickListener(this);

        // 回调接口和adapter设置
        presenter = new CirclePresenter(this);
        adapter = new CommunityListAdapter(this);
        adapter.getClassType("SchoolHometownActivity");
        AccountEntity sAccount = App.getInstance().getAccount();
        access_token = sAccount.getAccess_token();
        CuiUid = sAccount.getUser_id();
        adapter.getCurUserId(CuiUid);
        adapter.setCirclePresenter(presenter);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        //头部view设置，加入到adapter中
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) iv_bg.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = UIUtils.getScreenWidth(context) / 3 * 2;
        ;
        iv_bg.setLayoutParams(linearParams); //使设置好的布局参数应用到控件


        mLRecyclerViewAdapter.addHeaderView(header);
        rcv_news.setFooterViewHint("拼命加载中", "我是有底线的", "网络不给力啊，点击再试一次吧");
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
                        isOnRefresh = true;
                        isAddNew = false;
                        getCommunityList();
                    }
                }, 1000);
            }
        });
        rcv_news.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rcv_news.setNoMore(true);
                        isOnRefresh = false;
                        isAddNew = false;
                        getCommunityList();
                    }
                }, 1000);
            }
        });
        getCommunityList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_headright_view:
                if (myNewsPop == null) {
                    showPop(view);
                } else {
                    if (!myNewsPop.isShowing()) {
                        showPop(view);
                    }
                }
                //改为朋友圈的发消息先。
                break;

            case R.id.tv_introduce: // 简介
                Intent intent = new Intent(this, IntroduceActivity.class);
                intent.putExtra("title", title);
                if (isSchool) {
                    intent.putExtra("school_id", school_id);
                } else {
                    intent.putExtra("hometown_id", hometown_id);
                }
                startActivity(intent);
                break;
            case R.id.tv_famous_person: // 名人
                Intent intent1 = new Intent(this, FamousPersonActivity.class);
                if (isSchool) {
                    intent1.putExtra("school_id", school_id);
                } else {
                    intent1.putExtra("hometown_id", hometown_id);
                }
                startActivity(intent1);

                break;
            case R.id.tv_club: // 社团 或 商会
                Intent intent2 = new Intent(this, ClubActivity.class);
                if (isSchool) {
                    intent2.putExtra("school_id", school_id);
                } else {
                    intent2.putExtra("hometown_id", hometown_id);
                }
                startActivity(intent2);
                break;
            case R.id.tv_news: // 动态
                Intent intent3 = new Intent(this, NewsActivity.class);
                intent3.putExtra("name", title);
                if (isSchool) {
                    intent3.putExtra("school_id", school_id);
                } else {
                    intent3.putExtra("hometown_id", hometown_id);
                }
                startActivity(intent3);

                break;
            case R.id.tv_headlift_view:
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
        }

    }

    private void showPop(View parent) {
        if (communityMsgListener == null) {
            communityMsgListener = new CommunityMsgListener() {
                @Override
                public void goSendMsg() {
                    showActionSheet(1);
                }

                @Override
                public void goMyMsg() {
                    Bundle bundle = new Bundle();
                    bundle.putString("classType", "SchoolHometownActivity");
                    openActivity(CircleMessageListActivity.class, bundle);
                }
            };
        }
        myNewsPop = new MyNewsPop(this, communityMsgListener);
        myNewsPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        myNewsPop.setAnimationStyle(R.style.PopupTitleBarAnim1);
        myNewsPop.showAsDropDown(parent);
    }

    private void getCommunityList() {
        showLoadingDialog();
        String curPage, curSize;
        String url = HttpUrl.GET_COMMUNITY_LIST;
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
        if (isSchool) {
            url = url + "&id_value=" + 1 + "&id_value_ext=" + school_id;
        } else {
            url = url + "&id_value=" + 2 + "&id_value_ext=" + hometown_id;
        }
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (result != null) {
                    FriendCircleEntity data = JSON.parseObject(result, FriendCircleEntity.class);
                    if (data != null) {
                        if (data.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                            return;
                        }
                        if (data.getCode() == 1) {
                            List<FriendCircle> list = data.getResult();
                            if (list != null && list.size() > 0) {
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
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
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
                            if (obj.getInt("status") == 401) {
                                Intent intent = new Intent();
                                intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                                App.getInstance().sendBroadcast(intent);
                                return;
                            }
                            int code = obj.getInt("code");
                            String msg = obj.getString("message");
                            if (code == 1) {
                                adapter.remove(curPostion);
                                showToast("删除成功", false);
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
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast("删除失败", false);
                }
            }
        });
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
                        if (entity.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                            return;
                        }
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
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
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
                        if (data.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                            return;
                        }
                        int code = data.getCode();
                        String msg = data.getMessage();
                        if (code == 1) {
                            getServerPraiseList(id, isPraise);
                        } else {
                            showToast(msg, false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
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
                            return;
                        }
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
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
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
                        if (data.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                            return;
                        }
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
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
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

    private void showActionSheet(int type) {
        updateEditTextBodyVisible(View.GONE, null);
        actionSheetType = type;
        ActionSheet actionSheet = new ActionSheet(SchoolHometownActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        if (type == 1) {//发朋友圈
            actionSheet.addSheetItem(getString(R.string.small_video), ActionSheet.SheetItemColor.Black,
                    SchoolHometownActivity.this)
                    .addSheetItem(getString(R.string.take_photos), ActionSheet.SheetItemColor.Black,
                            SchoolHometownActivity.this)
                    .addSheetItem(getString(R.string.from_the_mobile_phone_photo_album_choice), ActionSheet.SheetItemColor.Black,
                            SchoolHometownActivity.this);
        } else if (type == 4) {//删除评论
            actionSheet.setTitle("删除我的评论");
            actionSheet.addSheetItem("删除", ActionSheet.SheetItemColor.Red, SchoolHometownActivity.this);
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
                    openActivity(ReplaceAlbumCoverActivity.class);
                } else if (actionSheetType == 3) {
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
                    if (Build.VERSION.SDK_INT < 24) {
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
                    } else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String imageName = getNowTime() + ".jpg";
                        File file = new File(savePath, imageName);
                        imageUri = FileProvider.getUriForFile(SchoolHometownActivity.this, "im.boss66.com.fileProvider", file);//这里进行替换uri的获得方式
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//这里加入flag
                        startActivityForResult(intent, OPEN_CAMERA);
                    }
                } else if (cameraType == OPEN_ALBUM) {
                    MultiImageSelector.create(context).
                            showCamera(false).
                            count(9)
                            .multi() // 多选模式, 默认模式;
                            .start(SchoolHometownActivity.this, OPEN_ALBUM);
                }
            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(SchoolHometownActivity.this, getString(R.string.giving_camera_permissions));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEND_TYPE_PHOTO_TX && resultCode == RESULT_OK) {
            isOnRefresh = true;
            isAddNew = true;
            getCommunityList();
        } else if (requestCode == OPEN_CAMERA && resultCode == RESULT_OK) {//打开相机
            if (imageUri != null) {
                String path = null;
                if (Build.VERSION.SDK_INT < 24) {
                    path = Utils.getPath(this, imageUri);
                } else {
                    path = imageUri.toString();
                }
                Bundle bundle = new Bundle();
                bundle.putString("sendType", "photo");
                bundle.putString("feedType", "1");
                bundle.putInt("type", OPEN_CAMERA);
                bundle.putString("img", path);
                bundle.putString("classType", "SchoolHometownActivity");
                if (isSchool) {
                    bundle.putString("id_value", "1");
                    bundle.putInt("id_value_ext", school_id);
                } else {
                    bundle.putString("id_value", "2");
                    bundle.putInt("id_value_ext", hometown_id);
                }
                openActvityForResult(FriendSendNewMsgActivity.class, SEND_TYPE_PHOTO_TX, bundle);
            }
        } else if (requestCode == OPEN_ALBUM && resultCode == RESULT_OK && data != null) { //打开相册
            ArrayList<String> selectPicList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            Bundle bundle = new Bundle();
            bundle.putInt("type", OPEN_ALBUM);
            bundle.putString("sendType", "photo");
            bundle.putStringArrayList("imglist", selectPicList);
            bundle.putString("classType", "SchoolHometownActivity");
            bundle.putString("feedType", "1");
            if (isSchool) {
                bundle.putString("id_value", "1");
                bundle.putInt("id_value_ext", school_id);
            } else {
                bundle.putString("id_value", "2");
                bundle.putInt("id_value_ext", hometown_id);
            }
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
                bundle.putString("classType", "SchoolHometownActivity");
                bundle.putString("feedType", "2");
                if (isSchool) {
                    bundle.putString("id_value", "1");
                    bundle.putInt("id_value_ext", school_id);
                } else {
                    bundle.putString("id_value", "2");
                    bundle.putInt("id_value_ext", hometown_id);
                }
                openActvityForResult(FriendSendNewMsgActivity.class, SEND_TYPE_PHOTO_TX, bundle);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 101 && resultCode == RESULT_OK) {
            isOnRefresh = true;
            isAddNew = false;
            getCommunityList();
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
            showActionSheet(4);
        }
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        ll_edit_text.setVisibility(visibility);
        String uidFromName = "";
        if (commentConfig != null) {
            Log.i("评论--键盘--", visibility + ":" + commentConfig.toString());
            feedId = commentConfig.feedid;
            curPostion = commentConfig.circlePosition;
            commentId = commentConfig.commentId;
            isReply = commentConfig.isReply;
            commentFromId = commentConfig.commentFromId;
            commentPid = commentConfig.pid;
            uidFromName = commentConfig.uid_to_name;
        }
        if (View.VISIBLE == visibility) {
            if (isReply && !TextUtils.isEmpty(uidFromName)) {
                et_send.setHint("回复" + uidFromName);
            } else {
                et_send.setHint("评论");
            }
            et_send.requestFocus();
            //弹出键盘
            UIUtils.showSoftInput(et_send, this);
        } else if (View.GONE == visibility) {
            et_send.setText("");
            //隐藏键盘
            UIUtils.hideSoftInput(et_send, this);
        }
    }

    @Override
    public void update2loadData(int loadType, List<CircleItem> datas) {

    }
}
