package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 设置-照片和视频
 */
public class PersonalSetPhotosVideoActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back,tv_title;
    private ToggleButton tb_photo,tb_video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_set_photos_video);
        initView();
    }

    private void initView() {
        tb_photo = (ToggleButton) findViewById(R.id.tb_photo);
        tb_video = (ToggleButton) findViewById(R.id.tb_video);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.general));
        tv_back.setOnClickListener(this);
        tb_photo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ToastUtil.showShort(PersonalSetPhotosVideoActivity.this,"photo"+b);
            }
        });
        tb_video.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ToastUtil.showShort(PersonalSetPhotosVideoActivity.this,"video"+b);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
        }
    }
}
