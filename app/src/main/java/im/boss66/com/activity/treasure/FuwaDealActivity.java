package im.boss66.com.activity.treasure;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.FuwaSellAdapter;
import im.boss66.com.fragment.FuwaMySellFragment;
import im.boss66.com.fragment.FuwaSellFragment;

/**
 * Created by liw on 2017/3/13.
 */

public class FuwaDealActivity extends BaseActivity implements View.OnClickListener {
    private FragmentTransaction fragmentTransaction;
    private FuwaSellFragment fuwaSellFragment;
    private FuwaMySellFragment fuwaMySellFragment;
    private View view_lift;
    private View view_right;

    private TextView tv_lift;
    private TextView tv_right;
    private TextView tv_edit;
    private FragmentManager supportFragmentManager;
    private boolean flag;
    private FuwaSellAdapter adapter;

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

        view_lift = findViewById(R.id.view_lift);
        view_right = findViewById(R.id.view_right);

        tv_lift = (TextView) findViewById(R.id.tv_lift);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        tv_edit.setOnClickListener(this);

        fuwaSellFragment = new FuwaSellFragment();
        fuwaMySellFragment = new FuwaMySellFragment();

        supportFragmentManager = getSupportFragmentManager();
        fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.view_content, fuwaSellFragment, fuwaSellFragment.getClass().getSimpleName());
        fragmentTransaction.add(R.id.view_content, fuwaMySellFragment, fuwaMySellFragment.getClass().getSimpleName());

        fragmentTransaction.hide(fuwaSellFragment).hide(fuwaMySellFragment);
        fragmentTransaction.show(fuwaSellFragment).commit();


    }

    @Override
    public void onClick(View view) {
        fragmentTransaction = supportFragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.ll_lift:
                tv_edit.setVisibility(View.GONE);
                tv_lift.setTextColor(0xffe7d09a);
                tv_right.setTextColor(0xff8d1800);
                view_right.setVisibility(View.INVISIBLE);
                view_lift.setVisibility(View.VISIBLE);


                fragmentTransaction.hide(fuwaSellFragment).hide(fuwaMySellFragment);
                fragmentTransaction.show(fuwaSellFragment).commit();


                break;
            case R.id.ll_right:
                tv_edit.setVisibility(View.VISIBLE);
                tv_lift.setTextColor(0xff8d1800);
                tv_right.setTextColor(0xffe7d09a);

                view_lift.setVisibility(View.INVISIBLE);
                view_right.setVisibility(View.VISIBLE);


                fragmentTransaction.hide(fuwaSellFragment).hide(fuwaMySellFragment);
                fragmentTransaction.show(fuwaMySellFragment).commit();

                break;
            case R.id.tv_edit:

                if (adapter == null) {
                    adapter = fuwaMySellFragment.getAdapter();
                }

                if (!flag) {
                    tv_edit.setText("取消");
                    adapter.edit = true;
                    adapter.notifyDataSetChanged();
                    flag = true;
                } else {
                    tv_edit.setText("编辑");
                    adapter.edit = false;
                    adapter.notifyDataSetChanged();
                    flag = false;
                }

                break;

        }
    }
}
