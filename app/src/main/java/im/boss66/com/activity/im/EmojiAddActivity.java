package im.boss66.com.activity.im;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 我添加的表情
 * Created by Johnny on 2017/2/11.
 */
public class EmojiAddActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvBack, tvDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_add);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvDo = (TextView) findViewById(R.id.tv_ok);

        tvBack.setOnClickListener(this);
        tvDo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;

            case R.id.tv_ok:

                break;
        }
    }
}
