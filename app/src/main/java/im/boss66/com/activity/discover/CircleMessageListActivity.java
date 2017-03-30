package im.boss66.com.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.CircleMessageListAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.CircleMsgListEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.widget.ActionSheet;

/**
 * 朋友圈新消息列表
 */
public class CircleMessageListActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private TextView tv_back, tv_title, tv_right;
    private LRecyclerView rv_content;
    private String access_token;
    private int page = 0;
    private CircleMessageListAdapter adapter;
    private List<CircleMsgListEntity.CircleMsgItem> allList;
    private String classType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_message_list);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                classType = bundle.getString("classType");
            }
        }
        AccountEntity sAccount = App.getInstance().getAccount();
        access_token = sAccount.getAccess_token();
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);
        rv_content = (LRecyclerView) findViewById(R.id.rv_content);
        tv_title.setText(getString(R.string.main_homepager));
        tv_right.setText(getString(R.string.vacum_up));
        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        tv_right.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_content.setLayoutManager(layoutManager);
        rv_content.setPullRefreshEnabled(false);
        adapter = new CircleMessageListAdapter(this);
        LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        rv_content.setAdapter(lRecyclerViewAdapter);
        rv_content.addOnItemTouchListener(new CircleMessageListAdapter.RecyclerItemClickListener(this,
                new CircleMessageListAdapter.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        int size = adapter.getItemCount();
                        if (position >= size && (position - 1) >= 0) {
                            position = position - 1;
                        }
                        CircleMsgListEntity.CircleMsgItem item = (CircleMsgListEntity.CircleMsgItem) adapter.getDatas().get(position);
                        Bundle bundle = new Bundle();
                        bundle.putInt("feedId", item.getFeed_id());
                        bundle.putString("classType", classType);
                        openActivity(PhotoAlbumDetailActivity.class, bundle);
                    }

                    @Override
                    public void onLongClick(View view, int posotion) {
                    }
                }));
        rv_content.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!TextUtils.isEmpty(classType) && "SchoolHometownActivity".equals(classType)) {
                    getcommunityServerData();
                } else {
                    getServerData();
                }
            }
        });
        if (!TextUtils.isEmpty(classType) && "SchoolHometownActivity".equals(classType)) {
            getcommunityServerData();
        } else {
            getServerData();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right:
                showActionSheet();
                break;
        }
    }

    private void getServerData() {
        showLoadingDialog();
        String url = HttpUrl.GET_CIRCLE_MSG_LIST;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        url = url + "?page=" + page + "&size=" + 20;
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (result != null) {
                    CircleMsgListEntity data = JSON.parseObject(result, CircleMsgListEntity.class);
                    if (data != null) {
                        if (data.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            List<CircleMsgListEntity.CircleMsgItem> list = data.getResult();
                            if (list != null && list.size() > 0) {
                                showData(list);
                            }
                        }
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

    private void showData(List<CircleMsgListEntity.CircleMsgItem> list) {
        if (allList == null) {
            allList = new ArrayList<>();
        }
        int size = list.size();
        if (size == 20) {
            page++;
        } else {
            rv_content.setNoMore(true);
        }
        allList.addAll(list);
        adapter.setDatas(allList);
    }

    private void clearMsg() {
        showLoadingDialog();
        String url = HttpUrl.CLEAR_CIRCLE_MSG_LIST;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
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
                            if (code == 1) {
                                if (status == 401) {
                                    Intent intent = new Intent();
                                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                                    App.getInstance().sendBroadcast(intent);
                                } else {
                                    allList.clear();
                                    adapter.setDatas(allList);
                                    tv_right.setEnabled(false);
                                }
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
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
                cancelLoadingDialog();
            }
        });
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(CircleMessageListActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem("清空消息列表", ActionSheet.SheetItemColor.Red, CircleMessageListActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        if (!TextUtils.isEmpty(classType) && "SchoolHometownActivity".equals(classType)) {
            clearcommunityMsg();
        } else {
            clearMsg();
        }
    }

    private void getcommunityServerData() {
        showLoadingDialog();
        String url = HttpUrl.COMMUNITY_GET_MY_MSG;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        url = url + "?page=" + page + "&size=" + 20;
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (result != null) {
                    CircleMsgListEntity data = JSON.parseObject(result, CircleMsgListEntity.class);
                    if (data != null) {
                        if (data.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            List<CircleMsgListEntity.CircleMsgItem> list = data.getResult();
                            if (list != null && list.size() > 0) {
                                showData(list);
                            }
                        }
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

    private void clearcommunityMsg() {
        showLoadingDialog();
        String url = HttpUrl.COMMUNITY_CLEAR_MY_MSG;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
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
                            if (code == 1) {
                                if (status == 401) {
                                    Intent intent = new Intent();
                                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                                    App.getInstance().sendBroadcast(intent);
                                } else {
                                    allList.clear();
                                    adapter.setDatas(allList);
                                    tv_right.setEnabled(false);
                                }
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
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
                cancelLoadingDialog();
            }
        });
    }

}
