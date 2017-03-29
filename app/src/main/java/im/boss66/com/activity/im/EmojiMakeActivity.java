package im.boss66.com.activity.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.BaseCate;
import im.boss66.com.entity.CateEntity;
import im.boss66.com.fragment.MyFragment;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.CategroyRequest;

/**
 * Created by Johnny on 2017/1/23.
 * 制作表情
 */
public class EmojiMakeActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = EmojiMakeActivity.class.getSimpleName();
    private LocalBroadcastReceiver mLocalBroadcastReceiver;
    private InputMethodManager inputMethodManager;
    private TextView tvBack;
    private RelativeLayout rlSearch, rlTag;
    private TextView tvSearch;
    protected ImageButton clearSearch;
    protected EditText query;
    private Resources resources;
    private ViewPager viewPager;
    private MyFragmentPageAdapter mAdapter;
    private ArrayList<TextView> textViews;
    private LinearLayout linearLayout;
    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Session.getInstance().addObserver(this);
        setContentView(R.layout.activity_emoji_make);
        initViews();
    }

    private void initViews() {
        resources = getResources();
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        tvBack = (TextView) findViewById(R.id.tv_back);
        viewPager = (ViewPager) findViewById(R.id.pager);
        rlSearch = (RelativeLayout) findViewById(R.id.rl_search);
        rlTag = (RelativeLayout) findViewById(R.id.rl_tag);
        tvSearch = (TextView) findViewById(R.id.tv_search);
        query = (EditText) findViewById(R.id.query);
        clearSearch = (ImageButton) findViewById(R.id.search_clear);
        linearLayout = (LinearLayout) findViewById(R.id.ll_main);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.hindView(rlTag);
                UIUtils.showView(rlSearch);
            }
        });

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

        query.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    String keyWords = getText(query);
                    int index = viewPager.getCurrentItem();
                    if (keyWords != null && !keyWords.equals("")) {
                        ((MyFragment) fragments.get(index)).search(keyWords);
                    } else {
                        ((MyFragment) fragments.get(index)).requestEmos();
                    }
                    return true;
                }
                return false;
            }
        });
        mAdapter = new MyFragmentPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("info", "==========onPageSelected()");
                setSelector(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mLocalBroadcastReceiver = new LocalBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Action.EMOJI_EDITED_SEND);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mLocalBroadcastReceiver, filter);
        requestCategroys();
    }


//    private void initViewPager() {
//        mAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);
//        viewPager.setAdapter(mAdapter);
//        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Log.i("info", "==========onPageSelected()");
//                setSelector(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//    }

    @Override
    public void onClick(View view) {
        String tag = (String) view.getTag();
        if (tag.equals("topBar")) {
            setSelector(view.getId());
        }
    }

    private void requestCategroys() {
        showLoadingDialog();
        CategroyRequest request = new CategroyRequest(TAG);
        request.send(new BaseDataRequest.RequestCallback<BaseCate>() {
            @Override
            public void onSuccess(BaseCate pojo) {
                cancelLoadingDialog();
                initData(pojo);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

//    private ArrayList<CateEntity> categorys;

    private void initData(BaseCate baseCate) {
        linearLayout.removeAllViews();
        if (textViews != null) {
            textViews.clear();
            textViews = null;
        }
        ArrayList<CateEntity> list = baseCate.getResult();
        if (list != null && list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                title.add(list.get(i).getCate_name());
            }
        }
        title.add(0, "全部");
        if (title != null && title.size() != 0) {
            textViews = new ArrayList<TextView>();
            int width = getWindowManager().getDefaultDisplay().getWidth() / 5;
            int height = UIUtils.dip2px(this, 50);
            for (int i = 0; i < title.size(); i++) {
                String name = title.get(i);
//                String cateid = list.get(i).getCate_id();
//                Log.i("info", "=======cateid:" + cateid);
                String cateid;
                if (i == 0) {
                    cateid = "0";
                } else {
                    cateid = list.get(i - 1).getCate_id();
                }
                TextView textView = new TextView(this);
                textView.setText(name);
                textView.setTextSize(17);
                textView.setTextColor(resources.getColor(R.color.black));
                textView.setWidth(width);
                textView.setHeight(height - 30);
                textView.setGravity(Gravity.CENTER);
                textView.setTag("topBar");
                textView.setId(i);
                textView.setOnClickListener(this);
                textViews.add(textView);
                // �ָ���
                View view = new View(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.width = 1;
                layoutParams.height = height - 40;
                layoutParams.gravity = Gravity.CENTER;
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(resources.getColor(R.color.gray));
                linearLayout.addView(textView);
                if (i != title.size() - 1) {
                    linearLayout.addView(view);
                }
                MyFragment fragment = MyFragment.newInstance(name, cateid);
//                fragment.setCateid(cateid);
                fragments.add(fragment);
            }
        }
        mAdapter.setFragments(fragments);
        setSelector(0);
    }


    public void setSelector(int id) {
        if (title != null && title.size() != 0) {
            for (int i = 0; i < title.size(); i++) {
                if (id == i) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                            R.drawable.grouplist_item_bg_normal);
                    textViews.get(id).setBackgroundDrawable(
                            new BitmapDrawable(bitmap));
                    textViews.get(id).setTextColor(Color.RED);
                    viewPager.setCurrentItem(i);
//                    EmoCate cate = categorys.get(i);
//                    initCatePager(cate.getCate_id());
                } else {
                    textViews.get(i).setBackgroundDrawable(new BitmapDrawable());
                    textViews.get(i).setTextColor(resources.getColor(R.color.black));
                }
            }
        }
    }

//    private void initDatas() {
//        for (int i = 0; i < title.size(); i++) {
//            fragments.add(MyFragment.newInstance(title.get(i)));
//        }
//    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
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

    public class MyFragmentPageAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private FragmentManager fm;

        public MyFragmentPageAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
//            this.fragments = fragments;
            fragments = new ArrayList<>();
        }

        public MyFragmentPageAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.fragments = fragments;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (true) {//根据需求添加更新标示，UI更新完成后改回false，看不懂的回家种田
                //得到缓存的fragment
                Fragment fragment = (Fragment) super.instantiateItem(container, position);
//得到tag，这点很重要
                String fragmentTag = fragment.getTag(); //这里的tag是系统自己生产的，我们直接取就可以
//如果这个fragment需要更新
                FragmentTransaction ft = fm.beginTransaction();
//移除旧的fragment
                ft.remove(fragment);
//换成新的fragment
                fragment = fragments.get(position);
//添加新fragment时必须用前面获得的tag，这点很重要
                ft.add(container.getId(), fragment, fragmentTag);
                ft.attach(fragment);
                ft.commit();
                return fragment;
            } else {
                return super.instantiateItem(container, position);
            }
        }

        public void setFragments(ArrayList<Fragment> frags) {
            fragments.clear();
            fragments.addAll(frags);
            this.notifyDataSetChanged();
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.Action.EMOJI_EDITED_SEND.equals(action)) {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(mLocalBroadcastReceiver);
    }

    //    @Override
//    public void update(Observable observable, Object o) {
//        SessionInfo sin = (SessionInfo) o;
//        if (sin.getAction() == Session.ACTION_PREV_CLOSE_ACTIVITY) {
//            finish();
//        }
//    }
}
