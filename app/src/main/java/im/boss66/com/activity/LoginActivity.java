package im.boss66.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.LoginRequest;

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid1 = etAccount.getText().toString().trim();
                String uid2 = etPws.getText().toString().trim();
                if (!TextUtils.isEmpty(uid1) && !TextUtils.isEmpty(uid2)) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("uid1", uid1);
                    intent.putExtra("uid2", uid2);
                    startActivity(intent);
                }
            }
        });
    }

    private void requestLogin() {
        LoginRequest request = new LoginRequest(TAG, "username", "password");
        showLoadingDialog();
        request.send(new BaseDataRequest.RequestCallback<AccountEntity>() {
            @Override
            public void onSuccess(AccountEntity pojo) {
                cancelLoadingDialog();
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg,true);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                openActivity(RegisterActivity.class);
                break;
            case R.id.tv_forget_pws:

                break;
            case R.id.iv_weixin:

                break;
            case R.id.iv_qq:

                break;
        }
    }
}
