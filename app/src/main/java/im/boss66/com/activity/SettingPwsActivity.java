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
public class SettingPwsActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvBack;
    private EditText etPws, etPws2;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_pws);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        etPws = (EditText) findViewById(R.id.et_set_pws);
        etPws2 = (EditText) findViewById(R.id.et_psw);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);

        tvBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:

                break;
            case R.id.btn_confirm:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
