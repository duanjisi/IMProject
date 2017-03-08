package im.boss66.com.activity.connection;


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

import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.adapter.PeopleSearchAdapter;
import im.boss66.com.fragment.CountrymanFragment;
import im.boss66.com.fragment.CustomAddFragment;
import im.boss66.com.fragment.MyCountrymanFragment;
import im.boss66.com.fragment.MySchoolmateFragment;
import im.boss66.com.fragment.SchoolmateFragment;
import im.boss66.com.widget.ViewpagerIndicatorOver;

/**
 * 添加人脉
 * Created by liw on 2017/2/21.
 */
public class AddPeopleActivity extends ABaseActivity implements View.OnClickListener {


//    private PeopleSearchAdapter adapter;

    private ViewpagerIndicatorOver vp_indicator; //指示器
    private ViewPager vp_search_people;

    private List<String> titles;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);
        initViews();

        initIndicator();

    }

    private void initIndicator() {
        //Viewpager指示器的相关设置
        titles = Arrays.asList("同学","同乡","自定义");
        vp_indicator.setTabItemTitle(titles);
        vp_indicator.setVisiableTabCount(3);
        vp_indicator.setColorTabNormal(0xFF000000);
        vp_indicator.setColorTabSelected(0xFFFD2741);
        vp_indicator.setWidthIndicatorLine(0.5f);
        vp_indicator.setLineBold(5);

        fragments = new ArrayList<>();
        fragments.add(new SchoolmateFragment());
        fragments.add(new CountrymanFragment());
        fragments.add(new CustomAddFragment());
        vp_indicator.setViewPager(vp_search_people,0);
        vp_indicator.setViewPagerAdapter(getSupportFragmentManager(),fragments);
    }



    private void initViews() {
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("查找人脉");
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);


        vp_indicator = (ViewpagerIndicatorOver) findViewById(R.id.vp_indicator);
        vp_search_people = (ViewPager) findViewById(R.id.vp_search_people);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;

        }

    }
}
