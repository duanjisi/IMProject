package im.boss66.com.activity.treasure;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.activity.base.WebBaseActivity;
import im.boss66.com.http.HttpUrl;

/**
 * Created by liw on 2017/3/17.
 */
public class FuwaTopList extends WebBaseActivity{

    private RelativeLayout rl_top_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = HttpUrl.FUWA_TOPLIST+ App.getInstance().getUid();
        rl_top_bar = (RelativeLayout) findViewById(R.id.rl_top_bar);
        rl_top_bar.setVisibility(View.GONE);
        setTitleUrl();
    }

    @Override
    protected void setTitleUrl() {
        webview.loadUrl(url);
    }
}
