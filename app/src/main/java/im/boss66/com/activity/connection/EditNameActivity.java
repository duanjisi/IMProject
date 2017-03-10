package im.boss66.com.activity.connection;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by admin on 2017/3/2.
 */
public class EditNameActivity extends BaseActivity implements View.OnClickListener {
    private EditText et_name;
    private TextView tv_headright_view,tv_headlift_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        initViews();
    }

    private void initViews() {
        et_name = (EditText) findViewById(R.id.et_name);
        tv_headright_view = (TextView) findViewById(R.id.tv_headright_view);
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);
        tv_headright_view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_headright_view: //完成
                if(TextUtils.isEmpty(et_name.getText().toString())){
                    ToastUtil.showShort(context,"请您填写姓名");
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("name",et_name.getText().toString());
                    setResult(1,intent);
                    finish();
                }


                break;

        }
    }

}
