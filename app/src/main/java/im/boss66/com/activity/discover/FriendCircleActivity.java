package im.boss66.com.activity.discover;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.FriendCircleAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.CircleItem;
import im.boss66.com.entity.CommentConfig;
import im.boss66.com.entity.FriendCircleEntity;
import im.boss66.com.entity.FriendCircleItem;
import im.boss66.com.entity.FriendCircleTestData;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.http.request.DoPraiseRequest;
import im.boss66.com.http.request.FriendCircleRequest;
import im.boss66.com.listener.CircleContractListener;
import im.boss66.com.widget.ActionSheet;

/**
 * 朋友圈
 */
public class FriendCircleActivity extends BaseActivity implements View.OnClickListener,
        CircleContractListener.View, ActionSheet.OnSheetItemClickListener {
    private final static String TAG = FriendCircleActivity.class.getSimpleName();
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
    private List<FriendCircleEntity.FriendCircle> allList;
    private ImageLoader imageLoader;
    private boolean isAddNew = false;
    private Dialog dialog;
    private int sceenW;
    private int feedId;
    private int curPostion;
    private String access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_circle);
        initView();
    }

    private void initView() {
        sceenW = UIUtils.getScreenWidth(context);
        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_set = (ImageView) findViewById(R.id.iv_set);
        rv_friend = (LRecyclerView) findViewById(R.id.rv_friend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_friend.setLayoutManager(layoutManager);
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
        List<CircleItem> list = FriendCircleTestData.createCircleDatas();
        adapter = new FriendCircleAdapter(this);
        AccountEntity sAccount = App.getInstance().getAccount();
        String uid = sAccount.getUser_id();
        access_token = sAccount.getAccess_token();
        adapter.getCurUserId(uid);
        adapter.setCirclePresenter(presenter);
        //adapter.setDatas(list);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);

        View header = LayoutInflater.from(this).inflate(R.layout.item_friend_circle_head,
                (ViewGroup) findViewById(android.R.id.content), false);
        ImageView iv_bg = (ImageView) header.findViewById(R.id.iv_bg);
        ImageView iv_head = (ImageView) header.findViewById(R.id.iv_head);
        TextView tv_name = (TextView) header.findViewById(R.id.tv_name);
        ll_new = (LinearLayout) header.findViewById(R.id.ll_new);
        iv_new = (ImageView) header.findViewById(R.id.iv_new);
        tv_new_count = (TextView) header.findViewById(R.id.tv_new_count);
        v_new = header.findViewById(R.id.v_new);
        iv_bg.setOnClickListener(this);
        iv_head.setOnClickListener(this);
        String headicon = sAccount.getAvatar();
        imageLoader = ImageLoaderUtils.createImageLoader(this);
        imageLoader.displayImage(headicon, iv_head,
                ImageLoaderUtils.getDisplayImageOptions());

        SharedPreferences mPreferences = context.getSharedPreferences("albumCover", MODE_PRIVATE);
        String albumCover = mPreferences.getString("albumCover", "");
        imageLoader.displayImage(albumCover, iv_bg,
                ImageLoaderUtils.getDisplayImageOptions());

        String user_name = sAccount.getUser_name();
        if (TextUtils.isEmpty(user_name)) {
            tv_name.setText("" + uid);
        } else {
            tv_name.setText("" + user_name);
        }
        FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) iv_bg.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = sceenW / 3 * 2;
        ;
        iv_bg.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        mLRecyclerViewAdapter.addHeaderView(header);
        rv_friend.setAdapter(mLRecyclerViewAdapter);
        rv_friend.setLoadMoreEnabled(true);
        rv_friend.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rv_friend.refreshComplete(15);
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
        ToastUtil.showShort(this, "点赞");
    }

    @Override
    public void update2DeleteFavort(int circlePosition, int favortId) {
        ToastUtil.showShort(this, "取消点赞");
    }

    @Override
    public void update2AddComment(int circlePosition, FriendCircleItem addItem) {
        ToastUtil.showShort(this, "评论");
    }

    @Override
    public void update2DeleteComment(int circlePosition, String commentId) {
        ToastUtil.showShort(this, "删除评论");
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        ToastUtil.showShort(this, "评论--键盘--" + visibility + ":" + commentConfig.toString());
    }

    @Override
    public void update2loadData(int loadType, List<CircleItem> datas) {

    }

    private void showActionSheet(int type) {
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
        }
        actionSheet.show();
    }


    @Override
    public void onClick(int which) {
        switch (which) {
            case 1://1小视频 or 2更换相册封面 or 3消息列表
                if (actionSheetType == 1) {

                } else if (actionSheetType == 2) {
                    openActivity(ReplaceAlbumCoverActivity.class);
                } else if (actionSheetType == 3) {
                    openActivity(CircleMessageListActivity.class);
                }
                break;
            case 2://拍照
                Bundle bundle = new Bundle();
                String videoUrl = "http://livecdn.66boss.com/weibo_video/f9fe0be5f76a185e1fc8f62a1c04ac98.mp4";
                bundle.putString("url",videoUrl);
                bundle.putBoolean("isFull",false);
                openActivity(VideoPlayerActivity.class,bundle);
                break;
            case 3://从手机相册选择
                break;
        }
    }

    private void getFriendCircleList() {
        FriendCircleRequest request;
        String curPage, curSize;
        if (page > 0) {
            curPage = String.valueOf((page - 1));
            curSize = String.valueOf(20 * page);
        } else {
            curPage = "0";
            curSize = "20";
        }
        if (isOnRefresh) {
            request = new FriendCircleRequest(TAG, curPage, curSize);
        } else {
            request = new FriendCircleRequest(TAG, curPage, "20");
        }

        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                if (pojo != null) {
                    FriendCircleEntity data = JSON.parseObject(pojo, FriendCircleEntity.class);
                    if (data != null) {
                        if (data.getCode() == 1) {
                            List<FriendCircleEntity.FriendCircle> list = data.getResult();
                            if (list != null && list.size() > 0) {
                                Collections.reverse(list);
                                showData(list);
                            }
                        } else {
                            showToast("获取失败", false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, false);
            }
        });
    }

    private void showData(List<FriendCircleEntity.FriendCircle> list) {
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
                showToast("删除失败", false);
            }
        });
    }

    //获取点赞列表
    private void getServerPraiseList(int feedId) {
        showLoadingDialog();
        String main = HttpUrl.FRIEND_CIRCLE_GET_PRAISE_LIST + "?feed_id=" + feedId;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {

                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                showToast(s, false);
            }
        });
    }

    private void doPraise(int id, int isPraise) {
        DoPraiseRequest request = new DoPraiseRequest(TAG, String.valueOf(id), String.valueOf(isPraise));
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

}
