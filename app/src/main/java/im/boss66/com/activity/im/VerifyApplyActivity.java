package im.boss66.com.activity.im;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.http.BaseDataModel;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.AddFriendRequest;
import im.boss66.com.http.request.NotificationRequest;

/**
 * Created by Johnny on 2017/2/21.
 * 添加好友验证申请
 */
public class VerifyApplyActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = VerifyApplyActivity.class.getSimpleName();
    private TextView tvBack, tvSend;
    private EditText etNote;
    private ImageView ivClear;
    private String userid;
    private AccountEntity account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_apply);
        initViews();
    }

    private void initViews() {
        account = App.getInstance().getAccount();
        userid = getIntent().getExtras().getString("userid", "");
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvSend = (TextView) findViewById(R.id.tv_send);
        ivClear = (ImageView) findViewById(R.id.iv_clear);
        etNote = (EditText) findViewById(R.id.et_note);

        tvBack.setOnClickListener(this);
        tvSend.setOnClickListener(this);
        ivClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_send:
                String note = getText(etNote);
                if (!userid.equals("")) {
                    addFriendRequest(userid, note);
                }
                break;
            case R.id.iv_clear:
                etNote.setText("");
                break;
        }
    }

    private void addFriendRequest(final String userid, final String note) {
        if (TextUtils.isEmpty(note)) {
            showToast("验证申请为空!", true);
            return;
        }
        showLoadingDialog();
        AddFriendRequest request = new AddFriendRequest(TAG, userid, note);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                cancelLoadingDialog();
                String string = account.getUser_name() + "邀请你加为好友!";
                senNotification(userid, "chat", string);
                showToast("已发送!", true);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
                cancelLoadingDialog();
            }
        });
    }

    public void senNotification(String toUid, String msgType, String message) {
        if (toUid != null && !toUid.equals("")) {
            NotificationRequest request = new NotificationRequest("sendnotification", toUid, msgType, message);
            request.send(new BaseDataModel.RequestCallback<String>() {
                @Override
                public void onSuccess(String pojo) {
                    Log.i("info", "====通知发送完成!");
                    finish();
                }

                @Override
                public void onFailure(String msg) {

                }
            });
        }
    }
}
