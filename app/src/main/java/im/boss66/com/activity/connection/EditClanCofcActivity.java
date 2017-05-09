package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import im.boss66.com.R;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.activity.discover.ReplaceAlbumCoverActivity;
import im.boss66.com.event.CreateSuccess;
import im.boss66.com.widget.ActionSheet;

/**
 * Created by liw on 2017/4/15.
 */
public class EditClanCofcActivity extends ABaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private boolean isClan;
    private String id;
    private ImageView img_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_clan_cofc);
        Intent intent = getIntent();
        if (intent != null) {
            isClan = intent.getBooleanExtra("isClan", false);
            id = intent.getStringExtra("id");
        }
        initViews();

    }

    private void initViews() {
        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("编辑简介");
        img_logo = (ImageView) findViewById(R.id.img_logo);

        if(isClan){
            findViewById(R.id.rl_location).setVisibility(View.GONE);
            findViewById(R.id.rl_phone).setVisibility(View.GONE);
        }

        findViewById(R.id.rl_logo).setOnClickListener(this);
        findViewById(R.id.rl_info).setOnClickListener(this);
        findViewById(R.id.rl_location).setOnClickListener(this);
        findViewById(R.id.rl_phone).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;

            case R.id.rl_logo:
                showActionSheet();
                break;
            case R.id.rl_info:
                intent = new Intent(this, EditTextActivity.class);
                intent.putExtra("type","rl_info");
                intent.putExtra("isClan",isClan);
                intent.putExtra("id",id);
                startActivity(intent);
                break;
            case R.id.rl_location:
                intent = new Intent(this, EditTextActivity.class);
                intent.putExtra("type","rl_location");
                intent.putExtra("isClan",isClan);
                intent.putExtra("id",id);
                startActivity(intent);
                break;
            case R.id.rl_phone:
                intent = new Intent(this, EditTextActivity.class);
                intent.putExtra("type","rl_phone");
                intent.putExtra("isClan",isClan);
                intent.putExtra("id",id);
                startActivity(intent);
                break;


        }
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem("添加logo", ActionSheet.SheetItemColor.Black, this);

        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        if (which == 1) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("fromEditClanClub", true);
            bundle.putBoolean("isClan", isClan);
            bundle.putString("id", id);
            openActvityForResult(ReplaceAlbumCoverActivity.class, 1, bundle);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            if (data != null) {
                String imgurl = data.getStringExtra("imgurl");
//                Glide.with(this).load(imgurl).into(img_logo);
                EventBus.getDefault().post(new CreateSuccess(""));
            }
        }
    }
}
