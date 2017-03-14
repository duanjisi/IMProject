package im.boss66.com.activity.connection;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.fragment.FuwaMySellFragment;
import im.boss66.com.fragment.FuwaSellFragment;
/**
 * Created by liw on 2017/3/13.
 */

public class FuwaDealActivity extends BaseActivity implements View.OnClickListener {
    private View view_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuwa_deal);
        initViews();

    }

    private void initViews() {
        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        findViewById(R.id.ll_lift).setOnClickListener(this);
        findViewById(R.id.ll_right).setOnClickListener(this);

        view_content = findViewById(R.id.view_content);

        FuwaSellFragment fuwaSellFragment = new FuwaSellFragment();
        FuwaMySellFragment fuwaMySellFragment = new FuwaMySellFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.view_content,fuwaSellFragment,fuwaSellFragment.getClass().getSimpleName()).commit();
        fragmentTransaction.add(R.id.view_content,fuwaMySellFragment,fuwaMySellFragment.getClass().getSimpleName()).commit();
        fragmentTransaction.show(fuwaSellFragment);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.ll_lift:
                break;
            case R.id.ll_right:
                break;

        }
    }
}
