package im.boss66.com.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 软件许可协议
 */
public class SoftWareAgreementActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvback;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_agreement);
        initViews();
    }

    private void initViews() {
        tvback = (TextView) findViewById(R.id.tv_back);
        tvback.setOnClickListener(this);
        webView = (WebView) findViewById(R.id.wb);
        //找到Html文件，也可以用网络上的文件
        webView.loadUrl("file:///android_asset/service_agreement.html");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }
}
