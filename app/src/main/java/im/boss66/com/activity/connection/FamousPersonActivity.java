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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.adapter.FamousPeopleAdapter;
import im.boss66.com.entity.FamousPeopleEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * 名人
 * Created by liw on 2017/2/22.
 */
public class FamousPersonActivity extends ABaseActivity implements View.OnClickListener {

    private int id;
    private String url;
    private RecyclerView rcv_famous_people;
    private FamousPeopleAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    result = famousPeopleEntity.getResult();
                    adapter.setDatas(result);
                    adapter.notifyDataSetChanged();
                    break;

            }
        }
    };
    private FamousPeopleEntity famousPeopleEntity;
    private List<FamousPeopleEntity.ResultBean> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_famous_person);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras().containsKey("school_id")) {
                id = intent.getIntExtra("school_id", -1);
                url = HttpUrl.SCHOOL_FAMOUS_PEOPLE;
            } else {
                id = intent.getIntExtra("hometown_id", -1);
                url = HttpUrl.BUSINESS_FAMOUS_PEOPLE;

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
                    famousPeopleEntity = JSON.parseObject(result, FamousPeopleEntity.class);
                    if(famousPeopleEntity!=null){
                        if (famousPeopleEntity.getStatus() == 401) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                            return;
                        }
                        if (famousPeopleEntity.getCode() == 1) {
                            handler.obtainMessage(1).sendToTarget();
                        } else {
                            ToastUtil.show(context, famousPeopleEntity.getMessage(), Toast.LENGTH_SHORT);
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

    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);

        tv_headcenter_view.setText("名人");
        tv_headlift_view.setOnClickListener(this);

        rcv_famous_people = (RecyclerView) findViewById(R.id.rcv_famous_people);
        adapter = new FamousPeopleAdapter(this);
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                int id = result.get(postion).getId();
                String name = result.get(postion).getName();
                Intent intent = new Intent(FamousPersonActivity.this,FamousPersonDetailActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("name",name);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_famous_people.setLayoutManager(new LinearLayoutManager(this));
        rcv_famous_people.setAdapter(adapter);
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
