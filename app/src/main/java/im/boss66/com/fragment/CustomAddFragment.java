package im.boss66.com.fragment;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.connection.ClanClubActivity;
import im.boss66.com.activity.connection.SchoolHometownActivity;
import im.boss66.com.adapter.ConnectionSearchAdapter;
import im.boss66.com.adapter.RecommendAdapter;
import im.boss66.com.entity.ConnectionAllSearch;
import im.boss66.com.entity.SchoolmateListEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/2/21.
 */
public class CustomAddFragment extends BaseFragment {


    private RecyclerView rcv_recommend;

    private ConnectionSearchAdapter adapter;


    private List<ConnectionAllSearch.ResultBean.Bean> datas;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customadd, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    public void refresh(ConnectionAllSearch connectionAllSearch) {
        datas = new ArrayList<>();
        ConnectionAllSearch.ResultBean result = connectionAllSearch.getResult();
        List<ConnectionAllSearch.ResultBean.Bean> clan = result.getClan();
        List<ConnectionAllSearch.ResultBean.Bean> homes = result.getHomes();
        List<ConnectionAllSearch.ResultBean.Bean> users = result.getUsers();
        List<ConnectionAllSearch.ResultBean.Bean> schools = result.getSchools();

        if (clan != null && clan.size() > 0) {
            ConnectionAllSearch.ResultBean.Bean bean = new ConnectionAllSearch.ResultBean.Bean();
            bean.setFrom(1); //宗亲

            datas.add(bean);
            for (ConnectionAllSearch.ResultBean.Bean data : clan) {
                data.setType(1);
                datas.add(data);
            }
        }
        if (homes != null && homes.size() > 0) {
            ConnectionAllSearch.ResultBean.Bean bean = new ConnectionAllSearch.ResultBean.Bean();
            bean.setFrom(2); //家乡

            datas.add(bean);
            for (ConnectionAllSearch.ResultBean.Bean data : homes) {
                data.setType(2);
                datas.add(data);
            }
        }
        if (users != null && users.size() > 0) {
            ConnectionAllSearch.ResultBean.Bean bean = new ConnectionAllSearch.ResultBean.Bean();
            bean.setFrom(3); //好友

            datas.add(bean);
            for (ConnectionAllSearch.ResultBean.Bean data : users) {
                data.setType(3);
                datas.add(data);
            }
        }
        if (schools != null && schools.size() > 0) {
            ConnectionAllSearch.ResultBean.Bean bean = new ConnectionAllSearch.ResultBean.Bean();
            bean.setFrom(4); //学校

            datas.add(bean);
            for (ConnectionAllSearch.ResultBean.Bean data : schools) {
                data.setType(4);
                datas.add(data);
            }
        }

        adapter.setDatas(datas);
        adapter.notifyDataSetChanged();

    }


    private void initViews(View view) {

        rcv_recommend = (RecyclerView) view.findViewById(R.id.rcv_recommend);

        adapter = new ConnectionSearchAdapter(getActivity());
        rcv_recommend.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcv_recommend.setAdapter(adapter);
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int position) {
                int type = datas.get(position).getType();
                Intent intent;
                switch (type) {
                    case 1:   //宗亲
                        intent = new Intent(getActivity(), ClanClubActivity.class);
                        intent.putExtra("isClan", 1);
                        intent.putExtra("name", datas.get(position ).getName());
                        intent.putExtra("id", datas.get(position ).getId());
                        intent.putExtra("user_id", datas.get(position ).getUser_id());
                        startActivity(intent);
                        break;
                    case 2: //家乡
                        intent = new Intent(getActivity(), SchoolHometownActivity.class);
                        intent.putExtra("from", 2);
                        intent.putExtra("name", datas.get(position ).getName());
                        intent.putExtra("hometown_id", datas.get(position ).getId());
                        startActivity(intent);
                        break;

                    case 3: //好友
                        break;
                    case 4: //学校
                        intent = new Intent(getActivity(), SchoolHometownActivity.class);
                        intent.putExtra("from", 1);
                        intent.putExtra("name", datas.get(position ).getName());
                        intent.putExtra("school_id", datas.get(position ).getId());
                        startActivity(intent);
                        break;

                }


            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

    }

}
