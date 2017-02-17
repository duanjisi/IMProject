package im.boss66.com.activity.discover;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.NearByChildEntity;
import im.boss66.com.entity.PeopleNearbyEntity;

/**
 * 附近的人详细资料
 */
public class PersonalNearbyDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_name, tv_sex, tv_distance, tv_set_notes_labels,
            tv_area, tv_personalized_signature, tv_source;
    private ImageView iv_set, iv_head;
    private Button bt_greet,bt_complaint;
    private ImageLoader imageLoader;
    private LinearLayout ll_area,rl_privacy,rl_general;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_nearby_detail);
        initView();
        initData();
    }

    private void initData() {
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        if (getIntent() != null){
            Bundle bundle = getIntent().getExtras();
            if (bundle != null){
                String classType = bundle.getString("classType");
                if (!TextUtils.isEmpty(classType)){
                    if ("SharkItOffActivity".equals(classType)){
                        tv_source.setText("来自摇一摇");
                    }else if("PeopleNearbyActivity".equals(classType)){
                        tv_source.setText("附近的人");
                    }
                }
                NearByChildEntity item = (NearByChildEntity) bundle.getSerializable("people");
                if (item != null){
                    tv_name.setText("" + item.getUser_name());
                    int sex = item.getSex();
                    if (sex == 1){
                        tv_sex.setText("" + "男");
                    }else if(sex == 2){
                        tv_sex.setText("" + "女");
                    }
                    imageLoader.displayImage(item.getAvatar(),iv_head,
                            ImageLoaderUtils.getDisplayImageOptions());
                    int dis = item.getDistance();
                    tv_distance.setText(""+dis + "米以内");
                    rl_general.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void initView() {
        rl_privacy = (LinearLayout) findViewById(R.id.rl_privacy);
        rl_general = (LinearLayout) findViewById(R.id.rl_general);
        ll_area = (LinearLayout) findViewById(R.id.ll_area);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_set_notes_labels = (TextView) findViewById(R.id.tv_set_notes_labels);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_personalized_signature = (TextView) findViewById(R.id.tv_personalized_signature);
        tv_source = (TextView) findViewById(R.id.tv_source);

        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_set = (ImageView) findViewById(R.id.iv_set);

        iv_head = (ImageView) findViewById(R.id.iv_head);
        bt_greet = (Button) findViewById(R.id.bt_greet);
        bt_complaint = (Button) findViewById(R.id.bt_complaint);

        iv_set.setImageResource(R.drawable.hp_chat_more);
        tv_back.setOnClickListener(this);
        iv_set.setVisibility(View.VISIBLE);
        iv_set.setOnClickListener(this);

        iv_head.setOnClickListener(this);
        tv_set_notes_labels.setOnClickListener(this);
        bt_greet.setOnClickListener(this);
        bt_complaint.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_set:

                break;
            case R.id.iv_head://头像
                break;
            case R.id.tv_set_notes_labels://设置标签
                break;
            case R.id.bt_greet://打招呼

                break;
            case R.id.bt_complaint://投诉
                break;
        }
    }
}
