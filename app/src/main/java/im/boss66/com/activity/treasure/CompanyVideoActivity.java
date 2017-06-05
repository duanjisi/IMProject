package im.boss66.com.activity.treasure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.adapter.ViewPagerFragmentAdapter;
import im.boss66.com.entity.VideoCategory;
import im.boss66.com.fragment.VideoListFragment;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.PermissionListener;

/**
 * 找福娃视频推荐
 * Created by liw on 2017/5/31.
 */

public class CompanyVideoActivity extends ABaseActivity implements View.OnClickListener, AMapLocationListener {
    private TabLayout tabLayout;

    private ViewPagerFragmentAdapter viewPagerFragmentAdapter;


    private PermissionListener permissionListener;
    private List<CharSequence> listTitle;

    private List<Fragment> listData;
    private ViewPager viewPager;


    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private boolean first = true;

    private String lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_video);

        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        findViewById(R.id.iv_headright_view).setOnClickListener(this);

        getPermission();
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
                ToastUtil.showShort(CompanyVideoActivity.this, "请给予定位权限");
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_GROUP_LOACATION //定位授权
                ).request(permissionListener);
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

            //单次定位
//            mLocationOption.setOnceLocation(true);
//            mLocationOption.setOnceLocationLatest(true);

            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
    }

    private void initData() {

        showLoadingDialog();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        String url = HttpUrl.SEARCH_COMPANY_VIDEO;
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                Log.i("liwya", result);
                if (result != null) {
                    VideoCategory videoCategory = JSON.parseObject(result, VideoCategory.class);
                    if (videoCategory.getCode() == 0) {

                        List<VideoCategory.DataBean> datas = videoCategory.getData();
                        for (VideoCategory.DataBean data : datas) {
                            listTitle.add(data.getName());
                            listData.add(VideoListFragment.newInstance(data.getClassid(), lat, lng));
                        }

                        //刷新页面
                        if (viewPagerFragmentAdapter != null) {
                            viewPagerFragmentAdapter.setListTitle(listTitle);
                            viewPagerFragmentAdapter.setListData(listData);
                            viewPagerFragmentAdapter.notifyDataSetChanged();
                        }
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

    private void initUI() {
        listTitle = new ArrayList<>();
        listData = new ArrayList<>();

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), listData, listTitle);

        viewPager.setAdapter(viewPagerFragmentAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.iv_headright_view:

                Intent intent = new Intent(context, FindTreasureChildrenActivity.class);
                intent.putExtra("isFate", false);
                startActivity(intent);

                break;
        }

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (first) {
            first = false;
            lng = String.valueOf(aMapLocation.getLongitude());
            lat = String.valueOf(aMapLocation.getLatitude());

            initUI();
            initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
    }
}
