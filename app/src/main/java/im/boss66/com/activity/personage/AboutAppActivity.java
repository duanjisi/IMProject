package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 关于本app
 */
public class AboutAppActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back,tv_title;
    private RelativeLayout rl_to_score,rl_function_introduction,rl_system_informs,rl_complaint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        initView();
    }

    private void initView() {
        rl_to_score = (RelativeLayout) findViewById(R.id.rl_to_score);
        rl_function_introduction = (RelativeLayout) findViewById(R.id.rl_function_introduction);
        rl_system_informs = (RelativeLayout) findViewById(R.id.rl_system_informs);
        rl_complaint = (RelativeLayout) findViewById(R.id.rl_complaint);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.about));
        tv_back.setOnClickListener(this);
        rl_to_score.setOnClickListener(this);
        rl_function_introduction.setOnClickListener(this);
        rl_system_informs.setOnClickListener(this);
        rl_complaint.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_to_score:
                finish();
                break;
            case R.id.rl_function_introduction:
                finish();
                break;
            case R.id.rl_system_informs:
                finish();
                break;
            case R.id.rl_complaint:
                finish();
                break;
        }
    }
}
