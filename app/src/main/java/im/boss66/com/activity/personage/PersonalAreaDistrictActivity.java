package im.boss66.com.activity.personage;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.LocalAddressAdapter;
import im.boss66.com.config.LoginStatus;
import im.boss66.com.entity.LocalAddressEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.ChangeAreaRequest;

/**
 * 地区-区
 */
public class PersonalAreaDistrictActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = PersonalAreaDistrictActivity.class.getSimpleName();

    private TextView tv_back, tv_title;
    private RecyclerView rv_area;
    private LocalAddressAdapter adapter;
    private String province, city, district, pro_name, city_name, district_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_address);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rv_area = (RecyclerView) findViewById(R.id.rv_area);
        tv_back.setOnClickListener(this);
        tv_title.setText(getString(R.string.area));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_area.setLayoutManager(layoutManager);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                province = bundle.getString("province");
                city = bundle.getString("city");
                city_name = bundle.getString("city_name");
                pro_name = bundle.getString("pro_name");
                LocalAddressEntity.FourChild child = (LocalAddressEntity.FourChild) bundle.getSerializable("list");
                if (child != null) {
                    final List<LocalAddressEntity.LastChild> list = child.getList();
                    if (list != null) {
                        adapter = new LocalAddressAdapter(this);
                        adapter.getDistrictList(list, 3);
                        adapter.setOnItemClickListener(new LocalAddressAdapter.MyItemClickListener() {
                            @Override
                            public void onItemClick(View view, int postion) {
                                if (list != null && list.size() > postion) {
                                    LocalAddressEntity.LastChild child = list.get(postion);
                                    if (child != null) {
                                        district = child.getRegion_id();
                                        district_name = child.getRegion_name();
                                        changeArea();
                                    }
                                }
                            }
                        });
                        rv_area.setAdapter(adapter);
                    }
                }
            }
        }
    }

    private void changeArea() {
        ChangeAreaRequest request = new ChangeAreaRequest(TAG, province, city, district);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                LoginStatus loginStatus = LoginStatus.getInstance();
                loginStatus.setDistrict_str(pro_name + " " + city_name + " " + district_name);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.showShort(context, msg);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                List<Activity> tempActivityList = App.getInstance().getTempActivityList();
                if (tempActivityList != null && tempActivityList.size() > 0 && tempActivityList.contains(this)) {
                    tempActivityList.remove(this);
                }
                finish();
                break;
        }
    }
}
