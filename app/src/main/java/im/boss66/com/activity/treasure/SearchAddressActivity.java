package im.boss66.com.activity.treasure;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.FuwaHideAddressAdapter;
import im.boss66.com.widget.ClearEditText;

/**
 * Created by GMARUnity on 2017/3/18.
 */
public class SearchAddressActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener,
        PoiSearch.OnPoiSearchListener {

    private ClearEditText et_name;
    private TextView tv_close;
    private LRecyclerView rv_content;

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
    private String city, area, curCity, curGeohash;
    private FuwaHideAddressAdapter addressAdapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private LinearLayout ll_no_address;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
        initView();
        setUp();
        if (location != null) {
            initData(location);
        }
    }

    private void initView() {
        ll_no_address = (LinearLayout) findViewById(R.id.ll_no_address);
        ll_no_address.setOnClickListener(this);
        et_name = (ClearEditText) findViewById(R.id.et_name);
        tv_close = (TextView) findViewById(R.id.tv_close);
        rv_content = (LRecyclerView) findViewById(R.id.rv_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_content.setLayoutManager(layoutManager);
        View footView = LayoutInflater.from(this).inflate(R.layout.item_fuwa_hide_address,
                (ViewGroup) findViewById(android.R.id.content), false);
        TextView tv_foot_title = (TextView) footView.findViewById(R.id.tv_title);
        TextView tv_foot_content = (TextView) footView.findViewById(R.id.tv_content);
        tv_foot_title.setTextColor(getResources().getColor(R.color.hint_text_color));
        tv_foot_content.setTextColor(getResources().getColor(R.color.hint_text_color));
        tv_foot_title.setText("没有找到你的位置");
        tv_foot_content.setText("创建新的位置");
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("city", curCity);
                bundle.putString("area", area);
                bundle.putString("curGeohash", curGeohash);
                openActvityForResult(FuwaAddressSearchActivity.class, 101, bundle);
            }
        });
        poiItems = new ArrayList<>();
        addressAdapter = new FuwaHideAddressAdapter(this, poiItems);
        addressAdapter.setIsShowIcon(false);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(addressAdapter);

        rv_content.setAdapter(mLRecyclerViewAdapter);
        mLRecyclerViewAdapter.addFooterView(footView);
        rv_content.setPullRefreshEnabled(false);
        rv_content.setNoMore(true);
        rv_content.addOnItemTouchListener(new FuwaHideAddressAdapter.RecyclerItemClickListener(this,
                new FuwaHideAddressAdapter.RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (poiItems != null && position >= 0 && position < poiItems.size()) {
                            PoiItem item = poiItems.get(position);
                            if (item != null) {
                                String address = item.getTitle();
                                String geohash = item.getLatLonPoint().getLongitude() + "-" + item.getLatLonPoint().getLatitude();
                                Intent intent = new Intent();
                                intent.putExtra("address", address);
                                intent.putExtra("geohash", geohash);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onLongClick(View view, int posotion) {

                    }
                }));

        tv_close.setOnClickListener(this);
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                keyWord = et_name.getText().toString();
                doSearchQuery();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.ll_no_address:
                Bundle bundle = new Bundle();
                bundle.putString("city", curCity);
                bundle.putString("area", area);
                bundle.putString("curGeohash", curGeohash);
                openActvityForResult(FuwaAddressSearchActivity.class, 101, bundle);
                break;
        }
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
        this.location = aMapLocation;
        if (aMapLocation != null) {
            area = aMapLocation.getDistrict();
            curCity = aMapLocation.getCity();
            curGeohash = aMapLocation.getLongitude() + "-" + aMapLocation.getLatitude();
            if (isFirst) {
                keyWord = aMapLocation.getPoiName();
                city = curCity;
                if (lp == null) {
                    lp = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                }
                doSearchQuery();
                isFirst = false;
            }
        }
    }

    private void initData(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (lp == null) {
                lp = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            }
            city = aMapLocation.getCity();
        }
    }

    /**
     * 开始进行poi搜索
     */
    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        if (TextUtils.isEmpty(city) && location != null) {
            initData(location);
        }
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
            if (result != null && result.getQuery() != null) {
                // 搜索poi的结果
                rv_content.setVisibility(View.VISIBLE);
                ll_no_address.setVisibility(View.GONE);
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    addressAdapter.onDataChange(poiItems);
                    if (poiItems != null && poiItems.size() <= 0) {
                        rv_content.setVisibility(View.GONE);
                        ll_no_address.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                rv_content.setVisibility(View.GONE);
                ll_no_address.setVisibility(View.VISIBLE);
                showToast("没数据!", true);
            }
        } else {
            rv_content.setVisibility(View.GONE);
            ll_no_address.setVisibility(View.VISIBLE);
            showToast(rcode, true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
