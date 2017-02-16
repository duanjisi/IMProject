package im.boss66.com.activity.im;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/2/14.
 * 聊天文件
 */
public class ChatFileActivity extends BaseActivity {

    private TextView tvBack, tv_tips, tvSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_file);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        tvSelect = (TextView) findViewById(R.id.tv_selected);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
