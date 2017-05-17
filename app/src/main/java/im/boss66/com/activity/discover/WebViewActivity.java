package im.boss66.com.activity.discover;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.popupWindows.SharePopup;

/**
 * Created by GMARUnity on 2017/2/9.
 */
public class WebViewActivity extends BaseActivity implements View.OnClickListener, SharePopup.OnItemSelectedListener {

    private TextView tv_back;
    private ValueCallback<Uri> mUploadMessage;
    private WebView webview;
    /**
     * 要访问的url
     */
    private String mUrl;
    private String titleString;
    private RelativeLayout rl_top_bar;
    private TextView tv_title, tv_right;

    private SharePopup sharePopup;
    private String shareContent;
    private String targetUrl;
    private String title;
    private String imageUrl, userId;
    private int cindex = 0;
    private boolean isShop = false;
    private List<String> imgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initView();
    }

    private void initView() {
        userId = App.getInstance().getUid();
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rl_top_bar = (RelativeLayout) findViewById(R.id.rl_top_bar);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        webview = (WebView) findViewById(R.id.wv_content);
        //tv_title.setText("老板六六无线端官网");
        //设置WebView属性，能够执行Javascript脚本
        webview.setWebViewClient(new MyWebViewClient());
        WebSettings settings = webview.getSettings();
        webview.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        settings.setSupportZoom(true);
        settings.setDomStorageEnabled(true);
        webview.requestFocus();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        //加载需要显示的网页
        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringExtra("url");
            if (!TextUtils.isEmpty(mUrl) && "http://m.66boss.com/".equals(mUrl)) {
                imgList = new ArrayList<>();
                isShop = true;
                sharePopup = new SharePopup(context, mController);
                sharePopup.setOnItemSelectedListener(this);
            }
            boolean isHasTitle = intent.getBooleanExtra("isHasTitle", true);
            if (!isHasTitle) {
                rl_top_bar.setVisibility(View.GONE);
            }
            webview.loadUrl(mUrl);
        }
        //设置Web视图
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    }

    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String img) {
            cindex++;
            if (!TextUtils.isEmpty(img) && img.contains("/goods_img/")) {
                imgList.add(img);
            }
        }
    }

    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        } else {
            finish();
        }
        return false;

    }

    @Override
    public void onItemSelected(SHARE_MEDIA shareMedia) {
        UMediaObject uMediaObject = null;
        if (isShop) {
            int size = imgList.size();
            if (size > 0) {
                if (size > 1) {
                    imageUrl = imgList.get(1);
                } else {
                    imageUrl = imgList.get(0);
                }
            }
        }
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

    //Web视图
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (isShop && !TextUtils.isEmpty(url) && url.contains("goods-")) {
                targetUrl = url;
                if (imgList.size() > 0)
                    imgList.clear();
                tv_right.setVisibility(View.VISIBLE);
            } else {
                tv_right.setVisibility(View.GONE);
            }
            String title = view.getTitle();
            if (!TextUtils.isEmpty(title)) {
                rl_top_bar.setVisibility(View.VISIBLE);
                tv_title.setText(title);
            } else {
                tv_title.setText("");
            }

            view.loadUrl("javascript:(function(){"
                    + "var objs = document.getElementsByTagName(\"img\"); "
                    + "for(var i=0;i<objs.length;i++)  " + "{"
                    + "        window.local_obj.showSource(objs[i].src);  " + "    }  "
                    + "})()");
            Log.i("title", title);
            Log.i("url:", url);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:
                title = "嗨萌-购物";
                shareContent = "我正在嗨萌购物,一起来shopping!";
                //targetUrl = "https://api.66boss.com/web/download?uid=" + userId;
                if (!isFinishing()) {
                    if (sharePopup.isShowing()) {
                        sharePopup.dismiss();
                    } else {
                        sharePopup.show(getWindow().getDecorView());
                    }
                }
                break;
            case R.id.tv_back:
                if (webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
                break;
        }
    }
}
