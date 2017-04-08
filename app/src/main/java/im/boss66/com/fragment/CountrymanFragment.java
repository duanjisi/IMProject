package im.boss66.com.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.adapter.RecommendAdapter;
import im.boss66.com.entity.MyFollow;
import im.boss66.com.entity.SchoolmateListEntity;
import im.boss66.com.http.HttpUrl;

/**
 * Created by admin on 2017/2/21.
 */
public class CountrymanFragment extends BaseFragment {

    private TextView tv_recommend1;
    private TextView tv_recommend2;
    private List<MyFollow> datas;

    private RecyclerView  rcv_recommend;
    private RecommendAdapter adapter;

    private SchoolmateListEntity schoolmateListEntity;
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    List<SchoolmateListEntity.ResultBean> result = schoolmateListEntity.getResult();
                    adapter.setDatas(result);
                    adapter.setFrom(2);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_people, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initlist();
        initViews(view);

        initData();
    }

    public void refresh(SchoolmateListEntity schoolmateListEntity){
        adapter.setDatas(schoolmateListEntity.getResult());
        adapter.setFrom(2);
        adapter.notifyDataSetChanged();
    }

    private void initViews(View view) {

        tv_recommend2 = (TextView) view.findViewById(R.id.tv_recommend2);
        tv_recommend2.setText("同乡推荐");

        rcv_recommend = (RecyclerView) view.findViewById(R.id.rcv_recommend);

        adapter = new RecommendAdapter(getActivity());
        rcv_recommend.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcv_recommend.setAdapter(adapter);
    }


    private void initData() {


        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        String url = HttpUrl.COUNTRYMAN_LIST;
        url = url + "?page=" + 0+  "&size=" +"3";
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (result != null) {
                    schoolmateListEntity = JSON.parseObject(result, SchoolmateListEntity.class);
                    if (schoolmateListEntity.getResult() != null) {
                        if (schoolmateListEntity.getCode() == 1) {
                            handler.obtainMessage(1).sendToTarget();
                        }else{
                            showToast(schoolmateListEntity.getMessage(),false);
                        }
                    }else{
//                        showToast("empty",false);

                    }
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });

    }



    private void initlist() {
        datas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MyFollow myFollow = new MyFollow();
            myFollow.setImg("http://touxiang.qqzhi.com/uploads/2012-11/1111104151660.jpg");
            myFollow.setTv1("舒淇");
            myFollow.setTv2("相似度80%");
            myFollow.setTv3("11届 英语专业");
            datas.add(myFollow);
        }
    }


}
