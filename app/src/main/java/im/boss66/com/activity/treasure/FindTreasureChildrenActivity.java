package im.boss66.com.activity.treasure;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseChildren;
import im.boss66.com.entity.ChildEntity;
import im.boss66.com.http.BaseDataModel;
import im.boss66.com.http.request.AroundChildrenRequest;
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
        AMap.OnMapClickListener {
    private static final String TAG = FindTreasureChildrenActivity.class.getSimpleName();
    private MapView mMapView = null;
    private AMap aMap;
    //    private MarkerOptions markerOptions;
    private RouteSearch mRouteSearch;
    private WalkRouteResult mWalkRouteResult;
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private final int ROUTE_TYPE_WALK = 3;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private float distance = 0;
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
    private Marker mLocMarker, mPersonMarker;
    private LatLng mLatLng;
    private SensorEventHelper mSensorHelper;
    private Circle mCircle;
    public static final String LOCATION_MARKER_FLAG = "mylocation";
    private WrappingSlidingDrawer slidingDrawer;
    private ImageView handler;
    private TextView tv_location, tv_distanc_start, tv_distanc_target, tv_time, tv_num;
    private Button btn_catch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_children);
        initViews();
        initData(savedInstanceState);
    }

    private void initViews() {
        account = App.getInstance().getAccount();
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
        btn_catch = (Button) findViewById(R.id.btn_catch);

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

                break;
            case R.id.iv_reset_location:
                backOlderPosition();
                break;
            case R.id.btn_catch:
                if (currentChild != null) {

                }
                break;
        }
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
//            if (topBar.getVisibility() == View.VISIBLE) {
//                topBar.setVisibility(View.GONE);
//            }
            if (slidingDrawer.isOpened()) {
                slidingDrawer.animateClose();
            }
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
        this.currentChild = child;
        if (markerMap != null && markerMap.size() != 0) {
            Marker target = markerMap.get(child.getGid());
            distance = AMapUtils.calculateLineDistance(mLocMarker.getPosition(), target.getPosition());
        }
        mLatLng = mLocMarker.getPosition();
        Log.i("info", "===============distance:" + distance);
        if (topBar.getVisibility() != View.VISIBLE) {
            topBar.setVisibility(View.VISIBLE);
            if (distance > 20 || distance == 0) {
                topBar.findViewById(R.id.btn_catch).setVisibility(View.GONE);
                topBar.findViewById(R.id.tv_tips).setVisibility(View.VISIBLE);
            } else {
                topBar.findViewById(R.id.btn_catch).setVisibility(View.VISIBLE);
                topBar.findViewById(R.id.tv_tips).setVisibility(View.GONE);
            }
        }

        if (!slidingDrawer.isOpened()) {
            slidingDrawer.animateOpen();
        }

        String[] strs = child.getGeo().split("-");
        mEndPoint = new LatLonPoint(Double.parseDouble(strs[1]), Double.parseDouble(strs[0]));
        if (location != null) {
            mStartPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
        }
        searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WALK_DEFAULT);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {//移动地图后，停止。开始获取福娃
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
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
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
                    mCircle.setRadius(amapLocation.getAccuracy());
                    mLocMarker.setPosition(location);
                    mPersonMarker.setPosition(location);
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.i("info", "errText:" + errText);
            }
        }
    }


    private void refreshView(AMapLocation aMapLocation) {
        if (slidingDrawer.isOpened()) {
            tv_location.setText(aMapLocation.getAddress());
            if (currentChild != null) {
                tv_num.setText(currentChild.getId() + "号");
                if (markerMap != null && markerMap.size() != 0) {
                    Marker target = markerMap.get(currentChild.getGid());
                    float distance = AMapUtils.calculateLineDistance(mLocMarker.getPosition(), target.getPosition());
                    tv_distanc_target.setText("还有" + distance + "米");
                }
                if (mLatLng != null) {
                    float distance2 = AMapUtils.calculateLineDistance(mLocMarker.getPosition(), mLatLng);
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
//            aMap.clear();
//            aMap.removecache();
//            View view = getLayoutInflater().inflate(R.layout.item_map_position, null);
//            CircleImageView header = (CircleImageView) view.findViewById(R.id.header);
//            imageLoader.displayImage(account.getAvatar(), header, ImageLoaderUtils.getDisplayImageOptions());
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


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
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
        markerMap.put(child.getGid(), marker);
        latMap.put(child.getGid(), latLng);
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
//        options.radius(radius);
        options.radius(20);
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
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        latMap.clear();
        markerMap.clear();
        latMap = null;
        markerMap = null;
    }

    private void childrenRequest(CameraPosition cameraPosition) {
        if (aMap != null) {
            float scale = aMap.getScalePerPixel();
            int raduis = (int) (scale * UIUtils.getScreenPx(context) / 2);//半径
            Log.i("info", "==============raduis:" + raduis);
            String parms = cameraPosition.target.longitude + "-" + cameraPosition.target.latitude;
            Log.i("info", "==============parms:" + parms);
            AroundChildrenRequest request = new AroundChildrenRequest(TAG, parms, "" + raduis);
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
}
