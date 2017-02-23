package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.ABaseActivity;
import im.boss66.com.R;

/**
 * 人脉中心
 * Created by liw on 2017/2/21.
 */
public class PeopleCenterActivity extends ABaseActivity implements View.OnClickListener {
    private TextView tv_name;
    private TextView tv_change_info;
    private TextView tv_collage;
    private TextView tv_location;

    private TextView tv_my_follow;
    private TextView tv_my_information;

    private ImageView img_headimg;



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

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_change_info = (TextView) findViewById(R.id.tv_change_info);
        tv_collage = (TextView) findViewById(R.id.tv_collage);
        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_my_follow = (TextView) findViewById(R.id.tv_my_follow);
        tv_my_information = (TextView) findViewById(R.id.tv_my_information);

        img_headimg = (ImageView) findViewById(R.id.img_headimg);

        tv_my_follow.setOnClickListener(this);
        tv_my_information.setOnClickListener(this);
        tv_change_info.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_my_follow:
                intent = new Intent(this, MyFollowActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_my_information:
                intent = new Intent(this, MyInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_change_info:
                showToast("修改资料",false);
                break;
        }
    }
}
