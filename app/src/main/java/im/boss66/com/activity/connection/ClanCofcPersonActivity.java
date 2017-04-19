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

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.adapter.ClanCofcPeopleAdapter;
import im.boss66.com.adapter.FamousPeopleAdapter;
import im.boss66.com.entity.ClanCofcPeopleEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;
import im.boss66.com.widget.ActionSheet;

/**
 * Created by liw on 2017/4/18.
 */
public class ClanCofcPersonActivity extends ABaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {
    private String id;
    private String url;
    private RecyclerView rcv_famous_people;
    private ClanCofcPeopleAdapter adapter;
    private boolean isClan;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    result = famousPeopleEntity.getResult();
                    adapter.setDatas(result);
                    adapter.notifyDataSetChanged();
                    break;

            }
        }
    };
    private ClanCofcPeopleEntity famousPeopleEntity;
    private List<ClanCofcPeopleEntity.ResultBean> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_famous_person);
        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            isClan = intent.getBooleanExtra("isClan", false);
            if (isClan) {
                url = HttpUrl.CLAN_PERSON_LIST + "?clan_id=" + id;
            } else {
                url = HttpUrl.COFC_PERSON_LIST + "?cofc_id=" + id;
            }

        }
        initViews();
        initData();
    }

    private void initData() {

        showLoadingDialog();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (result != null) {
                    famousPeopleEntity = JSON.parseObject(result, ClanCofcPeopleEntity.class);
                    if (famousPeopleEntity != null) {

                        if (famousPeopleEntity.getCode() == 1) {
                            handler.obtainMessage(1).sendToTarget();
                        } else {
//                            ToastUtil.show(context, famousPeopleEntity.getMessage(), Toast.LENGTH_SHORT);
                        }
                    }

                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
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

    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        iv_headright_view = (ImageView) findViewById(R.id.iv_headright_view);
        iv_headright_view.setVisibility(View.VISIBLE);
        iv_headright_view.setOnClickListener(this);

        tv_headcenter_view.setText("名人");
        tv_headlift_view.setOnClickListener(this);

        rcv_famous_people = (RecyclerView) findViewById(R.id.rcv_famous_people);
        adapter = new ClanCofcPeopleAdapter(this);
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                int id = result.get(postion).getId();
                String name = result.get(postion).getName();
                Intent intent = new Intent(ClanCofcPersonActivity.this,ClanCofcPersonWebActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("isClan",isClan);
                intent.putExtra("name",name);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_famous_people.setLayoutManager(new LinearLayoutManager(this));
        rcv_famous_people.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.iv_headright_view:
                showActionSheet();

                break;
        }
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem("添加名人", ActionSheet.SheetItemColor.Black, this);

        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        switch (which) {
            case 1:
                Intent intent = new Intent(this, EditClanCofcPersonActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isClan", isClan);
                bundle.putString("id", id);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initData();
                }
            }, 500);

        }
    }
}