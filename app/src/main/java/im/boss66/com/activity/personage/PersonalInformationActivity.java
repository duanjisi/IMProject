package im.boss66.com.activity.personage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.widget.CircleImageView;

/**
 * 个人信息
 */
public class PersonalInformationActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title, tv_back;
    private RelativeLayout rl_head_icon, rl_name, rl_qr_code, rl_sex, rl_area, rl_signature;
    private ImageView iv_head;
    private TextView tv_name, tv_number, tv_sex, tv_area, tv_signature;

    private int ICON_CHANGE_REQUEST = 101;
    private int NAME_SEX_SIGNATURE_REQUEST = 102;
    private boolean isSignatureNull = true;
    private ImageLoader imageLoader;

    private String headUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        initView();
        initData();
    }

    private void initData() {
        AccountEntity sAccount = App.getInstance().getAccount();
        if (sAccount != null){
            headUrl = sAccount.getAvatar();
            if (!TextUtils.isEmpty(headUrl)){
                imageLoader.displayImage(headUrl, iv_head,
                        ImageLoaderUtils.getDisplayImageOptions());
            }
            String userid = sAccount.getUser_id();
            if (!TextUtils.isEmpty(userid)){
                tv_number.setText(userid);
            }
            String username = sAccount.getUser_name();
            if (!TextUtils.isEmpty(username)){
                tv_name.setText(username);
            }
            String sex = sAccount.getSex();
            if (!TextUtils.isEmpty(sex)){
                tv_sex.setText(sex);
            }
            String signature = sAccount.getSignature();
            if (!TextUtils.isEmpty(signature)){
                tv_signature.setText(signature);
            }
            String area = sAccount.getDistrict_str();
            if (!TextUtils.isEmpty(area)){
                tv_area.setText(area);
            }
        }
    }

    private void initView() {
        imageLoader = ImageLoaderUtils.createImageLoader(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_back = (TextView) findViewById(R.id.tv_back);
        rl_head_icon = (RelativeLayout) findViewById(R.id.rl_head_icon);
        rl_name = (RelativeLayout) findViewById(R.id.rl_name);
        rl_qr_code = (RelativeLayout) findViewById(R.id.rl_qr_code);
        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        rl_area = (RelativeLayout) findViewById(R.id.rl_area);
        rl_signature = (RelativeLayout) findViewById(R.id.rl_signature);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_signature = (TextView) findViewById(R.id.tv_signature);
        tv_title.setText("个人信息");
        tv_back.setOnClickListener(this);
        rl_head_icon.setOnClickListener(this);
        rl_name.setOnClickListener(this);
        rl_qr_code.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_area.setOnClickListener(this);
        rl_signature.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_head_icon://头像
                Bundle bundle0 = new Bundle();
                bundle0.putString("head",headUrl);
                openActvityForResult(PersonalIconActivity.class, ICON_CHANGE_REQUEST,bundle0);
                break;
            case R.id.rl_name://名字
                Bundle bundle = new Bundle();
                bundle.putString("changeType", "name");
                String name = tv_name.getText().toString();
                bundle.putString("changeValue",name);
                openActvityForResult(PersonalInfoChangeActivity.class, NAME_SEX_SIGNATURE_REQUEST, bundle);
                break;
            case R.id.rl_qr_code://二维码
                openActivity(QrCodeActivity.class);
                break;
            case R.id.rl_sex://性别
                Bundle bundle1 = new Bundle();
                bundle1.putString("changeType", "sex");
                String sex = tv_sex.getText().toString();
                bundle1.putString("changeValue",sex);
                openActvityForResult(PersonalInfoChangeActivity.class, NAME_SEX_SIGNATURE_REQUEST, bundle1);
                break;
            case R.id.rl_area://地区
                break;
            case R.id.rl_signature://个性签名
                Bundle bundle2 = new Bundle();
                bundle2.putString("changeType", "signature");
                String signature = tv_signature.getText().toString();
                bundle2.putString("changeValue",signature);
                bundle2.putBoolean("isNull",isSignatureNull);
                openActvityForResult(PersonalInfoChangeActivity.class, NAME_SEX_SIGNATURE_REQUEST, bundle2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ICON_CHANGE_REQUEST && resultCode == 101 && data != null) {

        } else if (requestCode == NAME_SEX_SIGNATURE_REQUEST && resultCode == RESULT_OK && data != null) {
            String changeType = data.getStringExtra("changeType");
            if (!TextUtils.isEmpty(changeType)) {
                String value = data.getStringExtra("back_value");
                switch (changeType) {
                    case "name":
                        tv_name.setText("" + value);
                        break;
                    case "sex":
                        tv_sex.setText("" + value);
                        break;
                    case "signature":
                        if (!TextUtils.isEmpty(value)) {
                            tv_signature.setText("" + value);
                            isSignatureNull = false;
                        } else {
                            isSignatureNull = true;
                            tv_signature.setText(getString(R.string.not_filled));
                        }
                        break;
                }
            }
        }
    }

}
