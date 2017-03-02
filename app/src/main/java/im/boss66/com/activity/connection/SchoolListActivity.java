package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 学校列表
 * Created by admin on 2017/3/2.
 */
public class SchoolListActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_headright_view,tv_headlift_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

        initViews();

    }

    private void initViews() {
        tv_headright_view = (TextView) findViewById(R.id.tv_headright_view);
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headright_view.setOnClickListener(this);
        tv_headlift_view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_headright_view:
                Intent intent = new Intent(this, AddSchoolActivity.class);
                startActivity(intent);
                break;

        }
    }
}
