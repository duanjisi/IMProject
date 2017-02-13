package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelector;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.discover.CircleMessageListActivity;
import im.boss66.com.adapter.PersonalPhotoAlbumAdapter;
import im.boss66.com.entity.FriendCircleTestData;
import im.boss66.com.entity.PersonalPhotoAlbumItem;
import im.boss66.com.widget.ActionSheet;

/**
 * 个人相册
 */
public class PersonalPhotoAlbumActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private TextView tv_back,tv_title,tv_signature;
    private ImageView iv_set;
    private LRecyclerView rv_friend;
    private LinearLayout ll_personal;
    private PersonalPhotoAlbumAdapter adapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_photo_album);
        initView();
    }

    private void initView() {
        tv_signature = (TextView) findViewById(R.id.tv_signature);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_set = (ImageView) findViewById(R.id.iv_set);
        rv_friend = (LRecyclerView) findViewById(R.id.rv_friend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_friend.setLayoutManager(layoutManager);
        iv_set.setOnClickListener(this);
        iv_set.setImageResource(R.drawable.hp_chat_more);
        iv_set.setVisibility(View.VISIBLE);
        tv_back.setOnClickListener(this);

        View header = LayoutInflater.from(this).inflate(R.layout.item_friend_circle_head,
                (ViewGroup)findViewById(android.R.id.content), false);
        ll_personal = (LinearLayout)header.findViewById(R.id.ll_personal);
        tv_signature = (TextView) header.findViewById(R.id.tv_signature);
        ImageView iv_bg = (ImageView) header.findViewById(R.id.iv_bg);
        FrameLayout.LayoutParams linearParams =(FrameLayout.LayoutParams) iv_bg.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = UIUtils.getScreenWidth(context)/3*2;;
        iv_bg.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        ll_personal.setVisibility(View.VISIBLE);
        tv_signature.setVisibility(View.VISIBLE);

        adapter = new PersonalPhotoAlbumAdapter(this);
        List<PersonalPhotoAlbumItem> list = FriendCircleTestData.createPhotoAlbum();
        adapter.setDatas(list);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerViewAdapter.addHeaderView(header);
        rv_friend.setAdapter(mLRecyclerViewAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_set:
                //showActionSheet();
                MultiImageSelector.create()
                        .showCamera(false) // 是否显示相机. 默认为显示
                        .count(1) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                        .multi() // 多选模式, 默认模式;
                        .start(this, 101);
                break;
        }
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(PersonalPhotoAlbumActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem(getString(R.string.message_list),ActionSheet.SheetItemColor.Black,PersonalPhotoAlbumActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        openActivity(CircleMessageListActivity.class);
    }
}
