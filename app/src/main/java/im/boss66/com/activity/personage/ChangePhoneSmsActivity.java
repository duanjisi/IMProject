package im.boss66.com.activity.personage;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
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
import im.boss66.com.http.request.SMSCodeRequest;

/**
 * Created by GMARUnity on 2017/2/20.
 */
public class ChangePhoneSmsActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = ChangePhoneSmsActivity.class.getSimpleName();
    private TextView tv_back, tv_title, tv_phone_num, tv_right;
    private EditText et_phone_num;
    private String phone,newPhone;
    private Dialog dialog;
    private int sceenW;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_sms);
        initView();
    }

    private void initView() {
        sceenW = UIUtils.getScreenWidth(this)/8*7;
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
        et_phone_num = (EditText) findViewById(R.id.et_phone_num);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.change_phone));
        tv_back.setOnClickListener(this);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText(getString(R.string.next_step));
        tv_right.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                phone = bundle.getString("phone");
                if (!TextUtils.isEmpty(phone)) {
                    tv_phone_num.setText(phone);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right:
                newPhone = et_phone_num.getText().toString().trim();
                if (!UIUtils.isMobile(newPhone)) {
                    showToast("手机号格式不正确!", true);
                    return;
                } else if (phone.equals(newPhone)) {
                    showToast(getString(R.string.phone_number_the_same_as_current_binding_phone), true);
                    return;
                }else if(TextUtils.isEmpty(newPhone)){
                    showToast(getString(R.string.phone_num_no_empty), true);
                    return;
                }
                showDialog();
                break;
            case R.id.bt_ok:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                getSms();
                break;
            case R.id.bt_close:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;
        }
    }

    private void showDialog() {
        if (dialog == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_sure_phone_num, null);
            Button bt_close = (Button) view.findViewById(R.id.bt_close);
            Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
            TextView tv_dia_num = (TextView) view.findViewById(R.id.tv_dia_num);
            tv_dia_num.setText("+86" + newPhone);
            bt_close.setOnClickListener(this);
            bt_ok.setOnClickListener(this);
            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setContentView(view);
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = sceenW;
            dialogWindow.setAttributes(lp);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
    }

    private void getSms(){
        showLoadingDialog();
        SMSCodeRequest request = new SMSCodeRequest(TAG, newPhone, "4");
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
                Bundle bundle = new Bundle();
                bundle.putString("phone",newPhone);
                openActvityForResult(PersonalInputSmsActivity.class,101,bundle);
            }
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
}
