package im.boss66.com.activity.im;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.db.MessageDB;
import im.boss66.com.entity.MessageItem;

/**
 * Created by Johnny on 2016/10/3.
 */
public class CopyTextActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvCopy;
    private TextView tvRewarding;
    private TextView tvDelete;
    private MessageDB mMsgDB;// 保存消息的数据库
    private String MsgTab;
    private String toUid;
    private MessageItem messageItem;
    private CharSequence message;
    private boolean isTxt = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_copy_text);
        setContentView(R.layout.dialog_chat);
        initViews();
    }

    private void initViews() {
        mMsgDB = App.getInstance().getMessageDB();
        tvCopy = (TextView) findViewById(R.id.tv_copy);
        tvRewarding = (TextView) findViewById(R.id.tv_rewarding);
        tvDelete = (TextView) findViewById(R.id.tv_delete);

        tvCopy.setOnClickListener(this);
        tvRewarding.setOnClickListener(this);
        tvDelete.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
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
        } else {
            tvCopy.setVisibility(View.GONE);
            tvRewarding.setBackgroundResource(R.drawable.actionsheet_top_selector);
            tvDelete.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy:
                if (!message.equals("")) {
                    ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(message);
                    showToast("已复制", true);
                }
                break;
            case R.id.tv_rewarding:
                if (messageItem != null) {
                    Intent intent = new Intent(context, SelectConversationActivity.class);
                    intent.putExtra("item", messageItem);
                    startActivity(intent);
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
        finish();
    }

//    public void copy(View view) {
//        if (!message.equals("")) {
//            ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//            cmb.setText(message);
//            showToast("已复制", true);
//        }
//        finish();
//    }
//    public void rewarding(View view) {
//
//    }
}
