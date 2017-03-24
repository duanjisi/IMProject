package im.boss66.com.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.connection.EditSchoolActivity;
import im.boss66.com.activity.connection.PersonalDataActivity;
import im.boss66.com.adapter.ChooseLikeAdapter;
import im.boss66.com.entity.ChooseLikeEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.ChooseLikeRequest;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/3/2.
 */
public class ChooseYourLikeDialog extends BaseDialog implements View.OnClickListener {
    private TextView ok;
    private RecyclerView rcv_like;
    private ArrayList<ChooseLikeEntity.ResultBean> datas;

    private ImageView img_cancle;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    initAdapter();
                    break;

            }
        }
    };
    private ArrayList<Boolean> falseList = new ArrayList<>();
    private List<Boolean> isChoose;
    private ChooseLikeAdapter adapter;
    private CancelChosseDialog cancelChosseDialog;

    private ArrayList<String> strs = new ArrayList<>();
    private ArrayList<Integer> integers = new ArrayList<>();


    private boolean forceCancel ; //是否强制退出
    public void setForceCancel(boolean forceCancel) {
        this.forceCancel = forceCancel;
    }

    public ChooseYourLikeDialog(final Context context) {
        super(context);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_UP) {

                    showCancleDialog();


                    return true;
                }
                return false;
            }
        });
        ok = (TextView) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(this);
        rcv_like = (RecyclerView) dialog.findViewById(R.id.rcv_like);
        img_cancle = (ImageView) dialog.findViewById(R.id.img_cancle);
        img_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancleDialog();

            }
        });
        initData();

    }

    private void showCancleDialog() {

        //如果没有选中一个,直接退出, falseList值点击之后改变了，需要重新获取
        falseList.clear();
        isChoose = adapter.getIsChoose();
        for (int i = 0; i < isChoose.size(); i++) {
            Boolean aBoolean1 = isChoose.get(i);
            if (aBoolean1) {
                falseList.add(aBoolean1);
            }
        }

        if (falseList.size() < 1) {
            dialog.dismiss();
        } else {

            if (cancelChosseDialog == null) {
                cancelChosseDialog = new CancelChosseDialog(context, this);
                cancelChosseDialog.show();
            } else if (!cancelChosseDialog.isShowing()) {
                cancelChosseDialog.show();
            }

        }

    }

    private void initAdapter() {

        adapter = new ChooseLikeAdapter(context, datas);
        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {

                falseList.clear();
                isChoose = adapter.getIsChoose();
                //拿到为选中为ture的个数
                for (int i = 0; i < isChoose.size(); i++) {
                    Boolean aBoolean1 = isChoose.get(i);
                    if (aBoolean1) {
                        falseList.add(aBoolean1);
                    }
                }
                if (falseList.size() <= 2) {
                    Boolean aBoolean = isChoose.get(postion);
                    isChoose.set(postion, !aBoolean);
                    adapter.notifyDataSetChanged();
                    //如果三个，并且点击的是选中的
                } else if (isChoose.get(postion)) {
                    Boolean aBoolean = isChoose.get(postion);
                    isChoose.set(postion, !aBoolean);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtil.showShort(context,"只能选择3个");
                }

            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_like.setLayoutManager(new GridLayoutManager(context, 3));
        rcv_like.setAdapter(adapter);
    }


    private void initData() {
        ChooseLikeRequest request = new ChooseLikeRequest("");
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ChooseLikeEntity entity = JSON.parseObject(s, ChooseLikeEntity.class);
                datas = (ArrayList<ChooseLikeEntity.ResultBean>) entity.getResult();
                handler.obtainMessage(1).sendToTarget();

            }

            @Override
            public void onFailure(String msg) {

            }
        });

    }


    @Override
    protected int getView() {
        return R.layout.dialog_your_like;
    }

    @Override
    protected int getDialogStyleId() {
        return R.style.dialog_ios_style;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ok:
                dismiss();
                break;

        }

    }

    private IFinishCallBack iFinishCallBack;

    public interface IFinishCallBack {
        void showLike(ArrayList<String> strs, ArrayList<Integer> integers);
    }

    public void setCallBack(IFinishCallBack callBack) {
        iFinishCallBack = callBack;
    }


    @Override
    public void dismiss() {
        super.dismiss();
        // 如果是直接退出，不走该方法
        if(forceCancel){
            return;
        }
        strs.clear();
        integers.clear();
        isChoose = adapter.getIsChoose();
        for (int i = 0; i < isChoose.size(); i++) {
            if (isChoose.get(i)) {
                strs.add(datas.get(i).getTag_name());
                integers.add(datas.get(i).getTag_id());
            }
        }

        iFinishCallBack.showLike(strs,integers);


    }
}
