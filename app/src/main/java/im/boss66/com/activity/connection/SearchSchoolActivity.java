package im.boss66.com.activity.connection;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by admin on 2017/3/2.
 */
public class SearchSchoolActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_headright_view,tv_headlift_view;
    private EditText et_school;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_school);

        initViews();
    }

    private void initViews() {

        tv_headlift_view  = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headright_view  = (TextView) findViewById(R.id.tv_headright_view);
        tv_headlift_view.setOnClickListener(this);
        tv_headright_view.setOnClickListener(this);

        et_school = (EditText) findViewById(R.id.et_school);
        Editable ea = et_school.getText();
        et_school.setSelection(ea.length());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_headright_view:
                break;
        }
    }
}
