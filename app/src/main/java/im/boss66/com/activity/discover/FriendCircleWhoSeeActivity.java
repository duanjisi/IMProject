package im.boss66.com.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.book.SelectContactsActivity;

/**
 * 朋友圈发布限制条件
 */
public class FriendCircleWhoSeeActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_title, tv_right, tv_user_part, tv_user_part_no;
    private RelativeLayout rl_all, rl_privacy, rl_part_see, rl_part_no_see;
    private TextView tv_all_gou, tv_privacy_gou, tv_part_see_gou, tv_part_no_gou;
    private ImageView iv_part_see, iv_part_no;
    private RelativeLayout rl_part_book, rl_part_no_book;
    private TextView[] gouArr;
    private final int SELECT_PERSON = 5;//选择联系人
    private String member_ids,memberUserNames;
    private int whoCanSee = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_circle_who_see);
        initView();
    }

    private void initView() {
        int sceenW = UIUtils.getScreenWidth(this);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        tv_all_gou = (TextView) findViewById(R.id.tv_all_gou);
        tv_privacy_gou = (TextView) findViewById(R.id.tv_privacy_gou);
        tv_part_see_gou = (TextView) findViewById(R.id.tv_part_see_gou);
        tv_part_no_gou = (TextView) findViewById(R.id.tv_part_no_gou);
        gouArr = new TextView[]{tv_all_gou, tv_privacy_gou, tv_part_see_gou, tv_part_no_gou};
        rl_all = (RelativeLayout) findViewById(R.id.rl_all);
        rl_privacy = (RelativeLayout) findViewById(R.id.rl_privacy);
        rl_part_see = (RelativeLayout) findViewById(R.id.rl_part_see);
        rl_part_no_see = (RelativeLayout) findViewById(R.id.rl_part_no_see);
        rl_part_book = (RelativeLayout) findViewById(R.id.rl_part_book);
        rl_part_no_book = (RelativeLayout) findViewById(R.id.rl_part_no_book);
        tv_user_part = (TextView) findViewById(R.id.tv_user_part);
        tv_user_part_no = (TextView) findViewById(R.id.tv_user_part_no);
        LinearLayout.LayoutParams bookParam = (LinearLayout.LayoutParams) rl_part_book.getLayoutParams();
        bookParam.height = sceenW / 8;
        rl_part_book.setLayoutParams(bookParam);

        LinearLayout.LayoutParams nobookParam = (LinearLayout.LayoutParams) rl_part_no_book.getLayoutParams();
        nobookParam.height = sceenW / 8;
        rl_part_no_book.setLayoutParams(nobookParam);

        iv_part_see = (ImageView) findViewById(R.id.iv_part_see);
        iv_part_no = (ImageView) findViewById(R.id.iv_part_no);
        rl_all.setOnClickListener(this);
        rl_privacy.setOnClickListener(this);
        rl_part_see.setOnClickListener(this);
        rl_part_no_see.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        rl_part_no_book.setOnClickListener(this);
        rl_part_book.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        tv_back.setText(getString(R.string.mis_permission_dialog_cancel));
        tv_title.setText(getString(R.string.who_can_see));
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setText(getString(R.string.mis_action_done));
        showItem(0);
    }

    private void showItem(int type) {
        for (int i = 0; i < gouArr.length; i++) {
            gouArr[i].setVisibility(View.INVISIBLE);
        }
        gouArr[type].setVisibility(View.VISIBLE);
        iv_part_see.setImageResource(R.drawable.hp_some_down);
        iv_part_no.setImageResource(R.drawable.hp_some_down);
        rl_part_book.setVisibility(View.GONE);
        rl_part_no_book.setVisibility(View.GONE);
        if (type == 2) {
            rl_part_book.setVisibility(View.VISIBLE);
            iv_part_see.setImageResource(R.drawable.hp_some_up);
            tv_user_part_no.setText("");
            tv_user_part_no.setVisibility(View.GONE);
        } else if (type == 3) {
            rl_part_no_book.setVisibility(View.VISIBLE);
            iv_part_no.setImageResource(R.drawable.hp_some_up);
            tv_user_part.setText("");
            tv_user_part.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_all:
                whoCanSee = 0;
                showItem(whoCanSee);
                break;
            case R.id.rl_privacy:
                whoCanSee = 1;
                showItem(whoCanSee);
                break;
            case R.id.rl_part_see:
                whoCanSee = 2;
                showItem(whoCanSee);
                break;
            case R.id.rl_part_no_see:
                whoCanSee = 3;
                showItem(whoCanSee);
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_part_book:
                Bundle bundle = new Bundle();
                bundle.putString("classType", "FriendCircleWhoSeeActivity");
                bundle.putString("user_ids",member_ids);
                openActvityForResult(SelectContactsActivity.class, SELECT_PERSON, bundle);
                break;
            case R.id.rl_part_no_book:
                Bundle bundle1 = new Bundle();
                bundle1.putString("user_ids",member_ids);
                bundle1.putString("classType", "FriendCircleWhoSeeActivity");
                openActvityForResult(SelectContactsActivity.class, SELECT_PERSON, bundle1);
                break;
            case R.id.tv_right:
                Intent intent = new Intent();
                intent.putExtra("member_id", member_ids);
                intent.putExtra("memberUserNames",memberUserNames);
                intent.putExtra("whoCanSee",whoCanSee);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PERSON && resultCode == RESULT_OK && data != null) {
            member_ids = data.getStringExtra("member_id");
            memberUserNames = data.getStringExtra("memberUserNames");
            if (!TextUtils.isEmpty(memberUserNames)) {
                if (whoCanSee == 2) {
                    tv_user_part.setVisibility(View.VISIBLE);
                    tv_user_part.setText(memberUserNames);
                } else if (whoCanSee == 3) {
                    tv_user_part_no.setVisibility(View.VISIBLE);
                    tv_user_part_no.setText(memberUserNames);
                }
            }
        }
    }
}
