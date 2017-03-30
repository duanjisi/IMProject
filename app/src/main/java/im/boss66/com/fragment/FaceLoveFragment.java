package im.boss66.com.fragment;

import android.content.Intent;
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
import im.boss66.com.activity.im.EmojiAddActivity;
import im.boss66.com.adapter.FacePageAdeapter;
import im.boss66.com.adapter.PictureAdapter;
import im.boss66.com.db.dao.EmoLoveHelper;
import im.boss66.com.entity.EmoLove;

/**
 * Created by Johnny on 2017/2/10.
 */
public class FaceLoveFragment extends BaseFragment {
    private int mCurrentPage = 0;// 表情页数
    private String title;
    private View view;
    private ImageView[] imageViews;
    private ArrayList<EmoLove> emos = new ArrayList<>();
    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private int pagers = 1;
    private int size = 0;

    public static FaceLoveFragment newInstance() {
        FaceLoveFragment fragment = new FaceLoveFragment();
//        Bundle args = new Bundle();
//        args.putString("groupId", title);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments() != null ? getArguments().getString("groupId") : "";
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
        initData();
    }

    private void initData() {
        emos.clear();
        linearLayout.removeAllViews();
        ArrayList<EmoLove> list = (ArrayList<EmoLove>) EmoLoveHelper.getInstance().qureList();
        EmoLove love = new EmoLove();
        love.setEmo_url("firstItem");
        emos.add(love);
        emos.addAll(list);
        Log.i("info", "====size:" + emos.size());
        size = emos.size();
        if (size > App.PAGER_NUM) {
            if (size % App.PAGER_NUM != 0) {
                pagers = size / App.PAGER_NUM + 1;
            } else {
                pagers = size / App.PAGER_NUM;
            }
        } else {
            pagers = 1;
        }
        Log.i("info", "====pagers:" + pagers);
        initDotViews(pagers);
        List<View> lv = new ArrayList<View>();
        for (int i = 0; i < pagers; i++) {
            GridView gridView = getGridView();
            PictureAdapter adapter = new PictureAdapter(getActivity());
            adapter.setAddPager(false);
            gridView.setOnItemClickListener(new ItemClickListener());
            gridView.setAdapter(adapter);
            if (size > App.PAGER_NUM) {
                int startIndex = i * App.PAGER_NUM;
                int endIndex = (i + 1) * App.PAGER_NUM;
                Log.i("info", "====startIndex:" + startIndex + "\n" + "endIndex:" + endIndex);
                if (endIndex <= size) {
                    adapter.initFaceData(getList(startIndex, endIndex));
                } else {
                    adapter.initFaceData(getList(startIndex, size));
                }
            } else {
                adapter.initFaceData(emos);
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
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            EmoLove love = (EmoLove) adapterView.getItemAtPosition(i);
            if (love.getEmo_url().equals("firstItem")) {
//                getPermission(PermissionUtil.PERMISSIONS_GROUP_CAMERA);
                Intent intent = new Intent(getActivity(), EmojiAddActivity.class);
                startActivityForResult(intent, 101);
            } else {
                if (callback != null) {
                    callback.onItemLoveClick(love.getEmo_url());
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("info", "onActivityResult()");
        if (requestCode == 101) {
            initData();
        }
    }

    private LoveCallback callback;

    public void setLoveCallback(LoveCallback callback) {
        this.callback = callback;
    }

    public interface LoveCallback {
        void onItemLoveClick(String image);
    }

    private ArrayList<EmoLove> getList(int start, int end) {
        ArrayList<EmoLove> list = new ArrayList<>();
        for (int i = start; i < end; i++) {
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
