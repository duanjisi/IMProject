package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.CircleImageView;

/**
 * 个人信息
 */
public class PersonalInformationActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_title,tv_back;
    private RelativeLayout rl_head_icon,rl_name,rl_qr_code,rl_sex,rl_area,rl_signature;
    private ImageView iv_head;
    private TextView tv_name,tv_number,tv_sex,tv_area,tv_signature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infomation);
        initView();
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_back = (TextView) findViewById(R.id.tv_back);
        rl_head_icon = (RelativeLayout) findViewById(R.id.rl_head_icon);
        rl_name = (RelativeLayout) findViewById(R.id.rl_name);
        rl_qr_code = (RelativeLayout) findViewById(R.id.rl_qr_code);
        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        rl_area = (RelativeLayout) findViewById(R.id.rl_area);
        rl_signature = (RelativeLayout) findViewById(R.id.rl_signature);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_signature = (TextView) findViewById(R.id.tv_signature);
        tv_title.setText("个人信息");
        tv_back.setOnClickListener(this);
        rl_head_icon.setOnClickListener(this);
        rl_name.setOnClickListener(this);
        rl_qr_code.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_area.setOnClickListener(this);
        rl_signature.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_head_icon://头像
                break;
            case R.id.rl_name://名字
                break;
            case R.id.rl_qr_code://二维码
                break;
            case R.id.rl_sex://性别
                break;
            case R.id.rl_area://地区
                break;
            case R.id.rl_signature://个性签名
                break;
        }
    }
}
