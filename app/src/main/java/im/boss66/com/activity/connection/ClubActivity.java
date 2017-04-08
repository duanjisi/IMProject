package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.adapter.ClubAdapter;
import im.boss66.com.adapter.MySchoolAdapter;
import im.boss66.com.entity.ClubEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/3/7.
 */

public class ClubActivity extends ABaseActivity implements View.OnClickListener {
    protected RecyclerView rcv_club;
    protected ClubAdapter adapter;
    private int id;
    private boolean isSchool;
    private String url;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    result = clubEntity.getResult();
                    adapter.setDatas(result);
                    adapter.notifyDataSetChanged();
                    break;

            }
        }
    };
    private ClubEntity clubEntity;
    private List<ClubEntity.ResultBean> result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_club);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras().containsKey("school_id")) {
                id = intent.getIntExtra("school_id", -1);
                url = HttpUrl.SCHOOL_CLUB_LIST;
                isSchool = true;
            } else {
                id = intent.getIntExtra("hometown_id", -1);
                url = HttpUrl.HOMETOWN_CLUB_LIST;
                isSchool = false;
            }
        }
        initViews();
        initData();
    }

    private void initData() {
        showLoadingDialog();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        url = url + "?id=" + id;
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;


                if (result != null) {
                    clubEntity = JSON.parseObject(result, ClubEntity.class);

                    if (clubEntity != null) {
                        if (clubEntity.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                        } else {
                            if (clubEntity.getCode() == 1) {
                                handler.obtainMessage(1).sendToTarget();
                            } else {
//                                showToast(clubEntity.getMessage(), false);
                            }
                        }

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


    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        if (isSchool) {
            tv_headcenter_view.setText("社团");
        } else {
            tv_headcenter_view.setText("商会");

        }
        tv_headlift_view.setOnClickListener(this);


        rcv_club = (RecyclerView) findViewById(R.id.rcv_club);

        adapter = new ClubAdapter(this);
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                Intent intent = new Intent(ClubActivity.this, ClubDetailActivity.class);
                if (!isSchool) {
                    intent.putExtra("companyClubId", result.get(postion).getId());
                } else {
                    intent.putExtra("schoolClubId", result.get(postion).getId());
                }
                intent.putExtra("name", result.get(postion).getName());
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

        rcv_club.setAdapter(adapter);
        rcv_club.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
        }
    }
}
