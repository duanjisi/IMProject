package im.boss66.com.activity.connection;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.ABaseActivity;
import im.boss66.com.R;

/**
 * 我的消息
 * Created by liw on 2017/2/22.
 */
public class MyInformationActivity extends ABaseActivity implements View.OnClickListener {

    private TextView tv_headright_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        initViews();

    }

    private void initViews() {
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("我的消息");
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);

        tv_headright_view = (TextView) findViewById(R.id.tv_headright_view);
        tv_headright_view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_headright_view:
                showToast("清空",false);
                break;

        }
    }
}
