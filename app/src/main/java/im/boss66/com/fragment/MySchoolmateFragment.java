package im.boss66.com.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.adapter.MyFollowAdapter;
import im.boss66.com.entity.MyFollow;

/**
 *我关注的同学
 * Created by liw on 2017/2/22.
 */
public class MySchoolmateFragment extends BaseFragment {
    private LRecyclerView rcv_my_follow;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private List<MyFollow> datas;
    private MyFollowAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_follow,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initlist();
        initViews(view);
    }

    private void initlist() {
        datas = new ArrayList<>();
        for(int i = 0;i<3;i++){
            MyFollow myFollow = new MyFollow();
            myFollow.setImg("http://touxiang.qqzhi.com/uploads/2012-11/1111104151660.jpg");
            myFollow.setTv1("舒淇");
            myFollow.setTv2("相似度80%");
            myFollow.setTv3("11届 英语专业");
            datas.add(myFollow);
        }
    }

    private void initViews(View view) {
        rcv_my_follow  = (LRecyclerView) view.findViewById(R.id.rcv_my_follow);
        rcv_my_follow.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new MyFollowAdapter(getActivity());
        adapter.setDatas(datas);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);

        rcv_my_follow.setAdapter(mLRecyclerViewAdapter);
        rcv_my_follow.setLoadMoreEnabled(true);


    }
}
