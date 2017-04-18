package im.boss66.com.activity.treasure;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.MD5Util;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.FuwaListAdaper;
import im.boss66.com.entity.FuwaDetailEntity;
import im.boss66.com.entity.FuwaEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

import static im.boss66.com.entity.FuwaEntity.*;

/**
 * Created by liw on 2017/3/13.
 */

public class FuwaPackageActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView rcv_fuwalist;
    private FuwaListAdaper adaper;


    private Dialog dialog;
    private Dialog dialog2;


    private List<FuwaEntity.Data> fuwaList = new ArrayList<>();

    private int choosePosition;     //福娃列表的选中位置
    private long time1 = 0L; //防止快速点击
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    break;
                case 2:
                    setContent();
                    break;
                case 3:
                    showToast("出售成功", false);
                    gidList.remove(fuwa_gid);
                    dialog2.dismiss();
                    dialog.dismiss();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    }, 300);

                    break;
                case 4:
                    showToast("赠送成功", false);
                    gidList.remove(fuwa_gid);
                    dialog2.dismiss();
                    dialog.dismiss();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    }, 300);
                    break;

            }
        }
    };
    private FuwaDetailEntity fuwaDetailEntity;
    private String fuwa_id;
    private String fuwa_gid;
    private String uid;
    private List<String> gidList;

    private boolean first = true;  //第一次进页面存储福娃gid list
    private TextView tv_number;
    private TextView tv_fuwa_num;
    private TextView tv_from;
    private TextView tv_catch;
    private View view1;
    private int vp_position =0;
    private List<View> views=new ArrayList<>();
    private LinearLayout ll_points;
    private ViewPager vp_fuwa;

    private long time11 = 0; //出售防快速点击
    private long time3 = 0;//赠送防快速点击
    private ImageView img_left;
    private ImageView img_right;
    private int count; //福娃vp 页数
    private TextView tv_num; //当前页
    private TextView tv_count; //总页数
    private EditText et_price;

    private  boolean flag = false; // 用来控制出售还是赠送
    private TextView tv_price;
    private TextView tv_confirm;
    private TextView tv_yuan;
    private TextView tv_content;

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
        String url = HttpUrl.QUERY_MY_FUWA + uid + "&time=" + System.currentTimeMillis();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String res = responseInfo.result;
                Log.i("liwy", res);
                if (!TextUtils.isEmpty(res)) {
                    FuwaEntity entity = JSON.parseObject(res, FuwaEntity.class);
                    List<FuwaEntity.Data> data = entity.getData();
                    if (data != null ) {
                        showData(data);
                    }else{
//                        //没有数据的话
//                        fuwaList=new ArrayList<Data>();
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
        fuwaList.clear();    //先把之前的数据清空
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
        if (first) {
            gidList = new ArrayList<>();
            first = false;
            for (int i = 0; i < fuwaList.size(); i++) {
                List<String> idList = fuwaList.get(i).getIdList();
                gidList.addAll(idList);
            }


        }



        //拿到所有id
        List<String> ids = new ArrayList<>();
        for(FuwaEntity.Data bills : fuwaList){
            String id = bills.getId();
            ids.add(id);
        }


        //没有的补上对象，一共66个
        for(int i =1;i<67;i++){
            String id = String.valueOf(i);
            if(!ids.contains(id)){
                FuwaEntity.Data data1 = new FuwaEntity.Data();
                data1.setId(i+"");
                data1.setNum(0);
                fuwaList.add(data1);
            }
        }

        //排序
        Collections.sort(fuwaList, new Comparator<FuwaEntity.Data>() {
            @Override
            public int compare(FuwaEntity.Data data1, FuwaEntity.Data data2) {
                // 排序
                Integer i = Integer.parseInt(data1.getId());
                Integer j = Integer.parseInt(data2.getId());
//                return data1.getId().compareTo(data2.getId());
                return i.compareTo(j);
            }
        });

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
                long time2 = System.currentTimeMillis();
                if (time2 - time1 > 500L) {
                    choosePosition = postion;
                    vp_position=0; //点击后给vp_position设为0
                    if(fuwaList.get(choosePosition).getIdList()!=null&&fuwaList.get(choosePosition).getIdList().size()>0){
                        if(dialog==null){

                            showFuwaDialog(context);
                        }else if(!dialog.isShowing()){
                            setVp();
                            dialog.show();

                        }

                        time1 = time2;
                    }
                } else {
                    ToastUtil.showShort(context, "点击的太快啦");
                }
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_fuwalist.setAdapter(adaper);
    }

    private void initFuwaDetail(String fuwa_gid) {

        String url = HttpUrl.FUWA_DETAIL + fuwa_gid+"&time="+System.currentTimeMillis();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    fuwaDetailEntity = JSON.parseObject(res, FuwaDetailEntity.class);
                    handler.obtainMessage(2).sendToTarget();
                } else {
                    showToast(fuwaDetailEntity.getMessage(), false);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                showToast(e.getMessage(), false);
                Log.i("onFailure", s);

            }
        });


    }


    //弹第一个福娃dialog
    private void showFuwaDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_fuwa_item, null);

        dialog = new Dialog(context, R.style.dialog_ios_style);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        ll_points = (LinearLayout) view.findViewById(R.id.ll_points);

        tv_num = (TextView) view.findViewById(R.id.tv_num);
        tv_count = (TextView) view.findViewById(R.id.tv_count);

        img_left = (ImageView) view.findViewById(R.id.img_left);
        img_right = (ImageView) view.findViewById(R.id.img_right);

        vp_fuwa = (ViewPager) view.findViewById(R.id.vp_fuwa);

        setVp();



        img_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vp_position>0){
                    vp_fuwa.setCurrentItem(vp_position-1);
                }

            }
        });
        img_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vp_position<count-1){
                    vp_fuwa.setCurrentItem(vp_position+1);

                }

            }
        });

        dialog.findViewById(R.id.ll_sell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog2==null){
                    flag= false;
                    showFuwaDialog2(context);
                }else if(!dialog2.isShowing()){
                    flag= false;
                    setDialog2Content(flag);
                    dialog2.show();

                }
            }
        });

        dialog.findViewById(R.id.ll_give).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog2==null){
                    flag= true;
                    showFuwaDialog2(context);
                }else if(!dialog2.isShowing()){
                    flag= true;
                    setDialog2Content(flag);
                    dialog2.show();
                }
            }
        });



        vp_fuwa.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                if(count>1){
                    tv_num.setText(position+1+"");
                    tv_count.setText("/"+count);
                }

                if(position==0){
                    img_left.setVisibility(View.INVISIBLE);
                }else {
                    img_left.setVisibility(View.VISIBLE);

                }

                if(position==count-1){
                    img_right.setVisibility(View.INVISIBLE);

                }else{
                    img_right.setVisibility(View.VISIBLE);
                }



                vp_position = position;        //选中后
                fuwa_gid = fuwaList.get(choosePosition).getIdList().get(position);
                initFuwaDetail(fuwa_gid);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

    //设置vp
    private void setVp() {
        views.clear();   //清空views
        //页数
        count = fuwaList.get(choosePosition).getIdList().size();
        //初始化vp下面的数字和左右icon
        if(count>1){
            img_left.setVisibility(View.INVISIBLE);
            img_right.setVisibility(View.VISIBLE);
            tv_num.setText("1");
            tv_count.setText("/"+count);
        }else{
            img_left.setVisibility(View.INVISIBLE);
            img_right.setVisibility(View.INVISIBLE);
            tv_num.setText("1");
            tv_count.setText("/1");
        }



        //初始化vp的view
        for (int i = 0; i < fuwaList.get(choosePosition).getIdList().size(); i++) {
            view1 = LayoutInflater.from(context).inflate(R.layout.item_fuwa_vp, null);
            views.add(view1);
        }



        //给第一个view初始化数据
        if(fuwaList.get(choosePosition).getIdList()!=null&&fuwaList.get(choosePosition).getIdList().size()>0){
            fuwa_gid = fuwaList.get(choosePosition).getIdList().get(0); //首页的gid
            initFuwaDetail(fuwa_gid);
        }

        vp_fuwa.setAdapter(new ViewAdapter(views));

    }

    class ViewAdapter extends PagerAdapter {
        private List<View> viewList;//数据源

        public ViewAdapter(List<View> viewList) {
            this.viewList = viewList;
        }

        @Override
        public int getCount() {

            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position < viewList.size()) {      //防止vp重新初始化越界崩溃
                container.removeView(viewList.get(position));
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position < viewList.size()) {
                container.addView(viewList.get(position));//千万别忘记添加到container
                return viewList.get(position);
            }
            return null;
        }
    }

    private void setContent() {

        fuwa_id = fuwaList.get(choosePosition).getId(); //通过点击的位置，拿到id

        View view = views.get(vp_position);    //通过vp位置拿到view

        tv_number = (TextView) view.findViewById(R.id.tv_number);
        tv_fuwa_num = (TextView) view.findViewById(R.id.tv_fuwa_num);
        tv_from = (TextView) view.findViewById(R.id.tv_from);
        tv_catch = (TextView) view.findViewById(R.id.tv_catch);

        tv_number.setText(fuwa_id);
        tv_fuwa_num.setText(fuwa_id + "号福娃");
        tv_from.setText("来源于:" + fuwaDetailEntity.getData().getCreator());
        tv_catch.setText("捕获于：" + fuwaDetailEntity.getData().getPos());

    }


    //第二个福娃dialog2
    private void showFuwaDialog2(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        View view = inflater.inflate(R.layout.pop_fuwa_item2, null);

        dialog2 = new Dialog(context, R.style.dialog_ios_style);
        dialog2.setContentView(view);
        dialog2.setCancelable(true);
        dialog2.setCanceledOnTouchOutside(false);
        et_price = (EditText) dialog2.findViewById(R.id.et_price);
        tv_price = (TextView) dialog2.findViewById(R.id.tv_price);
        tv_yuan = (TextView) dialog2.findViewById(R.id.tv_yuan);
        tv_confirm = (TextView) dialog2.findViewById(R.id.tv_confirm);
        tv_content = (TextView) dialog2.findViewById(R.id.tv_content);

        setDialog2Content(flag);

        TextView tv_confirm = (TextView) dialog2.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (flag) { //赠送
                    long time22 = System.currentTimeMillis();
                    if(time22-time11>1000){
                        time11=time22;
                        if(et_price.getText().length()>0){

                            giveFuwa(et_price.getText().toString());
                        }else{
                            ToastUtil.showShort(context,"请填写接收人口令");
                        }
                    }
                } else { //出售
                    long time4 = System.currentTimeMillis();
                    if(time4-time3>1000){
                        time3 = time4;


                        if(et_price.getText().length()>0){

                            if(Double.parseDouble(et_price.getText().toString())==0){
                                ToastUtil.showShort(context,"出售金额必须大于0");
                            }else{
                                sellFuwa(Double.parseDouble(et_price.getText().toString()));
                            }
                        }else{
                            ToastUtil.showShort(context,"请填写出售金额");
                        }
                    }

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

    private void setDialog2Content(boolean flag) {
        if (flag) {
            //输入中文口令，赠送
            InputFilter[] filters = {new InputFilter.LengthFilter(25)};
            et_price.setFilters(filters);
            tv_price.setText("接收口令");
            et_price.setHint("填写接收人的口令");
            et_price.setText("");
            tv_confirm.setText("确认赠送");
            tv_yuan.setVisibility(View.INVISIBLE);
            tv_content.setVisibility(View.VISIBLE);
            et_price.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            InputFilter[] filters = {new InputFilter.LengthFilter(10)};
            et_price.setFilters(filters);
            tv_price.setText("出售价格");
            et_price.setHint("");
            et_price.setText("");
            tv_confirm.setText("确认出售");
            tv_yuan.setVisibility(View.VISIBLE);
            tv_content.setVisibility(View.INVISIBLE);
            et_price.setInputType(InputType.TYPE_CLASS_NUMBER| InputType.TYPE_TEXT_FLAG_CAP_WORDS); //数字和
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
    }

    //赠送福娃
    private void giveFuwa(String string) {
        if (!gidList.contains(fuwa_gid)) {
            showToast("赠送失败", false);
            return;
        }
        String url = HttpUrl.GIVE_FUWA + "?token=" + string + "&fuwagid=" + fuwa_gid + "&fromuser=" + uid;
        String sigh = "/donate" + "?token=" + string + "&fuwagid=" + fuwa_gid + "&fromuser=" + uid + "&platform=boss66";

        sigh = MD5Util.getStringMD5(sigh);
        url = url + "&sign=" + sigh+"&time="+System.currentTimeMillis();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    if (jsonObject.getInt("code") == 0) {
                        handler.obtainMessage(4).sendToTarget();
                    } else {
                        showToast("请填写正确的口令", false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("onFailure", s);
                showToast(e.getMessage(), false);
            }
        });
    }

    //出售福娃
    private void sellFuwa(Double price) {
        if (!gidList.contains(fuwa_gid)) {
            showToast("出售失败", false);
            return;
        }
        String url = HttpUrl.SELL_FUWA + "?id=" + fuwa_id + "&owner=" + uid + "&amount=" + price + "&fuwagid=" + fuwa_gid;
        String sigh = "/sell" + "?id=" + fuwa_id + "&owner=" + uid + "&amount=" + price + "&fuwagid=" + fuwa_gid + "&platform=boss66";
        sigh = MD5Util.getStringMD5(sigh);
        url = url + "&sign=" + sigh+"&time="+System.currentTimeMillis();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getInt("code") == 0) {
                            //出售成功，刷新ui
                            handler.obtainMessage(3).sendToTarget();
                        } else {
                            showToast(jsonObject.getString("message"), false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("onFailure", s);
                showToast(e.getMessage(), false);
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
