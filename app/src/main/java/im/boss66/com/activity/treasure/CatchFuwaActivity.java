package im.boss66.com.activity.treasure;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.MD5Util;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.im.VerifyApplyActivity;
import im.boss66.com.activity.player.VideoPlayerNewActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.ChildEntity;
import im.boss66.com.entity.FriendState;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.http.request.FriendShipRequest;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.widget.RoundImageView;
import im.boss66.com.widget.popupWindows.SharePopup;
import im.boss66.com.widget.scan.CameraManager;
import im.boss66.com.widget.scan.CameraPreview;

/**
 * 找福娃
 */
public class CatchFuwaActivity extends BaseActivity implements View.OnClickListener, SensorEventListener,
        SharePopup.OnItemSelectedListener {
    private final static String TAG = HideFuwaActivity.class.getSimpleName();
    private SharePopup sharePopup;
    private LinearLayout ll_thread;
    private ImageView iv_click, iv_thread, iv_thread_bg;
    private TextView tv_back, tv_address;
    private RelativeLayout rl_preciew;
    private Calendar mCalendar;
    public static final int STATUS_NONE = 0;
    public static final int STATUS_STATIC = 1;
    public static final int STATUS_MOVE = 2;
    private int STATUE = STATUS_NONE;
    private int mX, mY, mZ;
    private long lastStaticStamp = 0;
    boolean isFocusing = false;
    boolean canFocusIn = false;  //内部是否能够对焦控制机制
    boolean canFocus = false;
    public static final int DELEY_DURATION = 3500;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private CameraPreview mPreview;
    private Camera mCamera;
    private im.boss66.com.widget.scan.CameraManager mCameraManager;
    private Handler autoFocusHandler;
    private boolean previewing = true, isTakePic = false, isHideOk = true;
    ;
    private PermissionListener permissionListener;
    private String savePath = Environment.getExternalStorageDirectory() + "/IMProject/";

    private Dialog dialog;
    private String userId, fuwaId;
    private File imgFile;
    private ImageLoader imageLoader;
    private ImageView iv_success;
    private String fuwaNum;
    private int sceenH;
    private RelativeLayout rl_user;
    private String fuwaUserId;
    private ChildEntity currentChild;
    private String videoUrl, videoBgUrl = "";
    private boolean isFriend, isDialogShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_fuwa);
        initView();
        getPermission();
    }

    private void initView() {
        imageLoader = ImageLoaderUtils.createImageLoader(this);
        userId = App.getInstance().getUid();
        iv_success = (ImageView) findViewById(R.id.iv_success);
        sceenH = UIUtils.getScreenHeight(this);
        sharePopup = new SharePopup(context, mController);
        sharePopup.setOnItemSelectedListener(this);
        iv_thread_bg = (ImageView) findViewById(R.id.iv_thread_bg);
        ll_thread = (LinearLayout) findViewById(R.id.ll_thread);
        iv_thread = (ImageView) findViewById(R.id.iv_thread);
        iv_click = (ImageView) findViewById(R.id.iv_click);
        tv_address = (TextView) findViewById(R.id.tv_address);
        autoFocusHandler = new Handler();
        rl_preciew = (RelativeLayout) findViewById(R.id.rl_preciew);
        tv_back = (TextView) findViewById(R.id.tv_back);

        RelativeLayout.LayoutParams threadParam = (RelativeLayout.LayoutParams) iv_thread_bg.getLayoutParams();
        threadParam.height = sceenH / 4;
        threadParam.width = sceenH / 4;
        iv_thread_bg.setLayoutParams(threadParam);

        RelativeLayout.LayoutParams threadbgParam = (RelativeLayout.LayoutParams) iv_thread.getLayoutParams();
        threadbgParam.height = sceenH / 4;
        threadbgParam.width = sceenH / 4;
        iv_thread.setLayoutParams(threadbgParam);

        tv_back.setOnClickListener(this);
        mSensorManager = (SensorManager) App.getInstance().getSystemService(Activity.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
        iv_click.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按住
                        ll_thread.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动
                        break;
                    case MotionEvent.ACTION_UP:
                        //松开
                        ll_thread.setVisibility(View.GONE);
                        break;
                }
                return true;
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            currentChild = (ChildEntity) intent.getSerializableExtra("obj");
//            fuwaId = intent.getStringExtra("gid");
//            String imgUrl = intent.getStringExtra("pic");
//            Log.i("imgUrl:", imgUrl);
//            fuwaNum = intent.getStringExtra("id");
            if (currentChild != null) {
                fuwaUserId = currentChild.getHider();
                requestFriendShip(fuwaUserId);
                fuwaId = currentChild.getGid();
                fuwaNum = currentChild.getId();
                String imgUrl = currentChild.getPic();
                if (!TextUtils.isEmpty(imgUrl)) {
                    Picasso.with(context).load(imgUrl).error(R.drawable.zf_default_message_image).into(iv_thread);
//                    imageLoader.displayImage(imgUrl, iv_thread,
//                            ImageLoaderUtils.getDisplayImageOptions());
                }
            }
        }
    }

    private void initViewParams() {
        if (mCameraManager == null) {
            mCameraManager = new CameraManager(this);
        }
        try {
            mCameraManager.openDriver();
        } catch (IOException e) {
            showToast("获取摄像头出错", false);
            finish();
            return;
        } catch (RuntimeException runtimeException) {
            showToast(R.string.error_open_camera_error, false);
            finish();
            return;
        }
        if (mCamera == null) {
            mCamera = mCameraManager.getCamera();
        }
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            // 设置预览照片的大小
            List<Camera.Size> supportedPictureSizes =
                    parameters.getSupportedPictureSizes();// 获取支持保存图片的尺寸
            if (supportedPictureSizes != null && supportedPictureSizes.size() > 0) {
                Camera.Size pictureSize = UIUtils.getPictureSize(this, supportedPictureSizes);
                parameters.setRotation(90);
                parameters.setPictureSize(pictureSize.width, pictureSize.height);
                mCamera.setParameters(parameters);
            }
            if (mPreview == null) {
                mPreview = new CameraPreview(this, mCamera, mPreviewCallback, autoFocusCB);
            }
            if (rl_preciew.getChildCount() > 0) {
                rl_preciew.removeAllViews();
            }
            rl_preciew.addView(mPreview);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        canFocus = true;
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mSensor);
        canFocus = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_continue:
                if (dialog != null) {
                    dialog.dismiss();
                }
                finish();
                break;
            case R.id.bt_share:
                title = "嗨萌寻宝";
                shareContent = getResources().getString(R.string.share_content);
                targetUrl = "https://api.66boss.com/web/download?uid=" + userId;
                if (!isFinishing()) {
                    if (sharePopup.isShowing()) {
                        sharePopup.dismiss();
                    } else {
                        sharePopup.show(dialog.getWindow().getDecorView());
                    }
                }
                break;
            case R.id.iv_close_user:
                if (rl_user != null && rl_user.getVisibility() == View.VISIBLE) {
                    rl_user.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.tv_add_friend:
                Intent intent = new Intent(context, VerifyApplyActivity.class);
                intent.putExtra("userid", fuwaUserId);
                startActivity(intent);
                break;
            case R.id.iv_video_photo:
                if (!TextUtils.isEmpty(videoUrl)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("videoPath", videoUrl);
                    bundle.putString("imgurl", videoBgUrl);
                    openActivity(VideoPlayerNewActivity.class, bundle);
                }
                break;
        }
    }

    private String shareContent;
    private String targetUrl;
    private String title;
    private String imageUrl;

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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            mCalendar = Calendar.getInstance();
            long stamp = mCalendar.getTimeInMillis();// 1393844912

            int second = mCalendar.get(Calendar.SECOND);// 53

            if (STATUE != STATUS_NONE) {
                int px = Math.abs(mX - x);
                int py = Math.abs(mY - y);
                int pz = Math.abs(mZ - z);
                double value = Math.sqrt(px * px + py * py + pz * pz);
                if (value > 1.4) {
                    //Log.i("onSensorChanged", "检测手机在移动");
                    STATUE = STATUS_MOVE;
                } else {
                    //Log.i("onSensorChanged", "检测手机静止");
                    //上一次状态是move，记录静态时间点
                    if (STATUE == STATUS_MOVE) {
                        lastStaticStamp = stamp;
                        canFocusIn = true;
                    }
                    if (canFocusIn) {
                        if (stamp - lastStaticStamp > DELEY_DURATION) {
                            //移动后静止一段时间，可以发生对焦行为
                            if (!isFocusing) {
                                canFocusIn = false;
                                if (!previewing && isTakePic && mCamera != null) {
                                    mCamera.takePicture(null, null, mPictureCallback);
                                    isTakePic = false;
                                }
                            }
                        }
                    }
                    STATUE = STATUS_STATIC;
                }
            } else {
                lastStaticStamp = stamp;
                STATUE = STATUS_STATIC;
            }

            mX = x;
            mY = y;
            mZ = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 获取相机内容回调方法
     */
    Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.i("mPreviewCallback", "jinlai");
        }
    };

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            try {
                Bitmap bm = byteToBitmap(bytes);
                if (bm != null) {
                    String imageName = "hai_meng_fuwa.jpg";
                    // 指定调用相机拍照后照片的储存路径
                    File dir = new File(savePath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    imgFile = new File(dir, imageName);
                    BufferedOutputStream bos
                            = new BufferedOutputStream(new FileOutputStream(imgFile));
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                    bos.flush();
                    bos.close();
                    if (!bm.isRecycled()) {
                        bm.recycle();
                    }
                    getServerData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing) {
                if (mCamera != null) {
                    mCamera.autoFocus(autoFocusCB);
                }
                isTakePic = true;
                previewing = false;
            }
        }
    };

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iv_success = null;
        releaseCamera();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                initViewParams();
            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(CatchFuwaActivity.this, getString(R.string.giving_camera2_permissions));
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_CHAT_CAMERA //相机权限
                ).request(permissionListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
    }

    @SuppressLint("SimpleDateFormat")
    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    //根据拍照的图片来剪裁
    private Bitmap cropPhotoImage(Bitmap bmp) {
        int height;
        int width;
        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();
        int view_w = width * 448 / 720;
        int left = (width - view_w) / 2;
        int top = (height - view_w - height / 20) / 2;
        Bitmap b3 = null;
        try {
            Bitmap bitmap = Bitmap.createBitmap(bmp, left, top, view_w, view_w);
            if (bitmap != null) {
                b3 = getCircleBitmap(bitmap);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return b3;
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        origin = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        return origin;
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        //在画布上绘制一个圆
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void showDialog() {
        if (dialog == null) {
            View dialog_view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_catch_fuwa, null);
            int sceenW = UIUtils.getScreenWidth(this);
            int sceenH = UIUtils.getScreenHeight(this);

            RoundImageView roundImageView = (RoundImageView) dialog_view.findViewById(R.id.riv_head);
            TextView tv_name = (TextView) dialog_view.findViewById(R.id.tv_name);
            TextView tv_fuwa = (TextView) dialog_view.findViewById(R.id.tv_fuwa);
            Button bt_share = (Button) dialog_view.findViewById(R.id.bt_share);
            TextView tv_continue = (TextView) dialog_view.findViewById(R.id.tv_continue);
            tv_continue.setOnClickListener(this);
            bt_share.setOnClickListener(this);

            dialog = new Dialog(CatchFuwaActivity.this, R.style.ActionSheetDialogStyle);
            dialog.setContentView(dialog_view);

            LinearLayout ll_dialog = (LinearLayout) dialog_view.findViewById(R.id.ll_dialog);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ll_dialog.getLayoutParams();
            layoutParams.width = (int) (sceenW * 0.9);
            layoutParams.height = (int) (sceenH * 0.85);
            ll_dialog.setLayoutParams(layoutParams);

            rl_user = (RelativeLayout) dialog_view.findViewById(R.id.rl_user);
            RelativeLayout rl_video = (RelativeLayout) dialog_view.findViewById(R.id.rl_video);
            LinearLayout ll_user = (LinearLayout) dialog_view.findViewById(R.id.ll_user);
            ImageView iv_video_photo = (ImageView) dialog_view.findViewById(R.id.iv_video_photo);
            ImageView iv_video_play = (ImageView) dialog_view.findViewById(R.id.iv_video_play);
            RoundImageView riv_user_head = (RoundImageView) dialog_view.findViewById(R.id.riv_user_head);
            TextView tv_user_name = (TextView) dialog_view.findViewById(R.id.tv_user_name);
            TextView tv_user_area = (TextView) dialog_view.findViewById(R.id.tv_user_area);
            TextView tv_add_friend = (TextView) dialog_view.findViewById(R.id.tv_add_friend);
            ImageView iv_close_user = (ImageView) dialog_view.findViewById(R.id.iv_close_user);
            iv_close_user.setOnClickListener(this);
            tv_add_friend.setOnClickListener(this);
            iv_video_photo.setOnClickListener(this);
            if (isFriend) {
                tv_add_friend.setText("已是好友");
                tv_add_friend.setEnabled(false);
            } else {
                tv_add_friend.setText("+ 好友");
                tv_add_friend.setEnabled(true);
            }
            if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(fuwaUserId) && userId.equals(fuwaUserId)) {
                tv_add_friend.setVisibility(View.GONE);
            }
            if (currentChild != null) {
                String name = currentChild.getName();
                if (!TextUtils.isEmpty(name)) {
                    tv_user_name.setText(name);
                }
                String area = currentChild.getLocation();
                if (!TextUtils.isEmpty(area)) {
                    tv_user_area.setText(area);
                }
                videoUrl = currentChild.getVideo();
                if (!TextUtils.isEmpty(videoUrl)) {
                    rl_video.setVisibility(View.VISIBLE);
                    String[] arr = videoUrl.split("\\.");
                    if (arr != null && arr.length > 1) {
                        arr[arr.length - 1] = "jpg";
                        for (String s : arr) {
                            videoBgUrl += s;
                        }
                        for (int i = 0; i < arr.length; i++) {
                            if (i == 0) {
                                videoBgUrl = arr[i];
                            } else {
                                videoBgUrl = videoBgUrl + "." + arr[i];
                            }
                        }
                        Glide.with(this).load(videoBgUrl).error(R.drawable.zf_default_message_image).into(iv_video_photo);
                    }
                } else {
                    rl_video.setVisibility(View.GONE);
                }
                String head = currentChild.getAvatar();
                Glide.with(this).load(head).error(R.drawable.zf_default_message_image).into(riv_user_head);
            }
            RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) rl_user.getLayoutParams();
            rlParams.width = sceenW;
            rlParams.height = sceenH;
            rl_user.setLayoutParams(rlParams);

            RelativeLayout.LayoutParams llParams = (RelativeLayout.LayoutParams) ll_user.getLayoutParams();
            llParams.width = (int) (sceenW * 0.8);
            llParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            ll_user.setLayoutParams(llParams);

            RelativeLayout.LayoutParams ivVideoParams = (RelativeLayout.LayoutParams) iv_video_photo.getLayoutParams();
            ivVideoParams.width = sceenW / 5 * 3;
            ivVideoParams.height = sceenW / 5 * 3;
            iv_video_photo.setLayoutParams(ivVideoParams);
            RelativeLayout.LayoutParams ivVideoPlayParams = (RelativeLayout.LayoutParams) iv_video_play.getLayoutParams();
            ivVideoPlayParams.width = sceenW / 5;
            ivVideoPlayParams.height = sceenW / 5;
            iv_video_play.setLayoutParams(ivVideoPlayParams);
            RelativeLayout.LayoutParams ivHeadParams = (RelativeLayout.LayoutParams) riv_user_head.getLayoutParams();
            ivHeadParams.width = sceenW / 7;
            ivHeadParams.height = sceenW / 7;
            riv_user_head.setLayoutParams(ivHeadParams);

            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            params.height = sceenH;
            params.width = sceenW;
            dialogWindow.setAttributes(params);
            dialogWindow.setGravity(Gravity.CENTER);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (!isDialogShow) {
                        finish();
                    }
                }
            });
            AccountEntity sAccount = App.getInstance().getAccount();
            if (sAccount != null) {
                String head = sAccount.getAvatar();
                imageLoader.displayImage(head, roundImageView,
                        ImageLoaderUtils.getDisplayImageOptions());
                String userName = sAccount.getUser_name();
                if (!TextUtils.isEmpty(userName)) {
                    tv_name.setText(userName);
                } else {
                    tv_name.setText("" + sAccount.getUser_id());
                }
            }
            tv_fuwa.setText(fuwaNum + "号福娃");
        }
        dialog.show();
    }

    private void getServerData() {
        String signUrl = "/capture?user=" + userId + "&gid=" + fuwaId + "&platform=boss66";
        String sign = MD5Util.getStringMD5(signUrl);
        String url = HttpUrl.CATCH_MY_FUWA + userId + "&gid=" +
                fuwaId + "&sign=" + sign;
        HttpUtils httpUtils = new HttpUtils(12 * 1000);
        RequestParams params = new RequestParams();
        params.addBodyParameter("file", imgFile);
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    try {
                        JSONObject obj = new JSONObject(res);
                        int code = obj.getInt("code");
                        boolean isTrue = obj.getBoolean("data");
                        if (code == 0 && isTrue) {
//                            EventBus.getDefault().post("1");
                            Intent intent = new Intent(Constants.Action.MAP_MARKER_REFRESH);
                            intent.putExtra("gid", fuwaId);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            try {
                                playSucessGif();
                            } catch (Exception e) {
                                handler.sendEmptyMessageDelayed(111,
                                        1000);
                            }
                        } else {
                            previewing = true;
                            if (mCamera != null) {
                                mCamera.startPreview();
                            } else {
                                initViewParams();
                            }
                            autoFocusHandler.postDelayed(doAutoFocus, 1000);
                            showToast("图片匹配失败TAT，再试下吧", false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    previewing = true;
                    if (mCamera != null) {
                        mCamera.startPreview();
                    } else {
                        initViewParams();
                    }
                    autoFocusHandler.postDelayed(doAutoFocus, 1000);
                    showToast("图片匹配失败TAT，再试下吧", false);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                previewing = true;
                if (mCamera != null) {
                    mCamera.startPreview();
                } else {
                    initViewParams();
                }
                autoFocusHandler.postDelayed(doAutoFocus, 1000);
                showToast("图片匹配失败TAT，再试下吧", false);
            }
        });
    }

    private void playSucessGif() {
        iv_success.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(R.drawable.fuwa_catch_succ)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Integer, GlideDrawable>() {

                    @Override
                    public boolean onException(Exception arg0, Integer arg1,
                                               Target<GlideDrawable> arg2, boolean arg3) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource,
                                                   Integer model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        // 计算动画时长
                        GifDrawable drawable = (GifDrawable) resource;
                        GifDecoder decoder = drawable.getDecoder();
                        int duration = 0;
                        for (int i = 0; i < drawable.getFrameCount(); i++) {
                            duration += decoder.getDelay(i);
                        }
                        if (duration > 3000) {
                            duration = 3000;
                        }
                        //发送延时消息，通知动画结束
                        handler.sendEmptyMessageDelayed(111,
                                duration);
                        return false;
                    }
                }) //仅仅加载一次gif动画
                .into(new GlideDrawableImageViewTarget(iv_success, 1));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 111:
                    showDialog();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (isDialogShow && dialog != null) {
            isDialogShow = false;
            dialog.show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    public Bitmap byteToBitmap(byte[] imgByte) {
        InputStream input = null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        input = new ByteArrayInputStream(imgByte);
        SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(
                input, null, options));
        bitmap = (Bitmap) softRef.get();
        if (imgByte != null) {
            imgByte = null;
        }

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    private void requestFriendShip(String userId) {
        if (!userId.equals("")) {
            FriendShipRequest request = new FriendShipRequest(TAG, userId);
            request.send(new BaseDataRequest.RequestCallback<FriendState>() {
                @Override
                public void onSuccess(FriendState pojo) {
                    String isf = pojo.getIs_friend();
                    if ("1".equals(isf)) {
                        isFriend = true;
                    } else {
                        isFriend = false;
                    }
                }

                @Override
                public void onFailure(String msg) {
                    showToast(msg, true);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            isDialogShow = true;
        }
    }


}
