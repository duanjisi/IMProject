package im.boss66.com.activity.treasure;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.MD5Util;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.widget.RoundImageView;
import im.boss66.com.widget.scan.CameraManager;
import im.boss66.com.widget.scan.CameraPreview;

/**
 * 找福娃
 */
public class CatchFuwaActivity extends BaseActivity implements View.OnClickListener, SensorEventListener {

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
    private boolean previewing = true, isTakePic = false;
    private PermissionListener permissionListener;
    private String savePath = Environment.getExternalStorageDirectory() + "/IMProject/";

    private Dialog dialog;
    private ImageView iv_success_catch;
    // private Button bt_catch;
    private String userId, fuwaId;
    private File imgFile;
    private ImageLoader imageLoader;
    private ImageView iv_success;
    private String fuwaNum;

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
        iv_success_catch = (ImageView) findViewById(R.id.iv_success_catch);
        iv_success = (ImageView) findViewById(R.id.iv_success);
        int sceenH = UIUtils.getScreenHeight(this);

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
            fuwaId = intent.getStringExtra("gid");
            String imgUrl = intent.getStringExtra("pic");
            fuwaNum = intent.getStringExtra("id");
            if (!TextUtils.isEmpty(imgUrl)) {
                imageLoader.displayImage(imgUrl, iv_thread,
                        ImageLoaderUtils.getDisplayImageOptions());
            }
        }
    }

    private void initViewParams() {
        mCameraManager = new CameraManager(this);
        try {
            mCameraManager.openDriver();
        } catch (IOException e) {
            MycsLog.e("IOException:" + e.getMessage());
        } catch (RuntimeException runtimeException) {
            MycsLog.e("open camera error:" + runtimeException.getMessage());
            showToast(R.string.error_open_camera_error, false);
            finish();
        }
        mCamera = mCameraManager.getCamera();
        mPreview = new CameraPreview(this, mCamera, mPreviewCallback, autoFocusCB);
        rl_preciew.addView(mPreview);
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
                break;
        }
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
                                if (!previewing && isTakePic) {
                                    mCamera.takePicture(null, null, mPictureCallback);
                                    isTakePic = false;
                                    Log.i("onSensorChanged", "拍照");
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
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                if (bm != null) {
                    bm = rotateBitmap(bm, 90);
                    if (bm != null) {
                        if (bm != null) {
                            String imageName = getNowTime() + ".jpg";
                            // 指定调用相机拍照后照片的储存路径
                            File dir = new File(savePath);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            imgFile = new File(dir, imageName);
                            BufferedOutputStream bos
                                    = new BufferedOutputStream(new FileOutputStream(imgFile));
                            bm.compress(Bitmap.CompressFormat.JPEG, 40, bos);
                            bos.flush();
                            bos.close();
                            getServerData();
                        }
                    }
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
                mCamera.autoFocus(autoFocusCB);
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
        releaseCamera();
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
                ToastUtil.showShort(CatchFuwaActivity.this, getString(R.string.giving_camera_permissions));
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
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_catch_fuwa, null);
            int sceenW = UIUtils.getScreenWidth(this);
            int sceenH = UIUtils.getScreenHeight(this);

            RoundImageView roundImageView = (RoundImageView) view.findViewById(R.id.riv_head);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_fuwa = (TextView) view.findViewById(R.id.tv_fuwa);
            Button bt_share = (Button) view.findViewById(R.id.bt_share);
            TextView tv_continue = (TextView) view.findViewById(R.id.tv_continue);
            tv_continue.setOnClickListener(this);
            bt_share.setOnClickListener(this);

            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setContentView(view);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (sceenW * 0.8);
            lp.height = (int) (sceenH * 0.8);
            dialogWindow.setAttributes(lp);
            dialogWindow.setGravity(Gravity.CENTER);
            dialog.setCanceledOnTouchOutside(false);
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
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
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
                            EventBus.getDefault().post("1");
                            playSucessGif();
                        } else {
                            previewing = true;
                            mCamera.startPreview();
                            autoFocusHandler.postDelayed(doAutoFocus, 1000);
                            showToast("捕捉失败TAT，再试下吧", false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    previewing = true;
                    mCamera.startPreview();
                    autoFocusHandler.postDelayed(doAutoFocus, 1000);
                    showToast("捕捉失败TAT，再试下吧", false);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                previewing = true;
                mCamera.startPreview();
                autoFocusHandler.postDelayed(doAutoFocus, 1000);
                showToast("捕捉失败TAT，再试下吧", false);
            }
        });
    }

    private void playSucessGif() {
        iv_success.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(R.drawable.fuwa_catch_succ)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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
    }
}
