package im.boss66.com.activity.personage;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.MD5Util;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.entity.WalletEntity;
import im.boss66.com.http.HttpUrl;

/**
 * 钱包
 */
public class WalletActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_title, tv_money;
    private Button bt_withdraw;
    private EditText et_num, et_money, et_name;
    private String userId;
    private String amount, alipayNum, trueName;
    private float nowMoney, amount_f;
    private Dialog dialog;
    private TextView tv_name_value, tv_alipay_value, tv_withdraw_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initView();
    }

    private void initView() {
        userId = App.getInstance().getUid();
        et_name = (EditText) findViewById(R.id.et_name);
        et_num = (EditText) findViewById(R.id.et_num);
        et_money = (EditText) findViewById(R.id.et_money);
        tv_money = (TextView) findViewById(R.id.tv_money);
        bt_withdraw = (Button) findViewById(R.id.bt_withdraw);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.wallet));
        tv_back.setOnClickListener(this);
        bt_withdraw.setOnClickListener(this);
        getMyMoney();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.bt_withdraw:
                trueName = et_name.getText().toString().trim();
                if (TextUtils.isEmpty(trueName)) {
                    showToast("请输入真实姓名", false);
                    return;
                }
                int len = trueName.length();
                if (len < 2) {
                    showToast("请输入真实姓名", false);
                    return;
                }
                alipayNum = et_num.getText().toString().trim();
                if (TextUtils.isEmpty(alipayNum)) {
                    showToast("请输入支付宝账号", false);
                    return;
                }
                amount = et_money.getText().toString().trim();
                if (TextUtils.isEmpty(amount)) {
                    showToast("请输入金额", false);
                    return;
                }
                try {
                    amount_f = Float.parseFloat(amount);
                    if (amount_f > nowMoney) {
                        showToast("提现金额不能大于当前余额", false);
                        return;
                    }
                    if (amount_f < 10) {
                        showToast("提现金额不能小于10元", false);
                        return;
                    }
                } catch (Exception e) {
                    showToast("请输入纯数字", false);
                    return;
                }
                showDialog();
                break;
            case R.id.bt_ok:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                withdrawMoney();
                break;
            case R.id.bt_close:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;
        }
    }

    private void getMyMoney() {
        String url = HttpUrl.QUERY_MY_MONEY + userId;
        HttpUtils httpUtils = new HttpUtils(30 * 1000);
        httpUtils.configCurrentHttpCacheExpiry(1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                WalletEntity baseResult = JSON.parseObject(result, WalletEntity.class);
                if (baseResult != null) {
                    int code = baseResult.getCode();
                    if (code == 0) {
                        nowMoney = baseResult.getData();
                        DecimalFormat format = new DecimalFormat("0.00");
                        String aaa = format.format(new BigDecimal(nowMoney));
                        tv_money.setText("" + aaa);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });
    }

    private void withdrawMoney() {
        String signName = toURLEncoded(trueName);
        Log.i("signName:", signName);
        String signUrl = "/money?userid=" + userId + "&amount=" + amount + "&alipay=" + alipayNum + "&name=" + signName + "&platform=boss66";
        String sign = MD5Util.getStringMD5(signUrl);
        String url = HttpUrl.WHITDRAW_MONEY + "userid=" + userId + "&amount=" + amount +
                "&alipay=" + alipayNum + "&name=" + trueName + "&sign=" + sign;
        HttpUtils httpUtils = new HttpUtils(30 * 1000);
        httpUtils.configCurrentHttpCacheExpiry(1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                WalletEntity baseResult = JSON.parseObject(result, WalletEntity.class);
                if (baseResult != null) {
                    int code = baseResult.getCode();
                    if (code == 0) {
                        showToast("提现申请成功", false);
                        nowMoney = nowMoney - amount_f;
                        if (nowMoney < 0) {
                            nowMoney = 0;
                        }
                        DecimalFormat format = new DecimalFormat("0.00");
                        String aaa = format.format(new BigDecimal(nowMoney));
                        tv_money.setText(aaa);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });
    }

    public String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }
        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception localException) {
            Log.e("toURLEncoded error:" + paramString, localException.getMessage());
        }
        return "";
    }

    private void showDialog() {
        if (dialog == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_wallet, null);
            Button bt_close = (Button) view.findViewById(R.id.bt_close);
            Button bt_ok = (Button) view.findViewById(R.id.bt_ok);

            tv_name_value = (TextView) view.findViewById(R.id.tv_name_value);
            tv_alipay_value = (TextView) view.findViewById(R.id.tv_alipay_value);
            tv_withdraw_value = (TextView) view.findViewById(R.id.tv_withdraw_value);

            bt_close.setOnClickListener(this);
            bt_ok.setOnClickListener(this);
            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setContentView(view);
            Window dialogWindow = dialog.getWindow();
            dialogWindow.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = UIUtils.getScreenWidth(context) / 3 * 2;
            dialogWindow.setAttributes(lp);
            dialog.setCanceledOnTouchOutside(false);
        }
        tv_name_value.setText(trueName);
        tv_alipay_value.setText(alipayNum);
        tv_withdraw_value.setText(amount);
        dialog.show();
    }
}
