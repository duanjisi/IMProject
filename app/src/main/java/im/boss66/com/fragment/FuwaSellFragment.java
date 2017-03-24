package im.boss66.com.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.treasure.FuwaDealActivity;
import im.boss66.com.adapter.FuwaSellAdapter;
import im.boss66.com.entity.FuwaSellEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;
import im.boss66.com.widget.wheel.ArrayWheelAdapter;
import im.boss66.com.widget.wheel.OnWheelChangedListener;
import im.boss66.com.widget.wheel.WheelView;

/**
 * Created by liw on 2017/3/13.
 */

public class FuwaSellFragment extends BaseFragment implements View.OnClickListener {
    private TextView tv_choose;
    private TextView tv_price;


    private ImageView img_choose;
    private ImageView img_price;

    private TextView tv_buy;


    private FuwaSellAdapter adapter;

    private Dialog dialog;
    private List<FuwaSellEntity.DataBean> datas;
    private List<FuwaSellEntity.DataBean> datasChoose = new ArrayList<>();

    private RecyclerView rcv_fuwalist;


    private Dialog dialog2;
    private Dialog dialog3;
    private WheelView id_sex;
    private TextView mBtnConfirm2;
    private TextView btn_cancle2;

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

            }
        }
    };
    private String[] fuwas;

    private Map<String, Integer> fuwaMap = new HashMap<>();

    private int fuwaNum = 0;
    private FuwaSellEntity.DataBean chooseFuwa;
    private PopupWindow popupWindow;
    private View dialog_view;
    private Double fuwa_price;

    private boolean zhifubao = true; //支付宝购买

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
        String url = HttpUrl.SEARCH_FUWA_SELL;
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
        view.findViewById(R.id.ll_left).setOnClickListener(this);
        view.findViewById(R.id.ll_right).setOnClickListener(this);

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
                    dialog2.dismiss();
                    datasChoose.clear();
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

                if (chooseFuwa != null) {
                    showFuwaDialog(getActivity());
                }

                break;
        }

    }

    //弹第一个福娃dialog
    private void showFuwaDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        dialog_view = inflater.inflate(R.layout.pop_fuwa_buy, null);
        TextView tv_fuwa_num = (TextView) dialog_view.findViewById(R.id.tv_fuwa_num);
        tv_fuwa_num.setText(chooseFuwa.getFuwaid() + "号福娃");

        ImageView img_cancle = (ImageView) dialog_view.findViewById(R.id.img_cancle);
        img_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView tv_price = (TextView) dialog_view.findViewById(R.id.tv_price);
        fuwa_price = chooseFuwa.getAmount();
        tv_price.setText("购买金额: " + fuwa_price + "元");
        TextView tv_buy = (TextView) dialog_view.findViewById(R.id.tv_buy);
        tv_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showToast("buy", false);
//                showPop();
                showDialog();
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


    private void showDialog() {
        dialog3 = new Dialog(getActivity(), R.style.Dialog_full);
        View view_dialog = View.inflate(getActivity(),
                R.layout.pop_pay, null);


        dialog3.setContentView(view_dialog);


        Window dialogWindow = dialog3.getWindow();

        final ImageView img_zhifubao_choose = (ImageView) view_dialog.findViewById(R.id.img_zhifubao_choose);
        final ImageView img_wx_choose = (ImageView) view_dialog.findViewById(R.id.img_wx_choose);
        final TextView tv_count = (TextView) view_dialog.findViewById(R.id.tv_count);
        tv_count.setText(fuwa_price + "元");
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
                    showToast("支付宝", false);
                } else {
                    showToast("微信", false);
                }
            }
        });
        view_dialog.findViewById(R.id.img_xx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog3.dismiss();
            }
        });


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

}
