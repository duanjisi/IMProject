package im.boss66.com.activity.discover;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by GMARUnity on 2017/2/9.
 */
public class WebViewActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back;
    private ValueCallback<Uri> mUploadMessage;
    private WebView webview;
    /**
     * 要访问的url
     */
    private String mUrl;
    private String titleString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);
        webview= (WebView) findViewById(R.id.wv_content);
        //设置WebView属性，能够执行Javascript脚本
        webview.setWebViewClient(new MyWebViewClient());
        //加载需要显示的网页
        Intent intent = getIntent();
        if(intent != null){
            mUrl = intent.getStringExtra("url");
            webview.loadUrl(mUrl);
        }
        //设置Web视图
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }
    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        return false;

    }

    //Web视图
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
    public void onClick(View v) {
        if (v.getId()==R.id.tv_back){
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                finish();
            }
        }

    }
}
