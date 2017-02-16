package im.boss66.com.activity.im;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/1/21.
 */
public class ChatBackgroundActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvBack;
    private RelativeLayout rl_bg, rl_alum, rl_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_background);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        rl_bg = (RelativeLayout) findViewById(R.id.rl_01);
        rl_alum = (RelativeLayout) findViewById(R.id.rl_02);
        rl_photo = (RelativeLayout) findViewById(R.id.rl_04);

        tvBack.setOnClickListener(this);
        rl_bg.setOnClickListener(this);
        rl_alum.setOnClickListener(this);
        rl_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_01://选择背景图

                break;
            case R.id.rl_02://从手机相册选择

                break;
            case R.id.rl_04://拍一张

                break;
        }
    }
}
