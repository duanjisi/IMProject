package im.boss66.com.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtr.zbar.build.ZBarDecoder;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.lang.reflect.Field;

import im.boss66.com.R;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.scan.CameraManager;
import im.boss66.com.widget.scan.CameraPreview;

/**
 * 扫描二维码页面
 */
public class CaptureActivity extends BaseActivity {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private im.boss66.com.widget.scan.CameraManager mCameraManager;
    private TextView tvCancel;
    private FrameLayout mScanPreview;
    private RelativeLayout mScanContainer;
    private RelativeLayout mScanCropView;
    private Rect mCropRect = null;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private TextView tvBack;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewById();
        initViews();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void findViewById() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        mScanPreview = (FrameLayout) findViewById(R.id.capture_preview);
        mScanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        mScanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        autoFocusHandler = new Handler();
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
        mScanPreview.addView(mPreview);

        ImageView scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        TranslateAnimation animation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(1500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setInterpolator(new LinearInterpolator());
        scanLine.startAnimation(animation);
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    /**
     * 获取相机内容回调方法
     */
    PreviewCallback mPreviewCallback = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Size size = camera.getParameters().getPreviewSize();

            // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < size.height; y++) {
                for (int x = 0; x < size.width; x++)
                    rotatedData[x * size.height + size.height - y - 1] = data[x + y * size.width];
            }

            // 宽高也要调整
            int tmp = size.width;
            size.width = size.height;
            size.height = tmp;
            initCrop();
            ZBarDecoder zBarDecoder = new ZBarDecoder();
            String result = zBarDecoder.decodeCrop(rotatedData, size.width, size.height,
                    mCropRect.left, mCropRect.top, mCropRect.width(), mCropRect.height());

            if (!TextUtils.isEmpty(result)) {
//                showToast(result, true);
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                barcodeScanned = true;
                decodeResult(result);
            }
        }
    };

    /**
     * 解释二维码扫描的结果
     *
     * @param result 二维码扫描的结果
     */
    private void decodeResult(String result) {
        MycsLog.i("result:" + result);
//        if (result.contains("http://www.66boss.com/app/")) {
//            joinGroup(result);
//        } else if (result.contains("friend:")) {
//            String friendId = result.substring(result.indexOf(":") + 1, result.length());
//            Intent intent = new Intent(context, PersonPagerActivity.class);
//            intent.putExtra("user_id", friendId);
//            startActivity(intent);
//            finish();
//        } else if (result.contains("groupId:")) {
//            String groupId = result.substring(result.indexOf(":") + 1, result.length());
//            JoinGroup(groupId);
//        } else {
//            showToast(getString(R.string.notice_not_swwy_qr_code), false);
//            finish();
//        }
    }


    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = mCameraManager.getCameraResolution().y;
        int cameraHeight = mCameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        mScanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1]/* - getStatusBarHeight()*/;

        int cropWidth = mScanCropView.getWidth();
        int cropHeight = mScanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = mScanContainer.getWidth();
        int containerHeight = mScanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Capture Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://im.boss66.com.activity/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Capture Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://im.boss66.com.activity/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
