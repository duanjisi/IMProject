package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 设置-功能消息免打扰
 */
public class PersonalSetNoDisturbingActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back,tv_title;
    private RelativeLayout rl_open,rl_only_at_night,rl_close;
    private ImageView iv_open,iv_only_at_night,iv_close;
    private ImageView[] ivList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_set_no_disturbing);
        initView();
    }

    private void initView() {
        rl_open = (RelativeLayout) findViewById(R.id.rl_open);
        rl_only_at_night = (RelativeLayout) findViewById(R.id.rl_only_at_night);
        rl_close = (RelativeLayout) findViewById(R.id.rl_close);
        iv_open = (ImageView) findViewById(R.id.iv_open);
        iv_only_at_night = (ImageView) findViewById(R.id.iv_only_at_night);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.new_alerts));
        tv_back.setOnClickListener(this);
        rl_open.setOnClickListener(this);
        rl_only_at_night.setOnClickListener(this);
        rl_close.setOnClickListener(this);
        ivList = new ImageView[]{iv_open,iv_only_at_night,iv_close};
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_open:
                showDrawByTag(0);
                break;
            case R.id.rl_only_at_night:
                showDrawByTag(1);
                break;
            case R.id.rl_close:
                showDrawByTag(2);
                break;
        }
    }

    private void showDrawByTag(int tag){
        for (int i=0;i<ivList.length;i++){
            ivList[i].setVisibility(View.GONE);
        }
        ivList[tag].setVisibility(View.VISIBLE);
    }

}
