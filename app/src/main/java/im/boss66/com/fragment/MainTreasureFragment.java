package im.boss66.com.fragment;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
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

import org.greenrobot.eventbus.EventBus;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.Base64Utils;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.MakeQRCodeUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.CaptureActivity;
import im.boss66.com.activity.treasure.ApplyFuwaActivity;
import im.boss66.com.activity.treasure.FindTreasureChildrenActivity;
import im.boss66.com.activity.treasure.FuwaDealActivity;
import im.boss66.com.activity.treasure.FuwaMessageActivity;
import im.boss66.com.activity.treasure.FuwaPackageActivity;
import im.boss66.com.activity.treasure.FuwaTopList;
import im.boss66.com.activity.treasure.GameRuleActivity;
import im.boss66.com.activity.treasure.HideFuwaActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.ActionEntity;
import im.boss66.com.widget.CircleImageView;
import im.boss66.com.widget.popupWindows.SharePopup;

/**
 * Created by Johnny on 2017/4/20.
 */
public class MainTreasureFragment extends BaseFragment implements View.OnClickListener, SharePopup.OnItemSelectedListener {
    private TextView tv_apply;
    private ImageView iv_msg, iv_bag, iv_trade, iv_rank, iv_game, img_more;
    private Button btn_find, btn_find_fate, btn_store;

