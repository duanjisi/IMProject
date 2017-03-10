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
 * 简介
 * Created by liw on 2017/2/22.
 */
public class IntroduceActivity extends WebBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent!=null){
            title =  intent.getStringExtra("title");
            if(intent.getExtras().containsKey("school_id")){
                url = HttpUrl.SCHOOL_INFO+"?id="+intent.getIntExtra("school_id",-1);
            }else{
                url =HttpUrl.HOMETOWN_INFO+"?id="+intent.getIntExtra("hometown_id",-1);
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
