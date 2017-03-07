package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.discover.CirclePresenter;
import im.boss66.com.activity.discover.FriendSendNewMsgActivity;
import im.boss66.com.adapter.FriendCircleAdapter;
import im.boss66.com.widget.dialog.MyNewsPop;

/**
 * 学校和家乡类
 * Created by liw on 2017/2/22.
 */
public class SchoolHometownActivity extends ABaseActivity implements View.OnClickListener {
    private MyNewsPop myNewsPop;
    private RelativeLayout rl_top_bar;
    private boolean isSchool; // 是否是学校
    private LRecyclerView rcv_news;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private FriendCircleAdapter adapter;
    private CirclePresenter presenter;

    private int SEND_TYPE_PHOTO_TX = 101;
    private String title; //标题
    private int school_id; //学校id
    private int hometown_id; //家乡id

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
            title = intent.getStringExtra("name");
            if(isSchool){
                school_id = intent.getIntExtra("school_id", -1);

            }else {
                hometown_id = intent.getIntExtra("hometown_id", -1);
            }

        }

    }

    private void initViews() {
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText(title);

        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);
        iv_headright_view = (ImageView) findViewById(R.id.iv_headright_view);

        iv_headright_view.setVisibility(View.VISIBLE);
        iv_headright_view.setOnClickListener(this);
        iv_headright_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("sendType","text");
                openActivityForResult(ConnectionSendMsgActivity.class,SEND_TYPE_PHOTO_TX, bundle);

                return true;

            }
        });
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

        if(!isSchool){
            tv_club.setText("商会");
        }
        TextView tv_news = (TextView) header.findViewById(R.id.tv_news);
        tv_introduce.setOnClickListener(this);
        tv_famous_person.setOnClickListener(this);
        tv_club.setOnClickListener(this);
        tv_news.setOnClickListener(this);



        // 回调接口和adapter设置
//        presenter = new CirclePresenter(this);
//        List<CircleItem> list = FriendCircleTestData.createCircleDatas();
        adapter = new FriendCircleAdapter(this);
//        adapter.setCirclePresenter(presenter);
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
//                if (myNewsPop == null) {
//                    showPop(rl_top_bar);
//                } else {
//                    if (!myNewsPop.isShowing()) {
//                        showPop(rl_top_bar);
//                    }
//
//                }
                //改为朋友圈的发消息先。
                break;

            case R.id.tv_introduce: // 简介
                Intent intent = new Intent(this, IntroduceActivity.class);
                intent.putExtra("title",title);
                if(isSchool){
                    intent.putExtra("school_id",school_id);
                }else{
                    intent.putExtra("hometown_id",hometown_id);
                }
                startActivity(intent);
                break;
            case R.id.tv_famous_person: // 名人
                Intent intent1 = new Intent(this, FamousPersonActivity.class);
                if(isSchool){
                    intent1.putExtra("school_id",school_id);
                }else{
                    intent1.putExtra("hometown_id",hometown_id);
                }
                startActivity(intent1);

                break;
            case R.id.tv_club: // 社团 或 商会
                Intent intent2 = null;
                if(isSchool){
                    intent2 =new Intent(this,SchoolClubActivity.class);
                    intent2.putExtra("school_id",school_id);
                }else {
                    intent2 =new Intent(this,BusinessClubActivity.class);
                    intent2.putExtra("hometown_id",hometown_id);
                }
                startActivity(intent2);
                break;
            case R.id.tv_news: // 动态

                break;
            case R.id.tv_headlift_view:
                finish();
                break;




        }

    }

    private void showPop(View parent) {
        myNewsPop = new MyNewsPop(this);
        myNewsPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        myNewsPop.setAnimationStyle(R.style.PopupTitleBarAnim1);
        myNewsPop.showAsDropDown(parent);
    }

    public void openActivityForResult(Class<?> clazz,int requestCode,Bundle bundle){
        Intent intent = new Intent(this, clazz);
        if(intent!=null){
            intent.putExtras(bundle);
        }
        startActivityForResult(intent,requestCode);

    }


}
