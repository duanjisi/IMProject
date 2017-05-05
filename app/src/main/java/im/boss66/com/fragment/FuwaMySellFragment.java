package im.boss66.com.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.adapter.FuwaMySellAdapter;
import im.boss66.com.entity.FuwaSellEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/3/13.
 */

public class FuwaMySellFragment extends BaseFragment {

    private RecyclerView rcv_fuwalist;
    private FuwaMySellAdapter adapter;
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
    private Dialog dialog;
    private int orderid;
    private String fuwagid;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mysell, container, false);
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
        String url = HttpUrl.SEARY_MY_SELL_FUWA + App.getInstance().getUid() + "&time=" + System.currentTimeMillis();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (result != null) {
                    FuwaSellEntity fuwaSellEntity = JSON.parseObject(result, FuwaSellEntity.class);
                    if (fuwaSellEntity.getCode() == 0) {
                        datas = fuwaSellEntity.getData();
                        handler.obtainMessage(1).sendToTarget();
                    } else {
                        showToast(fuwaSellEntity.getMessage(), false);
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
        uid = App.getInstance().getUid();
        rcv_fuwalist = (RecyclerView) view.findViewById(R.id.rcv_fuwalist);
        rcv_fuwalist.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
//        rcv_fuwalist.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FuwaMySellAdapter(getActivity());
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                orderid = datas.get(postion).getOrderid();
                fuwagid = datas.get(postion).getFuwagid();

                if (dialog == null) {
                    showDialog();

                } else if (!dialog.isShowing()) {
                    dialog.show();

                }

            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_fuwalist.setAdapter(adapter);
    }

    public FuwaMySellAdapter getAdapter() {
        return adapter;
    }

    public void showDialog() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_cancle_sell);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cancelSell();
            }
        });


    }

    private void cancelSell() {
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        String url = HttpUrl.CANCEL_FUWA_SELL + "?orderid="+orderid+"&fuwagid="+fuwagid+"&userid="+ uid+ "&time=" + System.currentTimeMillis();
        Log.i("liwya",url);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.getInt("code")==0){

                            showToast("撤销成功",false);
                            dialog.dismiss();
                            initData();

                        }else {
                            showToast("撤销失败",false);
                        }

                    } catch (JSONException e) {
                        showToast("撤销失败",false);
                        e.printStackTrace();
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
}
