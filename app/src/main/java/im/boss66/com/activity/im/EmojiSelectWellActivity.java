package im.boss66.com.activity.im;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.EmojiWellAdapter;

/**
 * Created by Johnny on 2017/1/23.
 * 精选表情
 */
public class EmojiSelectWellActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvBack;
    private ImageView ivSeting;
    private TextView tvSearch;
    private ListView listView;
    private EmojiWellAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_select_well);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        ivSeting = (ImageView) findViewById(R.id.iv_more);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        listView = (ListView) findViewById(R.id.listView);
        tvBack.setOnClickListener(this);
        adapter = new EmojiWellAdapter(context);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_more://设置
                openActivity(EmojiMyActivity.class);
                break;
            case R.id.tv_search://收索

                break;
        }
    }
}
