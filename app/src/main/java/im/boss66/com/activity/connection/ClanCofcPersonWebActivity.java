package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;

import im.boss66.com.activity.base.WebBaseActivity;
import im.boss66.com.http.HttpUrl;

/**
 * Created by liw on 2017/3/29.
 */

public class ClanCofcPersonWebActivity extends WebBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent!=null){
            int id = intent.getIntExtra("id", -1);
            boolean isClan= intent.getBooleanExtra("isClan",false);
            if(isClan){
                url = HttpUrl.CLAN_PERSON_WEB+"?id="+id;
            }else{
                url = HttpUrl.COFC_PERSON_WEB+"?id="+id;
            }
            title = intent.getStringExtra("name");
        }
        setTitleUrl();
    }

    @Override
    protected void setTitleUrl() {
        tv_headcenter_view.setText(title);
        webview.loadUrl(url);
    }
}
