package im.boss66.com.activity.personage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.ChangeUserPwRequest;
import im.boss66.com.widget.ClearEditText;

/**
 * 修改密码
 */
public class ChangeUserPwActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = ChangeUserPwActivity.class.getSimpleName();
    private TextView tv_back, tv_title;
    private ClearEditText et_pw_old, et_pw_new, et_pw_new_1, et_email;
    private Button bt_sure;
    private LinearLayout ll_pw;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_pw);
        initView();
    }

    private void initView() {
        ll_pw = (LinearLayout) findViewById(R.id.ll_pw);
        et_email = (ClearEditText) findViewById(R.id.et_email);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        et_pw_old = (ClearEditText) findViewById(R.id.et_pw_old);
        et_pw_new = (ClearEditText) findViewById(R.id.et_pw_new);
        et_pw_new_1 = (ClearEditText) findViewById(R.id.et_pw_new_1);
        bt_sure = (Button) findViewById(R.id.bt_sure);
        bt_sure.setOnClickListener(this);
        tv_back.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                type = bundle.getString("changeType");
                String email = bundle.getString("email");
                if (!TextUtils.isEmpty(type)) {
                    if ("email".equals(type)) {
                        tv_title.setText("绑定邮箱地址");
                        ll_pw.setVisibility(View.GONE);
                        et_email.setVisibility(View.VISIBLE);
                        bt_sure.setText("确认绑定");
                    } else {
                        tv_title.setText("修改密码");
                        ll_pw.setVisibility(View.VISIBLE);
                        et_email.setVisibility(View.GONE);
                    }
                }
                if (!TextUtils.isEmpty(email)) {
                    tv_title.setText("修改邮箱地址");
                    et_email.setText(email);
                    ll_pw.setVisibility(View.GONE);
                    et_email.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_sure:
                if (!TextUtils.isEmpty(type)) {
                    if ("email".equals(type)) {
                        String email = et_email.getText().toString().trim();
                        PreferenceUtils.putString(context, "user_email", email);
                        String title = tv_title.getText().toString().trim();
                        if (!TextUtils.isEmpty(title)) {
                            if ("绑定邮箱地址".equals(title)) {
                                showToast("绑定邮箱成功", false);
                            } else {
                                showToast("修改邮箱成功", false);
                            }
                        }
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        loadServer();
                    }
                }
                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }

    private void loadServer() {
        String old = et_pw_old.getText().toString().trim();
        if (TextUtils.isEmpty(old)) {
            showToast("请输入旧密码", false);
            return;
        }
        String new_pw_1 = et_pw_new_1.getText().toString().trim();
        String new_pw = et_pw_new.getText().toString().trim();
        if (TextUtils.isEmpty(new_pw)) {
            showToast("请输入新密码", false);
            return;
        }
        if (TextUtils.isEmpty(new_pw_1)) {
            showToast("第二遍的新密码不能为空", false);
            return;
        }
        if (!new_pw.equals(new_pw_1)) {
            showToast("前后输入的新密码不一样，请重新输入", false);
            return;
        }
        showLoadingDialog();
        final ChangeUserPwRequest request = new ChangeUserPwRequest(TAG, old, new_pw);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                cancelLoadingDialog();
                BaseResult result = JSON.parseObject(pojo, BaseResult.class);
                if (result != null) {
                    int code = result.getCode();
                    if (code == 1) {
                        showToast("修改成功，请重新登录", false);
                        handle.sendEmptyMessageDelayed(0, 2000);
                    } else {
                        showToast(result.getMessage(), false);
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, false);
            }
        });
    }

    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                UIUtils.hideSoftInput(et_pw_new_1, context);
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                App.getInstance().sendBroadcast(intent);
                finish();
            }
        }
    };

}
