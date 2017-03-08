package im.boss66.com.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.adapter.FaceAdapter;
import im.boss66.com.adapter.FacePageAdeapter;
import im.boss66.com.db.dao.EmoHelper;
import im.boss66.com.entity.EmoEntity;

/**
 * Created by Johnny on 2017/2/10.
 */
public class FaceFragment extends BaseFragment {
    private int mCurrentPage = 0;// 表情页数
    private String title;
    private View view;
    private ImageView[] imageViews;
    private ArrayList<EmoEntity> emos;
    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private int pagers = 1;
    private int size = 0;

    public static FaceFragment newInstance(String title) {
        FaceFragment fragment = new FaceFragment();
        Bundle args = new Bundle();
        args.putString("groupId", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments() != null ? getArguments().getString("groupId") : "";
        emos = (ArrayList<EmoEntity>) EmoHelper.getInstance().queryByGroupId(title);
        size = emos.size();
        if (size > App.PAGER_NUM) {
            if (size % App.PAGER_NUM != 0) {
                pagers = size / App.PAGER_NUM + 1;
            } else {
                pagers = size / App.PAGER_NUM;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (view == null) {
//            view = inflater.inflate(R.layout.fragment_pager_list2, null);
//            initViews(view);
//        }
//        return view;
        return inflater.inflate(R.layout.fragment_pager_list2, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        linearLayout = (LinearLayout) view.findViewById(R.id.indicator);
        initDotViews(pagers);
        List<View> lv = new ArrayList<View>();
        for (int i = 0; i < pagers; i++) {
            GridView gridView = getGridView();
            FaceAdapter adapter = new FaceAdapter(getActivity());
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new ItemClickListener());
            if (size > App.PAGER_NUM) {
                int startIndex = i * App.PAGER_NUM;
                int endIndex = (i + 1) * App.PAGER_NUM - 1;
                Log.i("info", "====startIndex:" + startIndex + "\n" + "endIndex:" + endIndex);
//                if (endIndex <= size) {
//                    adapter.initData(emos.subList(startIndex, endIndex));
//                } else {
//                    adapter.initData(emos.subList(startIndex, size - 1));
//                }
                if (endIndex <= size) {
                    adapter.initData(getList(startIndex, endIndex));
                } else {
                    adapter.initData(getList(startIndex, size - 1));
                }
            } else {
                adapter.initData(emos);
            }
            lv.add(gridView);
        }
        FacePageAdeapter pageAdeapter = new FacePageAdeapter(lv);
        viewPager.setAdapter(pageAdeapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setImgSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        indicator.setViewPager(viewPager);
//        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageSelected(int arg0) {
//                mCurrentPage = arg0;
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//                // do nothing
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//                // do nothing
//            }
//        });
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            EmoEntity entity = (EmoEntity) adapterView.getItemAtPosition(i);
            if (callback != null) {
                callback.onItemClick(entity);
            }
        }
    }

    private clickCallback callback;

    public void setCallback(clickCallback callback) {
        this.callback = callback;
    }

    public interface clickCallback {
        void onItemClick(EmoEntity entity);
    }

    private ArrayList<EmoEntity> getList(int start, int end) {
        ArrayList<EmoEntity> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            list.add(emos.get(i));
        }
        return list;
    }

    private GridView getGridView() {
        GridView gv = new GridView(getActivity());
        gv.setNumColumns(4);
        gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
        gv.setBackgroundColor(Color.TRANSPARENT);
        gv.setCacheColorHint(Color.TRANSPARENT);
        gv.setHorizontalSpacing(10);
        gv.setVerticalSpacing(10);
        gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        gv.setGravity(Gravity.CENTER);
//        gv.setAdapter(new FaceAdapter(getActivity()));
        gv.setOnTouchListener(forbidenScroll());
        return gv;
    }

    // 防止乱pageview乱滚动
    private View.OnTouchListener forbidenScroll() {
        return new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        };
    }


    private void setImgSelected(int which) {
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[which].setImageResource(R.drawable.ad_dot_checked);
            if (which != i) {
                imageViews[i].setImageResource(R.drawable.ad_dot_default);
            }
        }
    }

    private void initDotViews(int count) {
        if (count < 2) {
            return;
        }
        int margin = UIUtils.dip2px(getContext(), 8);
        int radius = UIUtils.dip2px(getContext(), 8);
        imageViews = new ImageView[count];
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(radius, radius);
            layoutParams.leftMargin = margin;
            imageViews[i].setLayoutParams(layoutParams);
            imageViews[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
            //mDotIms[i].setPadding(pad, 0, pad, 0);
            linearLayout.addView(imageViews[i]);
        }
        setImgSelected(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        ((ViewGroup) view.getParent()).removeView(view);
    }
}
