package im.boss66.com.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.adapter.FuwaSellAdapter;
import im.boss66.com.entity.FuwaSellEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/3/13.
 */

public class FuwaMySellFragment extends BaseFragment {

    private RecyclerView rcv_fuwalist;
    private FuwaSellAdapter adapter;
    private List<FuwaSellEntity.DataBean> datas;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    adapter.setDatas(datas);
                    adapter.setChooses();

                    adapter.notifyDataSetChanged();
                    break;

            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_mysell,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initViews(view);

        initData();

    }

    private void initData() {
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        String url = HttpUrl.SEARY_MY_SELL_FUWA+ App.getInstance().getUid();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (result != null) {
                    FuwaSellEntity fuwaSellEntity = JSON.parseObject(result, FuwaSellEntity.class);
                    if(fuwaSellEntity.getCode()==0){
                        datas = fuwaSellEntity.getData();
                        handler.obtainMessage(1).sendToTarget();
                    }else{
                        showToast(fuwaSellEntity.getMessage(),false);
                    }

                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                showToast(e.getMessage(), false);
            }
        });

    }

    private void initViews(View view) {
        rcv_fuwalist = (RecyclerView) view.findViewById(R.id.rcv_fuwalist);
        rcv_fuwalist.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false));
        adapter = new FuwaSellAdapter(getActivity());
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {

            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_fuwalist.setAdapter(adapter);
    }
}
