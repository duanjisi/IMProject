package im.boss66.com.activity.personage;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.ChangePhoneRequest;
import im.boss66.com.http.request.SMSCodeRequest;
import im.boss66.com.widget.ActionSheet;

/**
 * 填写验证码
 */
public class PersonalInputSmsActivity extends BaseActivity implements View.OnClickListener ,ActionSheet.OnSheetItemClickListener{

    private final static String TAG = PersonalInputSmsActivity.class.getSimpleName();

    private TextView tv_back, tv_title, tv_phone_num,tv_repeat_send;
    private EditText et_phone_num;
    private Button bt_greet;
    private String phone;
    private Dialog dialog;
    private int sceenW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_code);
        initView();
    }

    private void initView() {
        sceenW = UIUtils.getScreenWidth(this)/8*7;
        tv_repeat_send = (TextView) findViewById(R.id.tv_repeat_send);
        bt_greet = (Button) findViewById(R.id.bt_greet);
        et_phone_num = (EditText) findViewById(R.id.et_phone_num);
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.input_verification_code));
        tv_back.setOnClickListener(this);
        bt_greet.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                phone = bundle.getString("phone");
                if (!TextUtils.isEmpty(phone)){
                    tv_phone_num.setText("+86" + phone);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                showDialog();
                break;
            case R.id.bt_greet:
                getServerData();
                break;
            case R.id.tv_repeat_send:
                showActionSheet();
                break;case R.id.bt_ok:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                finish();
                break;
            case R.id.bt_close:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;
        }
    }

    private void getServerData(){
        String sms_code = et_phone_num.getText().toString().trim();
        if (TextUtils.isEmpty(sms_code)){
            showToast(getString(R.string.input_sms_code),true);
            return;
        }
        ChangePhoneRequest request = new ChangePhoneRequest(TAG,phone,sms_code);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                try {
                    JSONObject obj = new JSONObject(pojo);
                    if (obj != null){
                        int code = obj.getInt("code");
                        String message = obj.getString("message");
                        showToast(message,true);
                        if (code == 1){
                            App.getInstance().getAccount().setMobile_phone(phone);

                            setResult(RESULT_OK);
                            finish();
                        }
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

    private void getSms(){
        showLoadingDialog();
        SMSCodeRequest request = new SMSCodeRequest(TAG, phone, "4");
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
            int code = obj.getInt("interval");
            if (code > 0){
                showToast(getString(R.string.message_authentication_code_has_sent),false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        if (dialog == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_sure_phone_num, null);
            Button bt_close = (Button) view.findViewById(R.id.bt_close);
            Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
            TextView tv_dia_title = (TextView) view.findViewById(R.id.tv_dia_title);
            TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_dia_title.setVisibility(View.GONE);
            tv_content.setText(getString(R.string.back_repeat_get_verification_code));
            bt_close.setOnClickListener(this);
            bt_ok.setOnClickListener(this);
            bt_close.setText("等待");
            bt_ok.setText("返回");
            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setContentView(view);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = sceenW;
            dialogWindow.setAttributes(lp);
            dialogWindow.setGravity(Gravity.CENTER);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(PersonalInputSmsActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem(getString(R.string.repeat_get_verification_code), ActionSheet.SheetItemColor.Black,
                PersonalInputSmsActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        getSms();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            showDialog();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
