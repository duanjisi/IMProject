package im.boss66.com.activity.im;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.GroupMemberAdapter;
import im.boss66.com.entity.GroupInform;
import im.boss66.com.entity.MemberEntity;
import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.request.GroupCancelMemsRequest;
import im.boss66.com.http.request.GroupMembersRequest;

/**
 * Created by Johnny on 2017/3/1.
 */
public class GroupAllMembersActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = GroupAllMembersActivity.class.getSimpleName();
    private TextView tvBack, tvDelete;
    private ArrayList<MemberEntity> members;
    private GroupMemberAdapter mAdapter;
    private ListView listView;
    private ImageView iv_search;
    private EditText et_keyWords;
    private String groupid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_all_members);
        initViews();
        initListView();
    }

    private void initViews() {
        groupid = getIntent().getExtras().getString("groupid", "");
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvDelete = (TextView) findViewById(R.id.iv_delete);
        tvDelete.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        iv_search = (ImageView) findViewById(R.id.iv_search);
        et_keyWords = (EditText) findViewById(R.id.et_keywords);
        et_keyWords.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                matchMember(str);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

//        iv_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String keyWords = getText(et_keyWords);
//                if (!TextUtils.isEmpty(keyWords)) {
//
//                }
//            }
//        });
//        et_keyWords.setFocusable(true);
//        et_keyWords.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
//        et_keyWords.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
//                    String keyWords = getText(et_keyWords);
//                    if (!TextUtils.isEmpty(keyWords)) {
//
//                    } else {
//
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);
//        listView.addFooterView(rootView);
        mAdapter = new GroupMemberAdapter(context);
        mAdapter.setShow(true);
        listView.setOnItemClickListener(new ItemClickListener());
        listView.setAdapter(mAdapter);
//        listView.setOnScrollListener(this);
        requestMembers();
    }

    private void requestMembers() {
        showLoadingDialog();
        GroupMembersRequest request = new GroupMembersRequest(TAG, groupid);
        request.send(new BaseModelRequest.RequestCallback<GroupInform>() {
            @Override
            public void onSuccess(GroupInform pojo) {
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

    private void bindDatas(GroupInform inform) {
        if (inform != null) {
            ArrayList<MemberEntity> list = inform.getMembers();
            if (list != null && list.size() != 0) {
                this.members = list;
                mAdapter.initData(list);
            }
        }
    }

    private void matchMember(String str) {
        if (!str.equals("")) {
            ArrayList<MemberEntity> list = new ArrayList<>();
            for (int i = 0; i < members.size(); i++) {
                MemberEntity entity = members.get(i);
                if (entity.getNickname().contains(str)) {
                    list.add(entity);
                }
            }
            mAdapter.initData(list);
        } else {
            if (members != null) {
                mAdapter.initData(members);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_delete:
                outGroupMemberRequest();
                break;
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MemberEntity member = (MemberEntity) parent.getItemAtPosition(position);
            boolean isChecked = member.isChecked();
            member.setChecked(!isChecked);
            mAdapter.notifyDataSetChanged();
//            SocialMemberEntity member = (SocialMemberEntity) parent.getItemAtPosition(position);
//            if (isShow) {
//                boolean isChecked = member.isChecked();
//                member.setChecked(!isChecked);
//                mAdapter.notifyDataSetChanged();
//            } else {
//                if (!member.getUser_id().equals(account.getUser_id())) {
//                    Intent intent = new Intent(context, PersonPagerActivity.class);
//                    intent.putExtra("user_id", member.getUser_id());
//                    startActivity(intent);
//                } else {
//                    Intent intent = new Intent(context, MyBrightActivity.class);
//                    startActivity(intent);
//                }
//            }
        }
    }

    private void outGroupMemberRequest() {
        String member_ids = getMemberIds();
        Log.i("info", "member_ids:" + member_ids);
        if (member_ids.equals("")) {
            showToast("请选择群成员!", true);
            return;
        }
        showLoadingDialog();
        GroupCancelMemsRequest request = new GroupCancelMemsRequest(TAG, groupid, member_ids);
        request.send(new BaseModelRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                cancelLoadingDialog();
                onDeleteHandler();
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private void onDeleteHandler() {
        showToast("删除成功!", true);
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private String getMemberIds() {
        StringBuilder sb = new StringBuilder();
        ArrayList<MemberEntity> list = (ArrayList<MemberEntity>) mAdapter.getData();
        for (int i = 0; i < list.size(); i++) {
            MemberEntity user = list.get(i);
            if (user.isChecked()) {
                sb.append(user.getUserid() + ",");
            }
        }

        String str = sb.toString();
        if (!str.contains(",")) {
            return "";
        } else {
            return str.substring(0, str.lastIndexOf(","));
        }
    }
}
