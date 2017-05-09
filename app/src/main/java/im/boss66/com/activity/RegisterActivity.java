package im.boss66.com.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.RegistRequest;
import im.boss66.com.http.request.SMSCodeRequest;

/**
 * Created by Johnny on 2017/1/17.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = RegisterActivity.class.getSimpleName();
    private static final int DELAY_MILlIS = 1000;
    private TextView tvBack, tvCode, tv_bottom;
    private EditText etPhoneNum, etPws, etConfirmPws, etCode;
    private Button btnRegister;
    private int interval = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (interval != 0) {
                        interval--;
                        tvCode.setText(String.valueOf(interval) + "秒");
                        handler.sendEmptyMessageDelayed(0, DELAY_MILlIS);
                    } else {
                        tvCode.setText("发送验证码");
                        tvCode.setEnabled(true);
                        handler.removeMessages(0);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvCode = (TextView) findViewById(R.id.tv_code);
        tv_bottom = (TextView) findViewById(R.id.tv_bottom);
        etPhoneNum = (EditText) findViewById(R.id.et_phone_num);
        etCode = (EditText) findViewById(R.id.et_code);
        etPws = (EditText) findViewById(R.id.et_pws);
        etConfirmPws = (EditText) findViewById(R.id.et_pws_confirm);
        btnRegister = (Button) findViewById(R.id.btn_register);

        tvCode.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        tv_bottom.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_code:
                sendCode();
                break;
            case R.id.btn_register:
                register();
                break;
            case R.id.tv_bottom:
                openActivity(SoftWareAgreementActivity.class);
                break;
        }
    }

    private void register() {
        String phone = getText(etPhoneNum);
        String pass1 = getText(etPws);
        String pass2 = getText(etConfirmPws);
        String VerifyCode = getText(etCode);
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号不能为空!", true);
            return;
        }

        if (!UIUtils.isMobile(phone)) {
            showToast("手机号格式不正确!", true);
            return;
        }

        if (TextUtils.isEmpty(pass1)) {
            showToast("请输入密码!", true);
            return;
        }

        if (TextUtils.isEmpty(pass2)) {
            showToast("请输入确认密码!", true);
            return;
        }

        if (TextUtils.isEmpty(VerifyCode)) {
            showToast("验证码不能为空!", true);
            return;
        }

        if (!pass1.equals(pass2)) {
            showToast("两次输入的密码不一致!", true);
            return;
        }
        showLoadingDialog();
        tvCode.setEnabled(false);
        RegistRequest request = new RegistRequest(TAG, phone, pass1, VerifyCode);
        request.send(new BaseDataRequest.RequestCallback() {
            @Override
            public void onSuccess(Object pojo) {
                cancelLoadingDialog();
                tvCode.setEnabled(true);
                showToast("注册成功!", true);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                tvCode.setEnabled(true);
                showToast(msg, true);
            }
        });
    }

    private void sendCode() {
        String phone = etPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号不能为空!", true);
            return;
        }
        showLoadingDialog();
        SMSCodeRequest request = new SMSCodeRequest(TAG, phone, "1");
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                cancelLoadingDialog();
                bindData(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private void bindData(String string) {
        try {
            Log.i("info", "=====json:" + string);
            JSONObject obj = new JSONObject(string);
            interval = obj.getInt("interval");
            Log.i("info", "=====interval:" + interval);
            tvCode.setEnabled(false);
            handler.sendEmptyMessageDelayed(0, DELAY_MILlIS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
