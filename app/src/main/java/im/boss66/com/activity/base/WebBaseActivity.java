package im.boss66.com.activity.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.R;

/**
 * Created by liw on 2017/3/7.
 */

public abstract class WebBaseActivity extends ABaseActivity implements View.OnClickListener {
    protected WebView webview;
    protected String url;
    protected String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        initViews();
    }

    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headlift_view.setOnClickListener(this);

        webview = (WebView) findViewById(R.id.wv_content);
        //设置WebView属性，能够执行Javascript脚本
        webview.setWebViewClient(new WebBaseActivity.MyWebViewClient());
        //加载需要显示的网页

        //设置Web视图
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }


    protected abstract void setTitleUrl();





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

                finish();
                break;
        }
    }
}
