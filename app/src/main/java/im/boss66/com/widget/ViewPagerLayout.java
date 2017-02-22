package im.boss66.com.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.EmoAdEntity;

public class ViewPagerLayout extends FrameLayout {
    private ImageLoader mImageLoader;
    private ViewPager mADViewPager;
    private OnAdClickListener mOnClickListener;
    private LinearLayout mIndicatorLayout;
    private ProgressBar mLoadProgressBar;
    private ImageView[] mDotIms;
    private int mAdImIndicator, mViewCount;
    private ScheduledExecutorService mScheduledExecutorService;
    private Handler mHandler;
    private boolean mADLayoutInited = false;

    public ViewPagerLayout(Context context) {
        super(context);
        init(context);
    }

    public ViewPagerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mImageLoader = ImageLoaderUtils.createImageLoader(getContext());
    }

    public void init(ArrayList<EmoAdEntity> list) {
        if (list == null) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_show_ad, this, true);
        mADViewPager = (ViewPager) findViewById(R.id.ad_view_pager);
        //mADViewPager.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mIndicatorLayout = (LinearLayout) findViewById(R.id.ad_indicator_layout);
        mLoadProgressBar = (ProgressBar) findViewById(R.id.load_ad_progress_bar);
        mADLayoutInited = true;
        mViewCount = list.size();
        mHandler = new Handler();
        mADViewPager.setAdapter(new ViewPagerAdapter(getAdViews(list)));
        mADViewPager.setOnPageChangeListener(new PageChangeListener());
        initDotViews(mViewCount);
    }

    public void setOnAdClickListener(OnAdClickListener l) {
        mOnClickListener = l;
    }

    /**
     * 显示广告图片加载进度条
     *
     * @param isShow true显示，false关闭
     */
    public void showLoadProgressBar(boolean isShow) {
        mLoadProgressBar.setIndeterminate(isShow);
        mLoadProgressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 开始广告图片循环播放
     */
    public void startADLoop() {
        if (mViewCount > 1 && mADLayoutInited
                && (mScheduledExecutorService == null || mScheduledExecutorService.isShutdown())) {
            mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            mScheduledExecutorService.scheduleAtFixedRate(new ViewPagerTask(), 1, 5, TimeUnit.SECONDS);
        }
    }

    /**
     * 停止广告图片循环播放
     */
    public void stopADLoop() {
        if (mADLayoutInited && mScheduledExecutorService != null) {
            mScheduledExecutorService.shutdown();
        }
    }

    private class PageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            setImgSelected(arg0);
        }
    }

    private void initDotViews(int count) {
        if (count < 2) {
            return;
        }
        int margin = UIUtils.dip2px(getContext(), 8);
        int radius = UIUtils.dip2px(getContext(), 8);
        mDotIms = new ImageView[count];
        for (int i = 0; i < mDotIms.length; i++) {
            mDotIms[i] = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(radius, radius);
            layoutParams.leftMargin = margin;
            mDotIms[i].setLayoutParams(layoutParams);
            mDotIms[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
            //mDotIms[i].setPadding(pad, 0, pad, 0);
            mIndicatorLayout.addView(mDotIms[i]);
        }
        setImgSelected(0);
        getLayoutParams().height = (int) ((UIUtils.getScreenWidth(getContext()) - UIUtils.dip2px(getContext(), 10)) * 9 / 16f);
    }

    private void setImgSelected(int which) {
        mAdImIndicator = which;
        for (int i = 0; i < mDotIms.length; i++) {
            mDotIms[which].setImageResource(R.drawable.ad_dot_checked);
            if (which != i) {
                mDotIms[i].setImageResource(R.drawable.ad_dot_default);
            }
        }
    }

    private List<View> getAdViews(ArrayList<EmoAdEntity> list) {
        List<View> views = new ArrayList<View>();
        for (int i = 0; i < list.size(); i++) {
            views.add(getAdView(list.get(i)));
        }
        return views;
    }

    private View getAdView(EmoAdEntity entity) {
        View layout = View.inflate(getContext(), R.layout.view_ad, null);
        layout.setTag(entity);
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick((EmoAdEntity) v.getTag());
                }
            }
        });
        ImageView img = (ImageView) layout.findViewById(R.id.ad_im);
        String url = entity.getCover();
        mImageLoader.displayImage(url, img, ImageLoaderUtils.getDisplayImageOptions());
        return layout;
    }

    private class ViewPagerAdapter extends PagerAdapter {

        private List<View> pageViews;

        public ViewPagerAdapter(List<View> pageViews) {
            super();
            this.pageViews = pageViews;
        }

        @Override
        public int getCount() {
            return pageViews != null ? pageViews.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(pageViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }

    }

    // 用来完成图片切换的任务
    private class ViewPagerTask implements Runnable {

        public void run() {
            mAdImIndicator = (mAdImIndicator + 1) % mViewCount;
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    mADViewPager.setCurrentItem(mAdImIndicator, true);
                }
            });
        }
    }

    public interface OnAdClickListener {
        public void onClick(EmoAdEntity entity);
    }

}
