package im.boss66.com.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.SharedPreferencesMgr;
import im.boss66.com.activity.connection.AddPeopleActivity;
import im.boss66.com.activity.connection.PeopleCenterActivity;
import im.boss66.com.activity.connection.PersonalDataActivity;
import im.boss66.com.activity.treasure.ApplyFuwaActivity;
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
    private RelativeLayout rl_top_bar;
    private MySchoolAdapter mySchoolAdapter;
    private MyHometownAdapter myHometownAdapter;
    private MyInfo myInfo;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    if (school_list != null && school_list.size() > 0 && hometown_list != null && hometown_list.size() > 0) {
                        SharedPreferencesMgr.setBoolean("setSuccess2",true);
                    }else{
                        SharedPreferencesMgr.setBoolean("setSuccess2",false);
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
    private TextView tv_personal_center;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initData();
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
                if (popupWindow == null) {

                    showPop(rl_top_bar);
                } else if (!popupWindow.isShowing()) {
                    setContent();
                    popupWindow.showAsDropDown(rl_top_bar);
                }

                break;

        }
    }

    private PopupWindow popupWindow;

            private void showPop(View parent) {

                final View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_connection, null);
                popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
                view.findViewById(R.id.tv_add_people).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(), AddPeopleActivity.class);
                        startActivity(intent);
                popupWindow.dismiss();

            }
        });
        view.findViewById(R.id.tv_personal_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SharedPreferencesMgr.getBoolean("setSuccess2", false)) {
                    Intent intent = new Intent(getActivity(), PersonalDataActivity.class);
                    startActivity(intent);
                    popupWindow.dismiss();
                    return;
                }
                Intent intent1 = new Intent(getActivity(), PeopleCenterActivity.class);
                startActivity(intent1);
                popupWindow.dismiss();

            }
        });
        tv_personal_center = (TextView) view.findViewById(R.id.tv_personal_center);
        setContent();


        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        popupWindow.getBackground().setAlpha(0);
        popupWindow.showAsDropDown(parent);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int top = view.findViewById(R.id.pop_layout).getTop();
                int Bottom = view.findViewById(R.id.pop_layout).getBottom();
                int left = view.findViewById(R.id.pop_layout).getLeft();
                int right = view.findViewById(R.id.pop_layout).getRight();
                int y = (int) event.getY();
                int x = (int) event.getX();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (y < top || y > Bottom) {
                            popupWindow.dismiss();
                        }
                        if (x < left || x > right) {
                            popupWindow.dismiss();
                        }
                        break;
                }

                return true;
            }
        });

    }

    private void setContent() {

        if (SharedPreferencesMgr.getBoolean("setSuccess2", false)) {
            tv_personal_center.setText("人脉中心");
        } else {
            tv_personal_center.setText("完善资料");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //修改完后请求接口
        if (SharedPreferencesMgr.getBoolean("setSuccess", false)) {
            SharedPreferencesMgr.setBoolean("setSuccess", false);
            initData();
        }
        if (SharedPreferencesMgr.getBoolean("EditSchool2", false)) {
            SharedPreferencesMgr.setBoolean("EditSchool2", false);
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
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        //不能再onresume里，否则会在activity里就弹dialog
//        if (isVisibleToUser && flag) {
//            initData();
//        }
//
//    }

}
