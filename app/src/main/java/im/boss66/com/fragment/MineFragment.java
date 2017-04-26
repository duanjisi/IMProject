package im.boss66.com.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.activity.im.EmojiSelectWellActivity;
import im.boss66.com.activity.personage.PersonalCollectActivity;
import im.boss66.com.activity.personage.PersonalInformationActivity;
import im.boss66.com.activity.personage.PersonalPhotoAlbumActivity;
import im.boss66.com.activity.personage.PersonalSetActivity;
import im.boss66.com.activity.personage.WalletActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.ActionEntity;

/**
 * 个人中心
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    private RelativeLayout rl_information, rl_photo, rl_collect, rl_wallet, rl_expression, rl_set;
    private ImageView iv_head;
    private TextView tv_name, tv_number;
    private ImageLoader imageLoader;
    private AccountEntity sAccount;
    private ImageView iv_avatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initData();
    }

    private void initData() {
        imageLoader = ImageLoaderUtils.createImageLoader(getActivity());
        showInfo();
    }

    private void showInfo() {
        sAccount = App.getInstance().getAccount();
        if (sAccount != null) {
            String head = sAccount.getAvatar();
            if (!TextUtils.isEmpty(head)) {
                imageLoader.displayImage(head, iv_head,
                        ImageLoaderUtils.getDisplayImageOptions());
            }
            String userid = sAccount.getUser_id();
            String username = sAccount.getUser_name();
            if (!TextUtils.isEmpty(username)) {
                tv_name.setText(username);
            } else {
                tv_name.setText("" + userid);
            }
//            imageLoader.displayImage(sAccount.getAvatar(), iv_avatar, ImageLoaderUtils.getDisplayImageOptions());
        }
    }

    private void initViews(View view) {
        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        rl_information = (RelativeLayout) view.findViewById(R.id.rl_information);
        rl_photo = (RelativeLayout) view.findViewById(R.id.rl_photo);
        rl_collect = (RelativeLayout) view.findViewById(R.id.rl_collect);
        rl_wallet = (RelativeLayout) view.findViewById(R.id.rl_wallet);
        rl_expression = (RelativeLayout) view.findViewById(R.id.rl_expression);
        rl_set = (RelativeLayout) view.findViewById(R.id.rl_set);
        iv_head = (ImageView) view.findViewById(R.id.iv_head);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_number = (TextView) view.findViewById(R.id.tv_number);
        rl_information.setOnClickListener(this);
        rl_photo.setOnClickListener(this);
        rl_collect.setOnClickListener(this);
        rl_wallet.setOnClickListener(this);
        rl_expression.setOnClickListener(this);
        rl_set.setOnClickListener(this);
        iv_avatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_information://个人信息
                openActvityForResult(PersonalInformationActivity.class, 101);
                break;
            case R.id.rl_photo://相册
                Bundle bundle = new Bundle();
                bundle.putBoolean("isSelt", true);
                openActivity(PersonalPhotoAlbumActivity.class, bundle);
                break;
            case R.id.rl_collect://收藏
                openActivity(PersonalCollectActivity.class, null);
                break;
            case R.id.rl_wallet://钱包
                Intent intent0 = new Intent(getActivity(), WalletActivity.class);
                startActivity(intent0);
                break;
            case R.id.rl_expression://表情
                openActivity(EmojiSelectWellActivity.class, null);
                break;
            case R.id.rl_set://设置
                openActivity(PersonalSetActivity.class, null);
                break;
            case R.id.iv_avatar:
                EventBus.getDefault().post(new ActionEntity(Constants.Action.MENU_CAHNGE_CURRENT_TAB));
                break;
        }
    }

//    public void openActivity(Class<?> clazz, Bundle bundle) {
//        Intent intent = new Intent(getActivity(), clazz);
//        if (bundle != null) {
//            intent.putExtras(bundle);
//        }
//        startActivity(intent);
//    }

    public void openActvityForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 102 && data != null) {
            boolean isChange = data.getBooleanExtra("isChange", false);
            if (isChange) {
                showInfo();
            }
        }
    }
}
