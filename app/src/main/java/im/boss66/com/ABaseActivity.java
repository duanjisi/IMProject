package im.boss66.com;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by admin on 2017/2/21.
 */
public class ABaseActivity extends BaseActivity {

    protected TextView tv_headlift_view;
    protected TextView tv_headcenter_view;
    protected ImageView iv_headright_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected void goBack(){
        finish();
    }


}
