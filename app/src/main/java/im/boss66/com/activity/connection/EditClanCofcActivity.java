package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.ABaseActivity;

/**
 * Created by liw on 2017/4/15.
 */
public class EditClanCofcActivity extends ABaseActivity implements View.OnClickListener {

    private boolean isClan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clan_cofc);
        Intent intent = getIntent();
        if(intent!=null){
            isClan = intent.getBooleanExtra("isClan", false);
        }
        initViews();

    }

    private void initViews() {
        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("编辑简介");

        findViewById(R.id.rl_logo).setOnClickListener(this);
        findViewById(R.id.rl_info).setOnClickListener(this);
        findViewById(R.id.rl_location).setOnClickListener(this);
        findViewById(R.id.rl_phone).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;

            case R.id.rl_logo:
                break;
            case R.id.rl_info:
                break;
            case R.id.rl_location:
                break;
            case R.id.rl_phone:
                break;

        }
    }
}
