package im.boss66.com.activity.connection;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.boss66.com.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.fragment.CountrymanFragment;
import im.boss66.com.fragment.CustomAddFragment;
import im.boss66.com.fragment.FieldClubFragment;
import im.boss66.com.fragment.LocalClubFragment;
import im.boss66.com.fragment.SchoolmateFragment;
import im.boss66.com.fragment.TradeClubFragment;
import im.boss66.com.widget.ViewpagerIndicatorOver;

/**
 * Created by liw on 2017/2/22.
 */
public class BusinessClubActivity extends ABaseActivity implements View.OnClickListener {

    private ViewpagerIndicatorOver vp_indicator; //指示器
    private ViewPager vp_business;
    private List<String> titles;
    private List<Fragment> fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_club);
        initViews();

        initIndicator();
    }


    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("商会");
        tv_headlift_view.setOnClickListener(this);

        vp_indicator = (ViewpagerIndicatorOver) findViewById(R.id.vp_indicator);
        vp_business = (ViewPager) findViewById(R.id.vp_business);

    }

    private void initIndicator() {
        //Viewpager指示器的相关设置
        titles = Arrays.asList("本地商会","异地商会","行业商会");
        vp_indicator.setTabItemTitle(titles);
        vp_indicator.setVisiableTabCount(3);
        vp_indicator.setColorTabNormal(0xFF000000);
        vp_indicator.setColorTabSelected(0xFFFD2741);
        vp_indicator.setWidthIndicatorLine(0.5f);
        vp_indicator.setLineBold(5);

        fragments = new ArrayList<>();
        fragments.add(new LocalClubFragment());
        fragments.add(new FieldClubFragment());
        fragments.add(new TradeClubFragment());
        vp_indicator.setViewPager(vp_business,0);
        vp_indicator.setViewPagerAdapter(getSupportFragmentManager(),fragments);

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
