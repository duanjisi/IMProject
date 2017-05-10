package im.boss66.com.activity.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import im.boss66.com.R;

/**
 * Created by liw on 2017/3/7.
 */

public abstract class WebActivity extends ABaseActivity implements View.OnClickListener {
    protected WebView webview;
    protected String url;
    protected String title;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        initViews();
    }

    private void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.pergress);
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headlift_view.setOnClickListener(this);

        webview = (WebView) findViewById(R.id.wv_content);
        //设置WebView属性，能够执行Javascript脚本
        webview.setWebViewClient(new WebActivity.MyWebViewClient());
        //加载需要显示的网页

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    //progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);//设置加载进度
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String string) {
                super.onReceivedTitle(view, string);
                if (!TextUtils.isEmpty(string)) {
                    tv_headcenter_view.setText(string);
                    Log.i("info", "==============onReceivedTitle:" + string);
                }
            }
        });
        //设置Web视图
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }


    protected abstract void setTitleUrl();

    protected abstract void onBack();

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                onBack();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
