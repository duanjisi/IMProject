package im.boss66.com.activity.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.DragListAdapter;
import im.boss66.com.db.dao.EmoGroupHelper;
import im.boss66.com.entity.EmoGroup;
import im.boss66.com.widget.DragListView;

/**
 * Created by Johnny on 2017/2/11.
 */
public class EmojiManagerActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvBack, tv_ok;
    private DragListView listView;
    private DragListAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_manager);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        listView = (DragListView) findViewById(R.id.listView);
        tvBack.setOnClickListener(this);
        tv_ok.setOnClickListener(this);

        ArrayList<EmoGroup> list = (ArrayList<EmoGroup>) EmoGroupHelper.getInstance().query();
        if (list != null && list.size() != 0) {
            Collections.reverse(list);
            mAdapter = new DragListAdapter(context, list);
            listView.setAdapter(mAdapter);
            listView.setOrder(true);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_ok:
                sortList();
                break;
        }
    }

    private void sortList() {
        ArrayList<EmoGroup> list = (ArrayList<EmoGroup>) mAdapter.getList();
        if (list != null && list.size() != 0) {
            EmoGroupHelper.getInstance().saveSortList(list);
        }
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
