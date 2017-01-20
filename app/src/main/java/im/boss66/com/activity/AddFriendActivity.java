package im.boss66.com.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/1/16.
 */
public class AddFriendActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvBack;
    private EditText etAccount;
    private TextView tvNumber;
    private RelativeLayout rlScanning, rlContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        etAccount = (EditText) findViewById(R.id.et_account);
        tvNumber = (TextView) findViewById(R.id.tv_my_account);
        rlScanning = (RelativeLayout) findViewById(R.id.rl_scanning);
        rlContacts = (RelativeLayout) findViewById(R.id.rl_phone_contacts);

        tvBack.setOnClickListener(this);
        rlScanning.setOnClickListener(this);
        rlContacts.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_scanning:

                break;
            case R.id.rl_phone_contacts:

                break;
        }
    }
}
