package im.boss66.com.activity.connection;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.ABaseActivity;

/**
 * Created by liw on 2017/4/17.
 */
public class EditTextActivity extends ABaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);

        initViews();
    }

    private void initViews() {

        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("编辑");
        findViewById(R.id.tv_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_ok:

                break;
        }
    }
}
