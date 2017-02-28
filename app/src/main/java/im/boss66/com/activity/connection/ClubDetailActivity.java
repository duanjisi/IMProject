package im.boss66.com.activity.connection;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;

import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;

/**
 * Created by liw on 2017/2/23.
 */
public class ClubDetailActivity extends ABaseActivity implements View.OnClickListener {

    private TextView tv_location;

    private OptionsPickerView pvOptions; // 三级联动

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_club_detail);

        initOptionData();
        initOpthinPicker();

        initViews();
    }
    private void initOptionData() {


    }
    private void initOpthinPicker() {

    }



    private void initViews() {
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("club");
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);

        tv_location  = (TextView) findViewById(R.id.tv_location);
        tv_location.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;

            case R.id.tv_location:

                break;
        }
    }
}
