package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import im.boss66.com.R;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.ActionSheet;

/**
 * 设置-新消息通知
 */
public class PersonalNewAlertsActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView tv_back, tv_title;
    private RelativeLayout rl_no_disturbing;
    private ToggleButton sb_receive_new_alerts, sb_notify_display_msg, sb_voice, sb_vibration, sb_friends_photo_update;
    private LinearLayout ll_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_new_alerts);
        initView();
    }

    private void initView() {
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        sb_receive_new_alerts = (ToggleButton) findViewById(R.id.sb_receive_new_alerts);
        sb_notify_display_msg = (ToggleButton) findViewById(R.id.sb_notify_display_msg);
        sb_voice = (ToggleButton) findViewById(R.id.sb_voice);
        sb_friends_photo_update = (ToggleButton) findViewById(R.id.sb_friends_photo_update);
        sb_vibration = (ToggleButton) findViewById(R.id.sb_vibration);
        rl_no_disturbing = (RelativeLayout) findViewById(R.id.rl_no_disturbing);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.new_alerts));
        tv_back.setOnClickListener(this);
        rl_no_disturbing.setOnClickListener(this);
        sb_receive_new_alerts.setOnCheckedChangeListener(this);
        sb_notify_display_msg.setOnCheckedChangeListener(this);
        sb_voice.setOnCheckedChangeListener(this);
        sb_friends_photo_update.setOnCheckedChangeListener(this);
        sb_vibration.setOnCheckedChangeListener(this);
        boolean news_alert_voice = PreferenceUtils.getBoolean(context, "system_alerts_voice", true);
        sb_voice.setChecked(news_alert_voice);
        boolean news_alert = PreferenceUtils.getBoolean(context, "system_alerts", true);
        sb_receive_new_alerts.setChecked(news_alert);
        boolean news_shake = PreferenceUtils.getBoolean(context, "system_alerts_shake", true);
        sb_vibration.setChecked(news_shake);
        boolean system_alerts_circle = PreferenceUtils.getBoolean(context, "system_alerts_circle", true);
        sb_friends_photo_update.setChecked(system_alerts_circle);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_no_disturbing://功能消息免打扰
                openActivity(PersonalSetNoDisturbingActivity.class);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.sb_receive_new_alerts://接收新消息通知
                PreferenceUtils.putBoolean(this, "system_alerts", isChecked);
                if (isChecked) {
                    ll_content.setVisibility(View.VISIBLE);
                } else {
                    ll_content.setVisibility(View.GONE);
                }
                break;
            case R.id.sb_notify_display_msg://通知显示消息详情

                break;
            case R.id.sb_voice://声音
                PreferenceUtils.putBoolean(this, "system_alerts_voice", isChecked);
                break;
            case R.id.sb_vibration://振动
                PreferenceUtils.putBoolean(this, "system_alerts_shake", isChecked);
                break;
            case R.id.sb_friends_photo_update://朋友圈照片更新
                PreferenceUtils.putBoolean(this, "system_alerts_circle", isChecked);
                break;
        }
    }
}
