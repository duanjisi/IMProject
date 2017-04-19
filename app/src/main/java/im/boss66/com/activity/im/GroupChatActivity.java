package im.boss66.com.activity.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.GroupMemAdapter;
import im.boss66.com.domain.EaseUser;
import im.boss66.com.entity.BaseGrpMember;
import im.boss66.com.entity.GroupEntity;
import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.request.GroupsRequest;

/**
 * Created by Johnny on 2017/1/21.
 * 群聊
 */
public class GroupChatActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = GroupChatActivity.class.getSimpleName();
    private LocalBroadcastReceiver mLocalBroadcastReceiver;
    private TextView tvBack, tvTips;
    private ListView listView;
    private GroupMemAdapter adapter;
    private String userid;
    private boolean isWarding = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        initViews();
    }

    private void initViews() {
        userid = App.getInstance().getUid();
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvTips = (TextView) findViewById(R.id.tv_tips);
        listView = (ListView) findViewById(R.id.listView);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isWarding = bundle.getBoolean("forwarding", false);
        }
        tvBack.setOnClickListener(this);
        mLocalBroadcastReceiver = new LocalBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Action.EXIT_CURRETN_GROUP_REFRESH_DATAS);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mLocalBroadcastReceiver, filter);
        adapter = new GroupMemAdapter(context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ItemClickListener());
        requestGroups();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }

    private void requestGroups() {
        showLoadingDialog();
        GroupsRequest request = new GroupsRequest(TAG, userid);
        request.send(new BaseModelRequest.RequestCallback<BaseGrpMember>() {
            @Override
            public void onSuccess(BaseGrpMember pojo) {
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

    private void refrshGroups() {
        GroupsRequest request = new GroupsRequest(TAG, userid);
        request.send(new BaseModelRequest.RequestCallback<BaseGrpMember>() {
            @Override
            public void onSuccess(BaseGrpMember pojo) {
                bindDatas(pojo);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
            }
        });
    }

    private void bindDatas(BaseGrpMember baseGrpMember) {
        if (baseGrpMember != null) {
            ArrayList<GroupEntity> groups = baseGrpMember.getData();
            if (groups != null && groups.size() != 0) {
                tvTips.setText(groups.size() + "个群聊");
                adapter.initData(groups);
            }
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            GroupEntity groupEntity = (GroupEntity) adapterView.getItemAtPosition(i);
            if (groupEntity != null) {
                if (isWarding) {
                    EaseUser easeUser = new EaseUser();
                    easeUser.setMsgType("group");
                    easeUser.setAvatar(groupEntity.getSnap());
                    easeUser.setInitialLetter("");
                    easeUser.setNick(groupEntity.getName());
                    easeUser.setUserName(groupEntity.getName());
                    easeUser.setUserid(groupEntity.getGroupid());
                    Intent intent = new Intent();
                    intent.putExtra("user", easeUser);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("isgroup", true);
                    intent.putExtra("toUid", groupEntity.getGroupid());
                    intent.putExtra("title", groupEntity.getName());
                    intent.putExtra("toAvatar", groupEntity.getSnap());
                    startActivity(intent);
                }
            }
        }
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.Action.EXIT_CURRETN_GROUP_REFRESH_DATAS.equals(action)) {
                refrshGroups();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(mLocalBroadcastReceiver);
    }
}
