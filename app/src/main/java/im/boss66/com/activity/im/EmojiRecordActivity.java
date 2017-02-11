package im.boss66.com.activity.im;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.EmojiRecordAdapter;
import im.boss66.com.db.dao.EmoGroupHelper;
import im.boss66.com.entity.EmoGroup;

/**
 * 表情购买记录
 * Created by Johnny on 2017/2/11.
 */
public class EmojiRecordActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvBack;
    private ListView listView;
    private EmojiRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_record);
        initViews();
    }

    private void initViews() {
        listView = (ListView) findViewById(R.id.listView);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvBack.setOnClickListener(this);
        adapter = new EmojiRecordAdapter(context);
        listView.setAdapter(adapter);
        initDatas();
    }

    private void initDatas() {
        ArrayList<EmoGroup> groups = (ArrayList<EmoGroup>) EmoGroupHelper.getInstance().query();
        if (groups != null && groups.size() != 0) {
            adapter.initData(groups);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
