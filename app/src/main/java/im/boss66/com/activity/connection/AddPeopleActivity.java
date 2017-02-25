package im.boss66.com.activity.connection;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.fragment.CountrymanFragment;
import im.boss66.com.fragment.CustomAddFragment;
import im.boss66.com.fragment.SchoolmateFragment;
import im.boss66.com.widget.ViewpagerIndicatorOver;

/**
 * 添加人脉
 * Created by admin on 2017/2/21.
 */
public class AddPeopleActivity extends ABaseActivity implements View.OnClickListener {


    private ViewpagerIndicatorOver vp_indicator; //指示器
    private ViewPager vp_add_people;
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
        vp_indicator.setViewPager(vp_add_people,0);
        vp_indicator.setViewPagerAdapter(getSupportFragmentManager(),fragments);

    }

    private void initViews() {
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        iv_headright_view = (ImageView) findViewById(R.id.iv_headright_view);
        iv_headright_view.setVisibility(View.VISIBLE);
        Glide.with(this).load(R.drawable.usersearch).into(iv_headright_view);

        vp_indicator = (ViewpagerIndicatorOver) findViewById(R.id.vp_indicator);
        vp_add_people = (ViewPager) findViewById(R.id.vp_add_people);

        tv_headcenter_view.setText("添加人脉");
        tv_headlift_view.setOnClickListener(this);
        iv_headright_view.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();

                break;
            case R.id.iv_headright_view:
                showToast("查找", true);

                break;
        }

    }
}
