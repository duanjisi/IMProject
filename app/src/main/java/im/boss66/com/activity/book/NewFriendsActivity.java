package im.boss66.com.activity.book;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import im.boss66.com.R;
import im.boss66.com.activity.AddFriendActivity;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.discover.PersonalNearbyDetailActivity;
import im.boss66.com.adapter.NewFriendsAdapter;
import im.boss66.com.entity.BaseFriends;
import im.boss66.com.entity.NewFriend;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.NewFriendsRequest;

/**
 * Created by Johnny on 2017/2/17.
 */
public class NewFriendsActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = NewFriendsActivity.class.getSimpleName();
    private TextView tvBack, tvMore, tvAddFriend;
    private RelativeLayout rl_search;
    private ListView listView;
    private TextView tv_search;
    private NewFriendsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvMore = (TextView) findViewById(R.id.iv_more);
        tvAddFriend = (TextView) findViewById(R.id.tv_add_contacts);
        tv_search = (TextView) findViewById(R.id.tv_search);
        rl_search = (RelativeLayout) findViewById(R.id.rl_tag);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new NewFriendsAdapter(context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ItemClickListener());

        tvBack.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        tvAddFriend.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        rl_search.setOnClickListener(this);
        requestFriends();
    }

    private void requestFriends() {
        showLoadingDialog();
        NewFriendsRequest request = new NewFriendsRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<BaseFriends>() {
            @Override
            public void onSuccess(BaseFriends pojo) {
                cancelLoadingDialog();
                bindDatas(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private void bindDatas(BaseFriends baseFriend) {
        if (baseFriend != null) {
            ArrayList<NewFriend> friends = baseFriend.getResult();
            if (friends != null && friends.size() != 0) {
                adapter.initData(friends);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_more:
                openActivity(AddFriendActivity.class);
                break;
            case R.id.tv_add_contacts:
                openActivity(PhoneContactsActivity.class);
                break;
            case R.id.tv_search:
                openActivity(QureAccountActivity.class);
                break;
            case R.id.rl_tag:
                break;
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            NewFriend friend = (NewFriend) adapterView.getItemAtPosition(i);
            Intent intent = new Intent(context, PersonalNearbyDetailActivity.class);
            intent.putExtra("friend", friend);
            startActivity(intent);
        }
    }
}
