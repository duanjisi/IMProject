package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.ActionSheet;

/**
 * 聊天背景
 */
public class PersonalSetChatBgActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private TextView tv_back,tv_title,tv_apply;
    private RelativeLayout rl_select_bg,rl_photo_album,rl_take_a_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_set_chat_bg);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_apply = (TextView) findViewById(R.id.tv_apply);
        rl_select_bg = (RelativeLayout) findViewById(R.id.rl_select_bg);
        rl_photo_album = (RelativeLayout) findViewById(R.id.rl_photo_album);
        rl_take_a_photo = (RelativeLayout) findViewById(R.id.rl_take_a_photo);
        tv_title.setText(getString(R.string.chat_bg));
        tv_back.setOnClickListener(this);
        tv_apply.setOnClickListener(this);
        rl_select_bg.setOnClickListener(this);
        rl_photo_album.setOnClickListener(this);
        rl_take_a_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_apply://将背景应用到所有聊天场景
                showActionSheet();
                break;
            case R.id.rl_select_bg://选择背景图
                break;
            case R.id.rl_photo_album://从手机相册选择
                break;
            case R.id.rl_take_a_photo://拍一张
                break;
        }
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(PersonalSetChatBgActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem(getString(R.string.apply_bg_to_chat_all_scenarios), ActionSheet.SheetItemColor.Red,
                PersonalSetChatBgActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {

    }
}
