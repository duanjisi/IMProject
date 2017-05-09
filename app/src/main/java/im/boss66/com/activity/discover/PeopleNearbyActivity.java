package im.boss66.com.activity.discover;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.PeopleNearbyAdapter;
import im.boss66.com.entity.NearByChildEntity;
import im.boss66.com.entity.PeopleNearbyEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.PeopleNearbyRequest;
import im.boss66.com.http.request.UpdateLocationRequest;
import im.boss66.com.listener.PermissionListener;
import im.boss66.com.widget.ActionSheet;

/**
 * 附近的人
 */
public class PeopleNearbyActivity extends BaseActivity implements View.OnClickListener,
        ActionSheet.OnSheetItemClickListener, AMapLocationListener,
        PoiSearch.OnPoiSearchListener {

    private final static String TAG1 = PeopleNearbyActivity.class.getSimpleName();

    private TextView tv_back;
    private ImageView iv_set;
    private LRecyclerView rv_content;
    private PeopleNearbyAdapter adapter;
    private ActionSheet actionSheet;
    //定位都要通过LocationManager这个类实现
    private LocationManager locationManager;
    private String provider;
    private String lat, lng, access_token;
    private String sexType = "0";//0:全部 1：男 2：女
    private List<NearByChildEntity> list;

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private PermissionListener permissionListener;
    private long curTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_nearby);
        initView();
        getPermission();
        //initData();
    }

//    private void initData() {
//        permissionListener = new PermissionListener() {
//            @Override
//            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
//            }
//
//            @Override
//            public void onRequestPermissionSuccess() {
//                getLocation();
//            }
//
//            @Override
//            public void onRequestPermissionError() {
//                ToastUtil.showShort(PeopleNearbyActivity.this, getString(R.string.giving_orientation_privileges));
//            }
//        };
//        PermissionUtil
//                .with(this)
//                .permissions(
//                        PermissionUtil.PERMISSIONS_GROUP_LOACATION //定位授权
//                ).request(permissionListener);
//    }
//
//    private void getLocation() {
//
//        //获取定位服务
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        //获取当前可用的位置控制器
//        List<String> list = locationManager.getProviders(true);
//
//        if (list.contains(LocationManager.NETWORK_PROVIDER)) {
//            //是否为网络位置控制器
//            provider = LocationManager.NETWORK_PROVIDER;
//
//        } else if (list.contains(LocationManager.GPS_PROVIDER)) {
//            //是否为GPS位置控制器
//            provider = LocationManager.GPS_PROVIDER;
//        } else {
//            ToastUtil.showShort(this, "请检查网络或GPS是否打开");
//            return;
//        }
//        try {
//            Location location = locationManager.getLastKnownLocation(provider);
//            if (location == null) {
//                provider = LocationManager.PASSIVE_PROVIDER;
//            }
//            location = locationManager.getLastKnownLocation(provider);
//            access_token = App.getInstance().getAccount().getAccess_token();
//            if (location != null) {
//                lat = String.valueOf(location.getLatitude());
//                lng = String.valueOf(location.getLongitude());
//                updateServerLocation();
//            } else {
//                // ToastUtil.showShort(this,getString(R.string.giving_orientation_privileges_fail));
//                if (TextUtils.isEmpty(lat) && TextUtils.isEmpty(lng)) {
//                    lat = "23.089474";
//                    lng = "113.306867";
//                    updateServerLocation();
//                }
//            }
//        } catch (SecurityException e) {
//
//        }
//    }

    private void getServerData() {
        showLoadingDialog();
        PeopleNearbyRequest request = new PeopleNearbyRequest(TAG1, access_token, lng, lat, sexType);
        request.send(new BaseDataRequest.RequestCallback<PeopleNearbyEntity>() {
            @Override
            public void onSuccess(PeopleNearbyEntity pojo) {
                if (pojo != null) {
                    list = pojo.getResult();
                    if (list != null && list.size() > 0) {
                        showData(list);
                    }
                }
                cancelLoadingDialog();
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                ToastUtil.showShort(PeopleNearbyActivity.this, msg);
            }
        });
    }

    private void showData(List<NearByChildEntity> result) {
        adapter.onDataChange(result);
    }

    private void updateServerLocation() {
        showLoadingDialog();
        UpdateLocationRequest request = new UpdateLocationRequest(TAG1, access_token, lng, lat);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                cancelLoadingDialog();
                getServerData();
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                ToastUtil.showShort(PeopleNearbyActivity.this, msg);
            }
        });
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_set = (ImageView) findViewById(R.id.iv_set);
        rv_content = (LRecyclerView) findViewById(R.id.rv_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_content.setLayoutManager(layoutManager);
        rv_content.setPullRefreshEnabled(false);
        adapter = new PeopleNearbyAdapter(this, list);
        adapter.setOnItemClickListener(new PeopleNearbyAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                int curPos = postion - 1;
                if (list != null && list.size() > curPos) {
                    NearByChildEntity item = list.get(curPos);
                    Bundle bundle = new Bundle();
                    bundle.putString("classType", "PeopleNearbyActivity");
                    bundle.putSerializable("people", item);
                    bundle.putString("userid", item.getUser_id());
                    openActivity(PersonalNearbyDetailActivity.class, bundle);
                }
            }
        });
        LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        rv_content.setAdapter(lRecyclerViewAdapter);
        tv_back.setOnClickListener(this);
        iv_set.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_set:
                showActionSheet();
                break;
        }
    }

    private void showActionSheet() {
        actionSheet = new ActionSheet(PeopleNearbyActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem(getString(R.string.only_look_at_the_girl), ActionSheet.SheetItemColor.Black,
                PeopleNearbyActivity.this)
                .addSheetItem(getString(R.string.only_look_at_the_boy), ActionSheet.SheetItemColor.Black,
                        PeopleNearbyActivity.this)
                .addSheetItem(getString(R.string.see_all), ActionSheet.SheetItemColor.Black,
                        PeopleNearbyActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        switch (which) {
            case 1:
                sexType = "2";
                break;
            case 2:
                sexType = "1";
                break;
            case 3:
                sexType = "0";
                break;
        }
        getServerData();
    }

    private void setUp() {
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
            //设置定位参数
            mLocationOption.setInterval(30000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            lng = String.valueOf(aMapLocation.getLongitude());
            lat = String.valueOf(aMapLocation.getLatitude());
            if (curTime == 0) {
                curTime = System.currentTimeMillis();
                updateServerLocation();
            } else {
                long time = System.currentTimeMillis();
                long dif = time - curTime;
                if (dif / 1000 > 120) {
                    curTime = time;
                    updateServerLocation();
                }
            }
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        Log.i("poiResult:", poiResult.toString());
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        Log.i("poiItem:", poiItem.toString());
    }

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                setUp();
            }

            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(PeopleNearbyActivity.this, "请给予定位权限");
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_GROUP_LOACATION //定位授权
                ).request(permissionListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
    }
}
