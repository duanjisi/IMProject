package im.boss66.com.activity.personage;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.CollectEntity;
import im.boss66.com.widget.RoundImageView;

/**
 * 收藏詳情
 */
public class CollectDetailActivity extends BaseActivity {

    private RoundImageView riv_head;
    private ImageView iv_content, iv_video_img, iv_video_play;
    private FrameLayout fl_video_dialog_img;
    private TextView tv_name, tv_content, tv_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_detail);
        initView();
    }

    private void initView() {
        fl_video_dialog_img = (FrameLayout) findViewById(R.id.fl_video_dialog_img);
        iv_video_play = (ImageView) findViewById(R.id.iv_video_play);
        iv_content = (ImageView) findViewById(R.id.iv_content);
        riv_head = (RoundImageView) findViewById(R.id.riv_head);
        iv_video_img = (ImageView) findViewById(R.id.iv_video_img);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_time = (TextView) findViewById(R.id.tv_time);
        riv_head.setType(1);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                CollectEntity entity = (CollectEntity) bundle.getSerializable("collect");
                if (entity != null) {
                    String groupName = entity.getGroup_name();
                    String name = entity.getFrom_name();
                    if (TextUtils.isEmpty(groupName) || " ".equals(groupName)) {
                        tv_name.setText(name);
                    } else {
                        tv_name.setText(name + "-" + groupName);
                    }
                    Glide.with(context).load(entity.getThum()).
                            error(R.drawable.zf_default_album_grid_image).into(riv_head);
                    String time = entity.getAdd_time();
                    tv_time.setText(time);
                    String type = entity.getType();
                    switch (type) {
                        case "0":
                            tv_content.setVisibility(View.VISIBLE);
                            fl_video_dialog_img.setVisibility(View.GONE);
                            iv_content.setVisibility(View.GONE);
                            String tx = entity.getText();
                            tv_content.setText(tx);
                            break;
                        case "1":
                            tv_content.setVisibility(View.GONE);
                            fl_video_dialog_img.setVisibility(View.GONE);
                            iv_content.setVisibility(View.VISIBLE);
                            Glide.with(context).load(entity.getUrl()).
                                    error(R.drawable.zf_default_album_grid_image).into(iv_content);
                            break;
                        case "2":
                            tv_content.setVisibility(View.GONE);
                            fl_video_dialog_img.setVisibility(View.VISIBLE);
                            iv_content.setVisibility(View.GONE);
                            Glide.with(context).load(entity.getThum()).
                                    error(R.drawable.zf_default_album_grid_image).into(iv_video_img);
                            break;
                    }
                }
            }
        }
    }
}
