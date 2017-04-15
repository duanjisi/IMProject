package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import im.boss66.com.R;
import im.boss66.com.activity.base.WebBaseActivity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.widget.ActionSheet;

/**
 * Created by liw on 2017/4/14.
 */
public class ClanCofcDetailActivity extends WebBaseActivity implements ActionSheet.OnSheetItemClickListener {

    private boolean isClan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {
            String id = intent.getStringExtra("id");
            title= intent.getStringExtra("name");
            isClan = intent.getBooleanExtra("isClan", false);
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

                showActionSheet();
            }
        });

    }

    @Override
    protected void setTitleUrl() {
        tv_headcenter_view.setText(title);
        webview.loadUrl(url);
    }
    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem("编辑简介", ActionSheet.SheetItemColor.Black, this);

        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        switch (which){
            case 1:
                Intent intent = new Intent(this, EditClanCofcActivity.class);
                intent.putExtra("isClan",isClan);
                startActivity(intent);
                break;
        }
    }
}
