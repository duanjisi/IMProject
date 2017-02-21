package im.boss66.com.activity.personage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.SMSCodeRequest;
import im.boss66.com.http.request.VerifyCodeReques;

/**
 * 验证原手机
 */
public class VerifyOldPhoneActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_code;
    private TextView tv_back, tv_title,tv_code,tv_phone_num,tv_right;
    private final static String TAG = VerifyOldPhoneActivity.class.getSimpleName();
    private static final int DELAY_MILlIS = 1000;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_old_phone);
        initView();
    }

    private void initView() {
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
        et_code = (EditText) findViewById(R.id.et_code);
        tv_code = (TextView) findViewById(R.id.tv_code);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.verify_this_phone));
        tv_back.setOnClickListener(this);
        tv_code.setOnClickListener(this);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText(getString(R.string.next_step));
        tv_right.setTextColor(getResources().getColor(R.color.btn_green_light));
        tv_right.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                phone = bundle.getString("phone");
                if (!TextUtils.isEmpty(phone)){
                    tv_phone_num.setText(phone);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_code:
                sendCode();
                break;
            case R.id.tv_right:
                String code = et_code.getText().toString().trim();
                if (TextUtils.isEmpty(code)){
                    showToast(getString(R.string.input_sms_code),false);
                    return;
                }
                verifyCode(code);
                break;
        }
    }

    private void verifyCode(String code){
        VerifyCodeReques reques = new VerifyCodeReques(TAG,phone,code);
        reques.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                try {
                    JSONObject obj = new JSONObject(pojo);
                    int code = obj.getInt("code");
                    String msg = obj.getString("message");
                    if (code == 1){
                        Bundle bundle = new Bundle();
                        bundle.putString("phone", phone);
                        openActvityForResult(ChangePhoneSmsActivity.class, 101, bundle);
                    }else {
                        showToast(msg,false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg,false);
            }
        });
    }

    private void sendCode() {
        showLoadingDialog();
        SMSCodeRequest request = new SMSCodeRequest(TAG, phone, "5");
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
            interval = obj.getInt("interval");
            tv_code.setEnabled(false);
            handler.sendEmptyMessageDelayed(0, DELAY_MILlIS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }

    private int interval = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (interval != 0) {
                        interval--;
                        tv_code.setText(String.valueOf(interval) + "秒");
                        handler.sendEmptyMessageDelayed(0, DELAY_MILlIS);
                    } else {
                        tv_code.setText("获取验证码");
                        tv_code.setEnabled(true);
                        handler.removeMessages(0);
                    }
                    break;
            }
        }
    };

}
