package im.boss66.com.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/1/21.
 */
public class PersonPhotoAlbumActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvBack, tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_photo);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:

                break;
        }
    }
}
