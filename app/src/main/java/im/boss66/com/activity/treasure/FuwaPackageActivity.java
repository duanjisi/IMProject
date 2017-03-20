package im.boss66.com.activity.treasure;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.FuwaListAdaper;
import im.boss66.com.entity.FuwaDetailEntity;
import im.boss66.com.entity.FuwaEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/3/13.
 */

public class FuwaPackageActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rcv_fuwalist;
    private FuwaListAdaper adaper;


    private Dialog dialog;
    private Dialog dialog2;


    private List<FuwaEntity.Data> fuwaList=new ArrayList<>();

    private int choosePosition;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:

                break;
                case 2:
                    showFuwaDialog(context);
                    break;
                case 3:
                    showToast("出售成功",false);
                    dialog2.dismiss();
                    dialog.dismiss();
                    initData();
                    break;
                case 4:
                    showToast("赠送成功",false);
                    dialog2.dismiss();
                    dialog.dismiss();
                    initData();
                    break;

            }
        }
    };
    private FuwaDetailEntity fuwaDetailEntity;
    private String fuwa_id;
    private String fuwa_gid;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuwa_package);
        initViews();

        initData();
    }

    private void initData() {
        showLoadingDialog();
        uid = App.getInstance().getUid();
//        String url = HttpUrl.QUERY_MY_FUWA + "john"; //测试数据
        String url = HttpUrl.QUERY_MY_FUWA + uid;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    FuwaEntity entity = JSON.parseObject(res, FuwaEntity.class);
                    List<FuwaEntity.Data> data = entity.getData();
                    if (data != null && data.size() > 0) {
                        showData(data);
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                Log.i("onFailure", s);
            }
        });
    }

    private void showData(List<FuwaEntity.Data> data) {
        fuwaList.clear();
        for (FuwaEntity.Data bill : data) {
            boolean state = false;
            for (FuwaEntity.Data bills : fuwaList) {
                if (bills.getId().equals(bill.getId())) {
                    List<String> list = bills.getIdList();
                    String id = bill.getGid();
                    if (!TextUtils.isEmpty(id) && !list.contains(id)) {
                        list.add(id);
                        bills.setIdList(list);
                    }
                    int num = bills.getNum();
                    num += bill.getNum();
                    bills.setNum(num);
                    state = true;
                }
            }
            if (!state) {
                List<String> list = bill.getIdList();
                String id = bill.getGid();
                if (!TextUtils.isEmpty(id) && !list.contains(id)) {
                    list.add(id);
                    bill.setIdList(list);
                }
                fuwaList.add(bill);
            }
        }
        adaper.setDatas(fuwaList);
        adaper.notifyDataSetChanged();
    }

    private void initViews() {
        findViewById(R.id.tv_headlift_view).setOnClickListener(this);
        rcv_fuwalist = (RecyclerView) findViewById(R.id.rcv_fuwalist);
        rcv_fuwalist.setLayoutManager(new GridLayoutManager(this, 3));

        adaper = new FuwaListAdaper(this);
        adaper.setDatas(fuwaList);

        adaper.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                //弹pop
                choosePosition = postion;
                initFuwaDetail(postion);
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_fuwalist.setAdapter(adaper);
    }

    private void initFuwaDetail(int postion) {

        String url = HttpUrl.FUWA_DETAIL + fuwaList.get(postion).getGid();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    fuwaDetailEntity = JSON.parseObject(res, FuwaDetailEntity.class);
                    handler.obtainMessage(2).sendToTarget();

                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("onFailure", s);

            }
        });


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
                //先访问接口拿福娃详情
                    showFuwaDialog2(context, false);
            }
        });

        dialog.findViewById(R.id.ll_give).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showFuwaDialog2(context, true);
            }
        });
        TextView tv_number = (TextView) dialog.findViewById(R.id.tv_number);
        TextView tv_fuwa_num = (TextView) dialog.findViewById(R.id.tv_fuwa_num);
        TextView tv_from = (TextView) dialog.findViewById(R.id.tv_from);
        TextView tv_catch = (TextView) dialog.findViewById(R.id.tv_catch);
        fuwa_id = fuwaList.get(choosePosition).getId();
        fuwa_gid = fuwaList.get(choosePosition).getGid();
        tv_number.setText(fuwa_id);
        tv_fuwa_num.setText(fuwa_id+"号福娃");
        tv_from.setText("来源于:"+fuwaDetailEntity.getData().getCreator());
        tv_catch.setText("捕获于："+fuwaDetailEntity.getData().getPos());

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

        final EditText et_price = (EditText) dialog2.findViewById(R.id.et_price);
        if (flag) {          //输入中文口令，赠送
            TextView tv_price = (TextView) dialog2.findViewById(R.id.tv_price);
            tv_price.setText("接收口令");

            et_price.setHint("填写接收人的口令");
            et_price.setInputType(InputType.TYPE_CLASS_TEXT);
            TextView tv_yuan = (TextView) dialog2.findViewById(R.id.tv_yuan);
            tv_yuan.setVisibility(View.INVISIBLE);
            TextView tv_confirm = (TextView) dialog2.findViewById(R.id.tv_confirm);
            tv_confirm.setText("确认赠送");
            TextView tv_content = (TextView) dialog2.findViewById(R.id.tv_content);
            tv_content.setVisibility(View.VISIBLE);
        }else{
            et_price.addTextChangedListener(new TextWatcher() {     //输入金额的edittext监听
                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    if (s.toString().contains(".")) {
                        if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                            s = s.toString().subSequence(0,
                                    s.toString().indexOf(".") + 3);
                            et_price.setText(s);
                            et_price.setSelection(s.length());
                        }
                    }
                    if (s.toString().trim().substring(0).equals(".")) {
                        s = "0" + s;
                        et_price.setText(s);
                        et_price.setSelection(2);
                    }
                    if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                        if (!s.toString().substring(1, 2).equals(".")) {
                            et_price.setText(s.subSequence(0, 1));
                            et_price.setSelection(1);
                            return;
                        }
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void afterTextChanged(Editable s) {
                    // TODO Auto-generated method stub
                }
            });
        }
        TextView tv_confirm = (TextView) dialog2.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag){ //赠送
//                    showToast("赠送",false);

                    giveFuwa(et_price.getText().toString());



                }else{ //出售
                    sellFuwa(Double.parseDouble(et_price.getText().toString()));

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

    private void giveFuwa(String string) {
        String url = HttpUrl.GIVE_FUWA +"?token="+string+"&fuwagid="+fuwa_gid+"&fromuser="+uid;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    if(jsonObject.getInt("code")==0){
                        handler.obtainMessage(4).sendToTarget();
                    }else{
                        showToast("请填写正确的口令",false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("onFailure", s);
                showToast("请填写正确的口令",false);
            }
        });
    }

    private void sellFuwa(Double price) {
        String url = HttpUrl.SELL_FUWA +"?id="+fuwa_id+"&owner="+uid+"&amount="+price+"&fuwagid="+fuwa_gid;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        if(jsonObject.getInt("code")==0){
                            //出售成功，刷新ui
                            handler.obtainMessage(3).sendToTarget();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("onFailure", s);
            }
        });



    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_headlift_view:
                finish();
                break;

        }

    }

}
