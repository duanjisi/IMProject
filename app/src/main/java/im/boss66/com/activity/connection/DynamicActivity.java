package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;

import im.boss66.com.activity.base.WebBaseActivity;
import im.boss66.com.http.HttpUrl;

/**
 * 动态详情页
 * Created by liw on 2017/3/29.
 */

public class DynamicActivity extends WebBaseActivity {
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("title");
            if (intent.getExtras().containsKey("school_id")) {
                id = intent.getStringExtra("school_id");
                url = HttpUrl.SCHOOL_NEWS_DETATIL+"?id="+id;

            } else {
                id = intent.getStringExtra("hometown_id");
                url = HttpUrl.HOMETONW_NEWS_DETATIL+"?id="+id;
            }
        }
        setTitleUrl();
    }

    @Override
    protected void setTitleUrl() {
        tv_headcenter_view.setText(title);
        webview.loadUrl(url);
    }
}
