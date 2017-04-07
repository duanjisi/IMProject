package im.boss66.com.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.OrderInfoUtil2_0;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.treasure.FuwaDealActivity;
import im.boss66.com.activity.treasure.FuwaPackageActivity;
import im.boss66.com.adapter.FuwaSellAdapter;
import im.boss66.com.entity.AlipayOrder;
import im.boss66.com.entity.FuwaSellEntity;
import im.boss66.com.entity.PayResult;
import im.boss66.com.entity.PayWx;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.http.request.OrderAlipayRequest;
import im.boss66.com.http.request.OrderSystemNoticeRequest;
import im.boss66.com.http.request.OrderWxRequest;
import im.boss66.com.listener.RecycleViewItemListener;
import im.boss66.com.widget.wheel.ArrayWheelAdapter;
import im.boss66.com.widget.wheel.OnWheelChangedListener;
import im.boss66.com.widget.wheel.WheelView;

/**
 * Created by liw on 2017/3/13.
 */

public class FuwaSellFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = FuwaSellFragment.class.getSimpleName();
    private static final int SDK_PAY_FLAG = 2;
    private IWXAPI api;
    private Resources resources;
    private TextView tv_choose;
    private TextView tv_price;
    private ImageView img_choose;
    private ImageView img_price;

    private TextView tv_buy;


    private FuwaSellAdapter adapter;

    private Dialog dialog; //福娃详情dialog
    private List<FuwaSellEntity.DataBean> datas;
    private List<FuwaSellEntity.DataBean> datasChoose = new ArrayList<>();

    private RecyclerView rcv_fuwalist;


    private Dialog dialog2;     //选择器
    private Dialog dialog3;    //购买dialog
    private Dialog dialog4;   //购买成功提示
    private WheelView id_sex;
    private TextView mBtnConfirm2;
    private TextView btn_cancle2;

    private long time1  =0L; //用来防止多次点击
    private long timeStart = 0L; //第二个

    private boolean choose = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    adapter.setDatas(datas);
                    adapter.setChooses();
                    adapter.notifyDataSetChanged();
                    break;
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult(
                            (Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        showToast("支付成功", true);

                        dialog.dismiss();   //福娃详情dialog

                        if(dialog4==null){
                            showSuccessDialog();
                        }else if(!dialog4.isShowing()){
                            dialog4.show();

                        }

//                        requesResult();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showToast("支付失败", true);
                    }
                    break;
                }
            }
        }
    };
    private TextView tv_fuwa_num;
    private TextView tv_number;
    private TextView tv_price1;
    private TextView tv_count;

    private void showSuccessDialog() {


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        View view = inflater.inflate(R.layout.pop_buy_success, null);

        dialog4 = new Dialog(getActivity(), R.style.dialog_ios_style);
        dialog4.setContentView(view);
        dialog4.setCancelable(true);
        dialog4.setCanceledOnTouchOutside(false);

        TextView tv_word = (TextView) view.findViewById(R.id.tv_word);
        tv_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog4.dismiss();

                datas.remove(chooseFuwa);
                adapter.setDatas(datas);
                adapter.setChooses();
                adapter.notifyDataSetChanged();

                chooseFuwa =null;

                datasChoose = datas;

                tv_price.setTextColor(0xffcccccc);
                img_price.setImageResource(R.drawable.fuwa_price);

                tv_price.setText("价格");

                tv_choose.setTextColor(0xffcccccc);
                img_choose.setImageResource(R.drawable.fuwa_screen);

            }
        });

        //设置dialog大小
        Window dialogWindow = dialog4.getWindow();
        WindowManager manager = getActivity().getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.9，根据实际情况调整
        dialogWindow.setAttributes(params);

        dialog4.show();


    }

    private String[] fuwas;

    private Map<String, Integer> fuwaMap = new HashMap<>();

    private int fuwaNum = 0;
    private FuwaSellEntity.DataBean chooseFuwa;
    private PopupWindow popupWindow;
    private View dialog_view;
    private Double fuwa_price;
    private boolean zhifubao = true; //支付宝购买
    private String userid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sellfuwa, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initViews(view);

        initData();


    }

    private void initData() {
        showLoadingDialog();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        String url = HttpUrl.SEARCH_FUWA_SELL + "?time=" + System.currentTimeMillis();
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                cancelLoadingDialog();
                String result = responseInfo.result;
                if (result != null) {
                    FuwaSellEntity fuwaSellEntity = JSON.parseObject(result, FuwaSellEntity.class);
                    if (fuwaSellEntity.getCode() == 0) {
                        datas = fuwaSellEntity.getData();
                        handler.obtainMessage(1).sendToTarget();
                    } else {
                        showToast(fuwaSellEntity.getMessage(), false);
                    }

                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                cancelLoadingDialog();
                showToast(e.getMessage(), false);
            }
        });
    }


    private void initViews(View view) {
        userid = App.getInstance().getUid();
        resources = getResources();
        view.findViewById(R.id.ll_left).setOnClickListener(this);
        view.findViewById(R.id.ll_right).setOnClickListener(this);
        api = WXAPIFactory.createWXAPI(getActivity(), resources.getString(R.string.weixin_app_id2));
        tv_choose = (TextView) view.findViewById(R.id.tv_choose);
        tv_price = (TextView) view.findViewById(R.id.tv_price);
        img_choose = (ImageView) view.findViewById(R.id.img_choose);
        img_price = (ImageView) view.findViewById(R.id.img_price);

        tv_buy = (TextView) view.findViewById(R.id.tv_buy);
        tv_buy.setOnClickListener(this);


        rcv_fuwalist = (RecyclerView) view.findViewById(R.id.rcv_fuwalist);
        rcv_fuwalist.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.HORIZONTAL, false));
        adapter = new FuwaSellAdapter(getActivity());

        adapter.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {

                if (choose) {
                    if (datasChoose != null && datasChoose.size() > 0) {
                        for (int i = 0; i < datasChoose.size(); i++) {
                            if (postion == i) {
                                adapter.chooses[i] = true;
                            } else {
                                adapter.chooses[i] = false;
                            }
                        }
                        adapter.notifyDataSetChanged();
                        chooseFuwa = datasChoose.get(postion);
                    }
                } else {
                    if (datas != null && datas.size() > 0) {
                        for (int i = 0; i < datas.size(); i++) {
                            if (postion == i) {
                                adapter.chooses[i] = true;
                            } else {
                                adapter.chooses[i] = false;
                            }
                        }
                        adapter.notifyDataSetChanged();
                        chooseFuwa = datas.get(postion);
                    }
                }


            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });

        rcv_fuwalist.setAdapter(adapter);
        fuwas = new String[67];
        for (int i = 66; i >= 0; i--) {
            if (i == 0) {
                fuwas[i] = "全部";
            } else {
                fuwas[i] = i + "号福娃";
            }
            fuwaMap.put(fuwas[i], i);
        }


    }


    private void showAddressSelection() {
        if (dialog2 == null) {
            dialog2 = new Dialog(getActivity(), R.style.Dialog_full);
            View view_dialog = View.inflate(getActivity(),
                    R.layout.dialog_single_select, null);

            id_sex = (WheelView) view_dialog.findViewById(R.id.id_sex);
            mBtnConfirm2 = (TextView) view_dialog.findViewById(R.id.btn_confirm2);
            btn_cancle2 = (TextView) view_dialog.findViewById(R.id.btn_cancle2);

            // 设置可见条目数量
            id_sex.setVisibleItems(7);

            // 添加change事件
            id_sex.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    int currentItem = id_sex.getCurrentItem();
                    String fuwa = fuwas[currentItem];
                    fuwaNum = fuwaMap.get(fuwa);

                }
            });

            // 添加onclick事件
            mBtnConfirm2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseFuwa = null;
                    dialog2.dismiss();
                    datasChoose=new ArrayList<FuwaSellEntity.DataBean>();
                    //筛选福娃
                    if (fuwaNum > 0) {   //选中1-66号福娃

                        if (datas != null && datas.size() > 0) {
                            for (int i = 0; i < datas.size(); i++) {
                                if (fuwaNum == datas.get(i).getFuwaid()) {
                                    datasChoose.add(datas.get(i));
                                }
                            }
                            adapter.setDatas(datasChoose);
                            adapter.setChooses();
                            adapter.notifyDataSetChanged();
                        }
                        choose = true;
                    } else { //选中全部
                        adapter.setDatas(datas);
                        adapter.setChooses();
                        adapter.notifyDataSetChanged();
                        choose = false;
                    }

                }
            });
            btn_cancle2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog2.dismiss();
                }
            });
            dialog2.setContentView(view_dialog);
            Window dialogWindow = dialog2.getWindow();

            dialogWindow.setWindowAnimations(R.style.ActionSheetDialogAnimation);
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent); //加上可以在底部对其屏幕底部

            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(lp);
            dialog2.setCanceledOnTouchOutside(true);
        }
        dialog2.show();
        id_sex.setViewAdapter(new ArrayWheelAdapter<>(getActivity(), fuwas));

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_left:    //筛选
                tv_price.setTextColor(0xffcccccc);
                img_price.setImageResource(R.drawable.fuwa_price);

                tv_price.setText("价格");
                tv_choose.setTextColor(0xffe7d09a);
                img_choose.setImageResource(R.drawable.screen_click);

                showAddressSelection();
                break;


            case R.id.ll_right:     //价格
                chooseFuwa = null;
                tv_choose.setTextColor(0xffcccccc);
                img_choose.setImageResource(R.drawable.fuwa_screen);

                tv_price.setTextColor(0xffe7d09a);
                tv_price.setText("价格从低到高");
                img_price.setImageResource(R.drawable.price_click);
                if (fuwaNum > 0) {   //用dataschoose排序    先根据价格，再根据order id排序

                    if (datasChoose != null && datasChoose.size() > 0) {
                        Collections.sort(datasChoose, new Comparator<FuwaSellEntity.DataBean>() {
                            @Override
                            public int compare(FuwaSellEntity.DataBean data1, FuwaSellEntity.DataBean data2) {
                                // 排序
                                return data1.getAmount().compareTo(data2.getAmount());
                            }
                        });
                        adapter.setDatas(datasChoose);
                        adapter.setChooses();
                        adapter.notifyDataSetChanged();
                        choose = true;
                    }

                } else { //用datas排序
                    if (datas != null && datas.size() > 0) {
                        Collections.sort(datas, new Comparator<FuwaSellEntity.DataBean>() {
                            @Override
                            public int compare(FuwaSellEntity.DataBean data1, FuwaSellEntity.DataBean data2) {
                                // 排序
                                return data1.getAmount().compareTo(data2.getAmount());
                            }
                        });
                        adapter.setDatas(datas);
                        adapter.setChooses();
                        adapter.notifyDataSetChanged();
                        choose = false;
                    }
                }
                break;

            case R.id.tv_buy: //购买

                //如果选中了
                long time2 = System.currentTimeMillis();
                if(time2-time1>500L){
                    if (chooseFuwa != null) {
                        if(dialog==null){

                            showFuwaDialog(getActivity());
                        }else if(!dialog.isShowing()) {
                            setContent();
                            dialog.show();

                        }
                    }else{
                        showToast("请选择福娃",false);
                    }

                    time1=time2;
                }else{
                    ToastUtil.showShort(getContext(),"请不要点击太快");
                }


                break;
        }

    }




    //弹第一个福娃dialog
    private void showFuwaDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        dialog_view = inflater.inflate(R.layout.pop_fuwa_buy, null);


        ImageView img_cancle = (ImageView) dialog_view.findViewById(R.id.img_cancle);
        img_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tv_fuwa_num = (TextView) dialog_view.findViewById(R.id.tv_fuwa_num);
        tv_number = (TextView) dialog_view.findViewById(R.id.tv_number);
        tv_price1 = (TextView) dialog_view.findViewById(R.id.tv_price);
        setContent();

        TextView tv_buy = (TextView) dialog_view.findViewById(R.id.tv_buy);
        tv_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long timeEnd = System.currentTimeMillis();
                if(timeEnd-timeStart>500L){

                    if(dialog3==null){

                        showDialog();
                    }else if(!dialog3.isShowing()){
                        setContent2();
                        dialog3.show();

                    }

                    timeStart=timeEnd;
                }else {
                    ToastUtil.showShort(getActivity(),"请不要点击太快");
                }
            }
        });

        dialog = new Dialog(context, R.style.dialog_ios_style);
        dialog.setContentView(dialog_view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);


        //设置dialog大小
        Window dialogWindow = dialog.getWindow();
        WindowManager manager = ((FuwaDealActivity) context).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9，根据实际情况调整
        dialogWindow.setAttributes(params);

        dialog.show();
    }

    private void setContent() {
        tv_fuwa_num.setText(chooseFuwa.getFuwaid() + "号福娃");
        tv_number.setText(chooseFuwa.getFuwaid() + "");
        tv_price1.setText("购买金额: " + chooseFuwa.getAmount() + "元");
    }


    private void showDialog() {
        dialog3 = new Dialog(getActivity(), R.style.Dialog_full);
        View view_dialog = View.inflate(getActivity(),
                R.layout.pop_pay, null);


        dialog3.setContentView(view_dialog);


        Window dialogWindow = dialog3.getWindow();

        final ImageView img_zhifubao_choose = (ImageView) view_dialog.findViewById(R.id.img_zhifubao_choose);
        final ImageView img_wx_choose = (ImageView) view_dialog.findViewById(R.id.img_wx_choose);
        tv_count = (TextView) view_dialog.findViewById(R.id.tv_count);
        setContent2();

        view_dialog.findViewById(R.id.rl_zhifubao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_zhifubao_choose.setImageResource(R.drawable.money_choose);
                img_wx_choose.setImageResource(R.drawable.money_nochoose);
                zhifubao = true;
            }
        });
        view_dialog.findViewById(R.id.rl_weixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_wx_choose.setImageResource(R.drawable.money_choose);
                img_zhifubao_choose.setImageResource(R.drawable.money_nochoose);
                zhifubao = false;
            }
        });
        view_dialog.findViewById(R.id.tv_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (zhifubao) {
                    requestAlipayTrade();
                    showToast("支付宝", false);
                } else {
                    requestWxTrade();
                    showToast("微信", false);
                }
                dialog3.dismiss();
            }
        });
        view_dialog.findViewById(R.id.img_xx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog3.dismiss();
            }
        });

        TextView tv_buy = (TextView) view_dialog.findViewById(R.id.tv_buy);
        int screenWidth = UIUtils.getScreenWidth(getActivity());

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_buy.getLayoutParams();
        layoutParams.width = (int) (screenWidth*0.9);
        tv_buy.setLayoutParams(layoutParams);

        dialogWindow.setWindowAnimations(R.style.ActionSheetDialogAnimation);
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent); //加上可以在底部对其屏幕底部


        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(lp);
        dialog3.setCanceledOnTouchOutside(true);
        dialog3.show();
    }

    private void setContent2() {
        tv_count.setText(chooseFuwa.getAmount() + "元");
    }

    private void requestWxTrade() {
        if (chooseFuwa != null) {
            Log.i("info", "=========0000000000");
            OrderWxRequest request = new OrderWxRequest(TAG,
                    "" + chooseFuwa.getOrderid(),
                    "0.01",
                    chooseFuwa.getFuwagid());
            request.send(new BaseDataRequest.RequestCallback<PayWx>() {
                @Override
                public void onSuccess(PayWx pojo) {
                    bindDataWx(pojo);
                }

                @Override
                public void onFailure(String msg) {
                    Log.i("info", "=========msg:" + msg);
                    showToast(msg, true);
                }
            });
        }
    }

    private void bindDataWx(PayWx entity) {
        if (entity != null) {
            Log.i("info", "=========1111111111111");
            PayReq req = new PayReq();
            req.appId = resources.getString(R.string.weixin_app_id2);
            req.partnerId = entity.getPartnerid();
            req.prepayId = entity.getPrepayid();
            req.packageValue = entity.getPackageValue();
            req.nonceStr = entity.getNoncestr();
            req.timeStamp = entity.getTimestamp();
            req.sign = entity.getSign();
            // req.extData = "app data"; // optional
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            api.registerApp(resources.getString(R.string.weixin_app_id2));
            api.sendReq(req);
        }
    }

    private void requestAlipayTrade() {
        if (chooseFuwa != null) {
            OrderAlipayRequest request = new OrderAlipayRequest(TAG,
                    "" + chooseFuwa.getOrderid(),
                    chooseFuwa.getAmount()+"",
                    chooseFuwa.getFuwagid());
            request.send(new BaseDataRequest.RequestCallback<AlipayOrder>() {
                @Override
                public void onSuccess(AlipayOrder pojo) {
                    bindDataAlipay(pojo);
                }

                @Override
                public void onFailure(String msg) {
                    showToast(msg, true);
                }
            });
        }
    }

    private void bindDataAlipay(AlipayOrder order) {
        if (order != null) {
            /**
             * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
             * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
             * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
             *
             * orderInfo的获取必须来自服务端；
             */
            Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(resources.getString(R.string.alipay_app_id));
            String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//        String sign = OrderInfoUtil2_0.getSign(params, privateKey);
//        final String orderInfo = orderParam + "&" + sign;
            final String orderInfo = order.getOrder_str();
//            App.getInstance().setTrade_no(entity.getOut_trade_no());
            Runnable payRunnable = new Runnable() {
                @Override
                public void run() {
                    PayTask alipay = new PayTask(getActivity());
                    Map<String, String> result = alipay.payV2(orderInfo, true);
                    Log.i("msp", result.toString());

                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
            };
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        }
    }

    private String getOrderStr() {
        HashMap<String, String> map = new HashMap<>();
        map.put("partner", "2088421642497087");
        map.put("service", "mobile.securitypay.pay");
        map.put("_input_charset", "UTF-8");
        map.put("notify_url", "https://api.66boss.com/api/pay/alipaynotify");
        map.put("out_trade_no", "0819145412-6177");
        map.put("subject", "Alipay测试");
        map.put("payment_type", "1");
        map.put("seller_id", "RSA");
        map.put("total_fee", "0.01");
        map.put("body", "测试测试");
        map.put("seller_id", "gangmian_66boss@163.com");
        return "";
    }

    private void requesResult() {
        showLoadingDialog();
        if (chooseFuwa != null) {
            OrderSystemNoticeRequest request = new OrderSystemNoticeRequest(TAG,
                    "" + chooseFuwa.getOrderid(),
                    userid,
                    chooseFuwa.getFuwagid());
            request.send(new BaseModelRequest.RequestCallback<String>() {
                @Override
                public void onSuccess(String pojo) {
                    cancelLoadingDialog();
                    showToast("支付成功!", true);
                }

                @Override
                public void onFailure(String msg) {
                    cancelLoadingDialog();
                    showToast(msg, true);
                }
            });
        }

//        String tradeNo = App.getInstance().getTrade_no();
//        if (tradeNo != null && !tradeNo.equals("")) {
//            ChargeStatusRequest request = new ChargeStatusRequest(TAG, tradeNo);
//            request.send(new BaseDataRequest.RequestCallback() {
//                @Override
//                public void onSuccess(Object pojo) {
//                    cancelLoadingDialog();
//                    Session.getInstance().refreshCashesPager();
//                    showToast("支付成功!", true);
//                    finish();
//                }
//
//                @Override
//                public void onFailure(String msg) {
//                    cancelLoadingDialog();
//                    showToast("支付失败!", true);
//                }
//            });
//        }
    }
}
