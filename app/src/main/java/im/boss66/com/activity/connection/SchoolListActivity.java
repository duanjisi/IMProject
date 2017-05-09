package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.event.CreateSuccess;
import im.boss66.com.event.EditSchool;
import im.boss66.com.adapter.SchoolListAdapter;
import im.boss66.com.entity.SchoolListEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.http.request.SchoolListRequest;
import im.boss66.com.listener.RecycleViewItemListener;
import im.boss66.com.widget.dialog.DeleteDialog;

/**
 * 学校列表
 * Created by admin on 2017/3/2.
 */
public class SchoolListActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = SchoolListActivity.class.getSimpleName();
    private TextView tv_headright_view, tv_headlift_view;

    private ImageView img_school;
    private TextView tv_school;
    private RecyclerView rcv_school_list;
    private SchoolListEntity schoolListEntity;
    private SchoolListAdapter adapter;
    private DeleteDialog deleteDialog;
    private List<SchoolListEntity.ResultBean> result; //学校列表

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    result = schoolListEntity.getResult();
                    if (result.size() == 0) {
                        img_school.setVisibility(View.VISIBLE);
                        tv_school.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setDatas(result);
                        adapter.notifyDataSetChanged();
                        //把学校信息存起来
                        school_name = result.get(0).getSchool_name();
                        img_school.setVisibility(View.GONE);
                        tv_school.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    showToast("删除成功", false);
                    EventBus.getDefault().post(new CreateSuccess(""));
                    initData();
                    break;

            }
        }
    };
    private String school_name;

    private boolean noSchool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);

        EventBus.getDefault().register(this);
        initViews();

        initData();


    }

    private void initViews() {
        tv_headright_view = (TextView) findViewById(R.id.tv_headright_view);
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headright_view.setOnClickListener(this);
        tv_headlift_view.setOnClickListener(this);

        rcv_school_list = (RecyclerView) findViewById(R.id.rcv_school_list);
        img_school = (ImageView) findViewById(R.id.img_school);
        tv_school = (TextView) findViewById(R.id.tv_school);

        rcv_school_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SchoolListAdapter(context);
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                Intent intent = new Intent(SchoolListActivity.this, EditSchoolActivity.class);
                intent.putExtra("SchoolListActivity", true);
                intent.putExtra("schoolName", schoolListEntity.getResult().get(postion).getSchool_name());
                intent.putExtra("schoolNote", schoolListEntity.getResult().get(postion).getNote());
                intent.putExtra("schoolyear", schoolListEntity.getResult().get(postion).getEdu_year());
                intent.putExtra("schoolId", schoolListEntity.getResult().get(postion).getSchool_id());
                intent.putExtra("schoolType", schoolListEntity.getResult().get(postion).getLevel());
                intent.putExtra("us_id", schoolListEntity.getResult().get(postion).getUs_id());
                startActivity(intent);

            }

            @Override
            public boolean onItemLongClick(final int position) {

                deleteDialog.show();
                deleteDialog.setListener(new DeleteDialog.CallBack() {
                    @Override
                    public void delete() {
                        deleteSchoolInfo(position);
                    }
                });
                return true;
            }
        });
        rcv_school_list.setAdapter(adapter);
        deleteDialog = new DeleteDialog(SchoolListActivity.this);

    }

    private void deleteSchoolInfo(int position) {

        String url = HttpUrl.DELETE_SCHOOL_INFO;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());

        url = url + "?us_id=" + schoolListEntity.getResult().get(position).getUs_id();
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject != null) {
                        if(jsonObject.getInt("status")==401){
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                            App.getInstance().sendBroadcast(intent);
                            return;
                        }
                        if (jsonObject.getInt("code") == 1) {
                            handler.obtainMessage(2).sendToTarget();
                        } else {
                            showToast(jsonObject.getString("message"), false);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
            case R.id.tv_headlift_view:
                setSchool();
                break;
            case R.id.tv_headright_view:
                Intent intent = new Intent(this, AddSchoolActivity.class);
                startActivity(intent);
                break;

        }
    }



    private void initData() {
        SchoolListRequest request = new SchoolListRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String str) {
                schoolListEntity = JSON.parseObject(str, SchoolListEntity.class);
                handler.obtainMessage(1).sendToTarget();
                noSchool =false;
            }

            @Override
            public void onFailure(String msg) {
                //没数据code！=1的时候走该方法
//                //刷新adapter
                adapter.setDatas(new ArrayList<SchoolListEntity.ResultBean>());
                adapter.notifyDataSetChanged();
                img_school.setVisibility(View.VISIBLE);
                tv_school.setVisibility(View.VISIBLE);
                noSchool = true;
            }
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        setSchool();
    }

    private void setSchool() {
        if(noSchool){
            Intent intent = new Intent();
            intent.putExtra("school_name", "");
            setResult(2, intent);
            finish();
            noSchool = false;
            return;
        }
        if (result != null && result.size() > 0) {
            Intent intent = new Intent();
            intent.putExtra("school_name", school_name);
            setResult(2, intent);
            finish();
        }  else {
            finish();
        }

    }

    @Subscribe
    public void onMessageEvent(EditSchool event) {
        //显示学校列表
        img_school.setVisibility(View.GONE);
        tv_school.setVisibility(View.GONE);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
