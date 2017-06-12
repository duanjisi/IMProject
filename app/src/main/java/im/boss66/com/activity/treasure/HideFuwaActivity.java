package im.boss66.com.activity.treasure;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.discover.VideoListActivity;
import im.boss66.com.adapter.FuwaHideAddressAdapter;
import im.boss66.com.entity.BaseResult;
import im.boss66.com.entity.FuwaClassEntity;
import im.boss66.com.entity.FuwaEntity;
import im.boss66.com.entity.ImageTool;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.widget.ActionSheet;
import im.boss66.com.widget.scan.CameraManager;
import im.boss66.com.widget.scan.CameraPreview;

/**
 * 藏福娃
 */
public class HideFuwaActivity extends BaseActivity implements View.OnClickListener, SensorEventListener, AMapLocationListener,
        PoiSearch.OnPoiSearchListener, ActionSheet.OnSheetItemClickListener, TextWatcher {

    private TextView tv_back, tv_bottom, tv_change_place;
    private Button bt_catch;
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
    public static final int DELEY_DURATION = 2000;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private CameraPreview mPreview;
    private Camera mCamera;
    private im.boss66.com.widget.scan.CameraManager mCameraManager;
    private boolean previewing = true, isTakePic = false;
    private PermissionListener permissionListener;
    // private String savePath = Environment.getExternalStorageDirectory() + "/IMProject/";
    private String savePath;

    private TextView tv_address;
    private ImageView iv_show_address;
    private PopupWindow popWindow;
    private RelativeLayout rl_address;
    private RelativeLayout rl_search;
    private RecyclerView rv_address;

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp;//
    private PoiSearch poiSearch;
    private List<PoiItem> poiItems;// poi数据
    private AMapLocation location;
    private FuwaHideAddressAdapter addressAdapter;
    private String geohash, address;
    private File imgFile;
    private Dialog dialog;
    private ImageView iv_dialog_icon;
    private TextView tv_dialog_address, bt_hide_ok;
    private Bitmap bitmapImg;
    private boolean isJump = false, isHideOk = true;
    private String userId;
    private int currentFuwaNum = 0, curSelectFuwaNum = 0;
    private Dialog dialogNum, dialogRecommond;
    private EditText et_dialog_num;
    private TextView tv_dialog_num_tip;
    private Button bt_dialog_catch;

    private TextView tv_time;
    private ImageView iv_video, iv_video_img;
    private FrameLayout fl_video_dialog_img;
    private RelativeLayout rl_time;
    private EditText et_recommond;

    private PopupWindow popdialogWindow;
    private String[] popTime;
    private int validtime = 1;
    private int cameraType;
    private final int READ_VIDEO = 4;//本地视频
    private final int RECORD_VIDEO = 3;//拍视频
    private File videoFile;
    private String recommond;
    private ImageView iv_bg;
    private Camera.Parameters parameters;
    private String curCity, curArea;
    private boolean isDialogShow = false;
    private int fuwaSocialNum, fuwaTreasureNum, fuwaSelectType;
    private TextView tv_fuwa_type, tv_fuwa_class;
    private boolean isSelectVideo;
    private TextView tv_tx_num;
    private int sceenH, sceenW;
    private ListPopupWindow listPopupWindow;
    private List<FuwaClassEntity.DataBean> classStrList;
    private String classId;
    private LinearLayout ll_fuwa_class;
    private boolean isFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_fuwa);
        initView();
        getPermission();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 20) {
            savePath = getFilesDir().getPath();
        } else {
            savePath = Environment.getExternalStorageDirectory() + "/IMProject/";
        }
        classStrList = new ArrayList<>();
        getFuwaClass();
        sceenH = (int) (UIUtils.getScreenHeight(context) * 0.8);
        sceenW = (int) (UIUtils.getScreenWidth(context) * 0.8);
        userId = App.getInstance().getUid();
        getMyApplyFuwa();
        iv_bg = (ImageView) findViewById(R.id.iv_bg);

        rl_address = (RelativeLayout) findViewById(R.id.rl_address);
        tv_address = (TextView) findViewById(R.id.tv_address);
        iv_show_address = (ImageView) findViewById(R.id.iv_show_address);

        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_bottom = (TextView) findViewById(R.id.tv_bottom);
        tv_change_place = (TextView) findViewById(R.id.tv_change_place);
        bt_catch = (Button) findViewById(R.id.bt_catch);
        rl_preciew = (RelativeLayout) findViewById(R.id.rl_preciew);
        bt_catch.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        tv_change_place.setOnClickListener(this);
        iv_show_address.setOnClickListener(this);
        rl_address.setOnClickListener(this);
        mSensorManager = (SensorManager) App.getInstance().getSystemService(Activity.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
    }

    Handler autoFocusHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 5:
                    finish();
                    break;
            }
        }
    };

    private void initCamera() {
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
        if (parameters == null && mCamera != null) {
            parameters = mCamera.getParameters();
            // 设置预览照片的大小
            List<Camera.Size> supportedPictureSizes =
                    parameters.getSupportedPictureSizes();// 获取支持保存图片的尺寸
            if (supportedPictureSizes != null && supportedPictureSizes.size() > 0) {
                Camera.Size pictureSize = UIUtils.getPictureSize(this, supportedPictureSizes);
                parameters.setRotation(90);
                parameters.setPictureSize(pictureSize.width, pictureSize.height);
            }
        }
        if (parameters != null && mCamera != null) {
            try {
                mCamera.setParameters(parameters);
            } catch (Exception e) {

            }
        }
        if (mPreview != null) {
            mPreview = null;
        }
        mPreview = new CameraPreview(this, mCamera, mPreviewCallback, autoFocusCB);
        rl_preciew.removeAllViews();
        rl_preciew.addView(mPreview);
    }

    private void initViewParams() {
        initMap();
        initCamera();
    }

    /**
     * 获取相机内容回调方法
     */
    Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
        }
    };

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            try {
                if (Build.VERSION.SDK_INT >= 24) {
                    mCamera.stopPreview();
                }
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                opts.inSampleSize = ImageTool.computeSampleSize(opts, -1, sceenW * sceenH);
                opts.inJustDecodeBounds = false;
                bitmapImg = byteToBitmap(opts, bytes);
                if (bitmapImg != null) {
                    iv_bg.setVisibility(View.VISIBLE);
                    Glide.with(context).load(bytes).into(iv_bg);
                    createFileWithByte(bytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError error) {
                showToast("相机异常,本页面即将销毁，请重新进入", false);
                System.gc();
                autoFocusHandler.sendEmptyMessageDelayed(5, 1000);
            }
        }
    };

    private Bitmap byteToBitmap(BitmapFactory.Options options, byte[] imgByte) {
        InputStream input = null;
        Bitmap bitmap = null;
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
            e.printStackTrace();
        }
        return bitmap;
    }

    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing) {
                try {
                    mCamera.autoFocus(autoFocusCB);
                } catch (Exception e) {

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
            if (mCameraManager != null)
                mCameraManager.closeDriver();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFinish = true;
        if (dialogRecommond != null) {
            dialogRecommond.dismiss();
        }
        releaseCamera();
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        if (bitmapImg != null) {
            bitmapImg = null;
        }
        if (videoFile != null) {
            videoFile = null;
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        if (dialogNum != null) {
            dialogNum.dismiss();
        }
        imgFile = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                isFinish = true;
                finish();
                break;
            case R.id.bt_catch:
                if (TextUtils.isEmpty(address) || "编辑地址".equals(address)) {
                    showToast("请先编辑地址", false);
                    return;
                }
//                Bundle bundle = new Bundle();
//                bundle.putString("address", address);
//                bundle.putString("geohash", geohash);
//                ChooseFuwaHideActivity.getImgFile(imgFile);
//                openActvityForResult(ChooseFuwaHideActivity.class, 101, bundle);
//                isJump = true;
                showNumDialog();
                break;
            case R.id.tv_change_place:
                if (videoFile != null) {
                    fl_video_dialog_img.setVisibility(View.GONE);
                    iv_video.setVisibility(View.VISIBLE);
                    videoFile = null;
                }
                tv_change_place.setVisibility(View.INVISIBLE);
                bt_catch.setVisibility(View.GONE);
                tv_bottom.setVisibility(View.VISIBLE);
                previewing = true;
                canFocusIn = true;
                isHideOk = true;
                iv_bg.setVisibility(View.GONE);
//                mPreview.setVisibility(View.VISIBLE);
                if (mCamera == null) {
                    initCamera();
                } else {
                    mCamera.startPreview();
                }
                autoFocusHandler.postDelayed(doAutoFocus, 800);
                break;
            case R.id.iv_show_address:
                if (popWindow != null) {
                    if (popWindow.isShowing()) {
                        iv_show_address.setImageResource(R.drawable.down_fw);
                        popWindow.dismiss();
                    } else {
                        iv_show_address.setImageResource(R.drawable.up_fw);
                        showPop();
                    }
                } else {
                    iv_show_address.setImageResource(R.drawable.up_fw);
                    showPop();
                }
                break;
            case R.id.rl_search:
//                isJump = false;
////                openActvityForResult(SearchAddressActivity.class, 102);
//
//                Bundle bundle = new Bundle();
//                bundle.putString("city", curCity);
//                bundle.putString("area", curArea);
//                bundle.putString("curGeohash", geohash);
//                openActvityForResult(FuwaAddressSearchActivity.class, 102, bundle);

                break;
            case R.id.bt_hide_ok:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                isHideOk = true;
                finish();
                break;
            case R.id.bt_dialog_catch:
                String num = et_dialog_num.getText().toString().trim();
                if (!TextUtils.isEmpty(num)) {
                    curSelectFuwaNum = Integer.parseInt(num);
                    if (curSelectFuwaNum > currentFuwaNum) {
                        showToast("输入个数超过您申请的福娃", false);
                        return;
                    } else if (curSelectFuwaNum <= 0) {
                        showToast("输入的个数必须大于0", false);
                        return;
                    }
                } else {
                    showToast("输入的个数必须大于0", false);
                    return;
                }
                if (fuwaSelectType == 1) {
                    if (TextUtils.isEmpty(classId) || "i".equals(classId)) {
                        showToast("请选择福娃分类", false);
                        return;
                    }
                }
                if (dialogNum != null && dialogNum.isShowing()) {
                    dialogNum.dismiss();
                }
                showRecommondDialog();
                break;
            case R.id.iv_close:
                if (dialogNum != null && dialogNum.isShowing()) {
                    dialogNum.dismiss();
                }
                break;
            case R.id.tv_time_1:
                validtime = 1;
                setPopTime(validtime);
                break;
            case R.id.tv_time_2:
                validtime = 2;
                setPopTime(validtime);
                break;
            case R.id.tv_time_3:
                validtime = 3;
                setPopTime(validtime);
                break;
            case R.id.tv_time_4:
                validtime = 4;
                setPopTime(validtime);
                break;
            case R.id.rl_time:
                showTimeChoosePop();
                break;
            case R.id.iv_video:
                isSelectVideo = true;
                showActionSheet();
                break;
            case R.id.bt_sure:
                if (dialogRecommond != null && dialogRecommond.isShowing()) {
                    dialogRecommond.dismiss();
                }
                recommond = et_recommond.getText().toString().trim();
                et_recommond.setText("");
                hideFuwaServer();
                break;
            case R.id.rl_fuwa_type:
                isSelectVideo = false;
                showActionSheet();
                break;
            case R.id.tv_jump_over:
                if (dialogRecommond != null && dialogRecommond.isShowing()) {
                    dialogRecommond.dismiss();
                }
                recommond = et_recommond.getText().toString().trim();
                et_recommond.setText("");
                hideFuwaServer();
                break;
            case R.id.rl_fuwa_class:
                showClassPop(view);
                break;
            case R.id.rl_address:
                isJump = false;
                Bundle bundle = new Bundle();
                bundle.putString("city", curCity);
                bundle.putString("area", curArea);
                bundle.putString("curGeohash", geohash);
                openActvityForResult(FuwaAddressSearchActivity.class, 102, bundle);
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
                                if (!previewing && isTakePic && isHideOk) {
                                    try {
                                        mCamera.takePicture(null, null, mPictureCallback);
                                        isTakePic = false;
                                        tv_change_place.setVisibility(View.VISIBLE);
                                        bt_catch.setVisibility(View.VISIBLE);
                                        tv_bottom.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        //mCamera.startPreview();
                                        canFocusIn = true;
                                        showToast("对焦失败，请移动手机重试下", false);
                                    }
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

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                if (cameraType == READ_VIDEO) {
                    openActvityForResult(VideoListActivity.class, READ_VIDEO);
                } else if (cameraType == RECORD_VIDEO) {
                    try {
                        releaseCamera();
                        Intent mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        mIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 20 * 1024 * 1024L);
                        startActivityForResult(mIntent, RECORD_VIDEO);
                    } catch (Exception e) {
                        Log.i("Exception:", e.getMessage());
                    }
                } else {
                    initViewParams();
                }
            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(HideFuwaActivity.this, getString(R.string.giving_camera_permissions));
            }
        };
        if (cameraType == READ_VIDEO) {
            PermissionUtil
                    .with(this)
                    .permissions(
                            PermissionUtil.PERMISSIONS_SD_READ_WRITE //相机权限
                    ).request(permissionListener);
        } else if (cameraType == RECORD_VIDEO) {
            PermissionUtil
                    .with(this)
                    .permissions(
                            PermissionUtil.PERMISSIONS_GROUP_CAMERA //相机权限
                    ).request(permissionListener);
        } else {
            PermissionUtil
                    .with(this)
                    .permissions(
                            PermissionUtil.PERMISSIONS_CAMERA_LOCATION //相机权限
                    ).request(permissionListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
    }

    private void showPop() {
        if (popWindow == null) {
            View popupView = getLayoutInflater().inflate(R.layout.popupwd_fuwa_hide, null);
            rl_search = (RelativeLayout) popupView.findViewById(R.id.rl_search);
            rv_address = (RecyclerView) popupView.findViewById(R.id.rv_address);
            rl_search.setOnClickListener(this);
            int sceenH = UIUtils.getScreenHeight(this);
            popWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, sceenH / 2, true);
            popWindow.setAnimationStyle(R.style.hide_fuwa_pop_anim);
            popWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(OrientationHelper.VERTICAL);
            rv_address.setLayoutManager(layoutManager);
            rv_address.setAdapter(addressAdapter);
            rv_address.addOnItemTouchListener(new FuwaHideAddressAdapter.RecyclerItemClickListener(this,
                    new FuwaHideAddressAdapter.RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            if (poiItems != null) {
                                PoiItem item = poiItems.get(position);
                                if (item != null) {
                                    address = item.getTitle();
                                    geohash = item.getLatLonPoint().getLongitude() + "-" + item.getLatLonPoint().getLatitude();
                                    tv_address.setText("" + address);
                                    //String city = item.getCityName();
                                    // doSearchQuery(address, city);
                                    if (popWindow != null && popWindow.isShowing()) {
                                        popWindow.dismiss();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onLongClick(View view, int posotion) {

                        }
                    }));
            popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    iv_show_address.setImageResource(R.drawable.down_fw);
                }
            });
        }
        int xOff = UIUtils.getScreenWidth(this) / 2 - rl_address.getWidth() / 3;
        int xOffDp = UIUtils.px2dip(this, xOff);
        popWindow.showAsDropDown(rl_address, -xOffDp, 0);
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

    private void initMap() {
        poiItems = new ArrayList<>();
        addressAdapter = new FuwaHideAddressAdapter(this, poiItems);

        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationOption.setInterval(30000);
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        this.location = aMapLocation;
        if (location != null) {
            initPostionData(location);
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    if (poiItems != null && poiItems.size() > 0) {
                        //printStr(poiItems);
                        addressAdapter.onDataChange(poiItems);
                    }
                }
            } else {
                showToast("没数据!", true);
            }
        } else {
            showToast(rcode, true);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    private void initPostionData(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (lp == null) {
                lp = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            }
            //address = aMapLocation.getPoiName();
            geohash = aMapLocation.getLongitude() + "-" + aMapLocation.getLatitude();
            //tv_address.setText("" + address);
            curCity = aMapLocation.getCity();
            curArea = aMapLocation.getDistrict();
            doSearchQuery(address, curCity);
        }
    }

    /**
     * 开始进行poi搜索
     */
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String keyWord, String city) {
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }


    private void showSuccessHideDialog() {
        if (isFinish) {
            return;
        }
        if (dialog == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_hide_ok, null);
            int sceenW = UIUtils.getScreenWidth(this);
            int sceenH = UIUtils.getScreenHeight(this);
            tv_dialog_address = (TextView) view.findViewById(R.id.tv_dialog_address);
            iv_dialog_icon = (ImageView) view.findViewById(R.id.iv_dialog_icon);
            FrameLayout.LayoutParams imgParams = (FrameLayout.LayoutParams) iv_dialog_icon.getLayoutParams();
            imgParams.width = sceenW / 2;
            imgParams.height = sceenH / 3;
            iv_dialog_icon.setLayoutParams(imgParams);
            bt_hide_ok = (Button) view.findViewById(R.id.bt_hide_ok);
            bt_hide_ok.setOnClickListener(this);

            dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialog.setContentView(view);
            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (sceenW * 0.8);
            lp.height = (int) (sceenH * 0.7);
            dialogWindow.setAttributes(lp);
            dialogWindow.setGravity(Gravity.CENTER);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    finish();
                }
            });
        }
        if (imgFile != null) {
            Glide
                    .with(this)
                    .load(imgFile)
                    .into(iv_dialog_icon);
            imgFile = null;
        }
        tv_dialog_address.setText("" + address);
        if (!isFinish)
            dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
