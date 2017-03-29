package im.boss66.com.activity.personage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.LocalAddressAdapter;
import im.boss66.com.entity.LocalAddressEntity;

/**
 * 地区-省
 */
public class PersonalAreaProvinceActivity extends BaseActivity implements View.OnClickListener, LocalAddressAdapter.MyItemClickListener {

    private TextView tv_back, tv_title;
    private RecyclerView rv_area;
    private LocalAddressAdapter adapter;
    private LocalAddressEntity jsonDate;
    private App mApplication;
    private LocalAddressEntity.SecondChild result;
    private List<LocalAddressEntity.ThreeChild> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_address);
        mApplication = App.getInstance();
        mApplication.addTempActivity(this);
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
        result = mApplication.getLoacalAddress();
        if (result == null){
            jsonData(context);
            result = jsonDate.getResult();
        }
        showData(result);
    }

    public LocalAddressEntity jsonData(Context context) {//filename assets目录下的json文件名
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("province.json"), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            jsonDate = JSON.parseObject(stringBuilder.toString(), LocalAddressEntity.class);//得到JSONobject对象
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonDate;
    }

    private void showData(LocalAddressEntity.SecondChild result){
        if (result != null) {
            mApplication.setLoacalAddress(result);
            list = result.getList();
            if (list != null) {
                adapter = new LocalAddressAdapter(this);
                adapter.getPrivincedList(list,1);
                adapter.setOnItemClickListener(this);
                rv_area.setAdapter(adapter);
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
    public void onItemClick(View view, int postion) {
        if (list != null && list.size() > postion) {
            LocalAddressEntity.ThreeChild child = list.get(postion);
            Bundle bundle = new Bundle();
            bundle.putSerializable("list", child);
            bundle.putString("province", child.getRegion_id());
            bundle.putString("pro_name",child.getRegion_name());
            openActivity(PersonalAreaCityActivity.class, bundle);
        }
    }
}
