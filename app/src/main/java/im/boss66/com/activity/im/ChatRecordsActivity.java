package im.boss66.com.activity.im;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/2/14.
 * 聊天记录
 */
public class ChatRecordsActivity extends BaseActivity {

    private TextView tvBack;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_records);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        editText = (EditText) findViewById(R.id.et_keyword);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
