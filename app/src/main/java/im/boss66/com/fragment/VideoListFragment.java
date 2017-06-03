package im.boss66.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.activity.player.PlayFuwaVideoActivity;
import im.boss66.com.adapter.FuwaVideoAdapter;
import im.boss66.com.entity.FuwaVideoEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/5/31.
 */

public class VideoListFragment extends BaseFragment implements AMapLocationListener {

    private String classid;

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private String lat, lng;
    private boolean first = true;
    private RecyclerView rcv_video;
    private FuwaVideoAdapter adapter;
    private View view;
    private String result;


    public static VideoListFragment newInstance(String classid) {
        VideoListFragment fragment = new VideoListFragment();
        fragment.classid = classid;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_video_list, null);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initUI(view);
        setUp();

    }

    private void initUI(View view) {
        rcv_video = (RecyclerView) view.findViewById(R.id.rcv_video);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rcv_video.setLayoutManager(layoutManager);

        adapter = new FuwaVideoAdapter(getActivity());
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                //跳转到页面看视频
                showToast("-----",false);
                Intent intent = new Intent(getActivity(), PlayFuwaVideoActivity.class);
                intent.putExtra("position",postion);
                intent.putExtra("result",result);
                intent.putExtra("classid",classid);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_video.setAdapter(adapter);



    }


    private void setUp() {
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(getActivity());
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            //设置定位参数
//            mLocationOption.setInterval(30000);、
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除

            //单次定位
            mLocationOption.setOnceLocation(true);
            mLocationOption.setOnceLocationLatest(true);

            mlocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (first) {
                first = false;
                lng = String.valueOf(aMapLocation.getLongitude());
                lat = String.valueOf(aMapLocation.getLatitude());

                initData();
            }

        }
    }

    private void initData() {


        String url = HttpUrl.SEARCH_VIDEO_LIST + "?geohash=" + lng + "-" + lat + "&class=" + classid;
        Log.i("liwya", url);

        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                result = responseInfo.result;

                Log.i("liwya", result);
                if (result != null) {
                    FuwaVideoEntity fuwaVideoEntity = JSON.parseObject(result, FuwaVideoEntity.class);
                    if (fuwaVideoEntity.getCode() == 0) {
                        List<FuwaVideoEntity.DataBean> datas = fuwaVideoEntity.getData();

                        adapter.setDatas(datas);
                        adapter.notifyDataSetChanged();

                    }

                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mlocationClient != null) {
            mlocationClient.onDestroy();
        }
    }
}
