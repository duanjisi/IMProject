package im.boss66.com.activity.personage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.PhotoAlbumUtil.MultiImageSelector;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.discover.CircleMessageListActivity;
import im.boss66.com.activity.discover.ReplaceAlbumCoverActivity;
import im.boss66.com.adapter.PersonalPhotoAlbumAdapter;
import im.boss66.com.entity.FriendCircleTestData;
import im.boss66.com.entity.PersonalPhotoAlbumItem;
import im.boss66.com.widget.ActionSheet;

/**
 * 个人相册
 */
public class PersonalPhotoAlbumActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private TextView tv_back, tv_title, tv_signature;
    private ImageView iv_set;
    private LRecyclerView rv_friend;
    private LinearLayout ll_personal;
    private PersonalPhotoAlbumAdapter adapter;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private boolean isChangeIcon = false;
    private int ALBUM_COVER = 101;
    private ImageLoader imageLoader;
    private ImageView iv_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_photo_album);
        initView();
    }

    private void initView() {
        imageLoader = ImageLoaderUtils.createImageLoader(this);
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
                (ViewGroup) findViewById(android.R.id.content), false);
        ll_personal = (LinearLayout) header.findViewById(R.id.ll_personal);
        tv_signature = (TextView) header.findViewById(R.id.tv_signature);
        iv_bg = (ImageView) header.findViewById(R.id.iv_bg);
        ImageView iv_head = (ImageView) header.findViewById(R.id.iv_head);
        FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) iv_bg.getLayoutParams(); //取控件textView当前的布局参数
        linearParams.height = UIUtils.getScreenWidth(context) / 3 * 2;
        iv_bg.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
        SharedPreferences mPreferences = context.getSharedPreferences("albumCover", MODE_PRIVATE);
        if (mPreferences != null) {
            String albumCover = mPreferences.getString("albumCover", "");
            if (!TextUtils.isEmpty(albumCover)) {
                imageLoader.displayImage(albumCover, iv_bg,
                        ImageLoaderUtils.getDisplayImageOptions());
            }
        }

        String avatar = App.getInstance().getAccount().getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            imageLoader.displayImage(avatar, iv_head,
                    ImageLoaderUtils.getDisplayImageOptions());
        }
        ll_personal.setVisibility(View.VISIBLE);
        tv_signature.setVisibility(View.VISIBLE);

        adapter = new PersonalPhotoAlbumAdapter(this);
        List<PersonalPhotoAlbumItem> list = FriendCircleTestData.createPhotoAlbum();
        adapter.setDatas(list);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        mLRecyclerViewAdapter.addHeaderView(header);
        rv_friend.setAdapter(mLRecyclerViewAdapter);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChangeIcon = true;
                showActionSheet();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_set:
                isChangeIcon = false;
                showActionSheet();
                break;
        }
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(PersonalPhotoAlbumActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        if (isChangeIcon) {
            actionSheet.addSheetItem(getString(R.string.replace_the_album_cover), ActionSheet.SheetItemColor.Black, PersonalPhotoAlbumActivity.this);
        } else {
            actionSheet.addSheetItem(getString(R.string.message_list), ActionSheet.SheetItemColor.Black, PersonalPhotoAlbumActivity.this);
        }
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        if (isChangeIcon) {
            openActvityForResult(ReplaceAlbumCoverActivity.class, ALBUM_COVER);
        } else {
            openActivity(CircleMessageListActivity.class);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ALBUM_COVER && resultCode == RESULT_OK && data != null) {
            String albumCover = data.getStringExtra("icon_url");
            if (!TextUtils.isEmpty(albumCover)){
                imageLoader.displayImage(albumCover, iv_bg,
                        ImageLoaderUtils.getDisplayImageOptions());
            }
        }
    }
}
