package im.boss66.com.activity.discover;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 摇一摇设置
 */
public class SharkItOffSetActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back,tv_title;
    private RelativeLayout rl_hola_man,rl_history,rl_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shark_it_off_set);
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_back = (TextView) findViewById(R.id.tv_back);
        rl_history = (RelativeLayout) findViewById(R.id.rl_history);
        rl_hola_man = (RelativeLayout) findViewById(R.id.rl_hola_man);
        rl_message = (RelativeLayout) findViewById(R.id.rl_message);
        tv_title.setText(getString(R.string.shark_set));
        tv_back.setOnClickListener(this);
        rl_history.setOnClickListener(this);
        rl_hola_man.setOnClickListener(this);
        rl_message.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_history:
                break;
            case R.id.rl_hola_man:
                break;
            case R.id.rl_message:
                break;
        }
    }
}
