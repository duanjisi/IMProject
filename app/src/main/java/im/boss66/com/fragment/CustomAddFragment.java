package im.boss66.com.fragment;

import android.graphics.Paint;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.adapter.RecommendAdapter;
import im.boss66.com.entity.SchoolmateListEntity;
import im.boss66.com.http.HttpUrl;

/**
 * Created by liw on 2017/2/21.
 */
public class CustomAddFragment extends BaseFragment implements View.OnClickListener {


    private RecyclerView rcv_recommend;
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
                    adapter.setFrom(3);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customadd, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initData();
    }

    public void refresh(SchoolmateListEntity schoolmateListEntity){
        adapter.setDatas(schoolmateListEntity.getResult());
        adapter.setFrom(3);
        adapter.notifyDataSetChanged();
    }


    private void initData() {


        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        String url = HttpUrl.RANDOWM_LOOK;
        url = url + "?page=" + 0 +  "&size=" +"3";
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
                    }else {
                        showToast("empty",false);
                    }
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });

    }

    private void initViews(View view) {

        rcv_recommend = (RecyclerView) view.findViewById(R.id.rcv_recommend);

        adapter = new RecommendAdapter(getActivity());
        rcv_recommend.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcv_recommend.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

        }
    }
}
