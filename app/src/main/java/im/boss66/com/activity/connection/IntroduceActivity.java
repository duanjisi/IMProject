package im.boss66.com.activity.connection;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;

/**
 * 简介
 * Created by liw on 2017/2/22.
 */
public class IntroduceActivity extends ABaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        initViews();

    }

    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("简介");
        tv_headlift_view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
        }
    }
}
