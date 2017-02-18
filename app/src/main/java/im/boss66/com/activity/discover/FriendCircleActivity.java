package im.boss66.com.activity.discover;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import java.util.List;
import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.FriendCircleAdapter;
import im.boss66.com.entity.CircleItem;
import im.boss66.com.entity.CommentConfig;
import im.boss66.com.entity.FavortItem;
import im.boss66.com.entity.FriendCircleItem;
import im.boss66.com.entity.FriendCircleTestData;
import im.boss66.com.listener.CircleContractListener;
import im.boss66.com.widget.ActionSheet;
/**
 * 朋友圈
 */
public class FriendCircleActivity extends BaseActivity implements View.OnClickListener, CircleContractListener.View, ActionSheet.OnSheetItemClickListener {
    private TextView tv_back;
    private ImageView iv_set;
    private LRecyclerView rv_friend;
    private LinearLayout ll_new;
    private ImageView iv_new;
    private TextView tv_new_count;
    private View v_new;

    private FriendCircleAdapter adapter;
    private CirclePresenter presenter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter = null;
    private int actionSheetType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_circle);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_set = (ImageView) findViewById(R.id.iv_set);
        rv_friend = (LRecyclerView) findViewById(R.id.rv_friend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_friend.setLayoutManager(layoutManager);
        iv_set.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        presenter = new CirclePresenter(this);
        List<CircleItem> list = FriendCircleTestData.createCircleDatas();
        adapter = new FriendCircleAdapter(this);
        adapter.setCirclePresenter(presenter);
        adapter.setDatas(list);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);

        View header = LayoutInflater.from(this).inflate(R.layout.item_friend_circle_head,
                (ViewGroup)findViewById(android.R.id.content), false);
        ImageView iv_bg = (ImageView) header.findViewById(R.id.iv_bg);
        ImageView iv_head = (ImageView) header.findViewById(R.id.iv_head);
        TextView tv_name = (TextView) header.findViewById(R.id.tv_name);
        ll_new = (LinearLayout) header.findViewById(R.id.ll_new);
        iv_new = (ImageView) header.findViewById(R.id.iv_new);
        tv_new_count = (TextView) header.findViewById(R.id.tv_new_count);
        v_new = header.findViewById(R.id.v_new);
        iv_bg.setOnClickListener(this);
        iv_head.setOnClickListener(this);

        FrameLayout.LayoutParams linearParams =(FrameLayout.LayoutParams) iv_bg.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = UIUtils.getScreenWidth(context)/3*2;;
        iv_bg.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        mLRecyclerViewAdapter.addHeaderView(header);
        rv_friend.setAdapter(mLRecyclerViewAdapter);
        rv_friend.setLoadMoreEnabled(true);
        rv_friend.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rv_friend.refreshComplete(15);
                        ToastUtil.showShort(FriendCircleActivity.this,"刷新完成");
                        List<CircleItem> list = FriendCircleTestData.createCircleDatas();
                        adapter.setDatas(list);
                        adapter.notifyDataSetChanged();
                    }
                },1000);
            }
        });
        rv_friend.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rv_friend.setNoMore(true);
                        ToastUtil.showShort(FriendCircleActivity.this,"加载更多");
                        List<CircleItem> list = FriendCircleTestData.createCircleDatas();
                        adapter.getDatas().addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                },1000);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_set:
                showActionSheet(1);
                break;
            case R.id.iv_bg:
                showActionSheet(2);
                break;
            case R.id.ll_new:
                showActionSheet(3);
                break;
        }
    }

    @Override
    public void update2DeleteCircle(String circleId) {
        ToastUtil.showShort(this,"删除本item");
    }

    @Override
    public void update2AddFavorite(int circlePosition, FavortItem addItem) {
        ToastUtil.showShort(this,"点赞");
    }

    @Override
    public void update2DeleteFavort(int circlePosition, String favortId) {
        ToastUtil.showShort(this,"取消点赞");
    }

    @Override
    public void update2AddComment(int circlePosition, FriendCircleItem addItem) {
        ToastUtil.showShort(this,"评论");
    }

    @Override
    public void update2DeleteComment(int circlePosition, String commentId) {
        ToastUtil.showShort(this,"删除评论");
    }

    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        ToastUtil.showShort(this,"评论--键盘--" + visibility + ":" + commentConfig.toString());
    }

    @Override
    public void update2loadData(int loadType, List<CircleItem> datas) {

    }

    private void showActionSheet(int type) {
        actionSheetType = type;
        ActionSheet actionSheet = new ActionSheet(FriendCircleActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        if (type == 1){//发朋友圈
            actionSheet.addSheetItem(getString(R.string.small_video), ActionSheet.SheetItemColor.Black,
                    FriendCircleActivity.this)
                    .addSheetItem(getString(R.string.take_photos), ActionSheet.SheetItemColor.Black,
                            FriendCircleActivity.this)
                    .addSheetItem(getString(R.string.from_the_mobile_phone_photo_album_choice), ActionSheet.SheetItemColor.Black,
                            FriendCircleActivity.this);
        }else if(type == 2){//更换相册封面
            actionSheet.addSheetItem(getString(R.string.replace_the_album_cover),ActionSheet.SheetItemColor.Black,FriendCircleActivity.this);
        }else if(type == 3){//消息列表
            actionSheet.addSheetItem(getString(R.string.message_list),ActionSheet.SheetItemColor.Black,FriendCircleActivity.this);
        }
        actionSheet.show();
    }


    @Override
    public void onClick(int which) {
        switch (which){
            case 1://1小视频 or 2更换相册封面 or 3消息列表
                if (actionSheetType == 1){

                }else if (actionSheetType == 2){
                    openActivity(ReplaceAlbumCoverActivity.class);
                }else if(actionSheetType == 3){
                    openActivity(CircleMessageListActivity.class);
                }
                break;
            case 2://拍照
                break;
            case 3://从手机相册选择
                break;
        }
    }
}