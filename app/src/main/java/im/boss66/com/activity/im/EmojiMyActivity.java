package im.boss66.com.activity.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.SessionInfo;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.MyEmojiAdapter;
import im.boss66.com.db.dao.EmoGroupHelper;
import im.boss66.com.entity.EmoGroup;

/**
 * Created by Johnny on 2017/1/23.
 * 我的表情
 */
public class EmojiMyActivity extends BaseActivity implements View.OnClickListener, Observer {

    private TextView tvBack, tvSort;
    private RelativeLayout rl_add_emoji, rl_emoji_buy;
    private ListView listView;
    private MyEmojiAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Session.getInstance().addObserver(this);
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
        initDatas();
    }


    private void initDatas() {
//        ArrayList<MyEmojiEntity> list = new ArrayList<>();
//        MyEmojiEntity emo1 = new MyEmojiEntity();
//        emo1.setIcon("http://pics.sc.chinaz.com/Files/pic/icons128/5858/265.png");
//        emo1.setTitle("哭笑不得篇");
//
//        MyEmojiEntity emo2 = new MyEmojiEntity();
//        emo2.setIcon("http://pics.sc.chinaz.com/Files/pic/icons128/5858/261.png");
//        emo2.setTitle("很酷篇");
//
//        MyEmojiEntity emo3 = new MyEmojiEntity();
//        emo3.setIcon("http://pics.sc.chinaz.com/Files/pic/icons128/5858/264.png");
//        emo3.setTitle("搞怪可爱篇");
//
//
//        MyEmojiEntity emo4 = new MyEmojiEntity();
//        emo4.setIcon("http://pics.sc.chinaz.com/Files/pic/icons128/5858/266.png");
//        emo4.setTitle("苦命篇");
//
//
//        MyEmojiEntity emo5 = new MyEmojiEntity();
//        emo5.setIcon("http://pics.sc.chinaz.com/Files/pic/icons128/5858/259.png");
//        emo5.setTitle("激萌篇");
//
//        list.add(emo1);
//        list.add(emo2);
//        list.add(emo3);
//        list.add(emo4);
//        list.add(emo5);
        ArrayList<EmoGroup> list = (ArrayList<EmoGroup>) EmoGroupHelper.getInstance().query();
        if (list != null && list.size() != 0) {
            adapter.initData(list);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back://返回
                finish();
                break;
            case R.id.iv_more://排序
                Intent intent = new Intent(context, EmojiManagerActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.rl_add_emoji://添加表情
                openActivity(EmojiAddActivity.class);
                break;
            case R.id.rl_emoji_buy://表情购买记录
                openActivity(EmojiRecordActivity.class);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            ArrayList<EmoGroup> list = (ArrayList<EmoGroup>) EmoGroupHelper.getInstance().query();
            if (list != null && list.size() != 0) {
                adapter.initData(list);
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        SessionInfo sin = (SessionInfo) o;
        if (sin.getAction() == Session.ACTION_DELETE_CURRENT_EMOJI_GROUP) {
            deleteItem((String) sin.getData());
        }
    }

    private void deleteItem(String groupid) {
        ArrayList<EmoGroup> groups = (ArrayList<EmoGroup>) adapter.getData();
        for (EmoGroup group : groups) {
            if (group.getGroup_id().equals(groupid)) {
                EmoGroupHelper.getInstance().delete(groupid);
                adapter.remove(group);
                break;
            }
        }
    }
}
