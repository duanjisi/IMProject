package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.socialize.bean.HandlerRequestCode;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMediaObject;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.activity.SplashActivity;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.services.MyPushIntentService;
import im.boss66.com.util.Utils;
import im.boss66.com.widget.ActionSheet;
import im.boss66.com.widget.popupWindows.SharePopup;

/**
 * 个人设置
 */
public class PersonalSetActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener, SharePopup.OnItemSelectedListener {

    private TextView tv_back, tv_title, tv_exit_login;
    private RelativeLayout rl_safe, rl_new_alerts, rl_privacy, rl_general, rl_help_feedback, rl_about;
    private Handler handler = new Handler();
    private SharePopup sharePopup;
//
//
    private String   shareContent = "我正在嗨萌寻宝,一起来寻宝吧!";
    private String targetUrl="http://www.boss89.com/product";
    private String  title = "嗨萌寻宝";
    private String imageUrl="http://wsimcdn.hmg66.com/hm_logo.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().addTempActivity(this);
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

        findViewById(R.id.rl_share).setOnClickListener(this);
        sharePopup = new SharePopup(context, mController);
        sharePopup.setOnItemSelectedListener(this);
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
            case R.id.rl_share: //分享
                   if(!sharePopup.isShowing()){
                       sharePopup.show(tv_back);
                   }
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
//        Session.getInstance().stopChatService();
        Utils.sendImMessage(Session.ACTION_STOP_CHAT_SERVICE, null);
        MyPushIntentService.stopPushService(context);
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

    @Override
    public void onItemSelected(SHARE_MEDIA shareMedia) {
        UMediaObject uMediaObject = null;
        MycsLog.i("info", "====title:" + title);
        MycsLog.i("info", "====targetUrl:" + targetUrl);
        switch (shareMedia) {
            case WEIXIN:
                if (!mController.getConfig().getSsoHandler(HandlerRequestCode.WX_REQUEST_CODE).isClientInstalled()) {
                    showToast(R.string.notice_weixin_not_install, false);
                    return;
                }
                //设置微信好友分享内容
                WeiXinShareContent weixinContent = new WeiXinShareContent();
                //设置分享文字
                weixinContent.setShareContent(shareContent);
                //设置title
//                weixinContent.setTitle(TextUtils.isEmpty(title) ? mWebView.getTitle() : title);
                weixinContent.setTitle(title);
                //设置分享内容跳转URL
                weixinContent.setTargetUrl(targetUrl);
                if (imageUrl != null && !imageUrl.equals("")) {
                    //设置分享图片
                    weixinContent.setShareImage(new UMImage(context, imageUrl));
                } else {
                    weixinContent.setShareImage(new UMImage(context, R.drawable.logo_tips));
                }
                uMediaObject = weixinContent;
                break;
            case WEIXIN_CIRCLE:
                if (!mController.getConfig().getSsoHandler(HandlerRequestCode.WX_REQUEST_CODE).isClientInstalled()) {
                    showToast(R.string.notice_weixin_not_install, false);
                    return;
                }
                //设置微信朋友圈分享内容
                CircleShareContent circleMedia = new CircleShareContent();
                circleMedia.setShareContent(shareContent);
                //设置朋友圈title
                circleMedia.setTitle(title);
                circleMedia.setTargetUrl(targetUrl);
                if (imageUrl != null) {
                    //设置分享图片
                    circleMedia.setShareImage(new UMImage(context, imageUrl));
                } else {
                    circleMedia.setShareImage(new UMImage(context, R.drawable.logo_tips));
                }
                uMediaObject = circleMedia;
                break;
            case QQ:
                if (!mController.getConfig().getSsoHandler(HandlerRequestCode.QQ_REQUEST_CODE).isClientInstalled()) {
                    showToast(R.string.notice_qq_not_install, false);
                    return;
                }
                QQShareContent qqShareContent = new QQShareContent();
                qqShareContent.setShareContent(shareContent);
                qqShareContent.setTitle(title);
                if (imageUrl != null && !imageUrl.equals("")) {
                    //设置分享图片
                    qqShareContent.setShareImage(new UMImage(context, imageUrl));
                } else {
                    qqShareContent.setShareImage(new UMImage(context, R.drawable.logo_tips));
                }

                qqShareContent.setTargetUrl(targetUrl);
                uMediaObject = qqShareContent;
                break;
            case QZONE:
                QZoneShareContent qzone = new QZoneShareContent();
//                // 设置分享文字
//                qzone.setShareContent("来自友盟社会化组件（SDK）让移动应用快速整合社交分享功能 -- QZone");
//                // 设置点击消息的跳转URL
//                qzone.setTargetUrl("http://www.baidu.com");
//                // 设置分享内容的标题
//                qzone.setTitle("QZone title");
                // 设置分享图片
                qzone.setShareContent(shareContent);
                qzone.setTitle(title);
                qzone.setTargetUrl(targetUrl);
                if (imageUrl != null && !imageUrl.equals("")) {
                    //设置分享图片
                    qzone.setShareImage(new UMImage(context, imageUrl));
                } else {
                    qzone.setShareImage(new UMImage(context, R.drawable.logo_tips));
                }
                uMediaObject = qzone;
                break;
        }
        mController.setShareMedia(uMediaObject);
        mController.postShare(context, shareMedia, new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
                    showToast("分享成功!", true);
                }
            }
        });
    }
}
