package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 *个人隐私设置
 */
public class PersonalPrivacyActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private TextView tv_back,tv_title;
    private ToggleButton sb_add_friend_verify,sb_recommended_friends,sb_allow_stranger,sb_only_show_friends;
    private RelativeLayout rl_add_my_type,rl_address_book_list,rl_donot_let_him_see,rl_donot_see_him;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_privacy);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        sb_add_friend_verify = (ToggleButton) findViewById(R.id.sb_add_friend_verify);
        sb_recommended_friends = (ToggleButton) findViewById(R.id.sb_recommended_friends);
        sb_allow_stranger = (ToggleButton) findViewById(R.id.sb_allow_stranger);
        sb_only_show_friends = (ToggleButton) findViewById(R.id.sb_only_show_friends);
        rl_add_my_type = (RelativeLayout) findViewById(R.id.rl_add_my_type);
        rl_address_book_list = (RelativeLayout) findViewById(R.id.rl_address_book_list);
        rl_donot_let_him_see = (RelativeLayout) findViewById(R.id.rl_donot_let_him_see);
        rl_donot_see_him = (RelativeLayout) findViewById(R.id.rl_donot_see_him);
        tv_back.setOnClickListener(this);
        tv_title.setText(getString(R.string.privacy));
        rl_add_my_type.setOnClickListener(this);
        rl_address_book_list.setOnClickListener(this);
        rl_donot_let_him_see.setOnClickListener(this);
        rl_donot_see_him.setOnClickListener(this);
        sb_add_friend_verify.setOnCheckedChangeListener(this);
        sb_recommended_friends.setOnCheckedChangeListener(this);
        sb_allow_stranger.setOnCheckedChangeListener(this);
        sb_only_show_friends.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_add_my_type://添加我的方式
                openActivity(PersonalSetAddMyWayActivity.class);
                break;
            case R.id.rl_address_book_list://通讯录黑名单
                break;
            case R.id.rl_donot_let_him_see://不让他（她）看我的朋友圈
                break;
            case R.id.rl_donot_see_him://不看他（她）的朋友圈
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.sb_add_friend_verify://加我为朋友时需要验证

                break;
            case R.id.sb_recommended_friends://向我推荐通讯录朋友

                break;
            case R.id.sb_allow_stranger://允许陌生人查看十条朋友圈

                break;
            case R.id.sb_only_show_friends://仅向朋友展示最近半年的朋友圈

                break;
        }
    }
}