    private Dialog dialog;
    private Dialog qr_code_dialog;
    private PopupWindow popupWindow;
    private TextView tv_word;
    private SharePopup sharePopup;
    private View view;
    private CircleImageView iv_avatar;
    private ImageLoader imageLoader;
    private AccountEntity account;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main_treasure, null);
            initViews(view);
        }
        return view;
    }

    private void initViews(View view) {
        account = App.getInstance().getAccount();
        imageLoader = ImageLoaderUtils.createImageLoader(getActivity());
        iv_avatar = (CircleImageView) view.findViewById(R.id.iv_avatar);

//        tv_rank = (TextView) view.findViewById(R.id.tv_rank);
        tv_apply = (TextView) view.findViewById(R.id.tv_apply);
//        tv_game = (TextView) view.findViewById(R.id.tv_game);
        iv_game = (ImageView) view.findViewById(R.id.iv_game);
        iv_rank = (ImageView) view.findViewById(R.id.iv_rank);

        iv_msg = (ImageView) view.findViewById(R.id.iv_msg);
        iv_bag = (ImageView) view.findViewById(R.id.iv_bag);
        iv_trade = (ImageView) view.findViewById(R.id.iv_trade);
        img_more = (ImageView) view.findViewById(R.id.img_more);

        btn_find = (Button) view.findViewById(R.id.btn_find);
        btn_find_fate = (Button) view.findViewById(R.id.btn_find_fate);
        btn_store = (Button) view.findViewById(R.id.btn_store);

//        tv_rank.setOnClickListener(this);
//        tv_game.setOnClickListener(this);
        iv_rank.setOnClickListener(this);
        iv_game.setOnClickListener(this);
        tv_apply.setOnClickListener(this);
        iv_msg.setOnClickListener(this);
        iv_bag.setOnClickListener(this);
        iv_trade.setOnClickListener(this);
        btn_find.setOnClickListener(this);
        btn_find_fate.setOnClickListener(this);
        btn_store.setOnClickListener(this);
        img_more.setOnClickListener(this);
        iv_avatar.setOnClickListener(this);

        sharePopup = new SharePopup(getActivity(), mController);
        imageLoader.displayImage(account.getAvatar(), iv_avatar, ImageLoaderUtils.getDisplayImageOptions());

        sharePopup.setWidth(UIUtils.getScreenWidth(getActivity()));

        sharePopup.setOnItemSelectedListener(this);

    }

    @Override
    public void onClick(View view) {
        SHARE_MEDIA shareMedia = null;
        UMediaObject uMediaObject = null;
        switch (view.getId()) {
            case R.id.img_more:
                if (popupWindow == null) {
                    showTopPop(img_more);
                } else if (!popupWindow.isShowing()) {
                    popupWindow.showAsDropDown(img_more);
                }
                break;
            case R.id.iv_rank://福娃排行榜
                openActivity(FuwaTopList.class);
                break;
            case R.id.iv_game://福娃游戏规则
                openActivity(GameRuleActivity.class);
                break;
            case R.id.tv_apply://申请福娃
                openActivity(ApplyFuwaActivity.class);
                break;
            case R.id.iv_msg://消息
                openActivity(FuwaMessageActivity.class);
                break;
            case R.id.iv_bag://背包
                openActivity(FuwaPackageActivity.class);
                break;
            case R.id.iv_trade://交易
                openActivity(FuwaDealActivity.class);

                break;
            case R.id.btn_find://找福娃
                Intent intent = new Intent(getActivity(), FindTreasureChildrenActivity.class);
                intent.putExtra("isFate", false);
                startActivity(intent);
                break;
            case R.id.btn_find_fate://找福娃
                Intent it = new Intent(getActivity(), FindTreasureChildrenActivity.class);
                it.putExtra("isFate", true);
                startActivity(it);
                break;
            case R.id.btn_store://藏福娃
                openActivity(HideFuwaActivity.class);
                break;
            case R.id.iv_avatar:
                EventBus.getDefault().post(new ActionEntity(0));
                break;

            case R.id.weixin_share_linear:
                shareMedia = SHARE_MEDIA.WEIXIN;
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
                    weixinContent.setShareImage(new UMImage(getActivity(), imageUrl));
                } else {
                    weixinContent.setShareImage(new UMImage(getActivity(), R.drawable.logo_tips));
                }
                uMediaObject = weixinContent;
                postShare(shareMedia, uMediaObject);
                break;
            case R.id.weixin_circle_share_linear:
                shareMedia = SHARE_MEDIA.WEIXIN_CIRCLE;
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
                    circleMedia.setShareImage(new UMImage(getActivity(), imageUrl));
                } else {
                    circleMedia.setShareImage(new UMImage(getActivity(), R.drawable.logo_tips));
                }
                uMediaObject = circleMedia;
                postShare(shareMedia, uMediaObject);
                break;
            case R.id.qq_share_linear:
                shareMedia = SHARE_MEDIA.QQ;
                if (!mController.getConfig().getSsoHandler(HandlerRequestCode.QQ_REQUEST_CODE).isClientInstalled()) {
                    showToast(R.string.notice_qq_not_install, false);
                    return;
                }
                QQShareContent qqShareContent = new QQShareContent();
                qqShareContent.setShareContent(shareContent);
                qqShareContent.setTitle(title);
                if (imageUrl != null && !imageUrl.equals("")) {
                    //设置分享图片
                    qqShareContent.setShareImage(new UMImage(getActivity(), imageUrl));
                } else {
                    qqShareContent.setShareImage(new UMImage(getActivity(), R.drawable.logo_tips));
                }

                qqShareContent.setTargetUrl(targetUrl);
                uMediaObject = qqShareContent;
                postShare(shareMedia, uMediaObject);
                break;
            case R.id.qq_zone_share_linear:
                shareMedia = SHARE_MEDIA.QZONE;
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
                    qzone.setShareImage(new UMImage(getActivity(), imageUrl));
                } else {
                    qzone.setShareImage(new UMImage(getActivity(), R.drawable.logo_tips));
                }
                uMediaObject = qzone;
                postShare(shareMedia, uMediaObject);
                break;
            case R.id.btn_cancel:
                shareDialog.dismiss();
                break;
        }
    }

    //分享回调
    @Override
    public void onItemSelected(SHARE_MEDIA shareMedia) {
        UMediaObject uMediaObject = null;
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
                    weixinContent.setShareImage(new UMImage(getActivity(), imageUrl));
                } else {
                    weixinContent.setShareImage(new UMImage(getActivity(), R.drawable.logo_tips));
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
                    circleMedia.setShareImage(new UMImage(getActivity(), imageUrl));
                } else {
                    circleMedia.setShareImage(new UMImage(getActivity(), R.drawable.logo_tips));
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
                    qqShareContent.setShareImage(new UMImage(getActivity(), imageUrl));
                } else {
                    qqShareContent.setShareImage(new UMImage(getActivity(), R.drawable.logo_tips));
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
                    qzone.setShareImage(new UMImage(getActivity(), imageUrl));
                } else {
                    qzone.setShareImage(new UMImage(getActivity(), R.drawable.logo_tips));
                }
                uMediaObject = qzone;
                break;
        }
        mController.setShareMedia(uMediaObject);
        mController.postShare(getActivity(), shareMedia, new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                Log.i("info", "================eCode:" + eCode);
//                showToast("========eCode:" + eCode, true);
                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
                    showToast("分享成功!", true);
                }
            }
        });


    }

    private void postShare(SHARE_MEDIA shareMedia, UMediaObject uMediaObject) {
        mController.setShareMedia(uMediaObject);
        mController.postShare(getActivity(), shareMedia, new SocializeListeners.SnsPostListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                Log.i("info", "================eCode:" + eCode);
//                showToast("========eCode:" + eCode, true);
                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
                    showToast("分享成功!", true);
                }
            }
        });
    }

    private void showTopPop(View parent) {

        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fuwa_pop, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setAnimationStyle(R.style.PopupTitleBarAnim1);
        view.findViewById(R.id.my_word).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog == null) {
                    showCodeDialog(getActivity());
                } else if (!dialog.isShowing()) {
                    dialog.show();
                }
                popupWindow.dismiss();

            }
        });
        view.findViewById(R.id.check_fuwa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //验证福娃
//                showToast("验证福娃",false);
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });


        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        popupWindow.getBackground().setAlpha(0);
        popupWindow.showAsDropDown(parent);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int top = view.findViewById(R.id.pop_layout).getTop();
                int Bottom = view.findViewById(R.id.pop_layout).getBottom();
                int left = view.findViewById(R.id.pop_layout).getLeft();
                int right = view.findViewById(R.id.pop_layout).getRight();
                int y = (int) event.getY();
                int x = (int) event.getX();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (y < top || y > Bottom) {
                            popupWindow.dismiss();
                        }
                        if (x < left || x > right) {
                            popupWindow.dismiss();
                        }
                        break;
                }

                return true;
            }
        });

    }


    private void showCodeDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        View view = inflater.inflate(R.layout.pop_fuwa_word, null);


        dialog = new Dialog(context, R.style.dialog_ios_style);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        tv_word = (TextView) dialog.findViewById(R.id.tv_word);
        tv_word.setText(Base64Utils.encodeBase64(App.getInstance().getUid()));
        tv_word.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(tv_word.getText());
                showToast("已复制", false);
                return false;
            }
        });
        dialog.findViewById(R.id.rl_qr_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //二维码pop
//                showToast("二维码",false);

                if (qr_code_dialog == null) {
                    showCodeDetailDialog(context);

                } else if (!qr_code_dialog.isShowing()) {
                    qr_code_dialog.show();

                }

            }
        });


        //设置dialog大小
        Window dialogWindow = dialog.getWindow();
        WindowManager manager = (getActivity()).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9，根据实际情况调整
        dialogWindow.setAttributes(params);

        dialog.show();
    }


    //二维码细节
    private void showCodeDetailDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        final View view = inflater.inflate(R.layout.pop_qr_code, null);


        qr_code_dialog = new Dialog(context, R.style.dialog_ios_style);
        qr_code_dialog.setContentView(view);
        qr_code_dialog.setCancelable(true);
        qr_code_dialog.setCanceledOnTouchOutside(true);

        view.findViewById(R.id.img_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qr_code_dialog.dismiss();
            }
        });
        view.findViewById(R.id.rl_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //分享
//                if (!isFinishing()) {
//                    if (sharePopup.isShowing()) {
//                        sharePopup.dismiss();
//                    } else {
//                        sharePopup.show(qr_code_dialog.getWindow().getDecorView());
////                        sharePopup.showAtLocation(view.findViewById(R.id.img_cancle),Gravity.BOTTOM,0,-200);
//                    }
//                }

                //用我之前那个dialog，然后加上这边的分享逻辑。
                if (shareDialog == null) {
                    showDialog();
                } else if (!shareDialog.isShowing()) {
                    shareDialog.show();
                }

            }
        });

        ImageView img_qr_code = (ImageView) view.findViewById(R.id.img_qr_code);

        int width = UIUtils.getScreenWidth(context);
        width = width * 3 / 5;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img_qr_code.getLayoutParams();
        params.weight = width;
        params.height = width;
        img_qr_code.setLayoutParams(params);
        String uri = "fuwa:user:" + tv_word.getText().toString();
        MakeQRCodeUtil.createQRImage(uri, width, width, img_qr_code);


        //设置dialog大小
        Window dialogWindow = dialog.getWindow();
        WindowManager manager = (getActivity()).getWindowManager();
        WindowManager.LayoutParams params2 = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9，根据实际情况调整
        dialogWindow.setAttributes(params2);

        qr_code_dialog.show();
    }

    private String shareContent = "我的二维码";
    private String targetUrl = "http://www.baidu.com";
    private String title = "嗨萌寻宝";
    private String imageUrl;
    private Dialog shareDialog;


    private void showDialog() {
        shareDialog = new Dialog(getActivity(), R.style.Dialog_full);
        View view_dialog = View.inflate(getActivity(),
                R.layout.pop_share, null);
        view_dialog.findViewById(R.id.weixin_share_linear).setOnClickListener(this);
        view_dialog.findViewById(R.id.weixin_circle_share_linear).setOnClickListener(this);
        view_dialog.findViewById(R.id.qq_share_linear).setOnClickListener(this);
        view_dialog.findViewById(R.id.qq_zone_share_linear).setOnClickListener(this);
        view_dialog.findViewById(R.id.btn_cancel).setOnClickListener(this);

        shareDialog.setContentView(view_dialog);
        Window dialogWindow = shareDialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.ActionSheetDialogAnimation);
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);


        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        shareDialog.setCanceledOnTouchOutside(true);
        shareDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }
}
