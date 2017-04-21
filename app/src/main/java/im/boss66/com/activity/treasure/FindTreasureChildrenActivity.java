package im.boss66.com.activity.treasure;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.SessionInfo;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.FuwaAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseChildren;
import im.boss66.com.entity.ChildEntity;
import im.boss66.com.http.BaseDataModel;
import im.boss66.com.http.request.AroundChildrenRequest;
import im.boss66.com.http.request.AroundFateRequest;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.util.AMapUtil;
import im.boss66.com.util.SensorEventHelper;
import im.boss66.com.widget.CircleImageView;
import im.boss66.com.widget.WrappingSlidingDrawer;

/**
 * Created by Johnny on 2017/3/14.
 * 找福娃
 */
public class FindTreasureChildrenActivity extends BaseActivity implements
        View.OnClickListener,
        LocationSource,
        AMapLocationListener,
        AMap.OnMapLoadedListener,
        AMap.OnMarkerClickListener,
        AMap.OnCameraChangeListener,
        RouteSearch.OnRouteSearchListener,
        AMap.OnMapClickListener,
        Observer {
    private static final String TAG = FindTreasureChildrenActivity.class.getSimpleName();
    private LocalBroadcastReceiver mLocalBroadcastReceiver;
    private PopupWindow popupWindow, popup;
    private MapView mMapView = null;
    private AMap aMap;
    //    private MarkerOptions markerOptions;
    private RouteSearch mRouteSearch;
    private WalkRouteResult mWalkRouteResult;
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private final int ROUTE_TYPE_WALK = 3;
    private ProgressDialog progDialog = null;// 搜索时进度条
    //    private float distance = 0;
    private ChildEntity currentChild;
    private View topBar;
    private HashMap<String, Marker> markerMap = new HashMap<>();
    private HashMap<String, LatLng> latMap = new HashMap<>();
    private View view;
    private UiSettings mUiSettings;
    private TextView tvBack, tvStore;
    private ImageView ivLocation;
    private boolean isFirst = true;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private ImageLoader imageLoader;
    private AccountEntity account;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = false;
    private boolean isFate = false;
    private Marker mLocMarker, mPersonMarker;
    private LatLng mLatLng;
    private SensorEventHelper mSensorHelper;
    private Circle mCircle;
    public static final String LOCATION_MARKER_FLAG = "mylocation";
    private WrappingSlidingDrawer slidingDrawer;
    private ImageView handler;
    private TextView tv_location, tv_distanc_start, tv_distanc_target, tv_time, tv_num, tv_detail;
    private Button btn_catch;
    private TextView tvTips;
    private PermissionListener permissionListener;
    private RelativeLayout titleBar;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Session.getInstance().addObserver(this);
        setContentView(R.layout.activity_treasure_children);
        initViews();
        getPermission();
        initData(savedInstanceState);
    }

    private void initViews() {
        account = App.getInstance().getAccount();
        resources = getResources();
        imageLoader = ImageLoaderUtils.createImageLoader(context);

        topBar = findViewById(R.id.content);
        mMapView = (MapView) findViewById(R.id.map);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvStore = (TextView) findViewById(R.id.tv_store);
        ivLocation = (ImageView) findViewById(R.id.iv_reset_location);
        slidingDrawer = (WrappingSlidingDrawer) findViewById(R.id.slidingDrawer);
        handler = (ImageView) findViewById(R.id.handle);

        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_time = (TextView) findViewById(R.id.tv_minus);
        tv_distanc_start = (TextView) findViewById(R.id.tv_distance_start);
        tv_distanc_target = (TextView) findViewById(R.id.tv_distance_target);
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_detail = (TextView) findViewById(R.id.tv_detail);
        btn_catch = (Button) findViewById(R.id.btn_catch);
        tvTips = (TextView) findViewById(R.id.tv_bottom_tips);
        titleBar = (RelativeLayout) findViewById(R.id.rl_top_bar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isFate = bundle.getBoolean("isFate", false);
        }

        slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                handler.setImageResource(R.drawable.retract);
            }
        });

        slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                handler.setImageResource(R.drawable.launch);
            }
        });
        tvBack.setOnClickListener(this);
        tvStore.setOnClickListener(this);
        ivLocation.setOnClickListener(this);
        btn_catch.setOnClickListener(this);
        tv_detail.setOnClickListener(this);
        titleBar.setOnClickListener(this);

        mLocalBroadcastReceiver = new LocalBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Action.MAP_MARKER_REFRESH);
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(mLocalBroadcastReceiver, filter);

        view = getLayoutInflater().inflate(R.layout.item_map_position, null);
        CircleImageView header = (CircleImageView) view.findViewById(R.id.header);
        imageLoader.displayImage(account.getAvatar(), header, ImageLoaderUtils.getDisplayImageOptions());
    }

    private void initData(Bundle bundle) {
        mMapView.onCreate(bundle);
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        if (aMap == null) {
            aMap = mMapView.getMap();
            mUiSettings = aMap.getUiSettings();
            mUiSettings
                    .setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);// 设置地图logo显示在左下方
            mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
            aMap.setOnMapClickListener(this);
            aMap.setOnMarkerClickListener(this);//marker点击事件监听接口。
            aMap.setOnCameraChangeListener(this);
            aMap.setOnMapLoadedListener(this);//设置地图加载监听

            mSensorHelper = new SensorEventHelper(this);
            if (mSensorHelper != null) {
                mSensorHelper.registerSensorListener();
            }

            Log.i("info", "==================scaleSize:" + aMap.getScalePerPixel());
            // 设置定位监听
            aMap.setLocationSource(this);
//            mUiSettings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationEnabled(true);
            // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
//            setupLocationStyle();
            aMap.postInvalidate();
        }
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(context, "定位中，稍后再试...", Toast.LENGTH_LONG);
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(context, "终点未设置", Toast.LENGTH_LONG);
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_store:
                openActivity(HideFuwaActivity.class);
                break;
            case R.id.iv_reset_location:
                backOlderPosition();
                break;
            case R.id.tv_detail:
                if (popupWindow == null) {
                    if (currentChild != null) {
                        showDetail(context, titleBar, currentChild);
                    }
                } else {
                    if (!popupWindow.isShowing()) {
                        if (currentChild != null) {
                            showDetail(context, titleBar, currentChild);
                        }
                    }
                }
                break;
            case R.id.btn_catch:
                if (currentChild != null) {
                    Intent intent = new Intent(context, CatchFuwaActivity.class);
//                    intent.putExtra("pic", currentChild.getPic());
//                    intent.putExtra("gid", currentChild.getGid());
//                    intent.putExtra("id", currentChild.getId());
                    intent.putExtra("obj", currentChild);
                    startActivity(intent);
                }
                break;
        }
    }

    private void showDetail(final Context context, View parent, ChildEntity entity) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        View view = inflater.inflate(R.layout.popwindow_item_detail2, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setAnimationStyle(R.style.PopupTitleBarAnim);

//        ImageView iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        TextView tvTips = (TextView) view.findViewById(R.id.tv_tips);
//        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
//        TextView tv_sign = (TextView) view.findViewById(R.id.tv_sign);
//        TextView tv_address = (TextView) view.findViewById(R.id.tv_address);

        String detail = entity.getDetail();
        if (!TextUtils.isEmpty(detail)) {
            tvTips.setText(detail);
        } else {
            tvTips.setText(resources.getString(R.string.no_act));
        }
//        tv_name.setText(entity.getName());
//        tv_sign.setText(entity.getSignature());
//        tv_address.setText(entity.getLocation());
//        String imageUrl = entity.getAvatar();
//        if (!TextUtils.isEmpty(imageUrl)) {
//            imageLoader.displayImage(imageUrl, iv_avatar, ImageLoaderUtils.getDisplayImageOptions());
//        }
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getDrawableFromRes(R.drawable.bg_popwindow));
        if (!isFinishing()) {
            popupWindow.showAsDropDown(parent);
        }
    }

    private Drawable getDrawableFromRes(int resId) {
        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resId);
        return new BitmapDrawable(bmp);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {

    }

    private WalkRouteOverlay walkRouteOverlay;

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        dissmissProgressDialog();
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    if (walkRouteOverlay != null) {
                        walkRouteOverlay.removeFromMap();
                        walkRouteOverlay = null;
                    }
                    walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.setNodeIconVisibility(false);
