package im.boss66.com.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dtr.zbar.build.ZBarDecoder;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Hashtable;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelector;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.discover.PersonalNearbyDetailActivity;
import im.boss66.com.activity.event.FuwaGid;
import im.boss66.com.entity.ClubEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.http.request.AddFriendRequest;
import im.boss66.com.http.request.GroupAddMemsRequest;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.util.RGBLuminanceSource;
import im.boss66.com.util.Utils;
import im.boss66.com.widget.scan.CameraManager;
import im.boss66.com.widget.scan.CameraPreview;

/**
 * 扫描二维码页面
 */
public class CaptureActivity extends BaseActivity {
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 234;
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
    private String photo_path;
    private Handler handler = new Handler();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;
    private TextView tvBack, tvPhoto;
    private Bitmap scanBitmap;
    private PermissionListener permissionListener;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewById();
        getPermission();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void findViewById() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvPhoto = (TextView) findViewById(R.id.tv_photo);
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
        tvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photo();
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("info", "================onResume()");
    }

    public void onPause() {
        super.onPause();
//        releaseCamera();
    }


    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
//            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

//        if (mCamera != null) {
//            try {
//                previewing = false;
//                mCamera.setPreviewCallback(null);
//                mCamera.setPreviewCallbackWithBuffer(null);
//                mCamera.stopPreview();
//                mCamera.release();
////                mCamera = null;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
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
        if (result.contains("uid=")) { //加好友
            String userid = result.substring(result.indexOf("=") + 1, result.length());
            Intent intent = new Intent(context, PersonalNearbyDetailActivity.class);
            intent.putExtra("classType", "CaptureActivity");
            intent.putExtra("userid", userid);
            startActivity(intent);
            finish();
        } else if (result.contains("gid=")) { //加群

            String gid = result.substring(result.indexOf("=") + 1, result.length());
            GroupAddMemsRequest request = new GroupAddMemsRequest(TAG, gid, App.getInstance().getUid());
            request.send(new BaseModelRequest.RequestCallback<String>() {
                @Override
                public void onSuccess(String pojo) {
                    onAddMember();
                }

                @Override
                public void onFailure(String msg) {
                    cancelLoadingDialog();
                    showToast(msg, true);
                }
            });

        } else if (result.contains("fuwa:user:")) {       //扫描二维码赠送
            String code = result.substring(result.lastIndexOf(":") + 1, result.length());
//            EventBus.getDefault().post(new FuwaGid(gid));
            Intent intent = new Intent();
            intent.putExtra("code", code);
            setResult(RESULT_OK, intent);
            finish();
        } else if (result.contains("fuwa:fuwa:")) { //商家扫描，兑奖


            String gid = result.substring(result.lastIndexOf(":") + 1, result.length());
            Log.i("liwya", "gid==" + gid);
            scanCode(gid);


        }
    }

    private void scanCode(String gid) {

        showLoadingDialog();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        String url = HttpUrl.SCAN_CODE;
        String userid = App.getInstance().getUid();
        url = url + "?userid=" + userid + "&fuwagid=" + gid + "&time=" + System.currentTimeMillis();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;


                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String message = jsonObject.getString("message");

                        switch (jsonObject.getInt("code")) {
                            case 0://成功
//                                showToast("兑奖成功",false);
                                showToast(message, false);
                                break;
                            case 1:
                                showToast(message, false);
                                break;
                            case 2:
                                showToast(message, false);
                                break;
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 800);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("兑奖失败", false);
                    }
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
            }
        });
    }

    private void onAddMember() {
        try {
            Thread.sleep(1000);
            cancelLoadingDialog();
            showToast("加入该群成功!", true);
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void addFriendRequest(String userid) {
        showLoadingDialog();
        AddFriendRequest request = new AddFriendRequest(TAG, userid);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                cancelLoadingDialog();
                finish();
                showToast("已发送好友请求!", true);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, true);
                cancelLoadingDialog();
            }
        });
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
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Capture Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://im.boss66.com.activity/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Capture Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://im.boss66.com.activity/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    private void photo() {
        Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
        if (Build.VERSION.SDK_INT < 19) {
            innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            innerIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        }
        // innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        innerIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
        CaptureActivity.this
                .startActivityForResult(wrapperIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    String[] proj = {MediaStore.Images.Media.DATA};
                    // 获取选中图片的路径
                    Cursor cursor = getContentResolver().query(data.getData(),
                            proj, null, null, null);
                    if (cursor.moveToFirst()) {
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(column_index);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(),
                                    data.getData());
                            Log.i("123path  Utils", photo_path);
                        }
                        Log.i("123path", photo_path);

                    }
                    cursor.close();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photo_path);
                            // String result = decode(photo_path);
                            if (result == null) {
                                Log.i("123", "   -----------");
                                Looper.prepare();
                                showToast("图片格式有误", true);
                                Looper.loop();
                            } else {
                                Log.i("123result", result.toString());
                                // Log.i("123result", result.getText());
                                // 数据返回
                                String recode = recode(result.toString());
                                decodeResult(recode);
//                                Intent data = new Intent();
//                                data.putExtra("result", recode);
//                                setResult(300, data);
//                                finish();
                            }
                        }
                    }).start();
                    break;
            }
        }
    }

    /**
     * 中文乱码
     * <p/>
     * 暂时解决大部分的中文乱码 但是还有部分的乱码无法解决 .
     * <p/>
     * 如果您有好的解决方式 请联系 2221673069@qq.com
     * <p/>
     * 我会很乐意向您请教 谢谢您
     *
     * @return
     */
    private String recode(String str) {
        String formart = "";

        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder()
                    .canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
                Log.i("1234      ISO8859-1", formart);
            } else {
                formart = str;
                Log.i("1234      stringExtra", str);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formart;
    }

    // TODO: 解析部分图片
    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {

            return null;

        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小

        int sampleSize = (int) (options.outHeight / (float) 200);

        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);

        // --------------测试的解析方法---PlanarYUVLuminanceSource-这几行代码对project没作功----------

        LuminanceSource source1 = new PlanarYUVLuminanceSource(
                rgb2YUV(scanBitmap), scanBitmap.getWidth(),
                scanBitmap.getHeight(), 0, 0, scanBitmap.getWidth(),
                scanBitmap.getHeight(), false);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                source1));
        MultiFormatReader reader1 = new MultiFormatReader();
        Result result1;
        try {
            result1 = reader1.decode(binaryBitmap);
            String content = result1.getText();
            Log.i("123content", content);
        } catch (NotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // ----------------------------
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {

            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {

            e.printStackTrace();

        } catch (ChecksumException e) {

            e.printStackTrace();

        } catch (FormatException e) {

            e.printStackTrace();

        }
        return null;
    }

    /**
     * //TODO: TAOTAO 将bitmap由RGB转换为YUV //TOOD: 研究中
     *
     * @param bitmap 转换的图形
     * @return YUV数据
     */
    public byte[] rgb2YUV(Bitmap bitmap) {
        // 该方法来自QQ空间
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int len = width * height;
        byte[] yuv = new byte[len * 3 / 2];
        int y, u, v;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = pixels[i * width + j] & 0x00FFFFFF;

                int r = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 16) & 0xFF;

                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;

                y = y < 16 ? 16 : (y > 255 ? 255 : y);
                u = u < 0 ? 0 : (u > 255 ? 255 : u);
                v = v < 0 ? 0 : (v > 255 ? 255 : v);

                yuv[i * width + j] = (byte) y;
                // yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
                // yuv[len + (i >> 1) * width + (j & ~1) + 1] = (byte) v;
            }
        }
        return yuv;
    }

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                initViews();
            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(CaptureActivity.this, getString(R.string.giving_camera_permissions));
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

}
