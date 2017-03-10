package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.http.HttpUrl;

/**
 * Created by admin on 2017/3/2.
 */
public class EditNameActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_name;
    private TextView tv_headright_view,tv_headlift_view;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    showToast("修改成功",false);
                    Intent intent = new Intent();
                    intent.putExtra("name", et_name.getText().toString());
                    setResult(1, intent);
                    finish();
                    break;

            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        initViews();
    }

    private void initViews() {
        et_name = (EditText) findViewById(R.id.et_name);
        tv_headright_view = (TextView) findViewById(R.id.tv_headright_view);
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);
        tv_headright_view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_headright_view: //完成
                if(TextUtils.isEmpty(et_name.getText().toString())){
                    ToastUtil.showShort(context,"请您填写姓名");
                }else{

                    initData();
                }


                break;

        }
    }

    private void initData() {
        showLoadingDialog();
        String url = HttpUrl.CHANGE_USER_NAME;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        params.addBodyParameter("user_name",et_name.getText().toString());

        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>(){

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if(result!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        if( jsonObject.getInt("code")==1){

                            handler.obtainMessage(1).sendToTarget();

                        }else{
                            showToast(jsonObject.getString("message"),false);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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

}
