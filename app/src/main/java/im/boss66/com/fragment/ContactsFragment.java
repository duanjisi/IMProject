package im.boss66.com.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.SharedPreferencesMgr;
import im.boss66.com.activity.connection.AddPeopleActivity;
import im.boss66.com.activity.connection.ApplyCreateActivity;
import im.boss66.com.activity.connection.ClanClubActivity;
import im.boss66.com.activity.connection.ClanClubListActivity;
import im.boss66.com.activity.connection.PeopleCenterActivity;
import im.boss66.com.activity.connection.PersonalDataActivity;
import im.boss66.com.activity.connection.SchoolHometownActivity;
import im.boss66.com.event.CreateSuccess;
import im.boss66.com.adapter.MyInfoAdapter;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.ActionEntity;
import im.boss66.com.entity.MyInfo;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.http.request.MyInfoRequest;
import im.boss66.com.listener.RecycleViewItemListener;
import im.boss66.com.widget.dialog.DeleteDialog;

/**
 * Created by Johnny on 2017/2/13.
 */
public class ContactsFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = ContactsFragment.class.getSimpleName();


    private ImageView iv_add;
    private RelativeLayout rl_top_bar;

    private MyInfo myInfo;

    private MyInfoAdapter adapter;

    private List<MyInfo.ResultBean.SchoolListBean> datas = new ArrayList<>();
    private LRecyclerView rcv_info;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    adapter.setDatas(datas);
                    adapter.notifyDataSetChanged();


                    break;
            }

        }
    };
    private TextView tv_personal_center;
    private List<MyInfo.ResultBean.SchoolListBean> school_list; //学校
    private List<MyInfo.ResultBean.SchoolListBean> hometown_list; //家乡
    private List<MyInfo.ResultBean.SchoolListBean> clan_list; //宗亲
    private List<MyInfo.ResultBean.SchoolListBean> cofc_list; //商会
    private MyInfo.ResultBean.SchoolListBean clanListBean;

    private DeleteDialog deleteDialog;
    private String clan_id;

    private boolean isClan;
    private MyInfo.ResultBean.SchoolListBean cofcListBean;
    private String cofc_id;
    private ImageView iv_avatar;
    private ImageLoader imageLoader;
    private AccountEntity account;
    private String url;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        EventBus.getDefault().register(this);
        initData();
    }


    private void initViews(View view) {
        account = App.getInstance().getAccount();
        imageLoader = ImageLoaderUtils.createImageLoader(getActivity());
        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        rl_top_bar = (RelativeLayout) view.findViewById(R.id.rl_top_bar);
        iv_avatar.setOnClickListener(this);

        rcv_info = (LRecyclerView) view.findViewById(R.id.rcv_info);
        rcv_info.setLayoutManager(new LinearLayoutManager(getActivity()));

        rcv_info.setHeaderViewColor(R.color.red_fuwa, R.color.red_fuwa_alpa_stroke, android.R.color.white);
        rcv_info.setRefreshProgressStyle(ProgressStyle.Pacman); //设置下拉刷新Progress的样式
        adapter = new MyInfoAdapter(getActivity());
        LRecyclerViewAdapter adapter1 = new LRecyclerViewAdapter(adapter);
        rcv_info.setAdapter(adapter1);
        rcv_info.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rcv_info.refreshComplete(10);
                        initData();
                    }
                }, 1000);


            }
        });
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int position) {
                int from = datas.get(position-1).getFrom();
                Intent intent;
                switch (from) {
                    case 1:
                        intent = new Intent(getActivity(), SchoolHometownActivity.class);
                        intent.putExtra("from", 1);
                        intent.putExtra("name", datas.get(position-1).getName());
                        intent.putExtra("school_id", datas.get(position-1).getSchool_id());
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getActivity(), SchoolHometownActivity.class);
                        intent.putExtra("from", 2);
                        intent.putExtra("name", datas.get(position-1).getName());
                        intent.putExtra("hometown_id", datas.get(position-1).getHometown_id());
                        startActivity(intent);
                        break;

                    case 3:
                        intent = new Intent(getActivity(), ClanClubActivity.class);
                        intent.putExtra("isClan", true);
                        intent.putExtra("name", datas.get(position-1).getName());
                        intent.putExtra("id", datas.get(position-1).getClan_id());
                        intent.putExtra("user_id",datas.get(position-1).getUser_id());
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(getActivity(), ClanClubActivity.class);
                        intent.putExtra("isClan", false);
                        intent.putExtra("name", datas.get(position-1).getName());
                        intent.putExtra("id", datas.get(position-1).getCofc_id());
                        intent.putExtra("user_id",datas.get(position-1).getUser_id());
                        startActivity(intent);
                        break;
                    case 0:
                        String type = datas.get(position-1).getType();
                        if("我的宗亲".equals(type)||"我的商会".equals(type)){
                            intent = new Intent(getActivity(), ClanClubListActivity.class);
                            intent.putExtra("type",type);
                            startActivity(intent);
                        }
                        break;

                }

            }

            @Override
            public boolean onItemLongClick(int position) {
                int from = datas.get(position-1).getFrom();
                String user_id = datas.get(position-1).getUser_id();
                switch (from) {
                    case 3:
                        if(!uid.equals(user_id)){
//                            ToastUtil.showShort(getActivity(),"不能删除别人创建的宗亲");
                            return true;
                        }
                        isClan = true;
                        clanListBean = datas.get(position-1);
                        clan_id = clanListBean.getClan_id();
                        deleteDialog.show();
                        break;
                    case 4:
                        if(!uid.equals(user_id)){
//                            ToastUtil.showShort(getActivity(),"不能删除别人创建的商会");
                            return true;
                        }
                        isClan = false;
                        cofcListBean = datas.get(position-1);
                        cofc_id = cofcListBean.getCofc_id();
                        deleteDialog.show();
                        break;
                }
                return true;
            }
        });


        iv_add = (ImageView) view.findViewById(R.id.iv_add);
        rl_top_bar = (RelativeLayout) view.findViewById(R.id.rl_top_bar);
        iv_add.setOnClickListener(this);


        deleteDialog = new DeleteDialog(getActivity());
        deleteDialog.setListener(new DeleteDialog.CallBack() {
            @Override
            public void delete() {
                if (isClan) {
                    deleteClanCofc(clan_id);
                } else {
                    deleteClanCofc(cofc_id);
                }


            }
        });
        uid = App.getInstance().getUid();

    }

    private void deleteClanCofc(String id) {
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        if (isClan) {

            url = HttpUrl.DELETE_CLAN + "?clan_id=" + id;
        } else {

            url = HttpUrl.DELETE_CLUB + "?cofc_id=" + id;
        }
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("code") == 1) {
                            if (isClan) {

                                datas.remove(clanListBean);
                            } else {

                                datas.remove(cofcListBean);
                            }
                            adapter.setDatas(datas);
                            adapter.notifyDataSetChanged();
                            showToast("删除成功", false);
                        } else {
                            showToast("不能删除别人创建的宗亲", false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("删除失败", false);
                    }
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
            }
        });
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
            case R.id.iv_avatar:
                EventBus.getDefault().post(new ActionEntity(Constants.Action.MENU_CAHNGE_CURRENT_TAB));
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
        view.findViewById(R.id.tv_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //申请创建
                Intent intent = new Intent(getActivity(), ApplyCreateActivity.class);
                startActivity(intent);
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
        popupWindow.setAnimationStyle(R.style.PopupTitleBarAnim1);
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


    private void initData() {
//        datas.clear();      //导致刷新崩溃

        MyInfoRequest request = new MyInfoRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String str) {
                if (TextUtils.isEmpty(str)) {
                    return;
                }
                myInfo = JSON.parseObject(str, MyInfo.class);
                if (myInfo != null) {
                    MyInfo.ResultBean result = myInfo.getResult();

                    if (result != null) {
                        school_list = result.getSchool_list();
                        hometown_list = result.getHometown_list();
                        clan_list = result.getClan_list();
                        cofc_list = result.getCofc_list();

                        //根据是否有数据，进行setSuccess2赋值，来对点击是否弹窗进行判断
                        if (school_list != null && school_list.size() > 0 && hometown_list != null && hometown_list.size() > 0) {
                            SharedPreferencesMgr.setBoolean("setSuccess2", true);
                        } else {
                            SharedPreferencesMgr.setBoolean("setSuccess2", false);
                        }

                        MyInfo.ResultBean.SchoolListBean schoolListBean = new MyInfo.ResultBean.SchoolListBean();
                        schoolListBean.setType("我的学校");
                        datas= new ArrayList<>();    //放在这里，防止数据叠加。

                        datas.add(schoolListBean);
                        for (MyInfo.ResultBean.SchoolListBean data : school_list) {
                            data.setFrom(1);
                            datas.add(data);
                        }
                        MyInfo.ResultBean.SchoolListBean schoolListBean2 = new MyInfo.ResultBean.SchoolListBean();
                        schoolListBean2.setType("我的家乡");
                        datas.add(schoolListBean2);
                        for (MyInfo.ResultBean.SchoolListBean data : hometown_list) {
                            data.setFrom(2);
                            datas.add(data);
                        }
                        MyInfo.ResultBean.SchoolListBean schoolListBean3 = new MyInfo.ResultBean.SchoolListBean();
                        schoolListBean3.setType("我的宗亲");
                        datas.add(schoolListBean3);
                        for (MyInfo.ResultBean.SchoolListBean data : clan_list) {
                            data.setFrom(3);
                            datas.add(data);
                        }
                        MyInfo.ResultBean.SchoolListBean schoolListBean4 = new MyInfo.ResultBean.SchoolListBean();
                        schoolListBean4.setType("我的商会");
                        datas.add(schoolListBean4);
                        for (MyInfo.ResultBean.SchoolListBean data : cofc_list) {
                            data.setFrom(4);
                            datas.add(data);
                        }
                        Log.i("liwya",datas.toString());
                        handler.obtainMessage(1).sendToTarget();
                    }

                }


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


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(CreateSuccess event) {
        initData();
    }

}
