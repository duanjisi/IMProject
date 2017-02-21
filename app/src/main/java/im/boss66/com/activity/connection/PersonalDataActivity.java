package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import im.boss66.com.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by admin on 2017/2/20.
 */
public class PersonalDataActivity extends ABaseActivity implements View.OnClickListener {

    private TextView tv_comfire;


    private EditText et_data1, et_data2, et_data3, et_data4, et_data5, et_data6, et_data7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        initViews();
    }

    private void initViews() {
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("完善资料");
        tv_headlift_view.setOnClickListener(this);
        tv_comfire = (TextView) findViewById(R.id.tv_comfire);
        tv_comfire.setOnClickListener(this);

        et_data1 = (EditText) findViewById(R.id.et_data1);
        et_data2 = (EditText) findViewById(R.id.et_data2);
        et_data3 = (EditText) findViewById(R.id.et_data3);
        et_data4 = (EditText) findViewById(R.id.et_data4);
        et_data5 = (EditText) findViewById(R.id.et_data5);
        et_data6 = (EditText) findViewById(R.id.et_data6);
        et_data7 = (EditText) findViewById(R.id.et_data7);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view://头部返回键
                finish();
                break;
            case R.id.tv_comfire: //保存
                if (TextUtils.isEmpty(et_data1.getText()) ||
                        TextUtils.isEmpty(et_data2.getText()) ||
                        TextUtils.isEmpty(et_data3.getText()) ||
                        TextUtils.isEmpty(et_data4.getText()) ||
                        TextUtils.isEmpty(et_data5.getText()) ||
                        TextUtils.isEmpty(et_data6.getText()) ||
                        TextUtils.isEmpty(et_data7.getText())) {
                    showToast("请完善内容", false);
                } else {
                    Intent intent = new Intent(this, PersonalData2Activity.class);
                    startActivity(intent);
                }
                break;
        }

    }
}
