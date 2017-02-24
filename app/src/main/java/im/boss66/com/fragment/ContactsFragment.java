package im.boss66.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.connection.SchoolHometownActivity;
import im.boss66.com.adapter.MyHometownAdapter;
import im.boss66.com.adapter.MySchoolAdapter;
import im.boss66.com.entity.MySchool;
import im.boss66.com.listener.RecycleViewItemListener;
import im.boss66.com.widget.dialog.PeopleConnectionPop;

/**
 * Created by Johnny on 2017/2/13.
 */
public class ContactsFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView rcv_mySchool;
    private RecyclerView rcv_myhome;
    private ImageView iv_add;
    private PeopleConnectionPop peopleConnectionPop;
    private RelativeLayout rl_top_bar;
    private MySchoolAdapter mySchoolAdapter;
    private MyHometownAdapter myHometownAdapter;
    private List<MySchool> list;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initlist();
        initViews(view);
    }

    private void initlist() {
        list = new ArrayList<>();
        MySchool mySchool1 = new MySchool();
        MySchool mySchool2 = new MySchool();
        mySchool1.setSchoolinfo("11111111");
        mySchool1.setSchoolname("北京大学");
        mySchool1.setImg("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487667055622&di=12bb18bc7c3c34d7b8f189f09857a5a7&imgtype=0&src=http%3A%2F%2Fwww.hhxx.com.cn%2Fuploads%2Fallimg%2F1609%2F276-160Z5150T4410.jpg");
        mySchool2.setSchoolname("清华大学");
        mySchool2.setSchoolinfo("22222222");
        mySchool2.setImg("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1487667055622&di=12bb18bc7c3c34d7b8f189f09857a5a7&imgtype=0&src=http%3A%2F%2Fwww.hhxx.com.cn%2Fuploads%2Fallimg%2F1609%2F276-160Z5150T4410.jpg");
        list.add(mySchool1);
        list.add(mySchool2);
    }

    private void initViews(View view) {

        rcv_mySchool = (RecyclerView) view.findViewById(R.id.rcv_mySchool);
        rcv_myhome = (RecyclerView) view.findViewById(R.id.rcv_myhome);
        iv_add = (ImageView) view.findViewById(R.id.iv_add);
        rl_top_bar = (RelativeLayout) view.findViewById(R.id.rl_top_bar);
        iv_add.setOnClickListener(this);

        mySchoolAdapter = new MySchoolAdapter(getActivity());
        mySchoolAdapter.setDatas(list);

        mySchoolAdapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), SchoolHometownActivity.class);
                intent.putExtra("isSchool",true);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

        rcv_mySchool.setAdapter(mySchoolAdapter);
        rcv_mySchool.setLayoutManager(new LinearLayoutManager(getActivity()));

        myHometownAdapter = new MyHometownAdapter(getActivity());
        myHometownAdapter.setDatas(list);
        myHometownAdapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), SchoolHometownActivity.class);
                intent.putExtra("isSchool",false);
                startActivity(intent);

            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_myhome.setAdapter(myHometownAdapter);
        rcv_myhome.setLayoutManager(new LinearLayoutManager(getActivity()));



    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_add:
                if (peopleConnectionPop == null) {
                    showPop(rl_top_bar);
                } else {
                    if (!peopleConnectionPop.isShowing()) {
                        showPop(rl_top_bar);
                    }

                }

                break;
        }
    }

    private void showPop(View parent) {
        peopleConnectionPop = new PeopleConnectionPop(getActivity());
        peopleConnectionPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        peopleConnectionPop.setAnimationStyle(R.style.PopupTitleBarAnim1);
        peopleConnectionPop.showAsDropDown(parent);
    }
}
