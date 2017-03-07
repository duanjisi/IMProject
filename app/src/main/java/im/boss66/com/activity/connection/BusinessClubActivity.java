package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import im.boss66.com.App;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.adapter.MySchoolAdapter;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * 商会
 * Created by liw on 2017/2/22.
 */
public class BusinessClubActivity extends ABaseActivity implements View.OnClickListener {
    private static final String TAG = BusinessClubActivity.class.getSimpleName();
    protected RecyclerView rcv_club;
    protected MySchoolAdapter adapter;
    private int hometown_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_club);
        Intent intent = getIntent();
        if(intent!=null){
            hometown_id = intent.getIntExtra("hometown_id", -1);
        }
        initViews();
        initData();


    }

    private void initData() {
        String url = HttpUrl.HOMETOWN_CLUB_LIST;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        url = url+"?id=" + hometown_id;
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>(){

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i("liwya",result);

            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });
    }


    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("商会");
        tv_headlift_view.setOnClickListener(this);

        rcv_club = (RecyclerView) findViewById(R.id.rcv_club);

        adapter = new MySchoolAdapter(this);
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                Intent intent = new Intent(BusinessClubActivity.this, ClubDetailActivity.class);
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
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
        }
    }
}
