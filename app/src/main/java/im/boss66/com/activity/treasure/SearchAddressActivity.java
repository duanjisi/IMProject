package im.boss66.com.activity.treasure;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.widget.ClearEditText;

/**
 * Created by GMARUnity on 2017/3/18.
 */
public class SearchAddressActivity extends BaseActivity implements View.OnClickListener {

    private ClearEditText et_name;
    private TextView tv_close;
    private RecyclerView rv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
        initView();
    }

    private void initView() {
        et_name = (ClearEditText) findViewById(R.id.et_name);
        tv_close = (TextView) findViewById(R.id.tv_close);
        rv_content = (RecyclerView) findViewById(R.id.rv_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_content.setLayoutManager(layoutManager);
        tv_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
        }
    }
}
