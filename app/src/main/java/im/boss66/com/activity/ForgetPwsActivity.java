package im.boss66.com.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import im.boss66.com.http.request.FindPwsRequest;
import im.boss66.com.http.request.SMSCodeRequest;

/**
 * Created by Johnny on 2017/2/15.
 */
public class ForgetPwsActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = ForgetPwsActivity.class.getSimpleName();
    private static final int DELAY_MILlIS = 1000;
    private TextView tvBack, tvCode, tvSend;
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
        setContentView(R.layout.activity_forget_pws);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvCode = (TextView) findViewById(R.id.tv_code);
        tvSend = (TextView) findViewById(R.id.tv_send);
        etPhoneNum = (EditText) findViewById(R.id.et_phone_num);
        etCode = (EditText) findViewById(R.id.et_code);
        etPws = (EditText) findViewById(R.id.et_pws);
        etConfirmPws = (EditText) findViewById(R.id.et_pws_confirm);
        btnRegister = (Button) findViewById(R.id.btn_register);

        tvCode.setOnClickListener(this);
        tvSend.setOnClickListener(this);
        tvBack.setOnClickListener(this);
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
            case R.id.tv_send:
                sendCode();
                break;
            case R.id.btn_register:
                findPws();
                break;
        }
    }

    private void sendCode() {
        String phone = etPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号不能为空!", true);
            return;
        }
        showLoadingDialog();
        SMSCodeRequest request = new SMSCodeRequest(TAG, phone, "2");
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
            JSONObject obj = new JSONObject(string);
//            JSONObject obj = object.getJSONObject("result");
            interval = obj.getInt("interval");
            tvCode.setEnabled(false);
            handler.sendEmptyMessageDelayed(0, DELAY_MILlIS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findPws() {
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
        FindPwsRequest request = new FindPwsRequest(TAG, phone, VerifyCode, pass1);
        request.send(new BaseDataRequest.RequestCallback() {
            @Override
            public void onSuccess(Object pojo) {
                cancelLoadingDialog();
                tvCode.setEnabled(true);
                showToast("找回密码成功!", true);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                tvCode.setEnabled(true);
//                showToast(msg, true);
                showToast("找回密码失败!", true);
            }
        });
    }


}
