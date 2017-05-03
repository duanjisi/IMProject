package im.boss66.com.activity.treasure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.fragment.FuwaMyApplyFragment;
import im.boss66.com.fragment.FuwaMyCatchFragment;
import im.boss66.com.widget.ViewpagerIndicatorOver3;

/**
 * Created by liw on 2017/5/3.
 */

public class FuwaPackageActivity extends BaseActivity implements View.OnClickListener {

    private List<String> titles;
    private List<Fragment> fragments;


    private ViewpagerIndicatorOver3 vp_indicator; //指示器
    private ViewPager vp_search_people;
    private FuwaMyCatchFragment fuwaMyCatchFragment;
    private FuwaMyApplyFragment fuwaMyApplyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuwa_package2);
        initViews();
        initIndicator();
    }

    private void initViews() {

        findViewById(R.id.tv_headlift_view).setOnClickListener(this);

        vp_search_people = (ViewPager) findViewById(R.id.vp_search_people);
        vp_indicator = (ViewpagerIndicatorOver3) findViewById(R.id.vp_indicator);
    }

    private void initIndicator() {
        titles = Arrays.asList("我捕捉的", "我申请的");
        vp_indicator.setTabItemTitle(titles);
        vp_indicator.setVisiableTabCount(2);
        vp_indicator.setColorTabNormal(0xFF8d1800);
        vp_indicator.setColorTabSelected(0xFFe7d09a);
        vp_indicator.setWidthIndicatorLine(0.5f);
        vp_indicator.setLineBold(5);


        fragments = new ArrayList<>();
        fuwaMyCatchFragment = new FuwaMyCatchFragment();
        fuwaMyApplyFragment = new FuwaMyApplyFragment();
        fragments.add(fuwaMyCatchFragment);
        fragments.add(fuwaMyApplyFragment);
        vp_indicator.setViewPager(vp_search_people, 0);
        vp_indicator.setViewPagerAdapter(getSupportFragmentManager(), fragments);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
        }
    }
}
