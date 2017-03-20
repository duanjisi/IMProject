package im.boss66.com.widget.popupWindows;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.widget.popupWindows.base.BasePopup;


/**
 * 上传图片PopupWindow
 */
public class SharePopup extends BasePopup implements View.OnClickListener {

    private OnItemSelectedListener mOnItemSelectedListener;
    private OnItemWeiboShareListener onItemWeiboShareListener;
    private Context context;
    private boolean weiboShare = false;

    public SharePopup(Context context, UMSocialService umSocialService) {
        super(context);
        this.context = context;
        setAnimationStyle(R.style.popwin_anim_style);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(UIUtils.dip2px(context, 121));
        View popView = LayoutInflater.from(context).inflate(R.layout.popup_share, null);
        initViews(popView);
        setContentView(popView);
        initSharePlatform(umSocialService);
    }

    public SharePopup(Context context, UMSocialService umSocialService, boolean weiboShare) {
        super(context);
        this.context = context;
        this.weiboShare = weiboShare;
        setAnimationStyle(R.style.popwin_anim_style);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(UIUtils.dip2px(context, 121));
        View popView = LayoutInflater.from(context).inflate(R.layout.popup_share, null);
        initViews(popView);
        setContentView(popView);
        initSharePlatform(umSocialService);
    }

    private void initViews(View view) {
        view.findViewById(R.id.weixin_share_linear).setOnClickListener(this);
        view.findViewById(R.id.weixin_circle_share_linear).setOnClickListener(this);
        view.findViewById(R.id.qq_share_linear).setOnClickListener(this);
        view.findViewById(R.id.qq_zone_share_linear).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
//        LinearLayout boss66Share = (LinearLayout) view.findViewById(R.id.boss66_share_linear);
//        if (weiboShare) {
//            boss66Share.setOnClickListener(this);
//            boss66Share.setVisibility(View.VISIBLE);
//        }
    }

    private void initSharePlatform(UMSocialService umSocialService) {
        String weixinAppId = mContext.getString(R.string.weixin_app_id);
        String weixinAppSecret = mContext.getString(R.string.weixin_app_secret);
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mContext, weixinAppId, weixinAppSecret);
        wxHandler.addToSocialSDK();

        // 添加微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, weixinAppId, weixinAppSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();

        String qqAppId = mContext.getString(R.string.qq_app_id);
        String qqAppSecret = mContext.getString(R.string.qq_app_key);
        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler((android.app.Activity) mContext, qqAppId, qqAppSecret);
        qqSsoHandler.addToSocialSDK();

        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler((android.app.Activity) mContext, qqAppId, qqAppSecret);
        qZoneSsoHandler.addToSocialSDK();
    }

    @Override
    public void onClick(View v) {
        SHARE_MEDIA shareMedia = null;
        switch (v.getId()) {
            case R.id.weixin_share_linear:
                shareMedia = SHARE_MEDIA.WEIXIN;
                dismiss();
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(shareMedia);
                }
                break;
            case R.id.weixin_circle_share_linear:
                shareMedia = SHARE_MEDIA.WEIXIN_CIRCLE;
                dismiss();
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(shareMedia);
                }
                break;
            case R.id.qq_share_linear:
                shareMedia = SHARE_MEDIA.QQ;
                dismiss();
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(shareMedia);
                }
                break;
            case R.id.qq_zone_share_linear:
                shareMedia = SHARE_MEDIA.QZONE;
                dismiss();
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(shareMedia);
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
//            case R.id.boss66_share_linear:
////                Intent intent = new Intent(context, CreateLiveActivity.class);
////                intent.putExtra("from", true);
////                context.startActivity(intent);
//                if (onItemWeiboShareListener != null) {
//                    onItemWeiboShareListener.onItemWeiboShareSelected();
//                }
//                break;
        }
//        dismiss();
//        if (mOnItemSelectedListener != null) {
//            mOnItemSelectedListener.onItemSelected(shareMedia);
//        }
    }


    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public void setOnItemWeiboShareListener(OnItemWeiboShareListener onItemWeiboShareListener) {
        this.onItemWeiboShareListener = onItemWeiboShareListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(SHARE_MEDIA shareMedia);
    }

    public interface OnItemWeiboShareListener {
        void onItemWeiboShareSelected();
    }

    /**
     * 显示PopupWindow
     *
     * @param view PopupWindow依附的View
     */
    public void show(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
        backgroundAlpha(0.45f);
    }
}
