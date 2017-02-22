package im.boss66.com.activity.im;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.EmojiInform;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.EmoInformRequest;

/**
 * Created by Johnny on 2017/2/18.
 * 表情组详情
 */
public class EmojiGroupDetailsActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = EmojiSelectWellActivity.class.getSimpleName();
    private TextView tvBack, tvTitle;
    private ImageView ivCover;
    private String packid;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_group_details);
        initViews();
        request();
    }

    private void initViews() {
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        packid = getIntent().getExtras().getString("packid", "");
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvTitle = (TextView) findViewById(R.id.title);
        ivCover = (ImageView) findViewById(R.id.iv_cover);

        tvBack.setOnClickListener(this);
    }

    private void request() {
        if (packid != null && !packid.equals("")) {
            showLoadingDialog();
            EmoInformRequest request = new EmoInformRequest(TAG, packid);
            request.send(new BaseDataRequest.RequestCallback<EmojiInform>() {
                @Override
                public void onSuccess(EmojiInform pojo) {
                    cancelLoadingDialog();
                    bindDatas(pojo);
                }

                @Override
                public void onFailure(String msg) {
                    cancelLoadingDialog();
                }
            });
        }
    }

    private void bindDatas(EmojiInform inform) {
        String cover = inform.getGroup_cover();
        if (!cover.equals("")) {
            imageLoader.displayImage(cover, ivCover, ImageLoaderUtils.getDisplayImageOptions());
        }
        tvTitle.setText(inform.getGroup_name());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
        }
    }
}
