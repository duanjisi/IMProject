package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.boss66.com.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.adapter.MySchoolAdapter;
import im.boss66.com.entity.MySchool;
import im.boss66.com.fragment.CountrymanFragment;
import im.boss66.com.fragment.CustomAddFragment;
import im.boss66.com.fragment.FieldClubFragment;
import im.boss66.com.fragment.LocalClubFragment;
import im.boss66.com.fragment.SchoolmateFragment;
import im.boss66.com.fragment.TradeClubFragment;
import im.boss66.com.listener.RecycleViewItemListener;
import im.boss66.com.widget.ViewpagerIndicatorOver;

/**
 * 商会
 * Created by liw on 2017/2/22.
 */
public class BusinessClubActivity extends ABaseActivity implements View.OnClickListener {
    protected RecyclerView rcv_club;
    protected MySchoolAdapter adapter;
    protected List<MySchool> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_club);
        initlist();
        initViews();


    }
    protected void initlist() {

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


    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("商会");
        tv_headlift_view.setOnClickListener(this);

        rcv_club = (RecyclerView) findViewById(R.id.rcv_club);

        adapter = new MySchoolAdapter(this);
        adapter.setDatas(list); //后期在子类添加数据，刷新页面
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                Intent intent = new Intent(BusinessClubActivity.this, ClubDetailActivity.class);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_club.setAdapter(adapter);
        rcv_club.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
        }
    }
}
