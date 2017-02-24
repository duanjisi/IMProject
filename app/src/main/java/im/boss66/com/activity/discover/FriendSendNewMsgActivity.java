package im.boss66.com.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.widget.MultiImageView;

/**
 * 朋友圈发消息
 */
public class FriendSendNewMsgActivity extends BaseActivity implements View.OnClickListener {

    private MultiImageView multiImagView;
    private EditText et_tx;
    private TextView tv_back, tv_title,tv_right;
    private String SEND_TYPE_TEXT = "text";
    private String SEND_TYPE_PHOTO = "photo";
    private int sceenW;
    private String access_token,who_see = "0";//who_see----0:公开 所有朋友可见 1:私密 仅自己可见 2:部分可见 3:不给谁看
    private String who_see_ext = "all";//若who_see=0 ，此字段值为all 若who_see=1，
    // 此字段值为myself 若who_see=2, 此字段值为可见的好友ID 若who_see=3，此字段值为不可见的好友ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_send_new_msg);
        initView();
    }

    private void initView() {
        access_token = App.getInstance().getAccount().getAccess_token();
        sceenW = UIUtils.getScreenWidth(this);
        multiImagView= (MultiImageView) findViewById(R.id.multiImagView);
        et_tx = (EditText) findViewById(R.id.et_tx);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.send_text));
        tv_right.setText(getString(R.string.send));
        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        tv_right.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) et_tx.getLayoutParams();

        Intent intent = getIntent();
        if (intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                String sendType = bundle.getString("sendType");
                if (!TextUtils.isEmpty(sendType)){
                    if (SEND_TYPE_TEXT.equals(sendType)){
                        multiImagView.setVisibility(View.GONE);
                        params.height = sceenW/4;
                        et_tx.setLayoutParams(params);
                    }else if(SEND_TYPE_PHOTO.equals(sendType)){
                        multiImagView.setVisibility(View.VISIBLE);
                        params.height = sceenW/6;
                        et_tx.setLayoutParams(params);
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
                sendPhotoText();
                break;
        }
    }

    private void sendPhotoText(){
        String content = et_tx.getText().toString().trim();
        showLoadingDialog();
        String url = HttpUrl.CREATE_CIRCLE_PHOTO_TX;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token",access_token);
        params.addBodyParameter("who_see",who_see);
        params.addBodyParameter("who_see_ext",who_see_ext);
        if (!TextUtils.isEmpty(content)){
            params.addBodyParameter("content",content);
        }
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)){
                    try {
                        JSONObject obj = new JSONObject(result);
                        if (obj != null){
                            int code = obj.getInt("code");
                            String msg = obj.getString("message");
                            if (code == 1){
                                showToast("发布成功",false);
                                setResult(RESULT_OK);
                                finish();
                            }else {
                                showToast(msg,false);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                showToast(s,false);
            }
        });
    }
}