//                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.showShort(context, "对不起，没有搜索到相关数据！");
                }
            } else {
                ToastUtil.showShort(context, "对不起，没有搜索到相关数据！");
            }
        } else {
            ToastUtil.showLong(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMapLoaded() {
        Log.i("info", "==============onMapLoaded()");
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (!isMarkerClick(latLng)) {
            if (slidingDrawer.getVisibility() != View.GONE) {
                UIUtils.hindView(slidingDrawer);
                if (walkRouteOverlay != null) {
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay = null;
                }
            }
//            else {
//                if (slidingDrawer.isOpened()) {
//                    slidingDrawer.animateClose();
//                }
//            }
        }
    }

    @Subscribe
    public void onMessageEvent(String gid) {
        deleteMarker(gid);
    }

    private void deleteMarker(String gid) {
        Log.i("info", "==============gid:" + gid);
        List<Marker> markers = aMap.getMapScreenMarkers();
        for (Marker marker : markers) {
            Object object = marker.getObject();
            if (object != null) {
                if (object.toString().equals(gid)) {
                    marker.remove();
                    if (slidingDrawer.getVisibility() != View.GONE) {
                        UIUtils.hindView(slidingDrawer);
                        if (walkRouteOverlay != null) {
                            walkRouteOverlay.removeFromMap();
                            walkRouteOverlay = null;
                        }
                    }
                }
            }
        }
        removeCautch(gid);
        aMap.invalidate();
    }


    private void removeCautch(String gid) {
        if (markerMap != null && markerMap.size() != 0) {
            Iterator<Map.Entry<String, Marker>> it = markerMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Marker> entry = it.next();
                Marker target = entry.getValue();
                Object object = target.getObject();
                if (object != null) {
                    if (object.toString().equals(gid)) {
                        it.remove();
                    }
                }
            }

//            for (Map.Entry<String, Marker> entry : markerMap.entrySet()) {
//                Marker target = entry.getValue();
//                Object object = target.getObject();
//                if (object != null) {
//                    if (object.toString().equals(gid)) {
//                        markerMap.remove(target);
//                    }
//                }
//
////                String snippet = target.getSnippet();
////                if (snippet != null && !snippet.equals("")) {
////                    ChildEntity childEntity = JSON.parseObject(snippet, ChildEntity.class);
////                    if (childEntity != null) {
////                        if (childEntity.getGid().equals(gid)) {
////                            markerMap.remove(target);
////                        }
////                    }
////                }
//            }
        }
    }

    private boolean isMarkerClick(LatLng latLng) {
        boolean flag = false;
        if (latMap != null && latMap.size() != 0) {
            for (Map.Entry<String, LatLng> entry : latMap.entrySet()) {
                LatLng lat = entry.getValue();
                if (latLng.latitude == lat.latitude &&
                        latLng.longitude == lat.longitude) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String snippet = marker.getSnippet();
        Log.i("info", "========snippet:" + snippet);
        if (snippet != null && !snippet.equals("")) {
            ChildEntity childEntity = JSON.parseObject(snippet, ChildEntity.class);
//            showToast("编号:" + childEntity.getId() + "\n" + "地址：" + childEntity.getPos(), true);
            if (childEntity != null) {
                catchChild(childEntity);
            }
        }
        return true;
    }


    private void catchChild(ChildEntity child) {
//        if (slidingDrawer.getVisibility() != View.VISIBLE) {
//            UIUtils.showView(slidingDrawer);
//        }
        float distance = 0;
        this.currentChild = child;
        if (markerMap != null && markerMap.size() != 0) {
            Marker target = markerMap.get(child.getGid());
            distance = AMapUtils.calculateLineDistance(mLocMarker.getPosition(), target.getPosition());
        }
        double latitude = mLocMarker.getPosition().latitude;
        double longitude = mLocMarker.getPosition().longitude;
        mLatLng = new LatLng(latitude, longitude);
//        mLatLng = mLocMarker.getPosition();
        Log.i("info", "===============distance:" + distance);
        if (distance > 30 || distance == 0) {
            if (slidingDrawer.getVisibility() != View.VISIBLE) {
                UIUtils.showView(slidingDrawer);
            }
            if (topBar.getVisibility() != View.VISIBLE) {
                topBar.setVisibility(View.VISIBLE);
            }
            btn_catch.setVisibility(View.GONE);
            tvTips.setVisibility(View.VISIBLE);
            if (!slidingDrawer.isOpened()) {
                slidingDrawer.animateOpen();
            }
            String[] strs = child.getGeo().split("-");
            mEndPoint = new LatLonPoint(Double.parseDouble(strs[1]), Double.parseDouble(strs[0]));
            if (location != null) {
                mStartPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
            }
            searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WALK_DEFAULT);
        } else {//扑捉范围内
            if (popup == null) {
                showChildrenPop(titleBar);
            } else {
                if (!popup.isShowing()) {
                    showChildrenPop(titleBar);
                }
            }
//            btn_catch.setVisibility(View.VISIBLE);
//            tvTips.setVisibility(View.GONE);
        }
//        if (!slidingDrawer.isOpened()) {
//            slidingDrawer.animateOpen();
//        }
//        String[] strs = child.getGeo().split("-");
//        mEndPoint = new LatLonPoint(Double.parseDouble(strs[1]), Double.parseDouble(strs[0]));
//        if (location != null) {
//            mStartPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
//        }
//        searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WALK_DEFAULT);
    }

    private ArrayList<ChildEntity> getRoundChildren() {
        ArrayList<ChildEntity> childs = null;
        if (markerMap != null && markerMap.size() != 0) {
            childs = new ArrayList<>();
            for (Map.Entry<String, Marker> entry : markerMap.entrySet()) {
                Marker target = entry.getValue();
                float distance = AMapUtils.calculateLineDistance(mLocMarker.getPosition(), target.getPosition());
                if (distance < 30) {
                    String snippet = target.getSnippet();
                    if (snippet != null && !snippet.equals("")) {
                        ChildEntity childEntity = JSON.parseObject(snippet, ChildEntity.class);
                        if (childEntity != null) {
                            if (!isContain(childs, childEntity)) {
                                childs.add(childEntity);
                            }
                        }
                    }
                }
            }
        }
        return childs;
    }

    private boolean isContain(ArrayList<ChildEntity> childs, ChildEntity entity) {
        boolean flag = false;
        for (ChildEntity mode : childs) {
            if (mode.getGid().equals(entity.getGid())) {
                flag = true;
            }
        }
        return flag;
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {//移动地图后，停止。开始获取福娃
        Log.i("info", "============onCameraChangeFinish()");
        childrenRequest(cameraPosition);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位时间间隔
            mLocationOption.setInterval(1500);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        Log.i("info", "====================onLocationChanged()" + "\n" +
                "纬度:" + amapLocation.getLatitude() + "\n" +
                "  经度：" + amapLocation.getLongitude());
//        if (mListener != null && amapLocation != null) {
//            if (amapLocation != null
//                    && amapLocation.getErrorCode() == 0) {
//                this.location = amapLocation;
//                currentPointIcon(amapLocation);
//                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//                if (isFirst) {
//                    isFirst = false;
//                    scalePoint(amapLocation);
//                }
//            } else {
//                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
//                Log.i("info", "errText:" + errText);
//            }
//        }
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                this.location = amapLocation;
                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
//                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                if (isFirst) {
                    isFirst = false;
                    scalePoint(amapLocation);
//                    currentPointIcon();
                    addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location);//添加定位图标
                    addMarkerIcon(location);
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                } else {
                    mCircle.setCenter(location);
//                    mCircle.setRadius(amapLocation.getAccuracy());
                    mCircle.setRadius(30);
                    mLocMarker.setPosition(location);
                    mPersonMarker.setPosition(location);
                }
                refreshView(amapLocation);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.i("info", "errText:" + errText);
            }
        }
    }


    private void refreshView(AMapLocation aMapLocation) {
        if (slidingDrawer.isOpened()) {
            tv_location.setText(aMapLocation.getPoiName());
            if (currentChild != null) {
                tv_num.setText(currentChild.getId() + "号");
                if (markerMap != null && markerMap.size() != 0) {
                    Marker target = markerMap.get(currentChild.getGid());
                    int distance = (int) AMapUtils.calculateLineDistance(mLocMarker.getPosition(), target.getPosition());
                    tv_distanc_target.setText("还有" + distance + "米");
                    int minus = (int) (distance / 1.4);
                    String time = UIUtils.changTime(minus);
                    tv_time.setText("" + time);
                }

                if (mLatLng != null) {
                    int distance2 = (int) AMapUtils.calculateLineDistance(mLocMarker.getPosition(), mLatLng);
                    tv_distanc_start.setText("" + distance2);
                }
            }
        }
    }

    //    private void currentPointIcon(AMapLocation aMapLocation) {
