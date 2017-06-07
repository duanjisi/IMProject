package im.boss66.com.fragment;

import android.content.Intent;
import android.os.Bundle;
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

public class VideoListFragment extends BaseFragment {

    private String classid;


    private String lat, lng;
    private boolean first = true;
    private RecyclerView rcv_video;
    private FuwaVideoAdapter adapter;
    private View view;
    private String result;


    public static VideoListFragment newInstance(String classid,String lat,String lng) {
        VideoListFragment fragment = new VideoListFragment();
        fragment.classid = classid;

        fragment.lat = lat;
        fragment.lng = lng;
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
        initData();
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
//                showToast("-----",false);
                Intent intent = new Intent(getActivity(), PlayFuwaVideoActivity.class);
                intent.putExtra("position", postion);
                intent.putExtra("result", result);
                intent.putExtra("classid", classid);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_video.setAdapter(adapter);


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


}
