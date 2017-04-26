package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;
import android.view.View;
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


import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.adapter.ClanCofclistAdapter;
import im.boss66.com.adapter.PersonalPhotoAlbumAdapter;
import im.boss66.com.entity.ClanCofcListEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/4/22.
 */
public class ClanClubListActivity extends ABaseActivity implements View.OnClickListener {

    private String type;
    private LRecyclerView rcv_list;
    private String url;

    private int page = 1;
    private boolean isOnRefresh = false;
    private List<ClanCofcListEntity.ResultBean> allDatas = new ArrayList<>();
    private ClanCofclistAdapter adapter;
    private List<ClanCofcListEntity.ResultBean> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clancclub_list);
        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
        }

        initViews();
        initData();
    }

    private void initData() {

        if ("我的宗亲".equals(type)) {
            url = HttpUrl.MORE_CLAN;
        } else {
            url = HttpUrl.MORE_COFC;

        }
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());

        if(isOnRefresh){
            page=1;
        }
        url = url + "?page=" + page + "&size=" + 10;
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i("liwya",result);

                if (result != null) {
                    ClanCofcListEntity data = JSON.parseObject(result, ClanCofcListEntity.class);

                    if(data==null){
                        return;
                    }
                    if(data.getCode()==1){
                        datas = data.getResult();

                        if(datas.size()==10){
                            rcv_list.setNoMore(false);
                            page++;
                        }else{
                            rcv_list.setNoMore(true);
                        }

                        if (!isOnRefresh) {  //第一次和加载更多走这里
                            allDatas.addAll(datas);
                        } else {         //下拉刷新走这里
                            if(allDatas.size()>0){
                                allDatas.clear();
                            }
                            allDatas.addAll(datas);
                        }
                        adapter.setDatas(allDatas);
                        adapter.notifyDataSetChanged();
                    }else {
                        rcv_list.setNoMore(true);
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

    private void initViews() {

        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        if ("我的宗亲".equals(type)) {
            tv_headcenter_view.setText("宗亲");
        } else {
            tv_headcenter_view.setText("商会");
        }
        rcv_list = (LRecyclerView) findViewById(R.id.rcv_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rcv_list.setLayoutManager(layoutManager);

        adapter = new ClanCofclistAdapter(this);
//        adapter.setItemListener(new RecycleViewItemListener() {
//            @Override
//            public void onItemClick(int postion) {
//                Intent intent = new Intent(ClanClubListActivity.this, ClanClubActivity.class);
//                if("我的宗亲".equals(type)){
//                    intent.putExtra("isClan", true);
//                }else {
//                    intent.putExtra("isClan", false);
//                }
//                String id = allDatas.get(postion-1).getId();
//                String name = allDatas.get(postion-1).getName();
//                intent.putExtra("name", name);
//                intent.putExtra("id", id);
//
//                startActivity(intent);
//
//            }
//
//            @Override
//            public boolean onItemLongClick(int position) {
//                return false;
//            }
//        });

        rcv_list.addOnItemTouchListener(new PersonalPhotoAlbumAdapter.RecyclerItemClickListener(this,
                new PersonalPhotoAlbumAdapter.RecyclerItemClickListener.OnItemClickListener(){

                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(ClanClubListActivity.this, ClanClubActivity.class);
                        if ("我的宗亲".equals(type)) {
                            intent.putExtra("isClan", true);
                        } else {
                            intent.putExtra("isClan", false);
                        }
                        String id = allDatas.get(position - 1).getId();
                        String name = allDatas.get(position - 1).getName();
                        intent.putExtra("name", name);
                        intent.putExtra("id", id);

                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int posotion) {

                    }
                }));

        LRecyclerViewAdapter adapter1 = new LRecyclerViewAdapter(adapter);


        rcv_list.setHeaderViewColor(R.color.red_fuwa, R.color.red_fuwa_alpa_stroke, android.R.color.white);
        rcv_list.setRefreshProgressStyle(ProgressStyle.Pacman); //设置下拉刷新Progress的样式

        rcv_list.setFooterViewHint("拼命加载中", "我是有底线的", "网络不给力啊，点击再试一次吧");
        rcv_list.setAdapter(adapter1);
        rcv_list.setLoadMoreEnabled(true);
        rcv_list.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rcv_list.refreshComplete(10);
                        isOnRefresh = true;
                        initData();
                    }
                }, 1000);


            }
        });
        rcv_list.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isOnRefresh = false;
                        initData();
                    }
                }, 1000);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
        }
    }
}
