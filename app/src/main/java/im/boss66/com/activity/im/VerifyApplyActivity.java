package im.boss66.com.activity.im;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.AddFriendRequest;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_apply);
        initViews();
    }

    private void initViews() {
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

    private void addFriendRequest(String userid, String note) {
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
                finish();
                showToast("已发送!", true);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
                cancelLoadingDialog();
            }
        });
    }
}
