package im.boss66.com.activity.personage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.ChangeSexRequest;
import im.boss66.com.http.request.ChangeSignatureRequest;
import im.boss66.com.http.request.ChangeUserNameRequest;
import im.boss66.com.widget.ClearEditText;

/**
 * 修改个人名字，性别，个性签名共用类
 */
public class PersonalInfoChangeActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = PersonalInfoChangeActivity.class.getSimpleName();

    private TextView tv_title, tv_back, tv_right;
    private ClearEditText et_name;
    private RelativeLayout rl_sex_man, rl_sex_woman;
    private View v_sex;
    private ImageView iv_man_gou, iv_woman_gou;
    private RelativeLayout rl_signature;
    private EditText et_signature;
    private TextView tv_signature_num;
    private String changeType, changeValue;
    private boolean isNull = true;
    private String access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_change);
        initView();
    }

    private void initView() {
        access_token = App.getInstance().getAccount().getAccess_token();
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            changeType = bundle.getString("changeType");
            changeValue = bundle.getString("changeValue");
            isNull = bundle.getBoolean("isNull");
            if (!TextUtils.isEmpty(changeType)) {
                showViewByType(changeType);
            }
        }
    }

    private void showViewByType(String classType) {
        switch (classType) {
            case "name":
                et_name = (ClearEditText) findViewById(R.id.et_name);
                et_name.setVisibility(View.VISIBLE);
                tv_right.setVisibility(View.VISIBLE);
                tv_title.setText("名字");
                if (!TextUtils.isEmpty(changeValue)) {
                    et_name.setText(changeValue);
                }
                break;
            case "sex":
                rl_sex_man = (RelativeLayout) findViewById(R.id.rl_sex_man);
                rl_sex_woman = (RelativeLayout) findViewById(R.id.rl_sex_woman);
                v_sex = findViewById(R.id.v_sex);
                iv_man_gou = (ImageView) findViewById(R.id.iv_man_gou);
                iv_woman_gou = (ImageView) findViewById(R.id.iv_woman_gou);
                rl_sex_man.setVisibility(View.VISIBLE);
                rl_sex_woman.setVisibility(View.VISIBLE);
                rl_sex_man.setOnClickListener(this);
                rl_sex_woman.setOnClickListener(this);
                if (!TextUtils.isEmpty(changeValue)) {
                    switch (changeValue) {
                        case "男":
                            iv_woman_gou.setVisibility(View.GONE);
                            iv_man_gou.setVisibility(View.VISIBLE);
                            break;
                        case "女":
                            iv_woman_gou.setVisibility(View.VISIBLE);
                            iv_man_gou.setVisibility(View.GONE);
                            break;
                    }
                }
                break;
            case "signature":
                rl_signature = (RelativeLayout) findViewById(R.id.rl_signature);
                et_signature = (EditText) findViewById(R.id.et_signature);
                tv_signature_num = (TextView) findViewById(R.id.tv_signature_num);
                InputFilter[] filters = {new NameLengthFilter(60)};
                et_signature.setFilters(filters);
                rl_signature.setVisibility(View.VISIBLE);
                tv_right.setVisibility(View.VISIBLE);
                tv_title.setText("个性签名");
                if (!isNull && !TextUtils.isEmpty(changeValue)) {
                    et_signature.setText(changeValue);
                }
                et_signature.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int len = charSequence.length();
                        Log.i("onTextChanged:", "" + len);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        int len = (60 - calculateLength(editable.toString())) / 2;
                        tv_signature_num.setText("" + len);
                    }
                });
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_right://保存
                if (!TextUtils.isEmpty(changeType)) {
                    switch (changeType) {
                        case "name":
                            String s = et_name.getText().toString();
                            if (TextUtils.isEmpty(s)) {
                                ToastUtil.showShort(this, getString(R.string.no_name_tip));
                            } else {
                                changeValueByServer(s);
                            }
                            break;
                        case "signature":
                            String value = et_signature.getText().toString();
                            changeValueByServer(value);
                            break;
                    }
                }
                break;
            case R.id.rl_sex_man:
                changeValueByServer("1");
                break;
            case R.id.rl_sex_woman:
                changeValueByServer("2");
                break;
        }
    }

    private void changeValueByServer(final String value) {
        showLoadingDialog();
        if (!TextUtils.isEmpty(changeType)) {
            if ("name".equals(changeType)) {
                ChangeUserNameRequest nameRequest = new ChangeUserNameRequest(TAG, value);
                nameRequest.send(new BaseDataRequest.RequestCallback<String>() {
                    @Override
                    public void onSuccess(String pojo) {
                        cancelLoadingDialog();
                        setBackValue(value);
                    }

                    @Override
                    public void onFailure(String msg) {
                        cancelLoadingDialog();
                    }
                });
            } else if ("signature".equals(changeType)) {
                ChangeSignatureRequest signRequest = new ChangeSignatureRequest(TAG, value);
                signRequest.send(new BaseDataRequest.RequestCallback<String>() {
                    @Override
                    public void onSuccess(String pojo) {
                        cancelLoadingDialog();
                        setBackValue(value);
                    }

                    @Override
                    public void onFailure(String msg) {
                        cancelLoadingDialog();
                    }
                });
            } else if ("sex".equals(changeType)) {
                ChangeSexRequest sexRequest = new ChangeSexRequest(TAG, value);
                sexRequest.send(new BaseDataRequest.RequestCallback<String>() {
                    @Override
                    public void onSuccess(String pojo) {
                        cancelLoadingDialog();
                        String sex = null;
                        if ("1".equals(value)){
                            sex = "男";
                        }else if("2".equals(value)){
                            sex = "女";
                        }
                        setBackValue(sex);
                    }

                    @Override
                    public void onFailure(String msg) {
                        cancelLoadingDialog();
                    }
                });
            }
        }
    }

    private void setBackValue(String value) {
        Intent intent = new Intent();
        intent.putExtra("back_value", value);
        intent.putExtra("changeType", changeType);
        setResult(RESULT_OK, intent);
        finish();
    }

    private class NameLengthFilter implements InputFilter {
        int MAX_EN;// 最大英文/数字长度 一个汉字算两个字母
        String regEx = "[\\u4e00-\\u9fa5]"; // unicode编码，判断是否为汉字

        public NameLengthFilter(int mAX_EN) {
            super();
            MAX_EN = mAX_EN;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            int destCount = dest.toString().length()
                    + getChineseCount(dest.toString());
            int sourceCount = source.toString().length()
                    + getChineseCount(source.toString());
            if (destCount + sourceCount > MAX_EN) {
                return "";

            } else {
                return source;
            }
        }

        private int getChineseCount(String str) {
            int count = 0;
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            while (m.find()) {
                for (int i = 0; i <= m.groupCount(); i++) {
                    count = count + 1;
                }
            }
            return count;
        }
    }

    private int calculateLength(String etstring) {
        char[] ch = etstring.toCharArray();

        int varlength = 0;
        for (int i = 0; i < ch.length; i++) {
            // changed by zyf 0825 , bug 6918，加入中文标点范围 ， TODO 标点范围有待具体化
            if ((ch[i] >= 0x2E80 && ch[i] <= 0xFE4F) || (ch[i] >= 0xA13F && ch[i] <= 0xAA40) || ch[i] >= 0x80) { // 中文字符范围0x4e00 0x9fbb
                varlength = varlength + 2;
            } else {
                varlength++;
            }
        }
        Log.d("TextChanged", "varlength = " + varlength);
        // 这里也可以使用getBytes,更准确嘛
        // varlength = etstring.getBytes(CharSet.forName("GBK")).lenght;// 编码根据自己的需求，注意u8中文占3个字节...
        return varlength;
    }

}
