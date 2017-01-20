package im.boss66.com.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/1/17.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvBack, tvCode;
    private EditText etPhoneNum, etCode;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvCode = (TextView) findViewById(R.id.tv_code);
        etPhoneNum = (EditText) findViewById(R.id.et_phone_num);
        etCode = (EditText) findViewById(R.id.et_code);
        btnRegister = (Button) findViewById(R.id.btn_register);

        tvCode.setOnClickListener(this);
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

                break;
            case R.id.btn_register:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