//        if (aMap != null) {
//            aMap.clear();
//            aMap.removecache();
//            View view = getLayoutInflater().inflate(R.layout.item_map_position, null);
//            CircleImageView header = (CircleImageView) view.findViewById(R.id.header);
//            imageLoader.displayImage(account.getAvatar(), header, ImageLoaderUtils.getDisplayImageOptions());
//            LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//            aMap.addMarker(new MarkerOptions().position(latLng).draggable(false).icon(BitmapDescriptorFactory.fromView(view)));
//        }
//    }
    private void addMarkerIcon(LatLng latLng) {
        if (aMap != null) {
            mPersonMarker = aMap.addMarker(new MarkerOptions().position(latLng).draggable(false).icon(BitmapDescriptorFactory.fromView(view)));
        }
    }

    private void scalePoint(AMapLocation aMapLocation) {
        LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
//        addMarkers(aMapLocation);
    }

    private void addMarkers(AMapLocation location) {
        double w = location.getLatitude();
        double h = location.getLongitude();
        addMarkerToMap(new LatLng(w - 0.008, h - 0.0013));
        addMarkerToMap(new LatLng(w + 0.008, h + 0.0013));
        addMarkerToMap(new LatLng(w - 0.008, h + 0.0013));
        addMarkerToMap(new LatLng(w + 0.008, h - 0.0013));
    }

    private AMapLocation location = null;

    private void backOlderPosition() {
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        }
    }


