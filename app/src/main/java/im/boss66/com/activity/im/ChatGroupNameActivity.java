package im.boss66.com.activity.im;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.GroupInform;
import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.request.UpdateGroupInformRequest;

/**
 * Created by Johnny on 2017/1/21.
 * 群聊名称
 */
public class ChatGroupNameActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = ChatGroupInformActivity.class.getSimpleName();
    private TextView tvBack, tvComplete, tvTitle, tvTag;
    private EditText etName;
    private GroupInform inform;
    private boolean isNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group_name);
        initViews();
    }

    private void initViews() {
        inform = getIntent().getParcelableExtra("obj");
        isNotice = getIntent().getBooleanExtra("isNotice", false);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTag = (TextView) findViewById(R.id.tv_tag);
        tvComplete = (TextView) findViewById(R.id.tv_complete);
        etName = (EditText) findViewById(R.id.et_group_name);
        tvComplete.setOnClickListener(this);
        tvBack.setOnClickListener(this);

        if (isNotice) {
            tvTitle.setText("群公告");
            tvTag.setText("群聊公告");
            etName.setHint("请编辑群公告");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_complete:
                requestUpdate();
                break;
        }
    }

    private void requestUpdate() {
        if (inform != null) {
            final String string = getText(etName);
            if (TextUtils.isEmpty(string)) {
                if (isNotice) {
                    showToast("群公告不能为空!", true);
                } else {
                    showToast("名称不能为空!", true);
                }
                return;
            }
            showLoadingDialog();
            UpdateGroupInformRequest request;
            if (isNotice) {
                request = new UpdateGroupInformRequest(TAG, string, inform.getName());
            } else {
                request = new UpdateGroupInformRequest(TAG, inform.getNotice(), string);
            }
            request.send(new BaseModelRequest.RequestCallback<String>() {
                @Override
                public void onSuccess(String pojo) {
                    cancelLoadingDialog();
                    Intent intent = new Intent();
                    intent.putExtra("data", string);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFailure(String msg) {
                    cancelLoadingDialog();
                    showToast(msg, true);
                }
            });
        }
    }
}
