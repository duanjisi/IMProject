package im.boss66.com.activity.treasure;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by GMARUnity on 2017/3/17.
 */
public class FuwaAddressSearchActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_back, tv_title, tv_selecte, tv_right;
    private EditText et_name, et_detail;
    private String curGeohash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuwa_edit_address);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_selecte = (TextView) findViewById(R.id.tv_selecte);
        et_name = (EditText) findViewById(R.id.et_name);
        et_detail = (EditText) findViewById(R.id.et_detail);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setOnClickListener(this);
        tv_back.setOnClickListener(this);
        tv_title.setText("创建当前位置名称");
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String city = bundle.getString("city");
                String area = bundle.getString("area");
                curGeohash = bundle.getString("curGeohash");
                tv_selecte.setText(city + " " + area);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right:
                String name = et_name.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    showToast("位置名称不能为空", false);
                    return;
                }
                String detail = et_detail.getText().toString().trim();
                if (TextUtils.isEmpty(detail)) {
                    showToast("街道门牌信息", false);
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("address", name);
                intent.putExtra("geohash", curGeohash);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }
}
