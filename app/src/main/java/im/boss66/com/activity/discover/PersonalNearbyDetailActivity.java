package im.boss66.com.activity.discover;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 附近的人详细资料
 */
public class PersonalNearbyDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_name, tv_sex, tv_distance, tv_set_notes_labels,
            tv_area, tv_personalized_signature, tv_source;
    private ImageView iv_set, iv_head;
    private Button bt_greet,bt_complaint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_nearby_detail);
        initView();
    }

    private void initView() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_set_notes_labels = (TextView) findViewById(R.id.tv_set_notes_labels);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_personalized_signature = (TextView) findViewById(R.id.tv_personalized_signature);
        tv_source = (TextView) findViewById(R.id.tv_source);

        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_set = (ImageView) findViewById(R.id.iv_set);

        iv_head = (ImageView) findViewById(R.id.iv_head);
        bt_greet = (Button) findViewById(R.id.bt_greet);
        bt_complaint = (Button) findViewById(R.id.bt_complaint);

        tv_back.setOnClickListener(this);
        iv_set.setVisibility(View.VISIBLE);
        iv_set.setOnClickListener(this);

        iv_head.setOnClickListener(this);
        tv_set_notes_labels.setOnClickListener(this);
        bt_greet.setOnClickListener(this);
        bt_complaint.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_set:

                break;
            case R.id.iv_head://头像
                break;
            case R.id.tv_set_notes_labels://设置标签
                break;
            case R.id.bt_greet://打招呼

                break;
            case R.id.bt_complaint://投诉
                break;
        }
    }
}
