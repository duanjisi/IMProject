package im.boss66.com.activity.treasure;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/3/16.
 */
public class AroundPosActivity extends BaseActivity implements AMapLocationListener,
        PoiSearch.OnPoiSearchListener {

    private TextView tvMsg, tvTitle;
    private Button btnSearch;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;


    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint lp;//

    private PoiSearch poiSearch;
    private List<PoiItem> poiItems;// poi数据
    private AMapLocation location;
    private String keyWord;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around_pos);
        initViews();
        setUp();
    }

    private void initViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (location != null) {
                    initData(location);
                    doSearchQuery();
                }
            }
        });
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
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.i("info", "==================onLocationChanged()");
        this.location = aMapLocation;
    }

    private void initData(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (lp == null) {
                lp = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            }
            keyWord = aMapLocation.getPoiName();
            city = aMapLocation.getCity();
            Log.i("info", "=========keyWord:" + keyWord + "\n" + "city:" + city);
            tvTitle.setText(keyWord);
        }
    }

    /**
     * 开始进行poi搜索
     */
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
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

    @Override
    public void onPoiItemSearched(PoiItem poiitem, int rcode) {

    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    if (poiItems != null && poiItems.size() > 0) {
                        printStr(poiItems);
                    }
                }
            } else {
                showToast("没数据!", true);
            }
        } else {
            showToast(rcode, true);
        }
    }

    private void printStr(List<PoiItem> poiItems) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < poiItems.size(); i++) {
            PoiItem item = poiItems.get(i);
            String s = item.getBusinessArea();
            Log.i("info", "================title:" + item.getTitle());
            Log.i("info", "================BusinessArea:" + item.getBusinessArea());
            Log.i("info", "================CityName:" + item.getCityName());
            Log.i("info", "================Snippet:" + item.getSnippet());
            Log.i("info", "================Snippet:" + item.getSnippet());
            Log.i("info", "================IndoorData:" + item.getIndoorData().getFloorName());
            sb.append(s);
            sb.append("\n");
        }
        tvMsg.setText(sb.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
}
