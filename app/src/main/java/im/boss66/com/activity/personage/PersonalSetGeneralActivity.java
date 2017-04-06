package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import im.boss66.com.R;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.im.EmojiMyActivity;
import im.boss66.com.widget.ActionSheet;

/**
 * 设置-通用
 */
public class PersonalSetGeneralActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView tv_back, tv_title, tv_clear;
    private RelativeLayout rl_font_size, rl_chat_bg, rl_my_face, rl_photos_videos,
            rl_function, rl_chat_logs_migration, rl_memory_space;
    private ToggleButton sb_receiver_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_set_general);
        initView();
    }

    private void initView() {
        tv_clear = (TextView) findViewById(R.id.tv_clear);
        rl_font_size = (RelativeLayout) findViewById(R.id.rl_font_size);
        rl_chat_bg = (RelativeLayout) findViewById(R.id.rl_chat_bg);
        rl_my_face = (RelativeLayout) findViewById(R.id.rl_my_face);
        rl_photos_videos = (RelativeLayout) findViewById(R.id.rl_photos_videos);
        rl_function = (RelativeLayout) findViewById(R.id.rl_function);
        rl_chat_logs_migration = (RelativeLayout) findViewById(R.id.rl_chat_logs_migration);
        rl_memory_space = (RelativeLayout) findViewById(R.id.rl_memory_space);
        sb_receiver_model = (ToggleButton) findViewById(R.id.sb_receiver_model);
        rl_font_size.setOnClickListener(this);
        rl_chat_bg.setOnClickListener(this);
        rl_my_face.setOnClickListener(this);
        rl_photos_videos.setOnClickListener(this);
        rl_function.setOnClickListener(this);
        rl_chat_logs_migration.setOnClickListener(this);
        rl_memory_space.setOnClickListener(this);
        tv_clear.setOnClickListener(this);

        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.general));
        tv_back.setOnClickListener(this);
        sb_receiver_model.setOnCheckedChangeListener(this);
        boolean phone_receiver = PreferenceUtils.getBoolean(context, "phone_receiver", true);
        sb_receiver_model.setChecked(phone_receiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_clear://清空聊天记录
                showActionSheet();
                break;
            case R.id.rl_font_size://字体大小
                break;
            case R.id.rl_chat_bg://聊天背景
                openActivity(PersonalSetChatBgActivity.class);
                break;
            case R.id.rl_my_face://我的表情
                openActivity(EmojiMyActivity.class);
                break;
            case R.id.rl_photos_videos://照片和视频
                openActivity(PersonalSetPhotosVideoActivity.class);
                break;
            case R.id.rl_function://功能
                break;
            case R.id.rl_chat_logs_migration://聊天记录迁移
                break;
            case R.id.rl_memory_space://存储空间
                break;
        }
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(PersonalSetGeneralActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.setTitle(getString(R.string.empty_all_chat_record_tip));
        actionSheet.addSheetItem(getString(R.string.empty_chat_record), ActionSheet.SheetItemColor.Red,
                PersonalSetGeneralActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.sb_receiver_model:
                PreferenceUtils.putBoolean(this, "phone_receiver", isChecked);
                break;
        }
    }
}
