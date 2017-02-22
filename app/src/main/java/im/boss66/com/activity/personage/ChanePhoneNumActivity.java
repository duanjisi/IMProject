package im.boss66.com.activity.personage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.AccountEntity;

/**
 * 更换手机号码
 */
public class ChanePhoneNumActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_title, tv_phone_num;
    private Button bt_check, bt_change_phone;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_num);
        initView();
    }

    private void initView() {
        bt_change_phone = (Button) findViewById(R.id.bt_change_phone);
        bt_check = (Button) findViewById(R.id.bt_check);
        tv_phone_num = (TextView) findViewById(R.id.tv_phone_num);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.binding_phone));
        tv_back.setOnClickListener(this);
        bt_change_phone.setOnClickListener(this);
        bt_check.setOnClickListener(this);
        AccountEntity sAccount = App.getInstance().getAccount();
        phone = sAccount.getMobile_phone();
                if (!TextUtils.isEmpty(phone)) {
                    tv_phone_num.setText("" + phone);
                }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_back:
                    finish();
                break;
            case R.id.bt_change_phone:
                Bundle bundle = new Bundle();
                bundle.putString("phone", phone);
                openActvityForResult(VerifyOldPhoneActivity.class, 101, bundle);
                break;
            case R.id.bt_check:

                break;
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
