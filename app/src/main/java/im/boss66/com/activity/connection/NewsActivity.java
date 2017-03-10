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

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.adapter.NewsAdapter;
import im.boss66.com.entity.ClubEntity;
import im.boss66.com.entity.NewsEntity;
import im.boss66.com.http.HttpUrl;

/**
 * 校区和家乡动态页
 * Created by liw on 2017/3/8.
 */

public class NewsActivity extends ABaseActivity implements View.OnClickListener {
    private int id;
    private boolean isSchool;
    private RecyclerView rcv_news;
    private NewsAdapter adapter;
    private String url;
    private String title;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:    //给adapter赋值

                    break;

            }
        }
    };
    private NewsEntity newsEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("name");
            if (intent.getExtras().containsKey("school_id")) {
                id = intent.getIntExtra("school_id", -1);
                url = HttpUrl.SCHOOL_NEWS;
                isSchool = true;
            } else {
                id = intent.getIntExtra("hometown_id", -1);
                url = HttpUrl.HOMETOWN_NEWS;
                isSchool = false;
            }
        }
        setContentView(R.layout.activity_news);
        initViews();
        initData();


    }


    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText(title);

        tv_headlift_view.setOnClickListener(this);
        rcv_news = (RecyclerView) findViewById(R.id.rcv_news);
        rcv_news.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this);
        rcv_news.setAdapter(adapter);
    }

    private void initData() {
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        url = url + "?id=" + id;
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if(result!=null){
                    newsEntity = JSON.parseObject(result, NewsEntity.class);
                    if(newsEntity!=null){
                        if(newsEntity.getCode()==1){

                            handler.obtainMessage(1).sendToTarget();
                        }else{
                            ToastUtil.show(NewsActivity.this,newsEntity.getMessage(), Toast.LENGTH_SHORT);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
        }
    }
}