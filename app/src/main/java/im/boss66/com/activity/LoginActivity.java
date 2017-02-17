package im.boss66.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;

import java.util.Map;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.LoginRequest;
import im.boss66.com.http.request.QQLoginRequest;
import im.boss66.com.http.request.WXLoginRequest;

/**
 * Created by Johnny on 2017/1/16.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = LoginActivity.class.getSimpleName();
    private EditText etAccount, etPws;
    private Button btnLogin;
    private TextView tvRegister, tvForget;
    private ImageView ivQQ, ivWX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etAccount = (EditText) findViewById(R.id.et_account);
        etPws = (EditText) findViewById(R.id.et_psw);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        tvForget = (TextView) findViewById(R.id.tv_forget_pws);
        ivQQ = (ImageView) findViewById(R.id.iv_qq);
        ivWX = (ImageView) findViewById(R.id.iv_weixin);
        tvRegister.setOnClickListener(this);
        tvForget.setOnClickListener(this);
        ivWX.setOnClickListener(this);
        ivQQ.setOnClickListener(this);

        addQQQZonePlatform();
        addWXPlatform();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = etAccount.getText().toString().trim();
                String pass = etPws.getText().toString().trim();
//                if (!TextUtils.isEmpty(uid1) && !TextUtils.isEmpty(uid2)) {
//                    Intent intent = new Intent(context, MainActivity.class);
//                    intent.putExtra("uid1", uid1);
//                    intent.putExtra("uid2", uid2);
//                    startActivity(intent);
//                }
                login(account, pass);
            }
        });
    }

    private void login(String number, String pass) {
        if (TextUtils.isEmpty(number)) {
            showToast("登录帐号不能为空!", true);
            return;
        }
        if (!UIUtils.isMobile(number)) {
            showToast("手机号格式不正确!", true);
            return;
        }

        if (TextUtils.isEmpty(pass)) {
            showToast("登录密码不能为空", true);
            return;
        }
        showLoadingDialog();
        LoginRequest request = new LoginRequest(TAG, number, pass);
        request.send(new BaseDataRequest.RequestCallback<AccountEntity>() {
            @Override
            public void onSuccess(AccountEntity entity) {
                cancelLoadingDialog();
                loginSuccessed(entity);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private void loginSuccessed(AccountEntity entity) {
        Log.i("info", "===name:" + entity.getUser_name() + "\n" + "id:" + entity.getUser_id());
        App.getInstance().initUser(entity);
        showToast("登录成功!", true);
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
    }

    private void ThirdLogin(SHARE_MEDIA platform, final String type) {
        mController.doOauthVerify(LoginActivity.this, platform,
                new SocializeListeners.UMAuthListener() {

                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                        Toast.makeText(LoginActivity.this, "授权开始",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(SocializeException e,
                                        SHARE_MEDIA platform) {
                        showToast(e.getMessage(), true);
                        Toast.makeText(LoginActivity.this, "授权失败",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
                        // 获取uid
                        Log.i("info", "Bundle:" + printBundle(value));
                        String uid = value.getString("uid");
                        String openid = value.getString("openid");
                        String access_token = value.getString("access_token");
                        String unionid = value.getString("unionid");
                        if (!TextUtils.isEmpty(uid)
                                && !TextUtils.isEmpty(openid)
                                && !TextUtils.isEmpty(access_token)) {
                            // uid不为空，获取用户信息
                            getUserInfo(platform, openid, unionid, access_token, type);
                        } else {
                            Toast.makeText(LoginActivity.this, "授权失败...",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        Toast.makeText(LoginActivity.this, "授权取消",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUserInfo(SHARE_MEDIA platform
            , final String openid
            , final String unionid
            , final String access_token
            , final String type) {
        mController.getPlatformInfo(LoginActivity.this, platform,
                new SocializeListeners.UMDataListener() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        if (info != null) {
                            String thusername = "";
                            String avatar = "";
                            if (info.containsKey("nickname")) {
                                thusername = (String) info.get("nickname");
                            } else if (info.containsKey("screen_name")) {
                                thusername = (String) info.get("screen_name");
                            }
                            if (info.containsKey("profile_image_url")) {
                                avatar = (String) info.get("profile_image_url");
                            } else if (info.containsKey("headimgurl")) {
                                avatar = (String) info.get("headimgurl");
                            }
                            Log.i("info", "===info:" + info.toString());
                            Log.i("info", "===thusername:" + thusername + "\n" + "avatar:" + avatar);
                            if (thusername != null && !thusername.equals("") && !avatar.equals("")) {
                                ThirdLoginPathform(type, access_token, avatar, openid, unionid, thusername);
                            }
                        }
                    }
                });
    }

    private void ThirdLoginPathform(String from, String token, String avatar, String open_id, String unionid, String user_name) {
        Log.i("info", "=====from:" + from + "\n" + "token:" + token + "\n" + "avatar:" + avatar + "\n" + "open_id:" + open_id + "\n" + "user_name:" + user_name);
        BaseDataRequest request = null;
        if (from.equals("wx")) {
            request = new WXLoginRequest(TAG, unionid, user_name, avatar);
        } else {
            request = new QQLoginRequest(TAG, token, user_name, avatar);
        }
        if (request != null) {
            request.send(new BaseDataRequest.RequestCallback<AccountEntity>() {
                @Override
                public void onSuccess(AccountEntity entity) {
                    loginSuccessed(entity);
                }

                @Override
                public void onFailure(String msg) {
                    showToast(msg, true);
                }
            });
        }
    }

    private String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
        }
        return sb.toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                openActivity(RegisterActivity.class);
                break;
            case R.id.tv_forget_pws:
                openActivity(ForgetPwsActivity.class);
                break;
            case R.id.iv_weixin:
                ThirdLogin(SHARE_MEDIA.WEIXIN, "wx");
                break;
            case R.id.iv_qq:
                ThirdLogin(SHARE_MEDIA.QQ, "qq");
                break;
        }
    }
}
