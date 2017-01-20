package im.boss66.com.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/1/19.
 */
public class BindPhoneActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvBack;
    private ImageView ivMore;
    private TextView tvPhoneNum;
    private Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvPhoneNum = (TextView) findViewById(R.id.tv_phone_num);
        ivMore = (ImageView) findViewById(R.id.iv_more);
        btnUpload = (Button) findViewById(R.id.btn_upload_contacts);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_more:

                break;
            case R.id.btn_upload_contacts:

                break;
        }
    }
}
