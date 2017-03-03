package im.boss66.com.activity.im;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.Utils.MakeQRCodeUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/2/14.
 * 群二维码
 */
public class ChatGroupCodeActivity extends BaseActivity {

    private TextView tvBack, tvName;
    private ImageView ivMore, ivIcon, ivCode;
    private int screenW;
    private String groupid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group_code);
        initViews();
    }

    private void initViews() {
        groupid = getIntent().getExtras().getString("groupid", "");
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvName = (TextView) findViewById(R.id.tv_name);
        screenW = UIUtils.getScreenWidth(context) * 3 / 5;
        ivMore = (ImageView) findViewById(R.id.iv_more);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        ivCode = (ImageView) findViewById(R.id.iv_code);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (!groupid.equals("")) {
            MakeQRCodeUtil.createQRImage("add_group:" + groupid, screenW, screenW, ivCode);
        }
    }

}
