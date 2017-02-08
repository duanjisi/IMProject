package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 设置-添加我的方式
 */
public class PersonalSetAddMyWayActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView tv_back, tv_title;
    private ToggleButton sb_phone_num,sb_app_num,sb_group_chat,sb_my_qrcode,sb_business_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_set_add_my_way);
        initView();
    }

    private void initView() {
        sb_phone_num = (ToggleButton) findViewById(R.id.sb_phone_num);
        sb_app_num = (ToggleButton) findViewById(R.id.sb_app_num);
        sb_group_chat = (ToggleButton) findViewById(R.id.sb_group_chat);
        sb_my_qrcode = (ToggleButton) findViewById(R.id.sb_my_qrcode);
        sb_business_card = (ToggleButton) findViewById(R.id.sb_business_card);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.privacy));
        tv_back.setOnClickListener(this);
        sb_phone_num.setOnCheckedChangeListener(this);
        sb_app_num.setOnCheckedChangeListener(this);
        sb_group_chat.setOnCheckedChangeListener(this);
        sb_my_qrcode.setOnCheckedChangeListener(this);
        sb_business_card.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.sb_phone_num:

                break;
            case R.id.sb_app_num:

                break;
            case R.id.sb_group_chat:

                break;
            case R.id.sb_my_qrcode:

                break;
            case R.id.sb_business_card:

                break;
        }
    }
}
