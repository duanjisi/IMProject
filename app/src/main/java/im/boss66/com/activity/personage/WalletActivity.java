package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 钱包
 */
public class WalletActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_back,tv_title;
    private Button bt_recharge,bt_withdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initView();
    }

    private void initView() {
        bt_withdraw = (Button) findViewById(R.id.bt_withdraw);
        bt_recharge = (Button) findViewById(R.id.bt_recharge);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.wallet));
        tv_back.setOnClickListener(this);
        bt_withdraw.setOnClickListener(this);
        bt_recharge.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.bt_withdraw:

                break;
            case R.id.bt_recharge:

                break;
        }
    }
}
