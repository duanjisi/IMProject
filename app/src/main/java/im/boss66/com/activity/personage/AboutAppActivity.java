package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.RoundImageView;

/**
 * 关于本app
 */
public class AboutAppActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_title;
    private RoundImageView iv_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        initView();
    }

    private void initView() {
        iv_head = (RoundImageView) findViewById(R.id.iv_head);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.about));
        int sceenW = UIUtils.getScreenWidth(this);
        iv_head.setType(1);
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) iv_head.getLayoutParams();
        params.width = sceenW/6;
        params.height = sceenW/6;
        iv_head.setLayoutParams(params);
        tv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }
}
