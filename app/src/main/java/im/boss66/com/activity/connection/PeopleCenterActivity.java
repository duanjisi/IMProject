package im.boss66.com.activity.connection;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by admin on 2017/2/21.
 */
public class PeopleCenterActivity extends ABaseActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_center);
        initViews();
    }

    protected void initViews() {
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        iv_headright_view = (ImageView) findViewById(R.id.iv_headright_view);
        tv_headlift_view.setOnClickListener(this);
        tv_headcenter_view.setText("人脉中心");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                goBack();
                break;
        }
    }
}
