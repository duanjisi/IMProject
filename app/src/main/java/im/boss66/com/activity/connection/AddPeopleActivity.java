package im.boss66.com.activity.connection;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.entity.ConnectionAllSearch;
import im.boss66.com.entity.SchoolmateListEntity;
import im.boss66.com.fragment.CountrymanFragment;
import im.boss66.com.fragment.CustomAddFragment;
import im.boss66.com.fragment.SchoolmateFragment;
import im.boss66.com.widget.ViewpagerIndicatorOver2;
import im.boss66.com.widget.dialog.SearchPop;

/**
 * 添加人脉
 * Created by liw on 2017/2/21.
 */
public class AddPeopleActivity extends ABaseActivity implements View.OnClickListener {



    private ViewpagerIndicatorOver2 vp_indicator; //指示器
    private ViewPager vp_search_people;

    private List<String> titles;
    private List<Fragment> fragments;

    private TextView tv_search;

    private RelativeLayout rl_search, rl_top_bar;
    private SearchPop searchPop;
    private SchoolmateFragment schoolmateFragment;
    private CountrymanFragment countrymanFragment;
    private CustomAddFragment customAddFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);
        initViews();
        initIndicator();
    }



    private void initIndicator() {
        //Viewpager指示器的相关设置
        titles = Arrays.asList("同学", "同乡", "自定义");
        vp_indicator.setTabItemTitle(titles);
        vp_indicator.setVisiableTabCount(3);
        vp_indicator.setColorTabNormal(0xFF000000);
        vp_indicator.setColorTabSelected(0xFFFD2741);
        vp_indicator.setWidthIndicatorLine(0.5f);
        vp_indicator.setLineBold(5);
        vp_indicator.setListener(new ViewpagerIndicatorOver2.ICallBack() {
            @Override
            public void setText(int position) {
                searchPop.setFragment_position(position);
                if (position == 2) {
                    tv_search.setText("搜索");
                } else {
                    tv_search.setText("搜索人脉");
                }

            }
        });

        fragments = new ArrayList<>();
        schoolmateFragment = new SchoolmateFragment();
        countrymanFragment = new CountrymanFragment();
        customAddFragment = new CustomAddFragment();
        fragments.add(schoolmateFragment);
        fragments.add(countrymanFragment);
        fragments.add(customAddFragment);
        vp_indicator.setViewPager(vp_search_people, 0);
        vp_indicator.setViewPagerAdapter(getSupportFragmentManager(), fragments);

    }


    private void initViews() {
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("查找人脉");
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);

        tv_search = (TextView) findViewById(R.id.tv_search);

        rl_search = (RelativeLayout) findViewById(R.id.rl_search);
        rl_top_bar = (RelativeLayout) findViewById(R.id.rl_top_bar); //顶部view

        rl_search.setOnClickListener(this);
        vp_indicator = (ViewpagerIndicatorOver2) findViewById(R.id.vp_indicator);
        vp_search_people = (ViewPager) findViewById(R.id.vp_search_people);



        searchPop = new SearchPop(this);      //切换后position不同。
        searchPop.setListener(new SearchPop.ICallBack() {
            @Override
            public void setVisible() {
                rl_top_bar.setVisibility(View.VISIBLE);
                vp_indicator.setVisibility(View.VISIBLE);
                rl_search.setVisibility(View.VISIBLE);
            }

            @Override
            public void refreash(int position,SchoolmateListEntity schoolmateListEntity) {

                switch (position){
                    case 0://fragment修改数据，adapter刷新
                        schoolmateFragment.refresh(schoolmateListEntity);

                        break;
                    case 1:
                        countrymanFragment.refresh(schoolmateListEntity);

                        break;


                }
            }

            @Override
            public void refreashAll(int position, ConnectionAllSearch connectionAllSearch) {
                customAddFragment.refresh(connectionAllSearch);

            }


        });

    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.rl_search:
                rl_top_bar.setVisibility(View.GONE);
                vp_indicator.setVisibility(View.GONE);
                rl_search.setVisibility(View.INVISIBLE);

                searchPop.showAtLocation(vp_search_people, Gravity.NO_GRAVITY,0,0);

                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