//            EventBus.getDefault().post("1");
            //showSuccessHideDialog();
        } else if (requestCode == 102 && resultCode == RESULT_OK && data != null) {
            address = data.getStringExtra("address");
            tv_address.setText("" + address);
            if (mlocationClient != null) {
                mlocationClient.stopLocation();
            }
        } else if (requestCode == RECORD_VIDEO && resultCode == RESULT_OK) {
            // 录制视频完成
            try {
                AssetFileDescriptor videoAsset = getContentResolver()
                        .openAssetFileDescriptor(data.getData(), "r");
                FileInputStream fis = videoAsset.createInputStream();
                videoFile = new File(
                        savePath,
                        "fuwavideo.mp4");
                FileOutputStream fos = new FileOutputStream(videoFile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
                fis.close();
                fos.close();
                showVideo();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == READ_VIDEO && resultCode == RESULT_OK && data != null) {
            String url = data.getStringExtra("filePath");
            videoFile = new File(url);
            showVideo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDialogShow && dialogRecommond != null) {
            if (!isFinish)
                dialogRecommond.show();
            isDialogShow = false;
        }
        if (isJump) {
            tv_change_place.setVisibility(View.INVISIBLE);
            tv_bottom.setVisibility(View.VISIBLE);
            bt_catch.setVisibility(View.GONE);
            previewing = true;
            canFocusIn = true;
            isHideOk = true;
        }
    }

    private void getMyApplyFuwa() {
        String url = HttpUrl.QUERY_MY_APPLY_FUWA + userId;
        HttpUtils httpUtils = new HttpUtils(45 * 1000);
        //设置当前请求的缓存时间
        httpUtils.configCurrentHttpCacheExpiry(0 * 1000);
        //设置默认请求的缓存时间
        httpUtils.configDefaultHttpCacheExpiry(0);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    FuwaEntity entity = JSON.parseObject(res, FuwaEntity.class);
                    List<FuwaEntity.Data> data = entity.getData();
                    if (data != null && data.size() > 0) {
                        getNumFuwaType(data);
                    } else {
                        fuwaSocialNum = 0;
                        fuwaTreasureNum = 0;
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });
    }

    private void getNumFuwaType(List<FuwaEntity.Data> data) {
        for (FuwaEntity.Data item : data) {
            String gid = item.getGid();
            if (!TextUtils.isEmpty(gid)) {
                if (gid.contains("c")) {
                    fuwaTreasureNum++;
                } else {
                    fuwaSocialNum++;
                }
            }
        }
    }

    private void showNumDialog() {
        if (dialogNum == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_hide_input_num, null);
            int sceenW = UIUtils.getScreenWidth(this);
            LinearLayout ll_p = (LinearLayout) view.findViewById(R.id.ll_p);
            LinearLayout ll_fuwa_type = (LinearLayout) view.findViewById(R.id.ll_fuwa_type);
            RelativeLayout rl_fuwa_type = (RelativeLayout) view.findViewById(R.id.rl_fuwa_type);

            ll_fuwa_class = (LinearLayout) view.findViewById(R.id.ll_fuwa_class);
            RelativeLayout rl_fuwa_class = (RelativeLayout) view.findViewById(R.id.rl_fuwa_class);
            tv_fuwa_class = (TextView) view.findViewById(R.id.tv_fuwa_class);

            LinearLayout ll_fuwa_num = (LinearLayout) view.findViewById(R.id.ll_fuwa_num);
            tv_fuwa_type = (TextView) view.findViewById(R.id.tv_fuwa_type);
            et_dialog_num = (EditText) view.findViewById(R.id.et_dialog_num);
            tv_dialog_num_tip = (TextView) view.findViewById(R.id.tv_dialog_num_tip);
            bt_dialog_catch = (Button) view.findViewById(R.id.bt_dialog_catch);
            bt_dialog_catch.setOnClickListener(this);
            et_dialog_num.addTextChangedListener(this);
            rl_fuwa_type.setOnClickListener(this);
            rl_fuwa_class.setOnClickListener(this);
            ImageView iv_close = (ImageView) view.findViewById(R.id.iv_close);
            iv_close.setOnClickListener(this);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll_p.getLayoutParams();
            params.width = (int) (sceenW * 0.85);
            ll_p.setLayoutParams(params);

            ll_fuwa_num.getLayoutParams().height = sceenW / 7;
            ll_fuwa_type.getLayoutParams().height = sceenW / 7;
            ll_fuwa_class.getLayoutParams().height = sceenW / 7;

            dialogNum = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialogNum.setContentView(view);
            dialogNum.setCanceledOnTouchOutside(true);
            tv_dialog_num_tip.setText("可藏福娃（个）：" + currentFuwaNum);
        }
        if (!isFinish)
            dialogNum.show();
    }

    private void showRecommondDialog() {
        if (dialogRecommond == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_hide_fuwa_recommend, null);
            int sceenH = UIUtils.getScreenHeight(this);

            tv_tx_num = (TextView) view.findViewById(R.id.tv_tx_num);
            rl_time = (RelativeLayout) view.findViewById(R.id.rl_time);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            iv_video = (ImageView) view.findViewById(R.id.iv_video);
            iv_video_img = (ImageView) view.findViewById(R.id.iv_video_img);
            fl_video_dialog_img = (FrameLayout) view.findViewById(R.id.fl_video_dialog_img);
            fl_video_dialog_img.setOnClickListener(this);
            rl_time.setOnClickListener(this);
            iv_video.setOnClickListener(this);
            tv_tx_num.setText("0/200");
            et_recommond = (EditText) view.findViewById(R.id.et_recommond);
            Button bt_sure = (Button) view.findViewById(R.id.bt_sure);
            TextView tv_jump_over = (TextView) view.findViewById(R.id.tv_jump_over);
            bt_sure.setOnClickListener(this);
            tv_jump_over.setOnClickListener(this);
            LinearLayout.LayoutParams etParam = (LinearLayout.LayoutParams) et_recommond.getLayoutParams();
            etParam.height = sceenH / 7;
            et_recommond.setLayoutParams(etParam);
            et_recommond.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int len = editable.length();
                    if (len <= 200) {
                        tv_tx_num.setText(len + "/200");
                    } else {
                        showToast("已超过字数限制", false);
                    }
                }
            });
            dialogRecommond = new Dialog(context, R.style.ActionSheetDialogStyle);
            dialogRecommond.setContentView(view);
            dialogRecommond.setCanceledOnTouchOutside(true);
        }
        if (!isFinish)
            dialogRecommond.show();
    }

    private void showTimeChoosePop() {
        if (popdialogWindow == null) {
            popTime = new String[]{"72小时", "一个月", "一年", "三年"};
            View popupView = getLayoutInflater().inflate(R.layout.popwindow_hide_fuwa_time, null);
            TextView tv_time_1 = (TextView) popupView.findViewById(R.id.tv_time_1);
            TextView tv_time_2 = (TextView) popupView.findViewById(R.id.tv_time_2);
            TextView tv_time_3 = (TextView) popupView.findViewById(R.id.tv_time_3);
            TextView tv_time_4 = (TextView) popupView.findViewById(R.id.tv_time_4);
            tv_time_1.setOnClickListener(this);
            tv_time_2.setOnClickListener(this);
            tv_time_3.setOnClickListener(this);
            tv_time_4.setOnClickListener(this);
            popdialogWindow = new PopupWindow(popupView, rl_time.getWidth(), WindowManager.LayoutParams.WRAP_CONTENT, true);
            popdialogWindow.setAnimationStyle(R.style.hide_fuwa_pop_anim);
            popdialogWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
            popdialogWindow.setFocusable(true);
            popdialogWindow.setOutsideTouchable(true);
        }
        //int xOff = UIUtils.getScreenWidth(this) / 2 - rl_time.getWidth() / 3;
        //int xOffDp = UIUtils.px2dip(this, xOff);
        popdialogWindow.showAsDropDown(rl_time, 0, 10);
        //popWindow.showAsDropDown(rl_time);
    }

    private void setPopTime(int tag) {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
        if (popdialogWindow != null && popdialogWindow.isShowing()) {
            popdialogWindow.dismiss();
        }
        if (tv_time != null && popTime != null) {
            tv_time.setText(popTime[tag - 1]);
        }
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        if (isSelectVideo) {
            actionSheet.addSheetItem(getString(R.string.small_video), ActionSheet.SheetItemColor.Black, this)
                    .addSheetItem(getString(R.string.local_small_video), ActionSheet.SheetItemColor.Black,
                            this);
        } else {
            actionSheet.addSheetItem("社交", ActionSheet.SheetItemColor.Black, this)
                    .addSheetItem("寻宝", ActionSheet.SheetItemColor.Black,
                            this);
        }
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        switch (which) {
            case 1:
                if (isSelectVideo) {
                    cameraType = RECORD_VIDEO;
                    getPermission();
                } else {
                    fuwaSelectType = 0;
                    if (tv_fuwa_type != null)
                        tv_fuwa_type.setText("社交");
                    currentFuwaNum = fuwaSocialNum;
                    classId = "i";
                    ll_fuwa_class.setVisibility(View.GONE);
                    tv_dialog_num_tip.setTextColor(getResources().getColor(R.color.top_bar_color));
                    tv_dialog_num_tip.setText("可藏福娃（个）：" + currentFuwaNum);
                }
                break;
            case 2:
                if (isSelectVideo) {
                    cameraType = READ_VIDEO;
                    getPermission();
                } else {
                    fuwaSelectType = 1;
                    if (tv_fuwa_type != null)
                        tv_fuwa_type.setText("寻宝");
                    currentFuwaNum = fuwaTreasureNum;
                    ll_fuwa_class.setVisibility(View.VISIBLE);
                    tv_dialog_num_tip.setTextColor(getResources().getColor(R.color.top_bar_color));
                    tv_dialog_num_tip.setText("可藏福娃（个）：" + currentFuwaNum);
                }
                break;
        }
    }

    private void showVideo() {
        if (videoFile != null) {
            String url = videoFile.getAbsolutePath();
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(url);
                Bitmap videoBitmap = mediaMetadataRetriever.getFrameAtTime();
                if (videoBitmap != null && iv_video_img != null) {
                    fl_video_dialog_img.setVisibility(View.VISIBLE);
                    iv_video.setVisibility(View.GONE);
                    iv_video_img.setImageBitmap(videoBitmap);
                }
            } catch (Exception e) {
                iv_video_img.setImageResource(R.drawable.zf_default_album_grid_image);
                e.printStackTrace();
            } finally {
                if (mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release();
                }
            }
        }
    }

    private void hideFuwaServer() {
        showLoadingDialog();
        if (TextUtils.isEmpty(recommond)) {
            recommond = "暂无活动介绍";
        }
        String now_address = "";
        try {
            recommond = URLEncoder.encode(recommond, "utf-8").replaceAll("\\+", "%20");
            now_address = URLEncoder.encode(address, "utf-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = HttpUrl.HIDE_MY_FUWA + userId + "&pos=" + now_address + "&geohash=" + geohash
                + "&detail=" + recommond + "&validtime=" + validtime + "&number="
                + curSelectFuwaNum + "&type=" + fuwaSelectType + "&class=" + classId;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        RequestParams params = new RequestParams();
        if (imgFile != null && imgFile.exists())
            params.addBodyParameter("file", imgFile);
        if (videoFile != null) {
            params.addBodyParameter("video", videoFile);
        }
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    BaseResult data = BaseResult.parse(res);
                    if (data != null) {
                        int code = data.getCode();
                        if (code == 0) {
                            showSuccessHideDialog();
                        }
                    }
                } else {
                    showToast("藏福娃失败TAT，请重试", false);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                if (!TextUtils.isEmpty(s) && s.contains("IOException")) {
                    showToast("网络加载失败，请检查网络连接", false);
                } else {
                    showToast("服务器开小差啦，请稍后再试", false);
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (tv_fuwa_type != null && tv_fuwa_type.equals("请选择")) {
            showToast("请先选择福娃用途", false);
            return;
        }
        String num = et_dialog_num.getText().toString().trim();
        if (!TextUtils.isEmpty(num) && tv_dialog_num_tip != null) {
            int num_i = Integer.parseInt(num);
            if (num_i > currentFuwaNum) {
                tv_dialog_num_tip.setTextColor(getResources().getColor(R.color.tx_fuwa_tip));
                tv_dialog_num_tip.setText("输入个数超过您申请的福娃");
            } else {
                tv_dialog_num_tip.setTextColor(getResources().getColor(R.color.top_bar_color));
                tv_dialog_num_tip.setText("可藏福娃（个）：" + currentFuwaNum);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialogRecommond != null && dialogRecommond.isShowing()) {
            dialogRecommond.dismiss();
            isDialogShow = true;
        }
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 根据byte数组生成文件
     *
     * @param bytes 生成文件用到的byte数组
     */
    private void createFileWithByte(byte[] bytes) {
        writeToSDFile();
        /**
         * 创建File对象，其中包含文件所在的目录以及文件的命名
         */
        String imageName = System.currentTimeMillis() + ".jpg";
        imgFile = new File(savePath,
                imageName);
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
//            if (!imgFile.exists()) {
//                imgFile.mkdir();//如果路径不存在就先创建路径
//            }
            // 如果文件存在则删除
//            if (imgFile.exists()) {
//                imgFile.delete();
//            }
            // 在文件系统中根据路径创建一个新的空文件
            //imgFile.createNewFile();
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(imgFile);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    private void showClassPop(View view) {
        if (listPopupWindow == null) {
            listPopupWindow = new ListPopupWindow(this);
            List<String> list = new ArrayList<>();
            if (classStrList != null && classStrList.size() > 0) {
                for (FuwaClassEntity.DataBean item : classStrList) {
                    list.add(item.name);
                }
            }
            listPopupWindow.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list));
            listPopupWindow.setAnchorView(view);
            listPopupWindow.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            listPopupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            listPopupWindow.setModal(true);
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (classStrList != null && classStrList.size() > position) {
                        FuwaClassEntity.DataBean item = classStrList.get(position);
                        if (item != null) {
                            classId = item.classid;
                            tv_fuwa_class.setText(item.name);
                        }
                    }
                    listPopupWindow.dismiss();
                }
            });
        }
        if (!isFinish)
            listPopupWindow.show();
    }

    private void getFuwaClass() {
        HttpUtils httpUtils = new HttpUtils(45 * 1000);
        //设置当前请求的缓存时间
        httpUtils.configCurrentHttpCacheExpiry(0 * 1000);
        //设置默认请求的缓存时间
        httpUtils.configDefaultHttpCacheExpiry(0);
        httpUtils.send(HttpRequest.HttpMethod.GET, HttpUrl.QUERY_FUWA_CLASS, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    BaseResult result = BaseResult.parse(res);
                    if (result != null && result.getCode() == 0) {
                        FuwaClassEntity entity = JSON.parseObject(res, FuwaClassEntity.class);
                        if (entity != null) {
                            List<FuwaClassEntity.DataBean> data = entity.getData();
                            if (data != null) {
                                classStrList.addAll(data);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });
    }

    public void makeRootDir(String filePath) {
        File file = null;
        String newPath = null;
        String[] path = filePath.split("/");
        for (int i = 0; i < path.length; i++) {
            if (newPath == null) {
                newPath = path[i];
            } else {
                newPath = newPath + "/" + path[i];
            }
            file = new File(newPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    //sdcard的写操作
    public void writeToSDFile() {
        try {
            makeRootDir(savePath);//先创建文件夹
// File file=new File(Environment.getExternalStorageDirectory()+"/temp_1","hahatext1.txt");
// FileOutputStream fOutputStream=new FileOutputStream(file);
// fOutputStream.write(fname.getBytes());
// fOutputStream.close();
        } catch (Exception e) {
// TODO: handle exception
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            isFinish = true;
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
