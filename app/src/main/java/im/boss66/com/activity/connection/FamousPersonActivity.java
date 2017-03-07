package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
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
import im.boss66.com.http.HttpUrl;

/**
 * 名人
 * Created by liw on 2017/2/22.
 */
public class FamousPersonActivity extends ABaseActivity implements View.OnClickListener {

    private int id;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_famous_person);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getExtras().containsKey("school_id")) {
                id = intent.getIntExtra("school_id",-1);
                url = HttpUrl.SCHOOL_FAMOUS_PEOPLE;
            } else {
                id = intent.getIntExtra("hometown_id",-1);
                url = HttpUrl.BUSINESS_FAMOUS_PEOPLE;

            }
            initViews();
            initData();
        }
    }

    private void initData() {

        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        url = url+"?id=" + id;
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
        tv_headcenter_view.setText("名人");
        tv_headlift_view.setOnClickListener(this);
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
