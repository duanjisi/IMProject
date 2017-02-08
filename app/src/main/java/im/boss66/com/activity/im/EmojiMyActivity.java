package im.boss66.com.activity.im;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.MyEmojiAdapter;

/**
 * Created by Johnny on 2017/1/23.
 * 我的表情
 */
public class EmojiMyActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvBack, tvSort;
    private RelativeLayout rl_add_emoji, rl_emoji_buy;
    private ListView listView;
    private MyEmojiAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_my);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvSort = (TextView) findViewById(R.id.iv_more);
        rl_add_emoji = (RelativeLayout) findViewById(R.id.rl_add_emoji);
        rl_emoji_buy = (RelativeLayout) findViewById(R.id.rl_emoji_buy);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new MyEmojiAdapter(context);
        listView.setAdapter(adapter);
        tvBack.setOnClickListener(this);
        tvSort.setOnClickListener(this);
        rl_add_emoji.setOnClickListener(this);
        rl_emoji_buy.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back://返回
                finish();
                break;
            case R.id.iv_more://排序

                break;
            case R.id.rl_add_emoji://添加表情

                break;
            case R.id.rl_emoji_buy://表情购买记录

                break;
        }
    }
}
