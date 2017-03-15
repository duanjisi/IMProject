package im.boss66.com.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.adapter.FuwaSellAdapter;
import im.boss66.com.entity.FuwaSellEntity;
import im.boss66.com.listener.RecycleViewItemListener;
import im.boss66.com.widget.ViewpagerIndicatorOver;
import im.boss66.com.widget.ViewpagerIndicatorOver2;
import im.boss66.com.widget.ViewpagerIndicatorOver3;

import static android.R.attr.width;

/**
 * Created by liw on 2017/3/13.
 */

public class FuwaSellFragment extends BaseFragment implements View.OnClickListener {
    private TextView tv_choose;
    private TextView tv_price;


    private ImageView img_choose;
    private ImageView img_price;

    private ViewPager vp_fuwa_sell;
    private TextView tv_buy;
    private List<View> views = new ArrayList<>();


    private ViewpagerIndicatorOver3 vp_indicator; //指示器

    private FuwaSellAdapter adapter;

    private List<FuwaSellEntity> datas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sellfuwa, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initList();

        initViews(view);

        initIndicator();


    }

    private void initList() {
        for (int i = 0; i < 21; i++) {
            FuwaSellEntity entity = new FuwaSellEntity();
            entity.setS1(i+"");
            entity.setS2(i+"");
            entity.setS3("https://img3.doubanio.com/img/celebrity/medium/11263.jpg");
            datas.add(entity);
        }

    }

    private void initIndicator() {
        //Viewpager指示器的相关设置

        vp_indicator.setVisiableTabCount(datas.size()%6==0?datas.size()/6:datas.size()/6+1);
        vp_indicator.setColorTabNormal(0xFF000000);
        vp_indicator.setColorTabSelected(0xFFFD2741);
        vp_indicator.setWidthIndicatorLine(1.0f);
        vp_indicator.setLineBold(5);
        vp_indicator.setViewPager(vp_fuwa_sell, 0);

    }


    private void initViews(View view) {
        view.findViewById(R.id.ll_left).setOnClickListener(this);
        view.findViewById(R.id.ll_right).setOnClickListener(this);

        tv_choose = (TextView) view.findViewById(R.id.tv_choose);
        tv_price = (TextView) view.findViewById(R.id.tv_price);
        img_choose = (ImageView) view.findViewById(R.id.img_choose);
        img_price = (ImageView) view.findViewById(R.id.img_price);

        vp_fuwa_sell = (ViewPager) view.findViewById(R.id.vp_fuwa_sell);
        tv_buy = (TextView) view.findViewById(R.id.tv_buy);
        tv_buy.setOnClickListener(this);

        vp_indicator = (ViewpagerIndicatorOver3) view.findViewById(R.id.vp_indicator);


        //
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        if(datas.size()>5){ //大于一页
            List<FuwaSellEntity> datas2 = new ArrayList<>();

            if(datas.size()%6==0){      //刚好整页
                 for (int i = 0; i < datas.size()/6; i++) {
                    View view1 = inflater.inflate(R.layout.fuwa_view, null);
                    RecyclerView rcv_fuwalist = (RecyclerView) view1.findViewById(R.id.rcv_fuwalist);
                    rcv_fuwalist.setLayoutManager(new GridLayoutManager(getActivity(), 3)); //每行3个，显示6个

                     adapter = new FuwaSellAdapter(getActivity());
                     datas2 = datas.subList(i*6,i*6+6); //前闭后开
                     adapter.setDatas(datas2);

                     setAdapterListener(adapter,datas2);

                    rcv_fuwalist.setAdapter(adapter);

                    views.add(view1);
                }
            }else {
                for (int i = 0; i < datas.size() / 6 + 1; i++) {
                    View view1 = inflater.inflate(R.layout.fuwa_view, null);
                    RecyclerView rcv_fuwalist = (RecyclerView) view1.findViewById(R.id.rcv_fuwalist);
                    rcv_fuwalist.setLayoutManager(new GridLayoutManager(getActivity(), 3)); //每行3个，显示6个
                    adapter = new FuwaSellAdapter(getActivity());


                    if(i==datas.size()/6){  //最后一页
                        datas2 = datas.subList(i*6,datas.size());
                    }else {
                        datas2 = datas.subList(i*6,i*6+6); //前闭后开
                    }

                    setAdapterListener(adapter,datas2);
                    adapter.setDatas(datas2);
                    rcv_fuwalist.setAdapter(adapter);
                    views.add(view1);
                }
            }

        }else{     //只有一页
            View view1 = inflater.inflate(R.layout.fuwa_view, null);
            RecyclerView rcv_fuwalist = (RecyclerView) view1.findViewById(R.id.rcv_fuwalist);
            rcv_fuwalist.setLayoutManager(new GridLayoutManager(getActivity(), 3)); //每行3个，显示6个
            adapter = new FuwaSellAdapter(getActivity());
            setAdapterListener(adapter,datas);
            adapter.setDatas(datas);
            rcv_fuwalist.setAdapter(adapter);
            views.add(view1);
        }


        MyPagerAdapter adapter = new MyPagerAdapter(views);
        vp_fuwa_sell.setAdapter(adapter);
        vp_fuwa_sell.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setAdapterListener(final FuwaSellAdapter adapter, final List<FuwaSellEntity> datas2) {

        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                //点击弹出
//                for(int i =0;i<datas2.size();i++){
//                    if(postion==i){
//                        adapter.chooses[i]=true;
//                    }else{
//                        adapter.chooses[i]=false;
//                    }
//                }
//                adapter.notifyDataSetChanged();

                //改变当前页面选中状态，然后还要让其他页面选中状态清空

            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

    }

    public class MyPagerAdapter extends PagerAdapter {
        private List<View> list_view;

        public MyPagerAdapter(List<View> list_view) {
            this.list_view = list_view;
        }

        @Override
        public int getCount() {
            return list_view.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(list_view.get(position));
            return list_view.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list_view.get(position));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_left:    //筛选
                tv_price.setTextColor(0xffcccccc);
                img_price.setImageResource(R.drawable.fuwa_price);

                tv_choose.setTextColor(0xffe7d09a);
                img_choose.setImageResource(R.drawable.screen_click);
                break;


            case R.id.ll_right:     //价格
                tv_choose.setTextColor(0xffcccccc);
                img_choose.setImageResource(R.drawable.fuwa_screen);

                tv_price.setTextColor(0xffe7d09a);
                img_price.setImageResource(R.drawable.price_click);

                break;

            case R.id.tv_buy: //购买
                break;
        }

    }
}
