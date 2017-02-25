package im.boss66.com.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.adapter.RecommendAdapter;
import im.boss66.com.entity.MyFollow;

/**
 * Created by admin on 2017/2/21.
 */
public class SchoolmateFragment extends BaseFragment{
    private TextView tv_look_more;
    private RecyclerView  rcv_recommend;
    private RecommendAdapter adapter;
    private List<MyFollow> datas;

    private TextView tv_recommend2;

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
        tv_look_more = (TextView) view.findViewById(R.id.tv_look_more);
        rcv_recommend = (RecyclerView) view.findViewById(R.id.rcv_recommend);

        tv_recommend2 = (TextView) view.findViewById(R.id.tv_recommend2);
        tv_recommend2.setText("同学推荐");

        adapter = new RecommendAdapter(getActivity());
        adapter.setDatas(datas);
        rcv_recommend = (RecyclerView) view.findViewById(R.id.rcv_recommend);
        rcv_recommend.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcv_recommend.setAdapter(adapter);

    }
}
