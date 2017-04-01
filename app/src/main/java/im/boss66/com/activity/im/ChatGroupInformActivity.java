package im.boss66.com.activity.im;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.CommonDialogUtils;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.book.SelectContactsActivity;
import im.boss66.com.activity.discover.PersonalNearbyDetailActivity;
import im.boss66.com.adapter.MemberAdapter;
import im.boss66.com.db.MessageDB;
import im.boss66.com.db.dao.ConversationHelper;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.GroupInform;
import im.boss66.com.entity.MemberEntity;
import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.request.GroupCancelMemsRequest;
import im.boss66.com.http.request.GroupMembersRequest;
import im.boss66.com.widget.EaseSwitchButton;
import im.boss66.com.widget.MyGridView;

/**
 * Created by Johnny on 2017/2/9.
 * 群聊天信息
 */
public class ChatGroupInformActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = ChatGroupInformActivity.class.getSimpleName();
    private TextView tvBack, tvTitle, tvGroupName, tvGroupNotice, tvMyNick;
    private RelativeLayout rlGroupName, rlCode, rlNotice, rlNick, rlBg, rlRecords, rlFile, rlClearRecords;
    private EaseSwitchButton switchTop;
    private MyGridView gridView;
    private MemberAdapter adapter;
    private Button btnExit;
    private MessageDB mMsgDB;// 保存消息的数据库
    private String userid;
    private String groupid;
    private String MsgTab;
    private AccountEntity account;
    private GroupInform groupInform;
    private boolean isTop;
    private EaseSwitchButton switchDisturb;
    private EaseSwitchButton switchNick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_informs);
        initViews();
    }

    private void initViews() {
        account = App.getInstance().getAccount();
        groupid = getIntent().getExtras().getString("groupid", "");
        userid = App.getInstance().getUid();
        MsgTab = userid + "_" + groupid;
        mMsgDB = App.getInstance().getMessageDB();
        isTop = PreferenceUtils.getBoolean(context, PrefKey.CHAT_GROUP_INFORMS_FIRST + "/" + MsgTab, false);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvGroupName = (TextView) findViewById(R.id.tv_group_name);
        tvMyNick = (TextView) findViewById(R.id.tv_group_my_nick);
        tvGroupNotice = (TextView) findViewById(R.id.tv_group_notic);
        rlGroupName = (RelativeLayout) findViewById(R.id.rl_name);
        rlCode = (RelativeLayout) findViewById(R.id.rl_qr_code);
        rlNotice = (RelativeLayout) findViewById(R.id.rl_group_info);
        rlNick = (RelativeLayout) findViewById(R.id.rl_group_nick);
        rlBg = (RelativeLayout) findViewById(R.id.rl_chat_bg);
        rlRecords = (RelativeLayout) findViewById(R.id.rl_chat_content);
        rlFile = (RelativeLayout) findViewById(R.id.rl_chat_file);
        rlClearRecords = (RelativeLayout) findViewById(R.id.rl_chat_clear);
        switchTop = (EaseSwitchButton) findViewById(R.id.switch_btn_top);
        gridView = (MyGridView) findViewById(R.id.gridView);
        switchDisturb = (EaseSwitchButton) findViewById(R.id.switch_btn);
        switchNick = (EaseSwitchButton) findViewById(R.id.switch_btn_nick);
        adapter = new MemberAdapter(context);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new ItemClickListener());

        btnExit = (Button) findViewById(R.id.btn_exit);
        tvBack.setOnClickListener(this);
        rlGroupName.setOnClickListener(this);
        rlCode.setOnClickListener(this);
        rlNotice.setOnClickListener(this);
        rlNick.setOnClickListener(this);
        rlBg.setOnClickListener(this);
        rlRecords.setOnClickListener(this);
        rlFile.setOnClickListener(this);
        rlClearRecords.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        switchTop.setOnClickListener(this);
        switchDisturb.setOnClickListener(this);
        switchNick.setOnClickListener(this);
        boolean showNick = PreferenceUtils.getBoolean(this, PrefKey.SHOW_NICK_NAME + groupid, true);
        if (showNick) {
            switchNick.openSwitch();
        } else {
            switchNick.closeSwitch();
        }

        boolean isopen = PreferenceUtils.getBoolean(this, PrefKey.AVOID_DISTURB + groupid, false);
        if (isopen) {
            switchDisturb.openSwitch();
        } else {
            switchDisturb.closeSwitch();
        }
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
            this.groupInform = inform;
            if (isTop) {
                switchTop.openSwitch();
            } else {
                switchTop.closeSwitch();
            }
            tvMyNick.setText(account.getUser_name());
            tvGroupName.setText(inform.getName());
            tvGroupNotice.setText(inform.getNotice());
            ArrayList<MemberEntity> list = inform.getMembers();
            if (list.size() != 0) {
                tvTitle.setText("聊天信息(" + list.size() + ")");
            } else {
                tvTitle.setText("聊天信息");
            }

            if (userid.equals(inform.getCreator())) {//是群主
                MemberEntity entity1 = new MemberEntity();
                entity1.setType(1);
                MemberEntity entity2 = new MemberEntity();
                entity2.setType(2);
                list.add(entity1);
                list.add(entity2);
            } else {
                MemberEntity entity1 = new MemberEntity();
                entity1.setType(1);
                list.add(entity1);
            }
            if (list != null && list.size() != 0) {
                adapter.initData(list);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.btn_exit:
                showExitGroup();
                break;
            case R.id.rl_name://群聊名称
                if (groupInform != null) {
                    Intent it = new Intent(context, ChatGroupNameActivity.class);
                    it.putExtra("obj", groupInform);
                    it.putExtra("isNotice", false);
                    startActivityForResult(it, 100);
                }
                break;
            case R.id.rl_qr_code://群二维码
                if (groupInform != null) {
                    Intent intent = new Intent(context, ChatGroupCodeActivity.class);
                    intent.putExtra("groupid", groupid);
                    intent.putExtra("snap", groupInform.getSnap());
                    intent.putExtra("name", groupInform.getName());
                    startActivity(intent);
                }
                break;
            case R.id.rl_group_info://群聊公告
                if (groupInform != null) {
                    if (groupInform.getCreator().equals(account.getUser_id())) {
                        Intent it = new Intent(context, ChatGroupNameActivity.class);
                        it.putExtra("obj", groupInform);
                        it.putExtra("isNotice", true);
                        startActivityForResult(it, 101);
                    } else {
                        showToast("仅群主可编辑公告", true);
                    }
                }
                break;
            case R.id.rl_group_nick://我在本群的昵称
                showMotifyNickDialog();
                break;
            case R.id.rl_chat_bg://设置当前聊天背景
                openActivity(ChatBackgroundActivity.class);
                break;
            case R.id.switch_btn_top://置顶聊天
                if (isTop) {
                    switchTop.closeSwitch();
                } else {
                    switchTop.openSwitch();
                    ConversationHelper.getInstance().sortTop(groupid);
                }
                isTop = !isTop;
                PreferenceUtils.putBoolean(context, PrefKey.CHAT_GROUP_INFORMS_FIRST + "/" + MsgTab, isTop);
                break;
            case R.id.rl_chat_content://查找聊天记录
                openActivity(ChatRecordsActivity.class);
                break;
            case R.id.rl_chat_file://聊天文件
                openActivity(ChatFileActivity.class);
                break;
            case R.id.rl_chat_clear://清空聊天记录
                showClearRecordsDialog();
                break;
            case R.id.switch_btn:
                boolean isOpen;
                if (switchDisturb.isSwitchOpen()) {
                    switchDisturb.closeSwitch();
                    isOpen = false;
                } else {
                    switchDisturb.openSwitch();
                    isOpen = true;
                }
                PreferenceUtils.putBoolean(this, PrefKey.AVOID_DISTURB + groupid, isOpen);
                break;
            case R.id.switch_btn_nick:
                boolean showNick;
                if (switchNick.isSwitchOpen()) {
                    switchNick.closeSwitch();
                    showNick = false;
                } else {
                    switchNick.openSwitch();
                    showNick = true;
                }
                PreferenceUtils.putBoolean(this, PrefKey.SHOW_NICK_NAME + groupid, showNick);
                Intent it = new Intent(Constants.Action.REFRSH_CHAT_PAGER);
                LocalBroadcastManager.getInstance(context).sendBroadcast(it);
                break;
        }
    }

    private void showClearRecordsDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_dialog_notification, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        ((TextView) view.findViewById(R.id.title)).setText("提示");
        ((TextView) view.findViewById(R.id.message)).setText("确定清空此群的聊天记录吗?");
        view.findViewById(R.id.option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMsgDB.clearMsgDatas(MsgTab);
                Intent intent = new Intent(Constants.Action.REFRSH_CHAT_PAGER_DATAS);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showMotifyNickDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_dialog_nick, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        EditText editText = (EditText) view.findViewById(R.id.et_input);
        ((TextView) view.findViewById(R.id.title)).setText("提示");
        ((TextView) view.findViewById(R.id.message)).setText("确定清空此群的聊天记录吗?");
        view.findViewById(R.id.option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showExitGroup() {
        String msg = "删除并退出后，将不再接受此群聊信息?";
        CommonDialogUtils.showDialog(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDialogUtils.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDialogUtils.dismiss();
                if (groupInform != null) {
                    if (groupInform.getCreator().equals(account.getUser_id())) {
//                        Intent it = new Intent(context, ChatGroupNameActivity.class);
//                        it.putExtra("obj", groupInform);
//                        it.putExtra("isNotice", true);
//                        startActivityForResult(it, 101);
                    } else {
//                        showToast("仅群主可编辑公告", true);
                        outGroupRequest();
                    }
                }
            }
        }, context, msg);
    }

    private void outGroupRequest() {
        showLoadingDialog();
        GroupCancelMemsRequest request = new GroupCancelMemsRequest(TAG, groupid, userid);
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
        if (groupInform != null) {
            showToast("退出" + groupInform.getName() + "群成功!", true);
        }
        mMsgDB.clearMsgDatas(MsgTab);
        Intent intent = new Intent(Constants.Action.EXIT_CURRETN_GROUP_REFRESH_DATAS);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        finish();
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MemberEntity member = (MemberEntity) parent.getItemAtPosition(position);
            int type = member.getType();
            if (type == 1) {
                Intent intent = new Intent(context, SelectContactsActivity.class);
                intent.putExtra("groupid", groupid);
                intent.putExtra("isAddMember", true);
                intent.putExtra("user_ids", getStrs());
                startActivityForResult(intent, 0);
            } else if (type == 2) {
                Intent intent = new Intent(context, GroupAllMembersActivity.class);
                intent.putExtra("groupid", groupid);
                startActivityForResult(intent, 0);
            } else {
                Intent intent = new Intent(context, PersonalNearbyDetailActivity.class);
                intent.putExtra("classType", "ChatGroupInformActivity");
                intent.putExtra("userid", member.getUserid());
                startActivity(intent);
            }
        }
    }

    private String getStrs() {
        StringBuilder sb = new StringBuilder();
        ArrayList<MemberEntity> list = (ArrayList<MemberEntity>) adapter.getData();
        for (int i = 0; i < list.size(); i++) {
            MemberEntity entity = list.get(i);
            sb.append(entity.getUserid() + ",");
        }
        String str = sb.toString();
        if (!str.contains(",")) {
            return "";
        } else {
            return str.substring(0, str.lastIndexOf(","));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    requestMembers();
                    break;
                case 100://更新名称
                    if (data != null) {
                        String name = data.getStringExtra("data");
                        if (name != null && !name.equals("")) {
                            tvGroupName.setText(name);
                        }
                    }
                    break;
                case 101://更新群公告
                    if (data != null) {
                        String name = data.getStringExtra("data");
                        if (name != null && !name.equals("")) {
                            tvGroupNotice.setText(name);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
