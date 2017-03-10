package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.SearchSchoolAdapter;
import im.boss66.com.entity.ClubEntity;
import im.boss66.com.entity.SearchSchoolListEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by admin on 2017/3/2.
 */
public class SearchSchoolActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_headright_view, tv_headlift_view;
    private EditText et_school;
    private int schoolType;
    private SearchSchoolListEntity searchSchoolListEntity;
    private RecyclerView rcv_search_school;
    private SearchSchoolAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    result = searchSchoolListEntity.getResult();
                    adapter.setDatas(result);
                    adapter.notifyDataSetChanged();
                    break;

            }
        }
    };
    private List<SearchSchoolListEntity.ResultBean> result;
    private String id;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_school);
        Intent intent = getIntent();

        if (intent != null) {
            schoolType = intent.getIntExtra("schoolType", -1);
        }
        initViews();
    }

    private void initViews() {

        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headright_view = (TextView) findViewById(R.id.tv_headright_view);
        tv_headlift_view.setOnClickListener(this);
        tv_headright_view.setOnClickListener(this);

        et_school = (EditText) findViewById(R.id.et_school);
        Editable ea = et_school.getText();
        et_school.setSelection(ea.length());
        et_school.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                //两个字以上才请求接口
                if (editable.length() > 1) {
                    initData();


                }

            }
        });
        rcv_search_school = (RecyclerView) findViewById(R.id.rcv_search_school);
        rcv_search_school.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchSchoolAdapter(this);
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                id = result.get(postion).getId();
                name = result.get(postion).getName();
                et_school.setText(name);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_search_school.setAdapter(adapter);
    }

    private void initData() {

        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        String url = HttpUrl.SEARCH_SCHOOL;
        url = url + "?key=" + et_school.getText().toString() +  "&level=" + schoolType+"";
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (result != null) {
                    searchSchoolListEntity = JSON.parseObject(result, SearchSchoolListEntity.class);
                    if (searchSchoolListEntity != null) {
                        if (searchSchoolListEntity.getCode() == 1) {
                            handler.obtainMessage(1).sendToTarget();
                        }else{
                            showToast(searchSchoolListEntity.getMessage(),false);
                        }
                    }
                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_headright_view:
                //如果id为空
                if (!TextUtils.isEmpty(id)) {
                    Intent intent = new Intent();
                    intent.putExtra("schoolId", id);
                    intent.putExtra("schoolName", name);
                    setResult(1, intent);
                    finish();
                } else {
                    ToastUtil.showShort(context,"请选择学校");
                }

                break;
        }
    }
}
