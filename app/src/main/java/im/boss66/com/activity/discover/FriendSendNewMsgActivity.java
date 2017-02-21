package im.boss66.com.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 朋友圈发消息
 */
public class FriendSendNewMsgActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_tx;
    private TextView tv_back, tv_title,tv_right;
    private String SEND_TYPE_TEXT = "text";
    private String SEND_TYPE_PHOTO = "photo";
    private int sceenW;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_send_new_msg);
        initView();
    }

    private void initView() {
        sceenW = UIUtils.getScreenWidth(this)/4;
        et_tx = (EditText) findViewById(R.id.et_tx);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.send_text));
        tv_right.setText(getString(R.string.send));
        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) et_tx.getLayoutParams();

        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                String sendType = bundle.getString("sendType");
                if (!TextUtils.isEmpty(sendType)){
                    if (SEND_TYPE_TEXT.equals(sendType)){

                    }else if(SEND_TYPE_PHOTO.equals(sendType)){

                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right://发送

                break;
        }
    }
}
