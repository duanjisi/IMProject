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
import java.util.Observable;
import java.util.Observer;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Session;
import im.boss66.com.SessionInfo;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.db.dao.EmoCateHelper;
import im.boss66.com.db.dao.EmoGroupHelper;
import im.boss66.com.db.dao.EmoHelper;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.EmoCate;
import im.boss66.com.entity.EmoEntity;
import im.boss66.com.entity.EmoGroup;
import im.boss66.com.fragment.ContactBooksFragment;
import im.boss66.com.fragment.ContactsFragment;
import im.boss66.com.fragment.DiscoverFragment;
import im.boss66.com.fragment.HomePagerFragment;
import im.boss66.com.fragment.MineFragment;
import im.boss66.com.services.ChatServices;
import im.boss66.com.widget.dialog.PeopleDataDialog;

/**
 * Created by Johnny on 2017/1/14.
 */
public class MainActivity extends BaseActivity implements Observer {
    private static final int VIEW_PAGER_PAGE_1 = 0;
    private static final int VIEW_PAGER_PAGE_2 = 1;
    private static final int VIEW_PAGER_PAGE_3 = 2;
    private static final int VIEW_PAGER_PAGE_4 = 3;
    private static final int VIEW_PAGER_PAGE_5 = 4;
    private static final int PAGE_COUNT = 5;
    private AccountEntity account;
    private RadioGroup mRadioGroup;
    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
    private ViewPager mViewPager;
    private RadioButton rbHomePager, rbBooks, rbContacts, rbDiscover, rbMine;
    //    private ImageView ivSearch, ivAdd;
//    private RelativeLayout rl_top_bar;
//    private String userid, uid;

