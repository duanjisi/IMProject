package im.boss66.com.activity.personage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.LocalAddressAdapter;
import im.boss66.com.entity.LocalAddressEntity;

/**
 * 地区-市
 */
public class PersonalAreaCityActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_title;
    private RecyclerView rv_area;
    private LocalAddressAdapter adapter;
    private String pro_name;

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
                final String province = bundle.getString("province");
                pro_name = bundle.getString("pro_name");
                LocalAddressEntity.ThreeChild child = (LocalAddressEntity.ThreeChild) bundle.getSerializable("list");
                if (child != null) {
                    final List<LocalAddressEntity.FourChild> list = child.getList();
                    if (list != null) {
                        adapter = new LocalAddressAdapter(this);
                        adapter.getCityList(list, 2);
                        adapter.setOnItemClickListener(new LocalAddressAdapter.MyItemClickListener() {
                            @Override
                            public void onItemClick(View view, int postion) {
                                if (list != null && list.size() > postion) {
                                    LocalAddressEntity.FourChild child = list.get(postion);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("list", child);
                                    bundle.putString("province", province);
                                    bundle.putString("pro_name", pro_name);
                                    bundle.putString("city_name", child.getRegion_name());
                                    bundle.putString("city", child.getRegion_id());
                                    openActvityForResult(PersonalAreaDistrictActivity.class, 108, bundle);
                                }
                            }
                        });
                        rv_area.setAdapter(adapter);
                    }
                }
            }
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 108 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

}