//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//        mMapView.onSaveInstanceState(outState);
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void addMarkerToMap(LatLng latLng) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.draggable(true);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.treasure_fuwa_loca)));
        aMap.addMarker(markerOption);
    }

    private void addMarkerToMap(LatLng latLng, ChildEntity child) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.draggable(true);
        markerOption.snippet(JSON.toJSONString(child));
        markerOption.title(child.getGid());
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.treasure_fuwa_loca)));
        Marker marker = aMap.addMarker(markerOption);
        marker.setObject(child.getGid());

        String key = child.getGid();
        if (!markerMap.containsKey(key)) {
            markerMap.put(key, marker);
        }

        if (!latMap.containsKey(key)) {
            latMap.put(child.getGid(), latLng);
        }
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
//        options.radius(radius);
        options.radius(30);
        mCircle = aMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle(LOCATION_MARKER_FLAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
//        EventBus.getDefault().unregister(this);
        LocalBroadcastManager.getInstance(context).
                unregisterReceiver(mLocalBroadcastReceiver);
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        latMap.clear();
        markerMap.clear();
        latMap = null;
        markerMap = null;
        popup = null;
        popupWindow = null;
    }

    private void childrenRequest(CameraPosition cameraPosition) {
        if (aMap != null) {
            float scale = aMap.getScalePerPixel();
            int raduis = (int) (scale * UIUtils.getScreenPx(context) / 2);//半径
            Log.i("info", "==============raduis:" + raduis);
            String parms = cameraPosition.target.longitude + "-" + cameraPosition.target.latitude;
            Log.i("info", "==============parms:" + parms);
            BaseDataModel request = null;
            if (!isFate) {
                request = new AroundChildrenRequest(TAG, parms, "" + raduis);
            } else {
                request = new AroundFateRequest(TAG, parms, "" + raduis);
            }
//            AroundChildrenRequest request = new AroundChildrenRequest(TAG, parms, "" + raduis);
            request.send(new BaseDataModel.RequestCallback<BaseChildren>() {
                @Override
                public void onSuccess(BaseChildren pojo) {
                    bindDatas(pojo);
                }

                @Override
                public void onFailure(String msg) {
                    showToast(msg, true);
                }
            });
        }
    }

    private void bindDatas(BaseChildren children) {
        ArrayList<ChildEntity> list = children.getData();
        if (list != null && list.size() != 0) {
            for (ChildEntity child : list) {
                Log.i("info", "========details:" + child.getDetail());
                if (!hasChild(child)) {
                    String[] strs = child.getGeo().split("-");
                    addMarkerToMap(new LatLng(Double.parseDouble(strs[1]), Double.parseDouble(strs[0])), child);
                }
            }
        }
    }

    private boolean hasChild(ChildEntity child) {
        boolean flag = false;
        List<Marker> markers = aMap.getMapScreenMarkers();
        if (markers != null && markers.size() != 0) {
            for (Marker marker : markers) {
                String title = marker.getTitle();
                Log.i("info", "=================title:" + title);
                if (title != null && !title.equals("")) {
                    if (title.equals(child.getGid())) {
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
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

            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(FindTreasureChildrenActivity.this, getString(R.string.giving_system_location_permissions));
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_GROUP_LOACATION //相机权限
                ).request(permissionListener);
    }

    private FuwaAdapter mAdapter;

    private void showChildrenPop(View parent) {
        if (slidingDrawer.getVisibility() != View.GONE) {
            UIUtils.hindView(slidingDrawer);
            if (walkRouteOverlay != null) {
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay = null;
            }
        }
        View view = View.inflate(this, R.layout.dialog_good_list, null);
        popup = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popup.setAnimationStyle(R.style.hide_fuwa_pop_anim);
        popup.setOutsideTouchable(true);
//        popupWindow.setTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(getDrawableFromRes(R.drawable.bg_popwindow));
        ListView listView = (ListView) view.findViewById(R.id.listView);
        mAdapter = new FuwaAdapter(this);
        listView.setOnItemClickListener(new ItemClickListener());
        listView.setAdapter(mAdapter);
        ArrayList<ChildEntity> goods = getRoundChildren();
        if (goods != null && goods.size() != 0) {
            if (goods.size() > 4) {
                ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
                layoutParams.height = UIUtils.getScreenHeight(this) / 2 - 50;    //获取屏幕高度
                listView.setLayoutParams(layoutParams);
            }
            mAdapter.initData(goods);
        }
//        int xOff = UIUtils.getScreenWidth(this) / 2 - parent.getWidth() / 3;
//        int xOffDp = UIUtils.px2dip(this, xOff);
//        popupWindow.showAtLocation(parent, 0, 0, Gravity.END);
//        popupWindow.showAsDropDown(parent, -xOffDp, 0);
        popup.showAsDropDown(parent, 0, 0);
    }


    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ChildEntity entity = (ChildEntity) adapterView.getItemAtPosition(i);
            if (entity != null) {
                Intent intent = new Intent(context, CatchFuwaActivity.class);
                intent.putExtra("pic", entity.getPic());
                intent.putExtra("gid", entity.getGid());
                intent.putExtra("id", entity.getId());
                startActivity(intent);
                if (popup != null && popup.isShowing()) {
                    popup.dismiss();
                }
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        SessionInfo sin = (SessionInfo) o;
        if (sin.getAction() == Session.ACTION_SELECTED_FUWA_CHILD) {
            final ChildEntity data = (ChildEntity) sin.getData();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (popupWindow == null) {
                        if (currentChild != null && data != null) {
                            showDetail(context, titleBar, data);
                        }
                    } else {
                        if (!popupWindow.isShowing()) {
                            if (currentChild != null && data != null) {
                                showDetail(context, titleBar, data);
                            }
                        }
                    }
                }
            }, 500);
        }
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.Action.MAP_MARKER_REFRESH.equals(action)) {
                String gid = intent.getStringExtra("gid");
                if (gid != null && !gid.equals("")) {
                    deleteMarker(gid);
                }
            }
        }
    }
}
