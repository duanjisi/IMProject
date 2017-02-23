package im.boss66.com.activity.connection;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.ABaseActivity;
import im.boss66.com.R;
import im.boss66.com.adapter.MyMessageAdapter;
import im.boss66.com.entity.MyMessage;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * 我的消息
 * Created by liw on 2017/2/22.
 */
public class MyMessageActivity extends ABaseActivity implements View.OnClickListener {

    private TextView tv_headright_view;
    private LRecyclerView rcv_my_message;
    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private MyMessageAdapter adapter;

    private List<MyMessage> datas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);

        initlist();
        initViews();

    }

    private void initlist() {
        datas = new ArrayList<>();
        for(int i =0; i<3;i++){
            MyMessage myMessage = new MyMessage();
            myMessage.setImg1("http://touxiang.qqzhi.com/uploads/2012-11/1111104151660.jpg");
            myMessage.setImg2("http://touxiang.qqzhi.com/uploads/2012-11/1111113307592.jpg");
            myMessage.setTv1("ying Zi");
            myMessage.setTv2("评论了你");
            myMessage.setTv3("2017.2.23");
            datas.add(myMessage);
        }
        Log.i("liw", datas.toString());


    }

    private void initViews() {
        tv_headcenter_view = (TextView) findViewById(R.id.tv_headcenter_view);
        tv_headcenter_view.setText("我的消息");
        tv_headlift_view = (TextView) findViewById(R.id.tv_headlift_view);
        tv_headlift_view.setOnClickListener(this);

        tv_headright_view = (TextView) findViewById(R.id.tv_headright_view);
        tv_headright_view.setOnClickListener(this);

        rcv_my_message = (LRecyclerView) findViewById(R.id.rcv_my_message);
        rcv_my_message.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyMessageAdapter(this);
        adapter.setDatas(datas);
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {

            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);

        rcv_my_message.setAdapter(mLRecyclerViewAdapter);
        rcv_my_message.setLoadMoreEnabled(true);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_headlift_view:
                finish();
                break;
            case R.id.tv_headright_view:
                showToast("清空",false);
                break;

        }
    }
}
