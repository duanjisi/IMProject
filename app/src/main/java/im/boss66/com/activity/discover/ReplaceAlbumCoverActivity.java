package im.boss66.com.activity.discover;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * 更换相册封面
 */
public class ReplaceAlbumCoverActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_back,tv_title,tv_photo_album,tv_take_photos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_album_cover);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_photo_album = (TextView) findViewById(R.id.tv_photo_album);
        tv_take_photos = (TextView) findViewById(R.id.tv_take_photos);
        tv_title.setText(getString(R.string.replace_the_album_cover));
        tv_back.setOnClickListener(this);
        tv_photo_album.setOnClickListener(this);
        tv_take_photos.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_photo_album://相册

                break;
            case R.id.tv_take_photos://拍照

                break;
        }
    }
}
