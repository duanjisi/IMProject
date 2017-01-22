package im.boss66.com.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/1/21.
 * 群聊名称
 */
public class GroupChatNameActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvBack, tvComplete;
    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group_name);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvComplete = (TextView) findViewById(R.id.tv_complete);
        etName = (EditText) findViewById(R.id.et_group_name);
        tvComplete.setOnClickListener(this);
        tvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:

                break;
            case R.id.tv_complete:

                break;
        }
    }
}