    private PeopleDataDialog peopleDataDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Session.getInstance().addObserver(this);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        account = App.getInstance().getAccount();
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            userid = bundle.getString("uid1", "");
//            uid = bundle.getString("uid2", "");
//        }
        mRadioGroup = (RadioGroup) findViewById(R.id.bottom_navigation_rg);
        rbHomePager = (RadioButton) findViewById(R.id.rb_home_pager);
        rbBooks = (RadioButton) findViewById(R.id.rb_contact_book);
        rbDiscover = (RadioButton) findViewById(R.id.rb_discover);
        rbMine = (RadioButton) findViewById(R.id.rb_mine);
        rbContacts = (RadioButton) findViewById(R.id.rb_contact);
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

//        Intent intent = new Intent(context, ChatServices.class);
//        intent.putExtra("userid", account.getUser_id());
//        startService(intent);
        ChatServices.startChatService(context);
    }

    private String groupIcon = "http://pics.sc.chinaz.com/Files/pic/icons128/5858/261.png";

    private void insertDatas() {
        EmoCate cate = new EmoCate();
        cate.setCate_name("搞笑");
        cate.setCate_id("1011");

        EmoCate cate1 = new EmoCate();
        cate1.setCate_name("动漫");
        cate1.setCate_id("1012");

        EmoCate cate2 = new EmoCate();
        cate2.setCate_name("滑稽");
        cate2.setCate_id("1013");

        EmoCate cate3 = new EmoCate();
        cate3.setCate_name("傻逼");
        cate3.setCate_id("1014");

        EmoCateHelper.getInstance().save(cate);
        EmoCateHelper.getInstance().save(cate1);
        EmoCateHelper.getInstance().save(cate2);
        EmoCateHelper.getInstance().save(cate3);

        EmoGroup group = new EmoGroup();
        group.setCate_id("1011");
        group.setGroup_id("10");
        group.setGroup_icon(groupIcon);
        group.setGroup_name("哭笑不得篇");

        EmoGroup group1 = new EmoGroup();
        group1.setCate_id("1011");
        group1.setGroup_id("12");
        group1.setGroup_icon(groupIcon);
        group1.setGroup_name("很酷篇");

        EmoGroup group2 = new EmoGroup();
        group2.setCate_id("1012");
        group2.setGroup_id("14");
        group2.setGroup_icon(groupIcon);
        group2.setGroup_name("搞怪可爱篇");

        EmoGroup group3 = new EmoGroup();
        group3.setCate_id("1012");
        group3.setGroup_id("15");
        group3.setGroup_icon(groupIcon);
        group3.setGroup_name("苦命篇");

        EmoGroup group4 = new EmoGroup();
        group4.setCate_id("1013");
        group4.setGroup_id("17");
        group4.setGroup_icon(groupIcon);
        group4.setGroup_name("很酷篇");

        EmoGroup group5 = new EmoGroup();
        group5.setCate_id("1014");
        group5.setGroup_id("18");
        group5.setGroup_icon(groupIcon);
        group5.setGroup_name("激萌篇");

        EmoGroup group6 = new EmoGroup();
        group6.setCate_id("1014");
        group6.setGroup_id("19");
        group6.setGroup_icon(groupIcon);
        group6.setGroup_name("很酷篇");

        EmoGroupHelper.getInstance().save(group);
        EmoGroupHelper.getInstance().save(group1);
        EmoGroupHelper.getInstance().save(group2);
        EmoGroupHelper.getInstance().save(group3);
        EmoGroupHelper.getInstance().save(group4);
        EmoGroupHelper.getInstance().save(group5);
        EmoGroupHelper.getInstance().save(group6);

        EmoEntity entity = new EmoEntity();
        entity.setEmo_group_id("10");
        entity.setEmo_id("1");

        EmoEntity entity1 = new EmoEntity();
        entity1.setEmo_group_id("10");
        entity1.setEmo_id("2");

        EmoEntity entity2 = new EmoEntity();
        entity2.setEmo_group_id("10");
        entity2.setEmo_id("3");

        EmoEntity entity15 = new EmoEntity();
        entity15.setEmo_group_id("10");
        entity15.setEmo_id("16");

        EmoEntity entity3 = new EmoEntity();
        entity3.setEmo_group_id("12");
        entity3.setEmo_id("4");


        EmoEntity entity4 = new EmoEntity();
        entity4.setEmo_group_id("14");
        entity4.setEmo_id("5");

        EmoEntity entity5 = new EmoEntity();
        entity5.setEmo_group_id("15");
        entity5.setEmo_id("6");

        EmoEntity entity8 = new EmoEntity();
        entity8.setEmo_group_id("17");
        entity8.setEmo_id("7");


        EmoEntity entity6 = new EmoEntity();
        entity6.setEmo_group_id("18");
        entity6.setEmo_id("8");

        EmoEntity entity11 = new EmoEntity();
        entity11.setEmo_group_id("18");
        entity11.setEmo_id("11");

        EmoEntity entity7 = new EmoEntity();
        entity7.setEmo_group_id("19");
        entity7.setEmo_id("9");

        EmoEntity entity9 = new EmoEntity();
        entity9.setEmo_group_id("19");
        entity9.setEmo_id("10");

        EmoEntity entity10 = new EmoEntity();
        entity10.setEmo_group_id("19");
        entity10.setEmo_id("12");

        EmoEntity entity12 = new EmoEntity();
        entity12.setEmo_group_id("19");
        entity12.setEmo_id("13");

        EmoEntity entity13 = new EmoEntity();
        entity13.setEmo_group_id("19");
        entity13.setEmo_id("14");

        EmoEntity entity14 = new EmoEntity();
        entity14.setEmo_group_id("19");
        entity14.setEmo_id("15");

        EmoEntity entity16 = new EmoEntity();
        entity16.setEmo_group_id("19");
        entity16.setEmo_id("20");

        EmoEntity entity17 = new EmoEntity();
        entity17.setEmo_group_id("19");
        entity17.setEmo_id("21");

        EmoEntity entity18 = new EmoEntity();
        entity18.setEmo_group_id("19");
        entity18.setEmo_id("22");

        EmoEntity entity19 = new EmoEntity();
        entity19.setEmo_group_id("18");
        entity19.setEmo_id("23");

        EmoEntity entity20 = new EmoEntity();
        entity20.setEmo_group_id("18");
        entity20.setEmo_id("24");

        EmoEntity entity21 = new EmoEntity();
        entity21.setEmo_group_id("18");
        entity21.setEmo_id("25");

        EmoHelper.getInstance().save(entity);
        EmoHelper.getInstance().save(entity1);
        EmoHelper.getInstance().save(entity2);
        EmoHelper.getInstance().save(entity3);
        EmoHelper.getInstance().save(entity4);
        EmoHelper.getInstance().save(entity5);
        EmoHelper.getInstance().save(entity6);
        EmoHelper.getInstance().save(entity7);
        EmoHelper.getInstance().save(entity8);
        EmoHelper.getInstance().save(entity9);
        EmoHelper.getInstance().save(entity10);
        EmoHelper.getInstance().save(entity11);
        EmoHelper.getInstance().save(entity12);
        EmoHelper.getInstance().save(entity13);
        EmoHelper.getInstance().save(entity14);
        EmoHelper.getInstance().save(entity15);
        EmoHelper.getInstance().save(entity16);
        EmoHelper.getInstance().save(entity17);
        EmoHelper.getInstance().save(entity18);
        EmoHelper.getInstance().save(entity19);
        EmoHelper.getInstance().save(entity20);
        EmoHelper.getInstance().save(entity21);
    }

    private void addData() {
        Fragment fragment = new HomePagerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userid", account.getUser_id());
        fragment.setArguments(bundle);
        mFragments.add(fragment);
        mFragments.add(new ContactBooksFragment());
        mFragments.add(new ContactsFragment());
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
                    rbContacts.setChecked(true);
                    break;
                case VIEW_PAGER_PAGE_4:
                    rbDiscover.setChecked(true);
                    break;
                case VIEW_PAGER_PAGE_5:
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
                case R.id.rb_contact:
                    mViewPager.setCurrentItem(VIEW_PAGER_PAGE_3);
                   if(peopleDataDialog==null){
                       peopleDataDialog = new PeopleDataDialog(MainActivity.this);
                       peopleDataDialog.show();
                   }else{
                       if(!peopleDataDialog.isShowing()){
                           peopleDataDialog.show();

                       }
                   }
                    break;
                case R.id.rb_discover:
                    mViewPager.setCurrentItem(VIEW_PAGER_PAGE_4);
                    break;
                case R.id.rb_mine:
                    mViewPager.setCurrentItem(VIEW_PAGER_PAGE_5);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        SessionInfo sin = (SessionInfo) data;
        if (sin.getAction() == Session.ACTION_APPLICATION_EXIT) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
