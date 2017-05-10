package im.boss66.com.activity.treasure;

import android.content.Intent;
import android.os.Bundle;

import im.boss66.com.activity.base.WebActivity;

/**
 * Created by Johnny on 2017/5/9.
 */
public class WebDetailActivity extends WebActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getExtras().getString("url", "");
        }
        setTitleUrl();
    }

    @Override
    protected void setTitleUrl() {
        webview.loadUrl(url);
    }

    @Override
    protected void onBack() {
        finish();
    }
}
