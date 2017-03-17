package im.boss66.com.activity.treasure;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.MycsLog;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.FuwaHideAddressAdapter;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.widget.scan.CameraManager;
import im.boss66.com.widget.scan.CameraPreview;

/**
 * 藏福娃
 */
public class HideFuwaActivity extends BaseActivity implements View.OnClickListener, SensorEventListener, AMapLocationListener,
        PoiSearch.OnPoiSearchListener {

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
    private Handler autoFocusHandler;
    private boolean previewing = true, isTakePic = false;
    private PermissionListener permissionListener;
    private String savePath = Environment.getExternalStorageDirectory() + "/IMProject/";

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
    private String userId, geohash, address;
    private File imgFile;
    private String classType;
    private Dialog dialog;
    private ImageView iv_dialog_icon;
    private TextView tv_dialog_address, bt_hide_ok;
    private Bitmap bitmapImg;
    private boolean isJump = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_fuwa);
        initView();
        getPermission();
    }

    private void initView() {
        userId = App.getInstance().getAccount().getUser_id();
        autoFocusHandler = new Handler();

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
        mSensorManager = (SensorManager) App.getInstance().getSystemService(Activity.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// TYPE_GRAVITY
    }

    private void initViewParams() {
        initMap();
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
        int PreviewWidth = 0, PreviewHeight = 0;
        mCamera = mCameraManager.getCamera();

        Camera.Parameters parameters = mCamera.getParameters();

        // 选择合适的预览尺寸
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        // 如果sizeList只有一个我们也没有必要做什么了，因为就他一个别无选择
        if (sizeList.size() > 1) {
            Iterator<Camera.Size> itor = sizeList.iterator();
            while (itor.hasNext()) {
                Camera.Size cur = itor.next();
                if (cur.width >= PreviewWidth
                        && cur.height >= PreviewHeight) {
                    PreviewWidth = cur.width;
                    PreviewHeight = cur.height;
                    break;
                }
            }
        }
        parameters.setPictureSize(PreviewWidth, PreviewHeight);
        mCamera.setParameters(parameters);
        mPreview = new CameraPreview(this, mCamera, mPreviewCallback, autoFocusCB);
        Camera.Size size = parameters.getPictureSize();
        Log.i("size", "width:" + size.width + "hegit:" + size.height +
                "----PreviewWidth:" + PreviewWidth + " PreviewHeight:" + PreviewHeight);
        rl_preciew.addView(mPreview);
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
                    bitmapImg = UIUtils.getRotateBitmap(bm, 90);
                    if (bitmapImg != null) {
                        if (bitmapImg != null) {
                            String imageName = getNowTime() + ".jpg";
                            // 指定调用相机拍照后照片的储存路径
                            File dir = new File(savePath);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            imgFile = new File(dir, imageName);
                            BufferedOutputStream bos
                                    = new BufferedOutputStream(new FileOutputStream(imgFile));
                            bitmapImg.compress(Bitmap.CompressFormat.JPEG, 40, bos);
                            bos.flush();
                            bos.close();
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
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.bt_catch:
                Bundle bundle = new Bundle();
                bundle.putString("address", address);
                bundle.putString("geohash", geohash);
                ChooseFuwaHideActivity.getImgFile(imgFile);
                openActvityForResult(ChooseFuwaHideActivity.class, 101, bundle);
                isJump = true;
                break;
            case R.id.tv_change_place:
                tv_change_place.setVisibility(View.INVISIBLE);
                bt_catch.setVisibility(View.GONE);
                tv_bottom.setVisibility(View.VISIBLE);
                previewing = true;
                mCamera.startPreview();
                break;
            case R.id.iv_show_address:
                if (popWindow != null && popWindow.isShowing()) {
                    iv_show_address.setImageResource(R.drawable.down_fw);
                    popWindow.dismiss();
                } else {
                    iv_show_address.setImageResource(R.drawable.up_fw);
                    showPop();
                }
                break;
            case R.id.rl_search:
                break;
            case R.id.bt_hide_ok:
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                finish();
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
                                    tv_change_place.setVisibility(View.VISIBLE);
                                    bt_catch.setVisibility(View.VISIBLE);
                                    tv_bottom.setVisibility(View.GONE);
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
                ToastUtil.showShort(HideFuwaActivity.this, getString(R.string.giving_camera_permissions));
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_CAMERA_LOCATION //相机权限
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

    private void showPop() {
        if (popWindow == null) {
            View popupView = getLayoutInflater().inflate(R.layout.popupwd_fuwa_hide, null);
            rl_search = (RelativeLayout) popupView.findViewById(R.id.rl_search);
            rv_address = (RecyclerView) popupView.findViewById(R.id.rv_address);
            rl_search.setOnClickListener(this);
            popWindow = new PopupWindow(popupView);
            //popWindow.setAnimationStyle(R.style.PopupTitleBarAnim1);
            popWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.umeng_socialize_share_transparent_corner));
            popWindow.setFocusable(true);
            popWindow.setOutsideTouchable(true);
        }
        popWindow.showAsDropDown(tv_back, 0, 0);
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
        addressAdapter = new FuwaHideAddressAdapter(poiItems);
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
            address = aMapLocation.getPoiName();
            geohash = aMapLocation.getLongitude() + "-" + aMapLocation.getLatitude();
            tv_address.setText("" + address);
            String city = aMapLocation.getCity();
            doSearchQuery(address, city);
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
        if (dialog == null) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.dialog_hide_ok, null);
            int sceenW = UIUtils.getScreenWidth(this);
            int sceenH = UIUtils.getScreenHeight(this);
            tv_dialog_address = (TextView) view.findViewById(R.id.bt_hide_ok);
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
        }
        if (bitmapImg != null) {
            iv_dialog_icon.setImageBitmap(bitmapImg);
        }
        tv_dialog_address.setText("" + address);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            showSuccessHideDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isJump)
            canFocusIn = true;
    }
}