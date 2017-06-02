package im.boss66.com.activity.treasure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import im.boss66.com.R;
import im.boss66.com.Utils.PermissonUtil.PermissionUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.fragment.VideoListFragment;
import im.boss66.com.listener.PermissionListener;

/**
 * 找萌友视频推荐
 * Created by liw on 2017/5/31.
 */

public class PersonalVideoActivity extends ABaseActivity implements View.OnClickListener {
    private VideoListFragment fragment;


    private PermissionListener permissionListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_video);
        initUI();
        getPermission();


    }


    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
               initFragment();
            }



            @Override
            public void onRequestPermissionError() {
                ToastUtil.showShort(PersonalVideoActivity.this, "请给予定位权限");
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_GROUP_LOACATION //定位授权
                ).request(permissionListener);
    }

    private void initFragment() {
        fragment = VideoListFragment.newInstance("i");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_content, fragment).show(fragment).commitAllowingStateLoss(); //防止崩溃
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
    }

    private void initUI() {
        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        findViewById(R.id.iv_headright_view).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.iv_headright_view:

                Intent intent = new Intent(context, FindTreasureChildrenActivity.class);
                intent.putExtra("isFate", true);
                startActivity(intent);

                break;
        }
    }
}
