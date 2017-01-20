package im.boss66.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.fragment.ContactBooksFragment;
import im.boss66.com.fragment.DiscoverFragment;
import im.boss66.com.fragment.HomePagerFragment;
import im.boss66.com.fragment.MineFragment;
import im.boss66.com.services.ChatServices;

/**
 * Created by Johnny on 2017/1/14.
 */
public class MainActivity extends BaseActivity {
    private static final int VIEW_PAGER_PAGE_1 = 0;
    private static final int VIEW_PAGER_PAGE_2 = 1;
    private static final int VIEW_PAGER_PAGE_3 = 2;
    private static final int VIEW_PAGER_PAGE_4 = 3;
    private static final int PAGE_COUNT = 4;
    private RadioGroup mRadioGroup;
    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
    private ViewPager mViewPager;
    private RadioButton rbHomePager, rbBooks, rbDiscover, rbMine;
    //    private ImageView ivSearch, ivAdd;
//    private RelativeLayout rl_top_bar;
    private String userid, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userid = bundle.getString("uid1", "");
            uid = bundle.getString("uid2", "");
        }
        mRadioGroup = (RadioGroup) findViewById(R.id.bottom_navigation_rg);
        rbHomePager = (RadioButton) findViewById(R.id.rb_home_pager);
        rbBooks = (RadioButton) findViewById(R.id.rb_contact_book);
        rbDiscover = (RadioButton) findViewById(R.id.rb_discover);
        rbMine = (RadioButton) findViewById(R.id.rb_mine);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mRadioGroup.setOnCheckedChangeListener(new CheckListener());
        addData();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),
                mFragments);
        mViewPager.setOffscreenPageLimit(PAGE_COUNT - 1);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new MyPageChangeListener());
        mViewPager.setCurrentItem(VIEW_PAGER_PAGE_1);

        Intent intent = new Intent(context, ChatServices.class);
        intent.putExtra("userid", userid);
        startService(intent);
    }

    private void addData() {
        Fragment fragment = new HomePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userid", userid);
        fragment.setArguments(bundle);
        mFragments.add(fragment);
        mFragments.add(new ContactBooksFragment());
        mFragments.add(new DiscoverFragment());
        mFragments.add(new MineFragment());
    }


    private class PagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragmentsList;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public PagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragmentsList = fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentsList.get(arg0);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case VIEW_PAGER_PAGE_1:
                    rbHomePager.setChecked(true);
                    break;
                case VIEW_PAGER_PAGE_2:
                    rbBooks.setChecked(true);
                    break;
                case VIEW_PAGER_PAGE_3:
                    rbDiscover.setChecked(true);
                    break;
                case VIEW_PAGER_PAGE_4:
                    rbMine.setChecked(true);
                    break;
                default:
                    break;
            }
        }
    }

    private class CheckListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup arg0, int arg1) {
            switch (arg1) {
                case R.id.rb_home_pager:
                    mViewPager.setCurrentItem(VIEW_PAGER_PAGE_1);
                    break;
                case R.id.rb_contact_book:
                    mViewPager.setCurrentItem(VIEW_PAGER_PAGE_2);
                    break;
                case R.id.rb_discover:
                    mViewPager.setCurrentItem(VIEW_PAGER_PAGE_3);
                    break;
                case R.id.rb_mine:
                    mViewPager.setCurrentItem(VIEW_PAGER_PAGE_4);
                    break;
                default:
                    break;
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
