package im.boss66.com.activity.connection;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.ABaseActivity;
import im.boss66.com.R;

/**
 * Created by liw on 2017/2/22.
 */
public class SchoolClubActivity extends ABaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_club);
        initViews();
    }

    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("社团");
        tv_headlift_view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
            break;
        }
    }
}
