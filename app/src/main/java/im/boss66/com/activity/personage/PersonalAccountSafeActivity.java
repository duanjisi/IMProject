package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 账号与安全
 */
public class PersonalAccountSafeActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_title;
    private RelativeLayout rl_app_num, rl_qq_num, rl_phone_num, rl_email_address, rl_password, rl_account_protect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account_safe);
        initView();
    }

    private void initView() {
        rl_app_num = (RelativeLayout) findViewById(R.id.rl_app_num);
        rl_qq_num = (RelativeLayout) findViewById(R.id.rl_qq_num);
        rl_phone_num = (RelativeLayout) findViewById(R.id.rl_phone_num);
        rl_email_address = (RelativeLayout) findViewById(R.id.rl_email_address);
        rl_password = (RelativeLayout) findViewById(R.id.rl_password);
        rl_account_protect = (RelativeLayout) findViewById(R.id.rl_account_protect);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.account_and_safe));
        tv_back.setOnClickListener(this);
        rl_app_num.setOnClickListener(this);
        rl_qq_num.setOnClickListener(this);
        rl_phone_num.setOnClickListener(this);
        rl_email_address.setOnClickListener(this);
        rl_password.setOnClickListener(this);
        rl_account_protect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_app_num://小6号
                break;
            case R.id.rl_qq_num://QQ号
                break;
            case R.id.rl_phone_num://手机号
                break;
            case R.id.rl_email_address://邮箱地址
                break;
            case R.id.rl_password://密码
                break;
            case R.id.rl_account_protect://账号保护
                break;
        }
    }
}
