package im.boss66.com.activity.connection;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.activity.AddFriendActivity;
import im.boss66.com.activity.CaptureActivity;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.activity.book.SelectContactsActivity;
import im.boss66.com.adapter.FuwaListAdaper;
import im.boss66.com.entity.FuwaListEntity;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/3/13.
 */

public class FuwaPackageActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rcv_fuwalist;
    private FuwaListAdaper adaper;


    private List<FuwaListEntity> datas = new ArrayList<>();
    private Dialog dialog;
    private Dialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuwa_package);
        initViews();

        initData();
    }

    private void initData() {

        for (int i = 0; i < 10; i++) {
            FuwaListEntity fuwaListEntity = new FuwaListEntity();
            fuwaListEntity.setS1(i + "");
            fuwaListEntity.setS2(i + "");
            fuwaListEntity.setS3("https://img3.doubanio.com/img/celebrity/medium/11263.jpg");
            datas.add(fuwaListEntity);
        }
    }

    private void initViews() {
        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        rcv_fuwalist = (RecyclerView) findViewById(R.id.rcv_fuwalist);
        rcv_fuwalist.setLayoutManager(new GridLayoutManager(this, 3));
        adaper = new FuwaListAdaper(this);
        adaper.setDatas(datas);
        adaper.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                //弹pop

                if (dialog == null) {
                    showFuwaDialog(context);
                } else if (!dialog.isShowing()) {
                    dialog.show();
                }
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_fuwalist.setAdapter(adaper);
    }


    //弹第一个福娃dialog
    private void showFuwaDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        View view = inflater.inflate(R.layout.pop_fuwa_item, null);

        dialog = new Dialog(context, R.style.dialog_ios_style);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        dialog.findViewById(R.id.ll_sell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showFuwaDialog2(context, false);
            }
        });

        dialog.findViewById(R.id.ll_give).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showFuwaDialog2(context, true);
            }
        });

        ImageView img_cancle = (ImageView) dialog.findViewById(R.id.img_cancle);
        img_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        //设置dialog大小
        Window dialogWindow = dialog.getWindow();
        WindowManager manager = ((FuwaPackageActivity) context).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9，根据实际情况调整
        dialogWindow.setAttributes(params);

        dialog.show();
    }

    //第二个个福娃dialog2
    private void showFuwaDialog2(final Context context, final boolean flag) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        View view = inflater.inflate(R.layout.pop_fuwa_item2, null);

        dialog2 = new Dialog(context, R.style.dialog_ios_style);
        dialog2.setContentView(view);
        dialog2.setCancelable(true);
        dialog2.setCanceledOnTouchOutside(false);

        if (flag) {
            TextView tv_price = (TextView) dialog2.findViewById(R.id.tv_price);
            tv_price.setText("接收口令");
            TextView et_price = (TextView) dialog2.findViewById(R.id.et_price);

            et_price.setHint("6-20个字个性中文口令");
            TextView tv_yuan = (TextView) dialog2.findViewById(R.id.tv_yuan);
            tv_yuan.setVisibility(View.INVISIBLE);
            TextView tv_confirm = (TextView) dialog2.findViewById(R.id.tv_confirm);
            tv_confirm.setText("确认赠送");
            TextView tv_content = (TextView) dialog2.findViewById(R.id.tv_content);
            tv_content.setVisibility(View.VISIBLE);
        }
        TextView tv_confirm = (TextView) dialog2.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag){ //赠送
                    showToast("赠送",false);

                }else{ //出售
                    showToast("出售",false);

                }
            }
        });
        ImageView img_cancle = (ImageView) dialog2.findViewById(R.id.img_cancle);
        img_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.dismiss();
            }
        });

        //设置dialog大小
        Window dialogWindow = dialog2.getWindow();
        WindowManager manager = ((FuwaPackageActivity) context).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9，根据实际情况调整
        dialogWindow.setAttributes(params);

        dialog2.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;

        }

    }

    private Drawable getDrawableFromRes(int resId) {
        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resId);
        return new BitmapDrawable(bmp);
    }
}
