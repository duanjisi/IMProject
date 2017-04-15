package im.boss66.com.widget.dialog;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.im.SelectConversationActivity;
import im.boss66.com.db.MessageDB;
import im.boss66.com.entity.MessageItem;

/**
 * Created by Johnny on 2017/4/14.
 */
public class ChatDialog extends BaseDialog implements View.OnClickListener {
    private TextView tvCopy;
    private TextView tvRewarding;
    private TextView tvDelete;
    private MessageDB mMsgDB;// 保存消息的数据库
    private String MsgTab;
    private String toUid;
    private MessageItem messageItem;
    private CharSequence message;
    private boolean isTxt = false;

    public ChatDialog(Context context, Intent intent) {
        super(context);
        dialog.setCanceledOnTouchOutside(true);
        initViews(intent);
    }


    private void initViews(Intent intent) {
        mMsgDB = App.getInstance().getMessageDB();
        tvCopy = (TextView) dialog.findViewById(R.id.tv_copy);
        tvRewarding = (TextView) dialog.findViewById(R.id.tv_rewarding);
        tvDelete = (TextView) dialog.findViewById(R.id.tv_delete);

        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) tvCopy.getLayoutParams();
        params.width= UIUtils.getScreenWidth(context)*8/10;
        tvCopy.setLayoutParams(params);

        LinearLayout.LayoutParams params2= (LinearLayout.LayoutParams) tvRewarding.getLayoutParams();
        params.width= UIUtils.getScreenWidth(context)*8/10;
        tvRewarding.setLayoutParams(params2);

        LinearLayout.LayoutParams params3= (LinearLayout.LayoutParams) tvDelete.getLayoutParams();
        params.width= UIUtils.getScreenWidth(context)*8/10;
        tvDelete.setLayoutParams(params3);

        tvCopy.setPadding(40,0,0,0);
        tvRewarding.setPadding(40,0,0,0);
        tvDelete.setPadding(40,0,0,0);
        tvCopy.setOnClickListener(this);
        tvRewarding.setOnClickListener(this);
        tvDelete.setOnClickListener(this);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            toUid = bundle.getString("toUid", "");
            messageItem = (MessageItem) bundle.getSerializable("item");
            message = bundle.getCharSequence("msg", "");
            isTxt = bundle.getBoolean("is_txt", false);
            MsgTab = App.getInstance().getUid() + "_" + toUid;
        }

        if (isTxt) {
            tvCopy.setVisibility(View.VISIBLE);
            tvCopy.setBackgroundResource(R.drawable.actionsheet_top_selector);
            tvRewarding.setBackgroundResource(R.drawable.actionsheet_middle_selector);
            tvDelete.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
            tvCopy.setPadding(40,0,0,0);
            tvRewarding.setPadding(40,0,0,0);
            tvDelete.setPadding(40,0,0,0);
        } else {
            tvCopy.setVisibility(View.GONE);
            tvRewarding.setBackgroundResource(R.drawable.actionsheet_top_selector);
            tvDelete.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
            tvCopy.setPadding(40,0,0,0);
            tvRewarding.setPadding(40,0,0,0);
            tvDelete.setPadding(40,0,0,0);
        }
    }


    @Override
    protected int getView() {
        return R.layout.dialog_chat;
    }

    @Override
    protected int getDialogStyleId() {
        return R.style.dialog_ios_style;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy:
                if (!message.equals("")) {
                    ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(message);
                    Toast.makeText(context, "已复制", Toast.LENGTH_SHORT);
                }
                break;
            case R.id.tv_rewarding:
                if (messageItem != null) {
                    Intent intent = new Intent(context, SelectConversationActivity.class);
                    intent.putExtra("item", messageItem);
                    context.startActivity(intent);
                }
                break;
            case R.id.tv_delete:
                if (messageItem != null) {
                    mMsgDB.clearMsgItem(messageItem, MsgTab);
                    Intent intent = new Intent(Constants.Action.REMOVE_CHAT_MESSAGE_DATA);
                    intent.putExtra("msg_obj", messageItem);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                break;
        }
        dismiss();
    }

    public void showDialog() {
        if (dialog != null && !isShowing()) {
            show();
        }
    }
}
