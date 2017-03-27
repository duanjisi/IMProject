package im.boss66.com.activity.im;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.MakeQRCodeUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/2/14.
 * 群二维码
 */
public class ChatGroupCodeActivity extends BaseActivity {

    private ImageLoader imageLoader;
    private TextView tvBack, tvName;
    private ImageView ivMore, ivIcon, ivCode;
    private int screenW;
    private String groupid;
    private String name, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group_code);
        initViews();
    }

    private void initViews() {
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupid = bundle.getString("groupid", "");
            name = bundle.getString("name", "");
            imageUrl = bundle.getString("snap", "");
        }
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

        tvName.setText(name);
        if (imageUrl != null && !imageUrl.equals("")) {
            imageLoader.displayImage(imageUrl, ivIcon, ImageLoaderUtils.getDisplayImageOptions());
        }
        if (!groupid.equals("")) {
            MakeQRCodeUtil.createQRImage("https://api.66boss.com/web/download?gid=" + groupid, screenW, screenW, ivCode);
        }
    }

}
