package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.activity.base.WebBaseActivity;
import im.boss66.com.http.HttpUrl;

/**
 * Created by liw on 2017/2/23.
 */
public class ClubDetailActivity extends WebBaseActivity {

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent!=null){
            if(intent.getExtras().containsKey("companyClubId")){
                id = intent.getIntExtra("companyClubId", -1);
                url = HttpUrl.CLUB_DETATIL+"?id="+id;
            }else{
                id = intent.getIntExtra("schoolClubId",-1);
                url = HttpUrl.SCHOOL_CLUB_DETATIL+"?id="+id;
            }

            title  =intent.getStringExtra("name");
        }
        setTitleUrl();

    }

    @Override
    protected void setTitleUrl() {
        tv_headcenter_view.setText(title);
        webview.loadUrl(url);
    }
}