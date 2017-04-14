package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import im.boss66.com.R;
import im.boss66.com.activity.base.WebBaseActivity;
import im.boss66.com.http.HttpUrl;

/**
 * Created by liw on 2017/4/14.
 */
public class ClanCofcDetailActivity extends WebBaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            String id = intent.getStringExtra("id");
            title= intent.getStringExtra("name");
            boolean isClan = intent.getBooleanExtra("isClan", false);
            if(isClan){
                url = HttpUrl.CLAN_DETAIL + "?id=" + id;
            }else {
                url = HttpUrl.COFC_DETAIL + "?id=" + id;
            }
        }
        setTitleUrl();
        iv_headright_view  = (ImageView) findViewById(R.id.iv_headright_view);
        iv_headright_view.setVisibility(View.VISIBLE);
        iv_headright_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void setTitleUrl() {
        tv_headcenter_view.setText(title);
        webview.loadUrl(url);
    }
}
