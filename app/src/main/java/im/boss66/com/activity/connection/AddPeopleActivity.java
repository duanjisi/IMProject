package im.boss66.com.activity.connection;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import im.boss66.com.activity.base.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.adapter.PeopleSearchAdapter;

/**
 * 添加人脉
 * Created by liw on 2017/2/21.
 */
public class AddPeopleActivity extends ABaseActivity implements View.OnClickListener {

    private TextView tv_cancle_search;

    private RecyclerView rcv_search;
    private PeopleSearchAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);

        initViews();

    }



    private void initViews() {
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("查找人脉");
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);


        tv_cancle_search = (TextView) findViewById(R.id.tv_cancle_search);
        tv_cancle_search.setOnClickListener(this);

        rcv_search = (RecyclerView) findViewById(R.id.rcv_search);
        rcv_search.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PeopleSearchAdapter(this);
        rcv_search.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;

            case R.id.tv_cancle_search:

                break;

        }

    }
}
