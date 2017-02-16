package im.boss66.com.activity.im;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.EaseSwitchButton;

/**
 * Created by Johnny on 2017/2/15.
 */
public class ChatInformActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvBack, tvName;
    private ImageView ivIcon;
    private RelativeLayout rl_chat_file, rl_chat_bg, rl_chat_records, rl_clear_records, rl_complain;
    private EaseSwitchButton switchTop, switchDisturb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informs);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);

        switchTop = (EaseSwitchButton) findViewById(R.id.switch_btn_top);
        switchDisturb = (EaseSwitchButton) findViewById(R.id.switch_btn);

        rl_chat_file = (RelativeLayout) findViewById(R.id.rl_chat_file);
        rl_chat_bg = (RelativeLayout) findViewById(R.id.rl_group_nick);
        rl_chat_records = (RelativeLayout) findViewById(R.id.rl_chat_records);
        rl_clear_records = (RelativeLayout) findViewById(R.id.rl_chat_clear);
        rl_complain = (RelativeLayout) findViewById(R.id.rl_bottom);

        tvBack.setOnClickListener(this);
        rl_chat_file.setOnClickListener(this);
        rl_chat_bg.setOnClickListener(this);
        rl_chat_records.setOnClickListener(this);
        rl_clear_records.setOnClickListener(this);
        rl_complain.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;

            case R.id.rl_chat_file://聊天文件
                openActivity(ChatFileActivity.class);
                break;

            case R.id.rl_group_nick://设置当前聊天背景
                openActivity(ChatBackgroundActivity.class);
                break;

            case R.id.rl_chat_records://查找聊天记录
                openActivity(ChatRecordsActivity.class);
                break;

            case R.id.rl_chat_clear://清空聊天记录
                showClearRecordsDialog();
                break;

            case R.id.rl_bottom://投诉

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
}
