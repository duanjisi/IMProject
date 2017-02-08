package im.boss66.com.activity.discover;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 朋友圈新消息列表
 */
public class CircleMessageListActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back,tv_title,tv_right;
    private LRecyclerView rv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_message_list);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);
        rv_content = (LRecyclerView) findViewById(R.id.rv_content);
        tv_title.setText(getString(R.string.main_homepager));
        tv_right.setText(getString(R.string.vacum_up));
        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right:

                break;
        }
    }
}
