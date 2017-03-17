package im.boss66.com.activity.treasure;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.treasure.CatchFuwaActivity;
import im.boss66.com.activity.treasure.ChooseFuwaHideActivity;
import im.boss66.com.activity.treasure.HideFuwaActivity;

/**
 * Created by Johnny on 2017/3/13
 * 寻宝首页.
 */
public class MainTreasureActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_command, tv_rank;
    private ImageView iv_msg, iv_bag, iv_trade;
    private Button btn_find, btn_store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_treasure);
        initViews();
    }

    private void initViews() {
        tv_command = (TextView) findViewById(R.id.tv_command);
        tv_rank = (TextView) findViewById(R.id.tv_rank);

        iv_msg = (ImageView) findViewById(R.id.iv_msg);
        iv_bag = (ImageView) findViewById(R.id.iv_bag);
        iv_trade = (ImageView) findViewById(R.id.iv_trade);

        btn_find = (Button) findViewById(R.id.btn_find);
        btn_store = (Button) findViewById(R.id.btn_store);

        tv_command.setOnClickListener(this);
        tv_rank.setOnClickListener(this);
        iv_msg.setOnClickListener(this);
        iv_bag.setOnClickListener(this);
        iv_trade.setOnClickListener(this);
        btn_find.setOnClickListener(this);
        btn_store.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_command://我的口令

                break;
            case R.id.tv_rank://福娃排行榜

                break;
            case R.id.iv_msg://消息
                openActivity(AroundPosActivity.class);
                break;
            case R.id.iv_bag://背包

                break;
            case R.id.iv_trade://交易

                break;
            case R.id.btn_find://找福娃
                //openActivity(FindTreasureChildrenActivity.class);
                openActivity(CatchFuwaActivity.class);
                break;
            case R.id.btn_store://藏福娃
                openActivity(HideFuwaActivity.class);
                break;
        }
    }
}
