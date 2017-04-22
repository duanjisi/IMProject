package im.boss66.com.activity.personage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.PersonalCollectAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.entity.CollectEntity;
import im.boss66.com.entity.PersonalCollect;
import im.boss66.com.http.HttpUrl;

/**
 * 收藏
 */
public class PersonalCollectActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_title, tv_right;
    private LRecyclerView rv_collect;
    private String access_token;
    private int page;
    private PersonalCollectAdapter adapter;
    private List<CollectEntity> allList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_collect);
        initView();
    }

    private void initView() {
        allList = new ArrayList<>();
        rv_collect = (LRecyclerView) findViewById(R.id.rv_collect);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        tv_title.setText("收藏");
        ((DefaultItemAnimator) rv_collect.getItemAnimator()).setSupportsChangeAnimations(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_collect.setLayoutManager(layoutManager);
        rv_collect.setPullRefreshEnabled(false);
        AccountEntity sAccoun = App.getInstance().getAccount();
        access_token = sAccoun.getAccess_token();
        adapter = new PersonalCollectAdapter(this);
        adapter.setDatas(allList);
        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        //设置头部加载颜色
        rv_collect.setHeaderViewColor(R.color.red_fuwa, R.color.red_fuwa_alpa_stroke, android.R.color.white);
        rv_collect.setRefreshProgressStyle(ProgressStyle.Pacman);
        rv_collect.setFooterViewHint("拼命加载中", "我是有底线的", "网络不给力啊，点击再试一次吧");
        rv_collect.setAdapter(mLRecyclerViewAdapter);
        rv_collect.setLoadMoreEnabled(true);
        rv_collect.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rv_collect.setNoMore(true);
                        getServerCollect();
                    }
                }, 500);
            }
        });
        rv_collect.addOnItemTouchListener(new PersonalCollectAdapter.RecyclerItemClickListener(this,
                new PersonalCollectAdapter.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        int size = adapter.getItemCount();
                        if (position < size && position >= 0) {
                            CollectEntity item = (CollectEntity) adapter.getDatas().get(position);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("collect", item);
                            openActivity(CollectDetailActivity.class, bundle);
                        }
                    }

                    @Override
                    public void onLongClick(View view, int posotion) {

                    }
                }));
        getServerCollect();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }

    private void getServerCollect() {
        showLoadingDialog();
        String url = HttpUrl.GET_PERSONAL_COLLECT;
        HttpUtils httpUtils = new HttpUtils(30 * 1000);
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        url = url + "?page=" + page + "&size=" + 20;
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                Log.i("onSuccess:", "" + result);
                if (result != null) {
                    PersonalCollect personalCollect = JSON.parseObject(result, PersonalCollect.class);
                    if (personalCollect != null) {
                        if (personalCollect.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            if (personalCollect.getCode() == 1) {
                                List<CollectEntity> list = personalCollect.getResult();
                                if (list != null && list.size() > 0) {
                                    showData(list);
                                }
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

    private void showData(List<CollectEntity> list) {
        if (list.size() == 20) {
            page++;
        } else {
            rv_collect.setNoMore(true);
        }
        allList.addAll(list);
        adapter.setDatas(allList);
        adapter.notifyDataSetChanged();
    }

    private void deleteServerData(String id) {
        showLoadingDialog();
        String url = HttpUrl.DELETE_PERSONAL_COLLECT;
        HttpUtils httpUtils = new HttpUtils(30 * 1000);
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", access_token);
        url = url + "?fid=" + id;
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                Log.i("onSuccess:", "" + result);
                if (result != null) {
                    BaseResult personalCollect = JSON.parseObject(result, BaseResult.class);
                    if (personalCollect != null) {
                        if (personalCollect.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            if (personalCollect.getCode() == 1) {
                                showToast("刪除成功", false);
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

    private void goLogin() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_LOGOUT_RESETING);
        App.getInstance().sendBroadcast(intent);
    }

}
