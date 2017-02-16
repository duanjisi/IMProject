package im.boss66.com.activity.im;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.Utils.CommonDialogUtils;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/2/9.
 * 群聊天信息
 */
public class ChatGroupInformActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvBack;
    private RelativeLayout rlGroupName, rlCode, rlNotice, rlNick, rlBg, rlRecords, rlFile, rlClearRecords;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_informs);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);

        rlGroupName = (RelativeLayout) findViewById(R.id.rl_name);
        rlCode = (RelativeLayout) findViewById(R.id.rl_qr_code);
        rlNotice = (RelativeLayout) findViewById(R.id.rl_group_info);
        rlNick = (RelativeLayout) findViewById(R.id.rl_group_nick);
        rlBg = (RelativeLayout) findViewById(R.id.rl_chat_bg);
        rlRecords = (RelativeLayout) findViewById(R.id.rl_chat_content);
        rlFile = (RelativeLayout) findViewById(R.id.rl_chat_file);
        rlClearRecords = (RelativeLayout) findViewById(R.id.rl_chat_clear);

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
                openActivity(ChatGroupNameActivity.class);
                break;
            case R.id.rl_qr_code://群二维码
                openActivity(ChatGroupCodeActivity.class);
                break;
            case R.id.rl_group_info://群聊公告

                break;
            case R.id.rl_group_nick://我在本群的昵称
                showMotifyNickDialog();
                break;
            case R.id.rl_chat_bg://设置当前聊天背景
                openActivity(ChatBackgroundActivity.class);
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
            }
        }, context, msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
