package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.event.CreateSuccess;
import im.boss66.com.event.EditWeb;
import im.boss66.com.http.HttpUrl;

/**
 * Created by liw on 2017/4/17.
 */
public class EditTextActivity extends ABaseActivity implements View.OnClickListener {
    private EditText et_content;
    private TextView tv_num;
    private String type;
    private boolean isClan;
    private String id;

    private String main;
    private String access_token;
    private Handler handler = new Handler() {
    };
    private String content;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            isClan = intent.getBooleanExtra("isClan", false);
            id = intent.getStringExtra("id");
            content = intent.getStringExtra("content");
        }

        initViews();
    }

    private void initViews() {
        access_token = App.getInstance().getAccount().getAccess_token();
        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("编辑");
        findViewById(R.id.tv_ok).setOnClickListener(this);
        tv_num = (TextView) findViewById(R.id.tv_num);


        et_content = (EditText) findViewById(R.id.et_content);
        if (content != null && content.length() > 0) {
            et_content.setText(content);
            et_content.setSelection(et_content.length());
            tv_num.setText("还可输入" + (50 - et_content.length()) + "字");
        }
        switch (type) {
            case "rl_info":
                et_content.setMaxEms(100000);
                tv_num.setVisibility(View.INVISIBLE);
                break;
            case "rl_location":
                et_content.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {


                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        tv_num.setText("还可输入" + (50 - et_content.length()) + "字");
                    }
                });
                break;
            case "rl_phone":
                et_content.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {


                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        tv_num.setText("还可输入" + (50 - et_content.length()) + "字");
                    }
                });
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_ok:

                String content = et_content.getText().toString();
                if(content.length()>0){
                    updateData(content);
                }else{
                    ToastUtil.showShort(context,"请填写内容");
                }
                break;
        }
    }

    private void updateData(String content) {
        showLoadingDialog();
        if (isClan) {
            main = HttpUrl.EDIT_CLAN;
        } else {
            main = HttpUrl.EDIT_COFC;
        }
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();

        params.addBodyParameter("access_token", access_token);
        if (isClan) {
            params.addBodyParameter("clan_id", id);
        } else {
            params.addBodyParameter("cofc_id", id);
        }
        switch (type) {
            case "rl_info":
//                String s = content.replaceAll("\n", "<br>");
                params.addBodyParameter("desc", content);
                break;
            case "rl_location":
                params.addBodyParameter("address", content);
                break;
            case "rl_phone":
                params.addBodyParameter("contact", content);
                break;
        }

        httpUtils.send(HttpRequest.HttpMethod.POST, main, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;

                if (result != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("code") == 1) {
                            showToast("更改成功", false);
//                            if("rl_info".equals(type)){      //更改了内容，刷新人脉首页,刷新web页
                            EventBus.getDefault().post(new CreateSuccess(""));
                            EventBus.getDefault().post(new EditWeb(""));
//                            }
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 500);
                        } else {
                            showToast("更改失败", false);
                        }
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
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
                    showToast("更改失败", false);
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
