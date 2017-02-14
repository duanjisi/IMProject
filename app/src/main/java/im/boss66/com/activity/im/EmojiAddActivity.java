package im.boss66.com.activity.im;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

import im.boss66.com.R;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelector;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelectorActivity;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.PictureAdapter;
import im.boss66.com.widget.MyGridView;

/**
 * 我添加的表情
 * Created by Johnny on 2017/2/11.
 */
public class EmojiAddActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvBack, tvDo;
    private MyGridView gridView;
    private PictureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_add);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
//        tvDo = (TextView) findViewById(R.id.tv_ok);
        gridView = (MyGridView) findViewById(R.id.gridView);
        adapter = new PictureAdapter(context);
        adapter.setAddPager(true);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new ItemClickListener());
        tvBack.setOnClickListener(this);
//        tvDo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                setResult(RESULT_OK);
                finish();
                break;
//            case R.id.tv_ok:
//
//                break;
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String string = (String) parent.getItemAtPosition(position);
            if (string.equals("lastItem")) {
                MultiImageSelector.create(context).
                        showCamera(true).
                        count(1)
                        .start(EmojiAddActivity.this, 100);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 100) {
                ArrayList<String> selectPicList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (selectPicList != null && selectPicList.size() != 0) {
                    Log.i("info", "==========00000000000");
                    adapter.addItem2(selectPicList.get(0));
                }
            }
        }
    }
}
