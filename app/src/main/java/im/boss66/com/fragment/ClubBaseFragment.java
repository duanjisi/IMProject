package im.boss66.com.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.adapter.MySchoolAdapter;
import im.boss66.com.entity.MySchool;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/2/23.
 */
public abstract class ClubBaseFragment extends BaseFragment {
    protected RecyclerView rcv_club;
    protected MySchoolAdapter adapter;
    protected List<MySchool> list;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_club, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initlist();
        initViews(view);
    }

    protected  abstract void  initlist();


    private void initViews(View view) {
        rcv_club = (RecyclerView) view.findViewById(R.id.rcv_club);

        adapter = new MySchoolAdapter(getActivity());
        adapter.setDatas(list); //后期在子类添加数据，刷新页面
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {

                click();
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_club.setAdapter(adapter);
        rcv_club.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    protected abstract void click();
}
