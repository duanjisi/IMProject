package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.List;

import im.boss66.com.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.discover.CirclePresenter;
import im.boss66.com.adapter.FriendCircleAdapter;
import im.boss66.com.entity.CircleItem;
import im.boss66.com.entity.CommentConfig;
import im.boss66.com.entity.FavortItem;
import im.boss66.com.entity.FriendCircleItem;
import im.boss66.com.entity.FriendCircleTestData;
import im.boss66.com.listener.CircleContractListener;
import im.boss66.com.widget.dialog.MyNewsPop;

/**
 * 学校和家乡类
 * Created by liw on 2017/2/22.
 */
public class SchoolHometownActivity extends ABaseActivity implements View.OnClickListener, CircleContractListener.View {
    private MyNewsPop myNewsPop;
    private RelativeLayout rl_top_bar;
    private boolean isSchool; // 是否是学校
    private LRecyclerView rcv_news;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private FriendCircleAdapter adapter;
    private CirclePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_hometown);


        initArgument();
        initViews();

    }

    private void initArgument() {
        Intent intent = getIntent();
        if (intent != null) {
            isSchool = intent.getBooleanExtra("isSchool", false);

        }
    }

    private void initViews() {
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        if (isSchool) {
            tv_headcenter_view.setText("北京大学");
        } else {
            tv_headcenter_view.setText("广州 天河区");
        }

        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);
        iv_headright_view = (ImageView) findViewById(R.id.iv_headright_view);

        iv_headright_view.setVisibility(View.VISIBLE);
        iv_headright_view.setOnClickListener(this);
        rl_top_bar = (RelativeLayout) findViewById(R.id.rl_top_bar);

        //rcv 设置
        rcv_news = (LRecyclerView) findViewById(R.id.rcv_news);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        rcv_news.setLayoutManager(layoutManager);

        // 头部view
        View header = LayoutInflater.from(this).inflate(R.layout.item_people_news_head,
                (ViewGroup) findViewById(android.R.id.content), false);
        ImageView iv_bg = (ImageView) header.findViewById(R.id.iv_bg);
        TextView tv_introduce = (TextView) header.findViewById(R.id.tv_introduce);
        TextView tv_famous_person = (TextView) header.findViewById(R.id.tv_famous_person);
        TextView tv_club = (TextView) header.findViewById(R.id.tv_club);
        TextView tv_follow = (TextView) header.findViewById(R.id.tv_follow);
        TextView tv_follow_count = (TextView) header.findViewById(R.id.tv_follow_count); //关注人数
        tv_follow.setOnClickListener(this);
        if(!isSchool){
            tv_club.setText("商会");
        }
        TextView tv_news = (TextView) header.findViewById(R.id.tv_news);
        tv_introduce.setOnClickListener(this);
        tv_famous_person.setOnClickListener(this);
        tv_club.setOnClickListener(this);
        tv_news.setOnClickListener(this);



        // 回调接口和adapter设置
        presenter = new CirclePresenter(this);
//        List<CircleItem> list = FriendCircleTestData.createCircleDatas();
        adapter = new FriendCircleAdapter(this);
        adapter.setCirclePresenter(presenter);
//        adapter.setDatas(list);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);



        //头部view设置，加入到adapter中
        RelativeLayout.LayoutParams linearParams =(RelativeLayout.LayoutParams) iv_bg.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = UIUtils.getScreenWidth(context)/3*2;;
        iv_bg.setLayoutParams(linearParams); //使设置好的布局参数应用到控件


        mLRecyclerViewAdapter.addHeaderView(header);

        //rcv设置adapter以及刷新
        rcv_news.setAdapter(mLRecyclerViewAdapter);
        rcv_news.setLoadMoreEnabled(true);
//        rcv_news.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        rcv_news.refreshComplete(15);
//                        ToastUtil.showShort(SchoolHometownActivity.this, "刷新完成");
//                        List<CircleItem> list = FriendCircleTestData.createCircleDatas();
//                        adapter.setDatas(list);
//                        adapter.notifyDataSetChanged();
//                    }
//                }, 1000);
//            }
//        });
//        rcv_news.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        rcv_news.setNoMore(true);
//                        ToastUtil.showShort(SchoolHometownActivity.this, "加载更多");
//                        List<CircleItem> list = FriendCircleTestData.createCircleDatas();
//                        adapter.getDatas().addAll(list);
//                        adapter.notifyDataSetChanged();
//                    }
//                }, 1000);
//            }
//        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_headright_view:
                if (myNewsPop == null) {
                    showPop(rl_top_bar);
                } else {
                    if (!myNewsPop.isShowing()) {
                        showPop(rl_top_bar);
                    }

                }
                break;

            case R.id.tv_introduce: // 简介
                Intent intent = new Intent(this, IntroduceActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_famous_person: // 名人
                Intent intent1 = new Intent(this, FamousPersonActivity.class);
                startActivity(intent1);

                break;
            case R.id.tv_club: // 社团 或 商会
                Intent intent2 = null;
                if(isSchool){
                    intent2 =new Intent(this,SchoolClubActivity.class);
                }else {
                    intent2 =new Intent(this,BusinessClubActivity.class);
                }
                startActivity(intent2);
                break;
            case R.id.tv_news: // 动态

                break;
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_follow:// 关注
                showToast("关注",false);
                break;



        }

    }

    private void showPop(View parent) {
        myNewsPop = new MyNewsPop(this);
        myNewsPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        myNewsPop.setAnimationStyle(R.style.PopupTitleBarAnim1);
        myNewsPop.showAsDropDown(parent);
    }


    @Override
    public void update2DeleteCircle(int circleId) {

    }

    @Override
    public void update2AddFavorite(int circlePosition, int favortId) {

    }

    @Override
    public void update2DeleteFavort(int circlePosition, int favortId) {

    }

    @Override
    public void update2AddComment(int circlePosition, FriendCircleItem addItem) {
        ToastUtil.showShort(this, "评论");
    }

    @Override
    public void update2DeleteComment(int circlePosition, String commentId) {
        ToastUtil.showShort(this, "删除评论");
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        ToastUtil.showShort(this, "评论--键盘--" + visibility + ":" + commentConfig.toString());
    }

    @Override
    public void update2loadData(int loadType, List<CircleItem> datas) {

    }
}
