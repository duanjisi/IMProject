package im.boss66.com.activity.treasure;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.Base64Utils;
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

    private Dialog dialog;

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
                if(dialog==null){
                    showFuwaDialog(this);
                }else if(!dialog.isShowing()){
                    dialog.show();
                }

                break;
            case R.id.tv_rank://福娃排行榜
                openActivity(FuwaTopList.class);

                break;
            case R.id.iv_msg://消息
                openActivity(AroundPosActivity.class);
                break;
            case R.id.iv_bag://背包
                openActivity(FuwaPackageActivity.class);
                break;
            case R.id.iv_trade://交易
                openActivity(FuwaDealActivity.class);

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
    //弹第一个福娃dialog
    private void showFuwaDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        View view = inflater.inflate(R.layout.pop_fuwa_word, null);

        dialog = new Dialog(context, R.style.dialog_ios_style);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        TextView tv_word = (TextView) dialog.findViewById(R.id.tv_word);
        tv_word.setText(Base64Utils.encodeBase64(App.getInstance().getUid()));


        //设置dialog大小
        Window dialogWindow = dialog.getWindow();
        WindowManager manager = ((MainTreasureActivity) context).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9，根据实际情况调整
        dialogWindow.setAttributes(params);

        dialog.show();
    }
}
