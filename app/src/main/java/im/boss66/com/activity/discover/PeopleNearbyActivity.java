package im.boss66.com.activity.discover;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.PeopleNearbyAdapter;
import im.boss66.com.widget.ActionSheet;

/**
 * 附近的人
 */
public class PeopleNearbyActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnSheetItemClickListener {

    private TextView tv_back;
    private ImageView iv_set;
    private LRecyclerView rv_content;
    private PeopleNearbyAdapter adapter;
    private ActionSheet actionSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_nearby);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        iv_set = (ImageView) findViewById(R.id.iv_set);
        rv_content = (LRecyclerView) findViewById(R.id.rv_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv_content.setLayoutManager(layoutManager);
        List<String> list = new ArrayList<>();
        for (int i = 0;i<15;i++){
            list.add("测试:..."+i);
        }
        adapter = new PeopleNearbyAdapter(this,list);
        LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        rv_content.setAdapter(lRecyclerViewAdapter);
        tv_back.setOnClickListener(this);
        iv_set.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_set:
                showActionSheet();
                break;
        }
    }

    private void showActionSheet() {
        actionSheet = new ActionSheet(PeopleNearbyActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        actionSheet.addSheetItem(getString(R.string.only_look_at_the_girl), ActionSheet.SheetItemColor.Black,
                PeopleNearbyActivity.this)
                .addSheetItem(getString(R.string.only_look_at_the_boy), ActionSheet.SheetItemColor.Black,
                        PeopleNearbyActivity.this)
                .addSheetItem(getString(R.string.see_all), ActionSheet.SheetItemColor.Black,
                        PeopleNearbyActivity.this);
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        ToastUtil.showShort(this,""+which);
    }
}
