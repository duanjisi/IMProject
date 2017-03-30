package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.activity.SplashActivity;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.ActionSheet;

/**
 * 个人设置
 */
public class PersonalSetActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private TextView tv_back, tv_title, tv_exit_login;
    private RelativeLayout rl_safe, rl_new_alerts, rl_privacy, rl_general, rl_help_feedback, rl_about;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_set);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_exit_login = (TextView) findViewById(R.id.tv_exit_login);
        rl_safe = (RelativeLayout) findViewById(R.id.rl_safe);
        rl_new_alerts = (RelativeLayout) findViewById(R.id.rl_new_alerts);
        rl_privacy = (RelativeLayout) findViewById(R.id.rl_privacy);
        rl_general = (RelativeLayout) findViewById(R.id.rl_general);
        rl_help_feedback = (RelativeLayout) findViewById(R.id.rl_help_feedback);
        rl_about = (RelativeLayout) findViewById(R.id.rl_about);
        tv_title.setText(getString(R.string.set));
        tv_back.setOnClickListener(this);
        rl_safe.setOnClickListener(this);
        rl_new_alerts.setOnClickListener(this);
        rl_privacy.setOnClickListener(this);
        rl_general.setOnClickListener(this);
        rl_help_feedback.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        tv_exit_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_safe://账号与安全
                openActivity(PersonalAccountSafeActivity.class);
                break;
            case R.id.rl_new_alerts://新消息通知
                openActivity(PersonalNewAlertsActivity.class);
                break;
            case R.id.rl_privacy://隐私
                openActivity(PersonalPrivacyActivity.class);
                break;
            case R.id.rl_general://通用
                openActivity(PersonalSetGeneralActivity.class);
                break;
            case R.id.rl_help_feedback://帮助与反馈
                break;
            case R.id.rl_about://关于
                openActivity(AboutAppActivity.class);
                break;
            case R.id.tv_exit_login://退出登录
                showActionSheet();
                break;
        }
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(PersonalSetActivity.this)
                .builder()
                .setTitle(getString(R.string.exit_login_tip))
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem(getString(R.string.exit_login), ActionSheet.SheetItemColor.Red,
                PersonalSetActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {//退出登录
        App.getInstance().logout();
        Session.getInstance().stopChatService();
//        ChatServices.stopChatService(context);
//        Session.getInstance().exitActivitys();
//        Intent intent = new Intent();
//        intent.setClass(PersonalSetActivity.this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//        ChatServices.stopChatService(context);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openActivity(SplashActivity.class);
                App.getInstance().exit();
            }
        }, 100);
    }
}
