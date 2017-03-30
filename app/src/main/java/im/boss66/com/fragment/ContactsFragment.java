package im.boss66.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.SharedPreferencesMgr;
import im.boss66.com.activity.MainActivity;
import im.boss66.com.activity.treasure.FuwaPackageActivity;
import im.boss66.com.activity.connection.SchoolHometownActivity;
import im.boss66.com.adapter.MyHometownAdapter;
import im.boss66.com.adapter.MySchoolAdapter;
import im.boss66.com.entity.MyInfo;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.MyInfoRequest;
import im.boss66.com.listener.RecycleViewItemListener;
import im.boss66.com.widget.dialog.PeopleConnectionPop;
import im.boss66.com.widget.dialog.PeopleDataDialog;

/**
 * Created by Johnny on 2017/2/13.
 */
public class ContactsFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = ContactsFragment.class.getSimpleName();

    private RecyclerView rcv_mySchool;
    private RecyclerView rcv_myHometown;
    private ImageView iv_add;
    private PeopleConnectionPop peopleConnectionPop;
    private RelativeLayout rl_top_bar;
    private MySchoolAdapter mySchoolAdapter;
    private MyHometownAdapter myHometownAdapter;
    private MyInfo myInfo;
    private boolean flag = true;

    private PeopleDataDialog peopleDataDialog;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //如果有数据
                    if (school_list != null && school_list.size() > 0 && hometown_list != null && hometown_list.size() > 0) {
                        flag=false;
                        //只要有一样没数据
                    } else {
                        if (peopleDataDialog == null) {
                            peopleDataDialog = new PeopleDataDialog(getActivity());
                            peopleDataDialog.show();
                        } else {
                            if (!peopleDataDialog.isShowing()) {
                                peopleDataDialog.show();
                            }
                        }
                    }

                    mySchoolAdapter.setDatas(school_list);
                    mySchoolAdapter.notifyDataSetChanged();
                    myHometownAdapter.setDatas(hometown_list);
                    myHometownAdapter.notifyDataSetChanged();

                    break;
            }

        }
    };
    private List<MyInfo.ResultBean.SchoolListBean> school_list; //学校
    private List<MyInfo.ResultBean.HometownListBean> hometown_list; //家乡


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }


    private void initViews(View view) {

        rcv_mySchool = (RecyclerView) view.findViewById(R.id.rcv_mySchool);
        rcv_myHometown = (RecyclerView) view.findViewById(R.id.rcv_myHometown);

        iv_add = (ImageView) view.findViewById(R.id.iv_add);
        rl_top_bar = (RelativeLayout) view.findViewById(R.id.rl_top_bar);
        iv_add.setOnClickListener(this);

        mySchoolAdapter = new MySchoolAdapter(getActivity());
        mySchoolAdapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), SchoolHometownActivity.class);
                intent.putExtra("isSchool", true);
                intent.putExtra("name", school_list.get(position).getName());
                intent.putExtra("school_id", school_list.get(position).getSchool_id());
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

        myHometownAdapter = new MyHometownAdapter(getActivity());
        myHometownAdapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                Intent intent = new Intent(getActivity(), SchoolHometownActivity.class);
                intent.putExtra("isSchool", false);
                intent.putExtra("name", hometown_list.get(postion).getName());
                intent.putExtra("hometown_id", hometown_list.get(postion).getHometown_id());
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

        rcv_mySchool.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcv_mySchool.setAdapter(mySchoolAdapter);


        rcv_myHometown.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcv_myHometown.setAdapter(myHometownAdapter);

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

    @Override
    public void onResume() {
        super.onResume();
        //修改完后请求接口
        if(SharedPreferencesMgr.getBoolean("setSuccess",false)){
            SharedPreferencesMgr.setBoolean("setSuccess",false);
            initData();
        }

        if(SharedPreferencesMgr.getBoolean("EditSchool2",false)){
            SharedPreferencesMgr.setBoolean("EditSchool2",false);
            initData();
        }


    }

    private void initData() {
        MyInfoRequest request = new MyInfoRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String str) {
                myInfo = JSON.parseObject(str, MyInfo.class);
                hometown_list = myInfo.getResult().getHometown_list();
                school_list = myInfo.getResult().getSchool_list();
                handler.obtainMessage(1).sendToTarget();

            }

            @Override
            public void onFailure(String msg) {
                showToast(msg, false);

            }
        });


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //不能再onresume里，否则会在activity里就弹dialog
        if (isVisibleToUser && flag) {
            initData();
        }

    }

}
