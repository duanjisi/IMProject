package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.ABaseActivity;

/**
 * Created by liw on 2017/3/2.
 */
public class AddSchoolActivity extends ABaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);
        initViews();

    }

    private void initViews() {
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("添加学校");
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);

        findViewById(R.id.rl_university).setOnClickListener(this);
        findViewById(R.id.rl_high_shcool).setOnClickListener(this);
        findViewById(R.id.rl_big_middle_shcool).setOnClickListener(this);
        findViewById(R.id.rl_middle_school).setOnClickListener(this);
        findViewById(R.id.rl_small_school).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.rl_university:
                Intent intent = new Intent(this, EditSchoolActivity.class);
                intent.putExtra("isUniversity",true);
                startActivity(intent);
                break;
            case R.id.rl_high_shcool:
                Intent intent1 = new Intent(this, EditSchoolActivity.class);
                intent1.putExtra("isUniversity",false);
                startActivity(intent1);
                break;
            case R.id.rl_big_middle_shcool:
                Intent intent2 = new Intent(this, EditSchoolActivity.class);
                intent2.putExtra("isUniversity",false);
                startActivity(intent2);
                break;
            case R.id.rl_middle_school:
                Intent intent3 = new Intent(this, EditSchoolActivity.class);
                intent3.putExtra("isUniversity",false);
                startActivity(intent3);
                break;
            case R.id.rl_small_school:
                Intent intent4 = new Intent(this, EditSchoolActivity.class);
                intent4.putExtra("isUniversity",false);
                startActivity(intent4);
                break;

        }
    }
}
