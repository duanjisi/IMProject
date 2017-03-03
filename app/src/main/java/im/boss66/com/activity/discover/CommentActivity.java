package im.boss66.com.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.CircleCommentCreateRequest;

/**
 * 相册评论
 */
public class CommentActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = CommentActivity.class.getSimpleName();
    private TextView tv_back, tv_title, tv_right;
    private EditText et_comment;
    private String feedId, feed_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
    }

    private void initView() {
        et_comment = (EditText) findViewById(R.id.et_comment);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_back.setText("取消");
        tv_title.setText("评论");
        tv_right.setText("发送");
        tv_right.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                feed_uid = bundle.getString("feed_uid");
                feedId = bundle.getString("feedId");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right:
                String content = et_comment.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    showToast("评论不能为空", false);
                    return;
                } else {
                    createComment(content);
                }
                break;
        }
    }

    //发表评论
    private void createComment(String content) {
        CircleCommentCreateRequest request = new CircleCommentCreateRequest(TAG, feedId, content, "0", feed_uid);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                if (!TextUtils.isEmpty(pojo)) {
                    try {
                        JSONObject obj = new JSONObject(pojo);
                        if (obj != null) {
                            int code = obj.getInt("code");
                            String msg = obj.getString("message");
                            if (code == 1) {
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                showToast(msg, false);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, false);
            }
        });
    }

}
