package im.boss66.com.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
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
import android.widget.RelativeLayout;
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
import java.util.LinkedList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.MD5Util;
import im.boss66.com.Utils.MakeQRCodeUtil;
import im.boss66.com.Utils.OnMultiClickListener;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.CaptureActivity;
import im.boss66.com.activity.connection.ClanClubActivity;
import im.boss66.com.activity.treasure.FuwaPackageActivity;
import im.boss66.com.adapter.FuwaListAdaper;
import im.boss66.com.entity.FuwaDetailEntity;
import im.boss66.com.entity.FuwaEntity;
import im.boss66.com.entity.TribeEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/5/3.
 */

public class FuwaMyCatchFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView rcv_fuwalist;
    private FuwaListAdaper adaper;


    private Dialog dialog;  //福娃详情
    private Dialog sellDialog; //出售
    private Dialog giveDialog; //赠送


    private List<FuwaEntity.Data> fuwaList = new ArrayList<>();


    private int choosePosition;     //福娃列表的选中位置
    private long time1 = 0L; //防止快速点击

    private boolean dialog_show = false;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    break;
                case 2:  //刷新当前vp页面数据
                    if (dialog_show) {
                        handler.sendEmptyMessageDelayed(5, 3000);
                    }
                    boolean old_awarded = fuwas.get(vp_position).awarded;

//                    Log.i("liwya","old_awarded"+old_awarded);
//                    Log.i("liwya","new_awarded"+new_awarded);
                    Log.i("liwya", "old_gid======" + old_gid);
                    Log.i("liwya", "fuwa_gid=====" + fuwa_gid);

                    if (!old_gid.equals(fuwa_gid)) { //如果不相同，说明请求回来的数据和当前页面不同。不要刷新

                        return;
                    }
                    if (new_awarded != old_awarded) {
                        fuwas.get(vp_position).awarded = new_awarded;
                        viewAdapter.setDatas(fuwas);
                    }

                    break;
                case 3:
                    showToast("出售成功", false);
                    gidList.remove(fuwa_gid);
                    sellDialog.dismiss();
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
                    giveDialog.dismiss();
                    dialog.dismiss();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    }, 300);
                    break;
                case 5:
                    initFuwaDetail(fuwa_gid);     //刷新当前页面view
//                    if (dialog_show) {
//                        handler.sendEmptyMessageDelayed(5, 3000);
//                    }

            }
        }
    };
    private FuwaDetailEntity fuwaDetailEntity;
    private String fuwa_id;
    private String fuwa_gid;
    private String uid;
    private List<String> gidList;

    private boolean first = true;  //第一次进页面存储福娃gid list
    private int vp_position = 0;
    private ViewPager vp_fuwa;

    private long time11 = 0; //出售防快速点击
    private long time3 = 0;//赠送防快速点击
    private ImageView img_left;
    private ImageView img_right;
    private int count; //福娃vp 页数
    private TextView tv_num; //当前页
    private TextView tv_count; //总页数
    private EditText et_price;
    private EditText et_code;

    private TextView tv_confirm;
    private TextView tv_confirm2;
    private ImageView award;   //已兑奖
    private ImageView img_fuwa;  //福娃图片
    private ImageView img_cancle;
    private ViewAdapter viewAdapter;
    private boolean new_awarded;
    private List<FuwaEntity.FuwaDetail> fuwas;     //vp的数据list
    private String old_gid;
    private Bitmap bitmap;
    private Bitmap qrImage;
    private TextView tv_introduce_info;
    private String introduce;
    private Dialog introduceDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_fuwa_package, container, false);
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
                    if (data != null) {
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
        fuwaList.clear();    //先把之前的数据清空
        for (FuwaEntity.Data bill : data) {
            boolean state = false;
            for (FuwaEntity.Data bills : fuwaList) {
                if (bills.getId().equals(bill.getId())) {
                    List<String> list = bills.getIdList();
                    List<FuwaEntity.FuwaDetail> fuwas = bills.getFuwas();
                    String id = bill.getGid();
                    if (!TextUtils.isEmpty(id) && !list.contains(id)) {
                        list.add(id);
                        fuwas.add(new FuwaEntity.FuwaDetail(bill.getGid(), bill.getId(), bill.isAwarded(), bill.getPos(), bill.getCreator(), bill.getCreatorid()));
                        bills.setIdList(list);
                        bills.setFuwas(fuwas);
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
                List<FuwaEntity.FuwaDetail> fuwas = bill.getFuwas();
                if (!TextUtils.isEmpty(id) && !list.contains(id)) {
                    list.add(id);
                    fuwas.add(new FuwaEntity.FuwaDetail(bill.getGid(), bill.getId(), bill.isAwarded(), bill.getPos(), bill.getCreator(), bill.getCreatorid()));
                    bill.setIdList(list);
                    bill.setFuwas(fuwas);
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
        for (FuwaEntity.Data bills : fuwaList) {
            String id = bills.getId();
            ids.add(id);
        }


        //没有的补上对象，一共66个
        for (int i = 1; i < 67; i++) {
            String id = String.valueOf(i);
            if (!ids.contains(id)) {
                FuwaEntity.Data data1 = new FuwaEntity.Data();
                data1.setId(i + "");
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


    private void initViews(View view) {

        rcv_fuwalist = (RecyclerView) view.findViewById(R.id.rcv_fuwalist);
        rcv_fuwalist.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        adaper = new FuwaListAdaper(getActivity());
        adaper.setDatas(fuwaList);

        adaper.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                //弹pop
                long time2 = System.currentTimeMillis();
                if (time2 - time1 > 500L) {
                    choosePosition = postion;
                    vp_position = 0; //点击后给vp_position设为0
                    if (fuwaList.get(choosePosition).getIdList() != null && fuwaList.get(choosePosition).getIdList().size() > 0) {
                        if (dialog == null) {
                            showFuwaDialog(getActivity());
                        } else if (!dialog.isShowing()) {
                            setVp();
                            dialog.show();

                        }

                        time1 = time2;
                    }
                } else {
                    ToastUtil.showShort(getActivity(), "点击的太快啦");
                }
            }

            @Override
            public boolean onItemLongClick(int position) {
                return false;
            }
        });
        rcv_fuwalist.setAdapter(adaper);
    }

    private void initFuwaDetail(final String fuwa_gid) {
        old_gid = fuwa_gid + ""; //不能把地址给他

        String url = HttpUrl.FUWA_DETAIL + fuwa_gid + "&time=" + System.currentTimeMillis();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    fuwaDetailEntity = JSON.parseObject(res, FuwaDetailEntity.class);
                    new_awarded = fuwaDetailEntity.getData().isAwarded();


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
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog_show = false; //如果vp dismiss
            }
        });

        tv_num = (TextView) view.findViewById(R.id.tv_num);
        tv_count = (TextView) view.findViewById(R.id.tv_count);

        img_left = (ImageView) view.findViewById(R.id.img_left);
        img_right = (ImageView) view.findViewById(R.id.img_right);

        vp_fuwa = (ViewPager) view.findViewById(R.id.vp_fuwa);
        TextView tv_introduce = (TextView) view.findViewById(R.id.tv_introduce);

        tv_introduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (introduceDialog == null) {
                    showIntroduceDialog();
                } else if (!introduceDialog.isShowing()) {
                    tv_introduce_info.setText(null);
                    introduceDialog.show();
                    setIntroduce();
                }
            }
        });

        setVp();


        img_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vp_position > 0) {
                    vp_fuwa.setCurrentItem(vp_position - 1);
                }

            }
        });
        img_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vp_position < count - 1) {
                    vp_fuwa.setCurrentItem(vp_position + 1);

                }

            }
        });

        dialog.findViewById(R.id.ll_sell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sellDialog == null) {
                    showSellFuwaDialog(context);
                } else if (!sellDialog.isShowing()) {
                    et_price.setText(null);
                    sellDialog.show();
                }
            }
        });

        dialog.findViewById(R.id.ll_give).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (giveDialog == null) {
                    showGiveFuwaDialog(context);
                } else if (!giveDialog.isShowing()) {
                    et_code.setText(null);
                    giveDialog.show();
                }
            }
        });


        vp_fuwa.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                if (count > 1) {
                    tv_num.setText(position + 1 + "");
                    tv_count.setText("/" + count);
                }

                if (position == 0) {
                    img_left.setVisibility(View.INVISIBLE);
                } else {
                    img_left.setVisibility(View.VISIBLE);

                }

                if (position == count - 1) {
                    img_right.setVisibility(View.INVISIBLE);

                } else {
                    img_right.setVisibility(View.VISIBLE);
                }


                vp_position = position;        //选中后

                fuwa_gid = fuwaList.get(choosePosition).getFuwas().get(vp_position).gid;
//                fuwa_id = fuwaList.get(choosePosition).getFuwas().get(vp_position).id; //滑动之后福娃id没变化
//                Log.i("liwya",fuwa_gid);

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

    private void showIntroduceDialog() {
        introduceDialog = new Dialog(getActivity(), R.style.dialog_ios_style);
        introduceDialog.setContentView(R.layout.introduce_dialog);
        introduceDialog.setCanceledOnTouchOutside(true);
        introduceDialog.setCancelable(true);
        ImageView img_cancle = (ImageView) introduceDialog.findViewById(R.id.img_cancle);
        img_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introduceDialog.dismiss();
            }
        });

        tv_introduce_info = (TextView) introduceDialog.findViewById(R.id.tv_introduce_info);

        tv_introduce_info.setMovementMethod(new ScrollingMovementMethod());
        //设置dialog大小
        Window dialogWindow = introduceDialog.getWindow();
        WindowManager manager = getActivity().getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9，根据实际情况调整
        dialogWindow.setAttributes(params);

        introduceDialog.show();


        setIntroduce();
    }

    private void setIntroduce() {

        String url = HttpUrl.FUWA_ACTIVITY_INFO + fuwa_gid + "&time=" + System.currentTimeMillis();
        HttpUtils httpUtils = new HttpUtils(60 * 1000);
        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String res = responseInfo.result;
                if (!TextUtils.isEmpty(res)) {
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.getInt("code") == 0) {
                            introduce = jsonObject.getString("data");

                            if (introduce.length() > 0) {
                                tv_introduce_info.setText(introduce);

                            } else {
                                tv_introduce_info.setText("暂无介绍");
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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


    //设置vp
    private void setVp() {

        //页数
        count = fuwaList.get(choosePosition).getIdList().size();
        //初始化vp下面的数字和左右icon
        if (count > 1) {
            img_left.setVisibility(View.INVISIBLE);
            img_right.setVisibility(View.VISIBLE);
            tv_num.setText("1");
            tv_count.setText("/" + count);
        } else {
            img_left.setVisibility(View.INVISIBLE);
            img_right.setVisibility(View.INVISIBLE);
            tv_num.setText("1");
            tv_count.setText("/1");
        }

        fuwas = fuwaList.get(choosePosition).getFuwas();

        viewAdapter = new ViewAdapter(fuwas, getActivity());

        vp_fuwa.setAdapter(viewAdapter);

        fuwa_gid = fuwaList.get(choosePosition).getGid();
        fuwa_id = fuwaList.get(choosePosition).getId();     //设置vp的时候，要拿到fuwaid。用来出售


        //vp打开后，循环请求接口.
        dialog_show = true; //显示
        handler.sendEmptyMessageDelayed(5, 3000);
    }

    class ViewAdapter extends PagerAdapter {
        private List<FuwaEntity.FuwaDetail> datas;
        private LinkedList<View> mViewCache = null;
        private Context context;
        private LayoutInflater mLayoutInflater = null;


        public ViewAdapter(List<FuwaEntity.FuwaDetail> datas, Context context) {
            this.datas = datas;
            this.context = context;
            this.mLayoutInflater = LayoutInflater.from(context);
            this.mViewCache = new LinkedList<>();
        }

        public void setDatas(List<FuwaEntity.FuwaDetail> datas) {
            this.datas = datas;
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return datas != null ? datas.size() : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position < datas.size()) {      //防止vp重新初始化越界崩溃
                View contentView = (View) object;
                container.removeView(contentView);
                this.mViewCache.add(contentView);
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;  //刷新数据
        }

//        @Override
//        public int getItemPosition(Object object) {
//            return super.getItemPosition(object);
//        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position < datas.size()) {

                View convertView = null;
                ViewAdapter.ViewHolder viewHolder = null;
                if (mViewCache.size() == 0) {
                    convertView = this.mLayoutInflater.inflate(R.layout.item_fuwa_vp, null, false);
                    TextView tv_fuwa_num = (TextView) convertView.findViewById(R.id.tv_fuwa_num);
                    TextView tv_from = (TextView) convertView.findViewById(R.id.tv_from);
                    TextView tv_catch = (TextView) convertView.findViewById(R.id.tv_catch);
                    TextView tv_type = (TextView) convertView.findViewById(R.id.tv_type);
                    ImageView img_fuwa = (ImageView) convertView.findViewById(R.id.img_fuwa);
                    ImageView award = (ImageView) convertView.findViewById(R.id.award);

                    viewHolder = new ViewAdapter.ViewHolder();
                    viewHolder.tv_fuwa_num = tv_fuwa_num;
                    viewHolder.tv_from = tv_from;
                    viewHolder.tv_type = tv_type;
                    viewHolder.tv_catch = tv_catch;
                    viewHolder.img_fuwa = img_fuwa;
                    viewHolder.award = award;

                    convertView.setTag(viewHolder);

                } else {
                    convertView = mViewCache.removeFirst();
                    viewHolder = (ViewAdapter.ViewHolder) convertView.getTag();
                }
                String gid = datas.get(position).gid;
                if (gid.contains("fuwa_c")) {
                    viewHolder.tv_type.setText("用    途: 寻宝");
                } else {
                    viewHolder.tv_type.setText("用    途: 社交");
                }


                if (datas.get(position).awarded) {     //因为复用问题，所以要做处理
                    viewHolder.award.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.award.setVisibility(View.INVISIBLE);
                }
                viewHolder.tv_fuwa_num.setText(datas.get(position).id + "号福娃");

                viewHolder.tv_from.setText("" + datas.get(position).creator);
                final String creatorid = datas.get(position).creatorid;

                viewHolder.tv_from.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {

                        initTribe(creatorid);
                    }
                });
                viewHolder.tv_catch.setText("捕获于: " + datas.get(position).pos);
                String uri = " fuwa:fuwa:" + datas.get(position).gid;

                // 给 ImageView 设置一个 tag
//                viewHolder.img_fuwa.setTag(uri);


//                MakeQRCodeUtil.createQRImage(uri, 800, 800, viewHolder.img_fuwa);  //生成普通二维码

                //生成带logo的二维码
                Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.fuwabig);
                Bitmap qrImage = MakeQRCodeUtil.createQRImage(getActivity(), uri, bitmap);
//                viewHolder.img_fuwa.setImageBitmap(qrImage);

                // 通过 tag 来防止图片错位
//                if (img_fuwa.getTag() != null && img_fuwa.getTag().equals(uri)) {


                viewHolder.img_fuwa.setImageBitmap(qrImage);
//                }

                container.addView(convertView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                return convertView;
            }
            return null;
        }


        public final class ViewHolder {
            public TextView tv_fuwa_num;
            public TextView tv_from;
            public TextView tv_catch;
            public TextView tv_type;
            private ImageView img_fuwa;
            private ImageView award;
        }
    }

    private void initTribe(String creatorid) {

        String url = HttpUrl.SEARCH_TRIBE_LIST;
        HttpUtils httpUtils = new HttpUtils(60 * 1000);//实例化RequestParams对象
        com.lidroid.xutils.http.RequestParams params = new com.lidroid.xutils.http.RequestParams();
        params.addBodyParameter("access_token", App.getInstance().getAccount().getAccess_token());
        url = url + "?user_id=" + creatorid;

        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                if (result != null) {
                    TribeEntity tribeEntity = JSON.parseObject(result, TribeEntity.class);
                    int code = tribeEntity.getCode();
                    if (code == 1) {
                        List<TribeEntity.ResultBean> beans = tribeEntity.getResult();
                        TribeEntity.ResultBean bean = beans.get(0);
                        String name = bean.getName();
                        int stribe_id = bean.getStribe_id();
                        int user_id = bean.getUser_id();

                        Intent intent = new Intent(getActivity(), ClanClubActivity.class);
                        intent.putExtra("isClan", 3);
                        intent.putExtra("name", name);
                        intent.putExtra("id", stribe_id+"");
                        intent.putExtra("user_id", user_id+"");
                        startActivity(intent);

                    } else {
                        //code==0 没数据，没部落 不作处理
                        showToast("该用户未创建部落",false);
                    }

                }


            }

            @Override
            public void onFailure(HttpException e, String s) {
                int code = e.getExceptionCode();
                if (code == 401) {
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_LOGOUT_RESETING);
                    App.getInstance().sendBroadcast(intent);
                } else {
                    showToast(e.getMessage(), false);
                }
            }
        });


    }


    //出售福娃dialog
    private void showSellFuwaDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        View view = inflater.inflate(R.layout.pop_fuwa_item2, null);

        sellDialog = new Dialog(context, R.style.dialog_ios_style);
        sellDialog.setContentView(view);
        sellDialog.setCancelable(true);
        sellDialog.setCanceledOnTouchOutside(false);
        et_price = (EditText) sellDialog.findViewById(R.id.et_price);
        tv_confirm = (TextView) sellDialog.findViewById(R.id.tv_confirm);

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


        tv_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                long time4 = System.currentTimeMillis();
                if (time4 - time3 > 1000) {
                    time3 = time4;

                    if (et_price.getText().length() > 0) {

                        if (Double.parseDouble(et_price.getText().toString()) == 0) {
                            ToastUtil.showShort(context, "出售金额必须大于0");
                        } else {
                            sellFuwa(Double.parseDouble(et_price.getText().toString()));
                        }
                    } else {
                        ToastUtil.showShort(context, "请填写出售金额");
                    }
                }

            }
        });
        img_cancle = (ImageView) sellDialog.findViewById(R.id.img_cancle);

        img_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellDialog.dismiss();
            }
        });

        //设置dialog大小
        Window dialogWindow = sellDialog.getWindow();
        WindowManager manager = ((FuwaPackageActivity) context).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9，根据实际情况调整
        dialogWindow.setAttributes(params);

        sellDialog.show();
    }

    //赠送福娃dialog
    private void showGiveFuwaDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // 不同页面加载不同的popup布局
        View view = inflater.inflate(R.layout.pop_fuwa_item3, null);

        giveDialog = new Dialog(context, R.style.dialog_ios_style);
        giveDialog.setContentView(view);
        giveDialog.setCancelable(true);
        giveDialog.setCanceledOnTouchOutside(false);
        et_code = (EditText) giveDialog.findViewById(R.id.et_price);
        tv_confirm2 = (TextView) giveDialog.findViewById(R.id.tv_confirm);
        RelativeLayout rl_scan = (RelativeLayout) giveDialog.findViewById(R.id.rl_scan);
        rl_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //扫描
                Intent intent = new Intent(context, CaptureActivity.class);
                startActivityForResult(intent, 1);
            }
        });


        tv_confirm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long time22 = System.currentTimeMillis();
                if (time22 - time11 > 1000) {
                    time11 = time22;
                    if (et_code.getText().length() > 0) {

                        giveFuwa(et_code.getText().toString());
                    } else {
                        ToastUtil.showShort(context, "请填写接收人口令");
                    }
                }
            }
        });

        img_cancle = (ImageView) giveDialog.findViewById(R.id.img_cancle);
        img_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giveDialog.dismiss();
            }
        });

        //设置dialog大小
        Window dialogWindow = giveDialog.getWindow();
        WindowManager manager = ((FuwaPackageActivity) context).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9，根据实际情况调整
        dialogWindow.setAttributes(params);

        giveDialog.show();
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
        url = url + "&sign=" + sigh + "&time=" + System.currentTimeMillis();
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
        url = url + "&sign=" + sigh + "&time=" + System.currentTimeMillis();
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

                break;

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        if (qrImage != null && !qrImage.isRecycled()) {
            // 回收并且置为null
            qrImage.recycle();
            qrImage = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            String code = data.getStringExtra("code");
            if(et_code!=null){
                et_code.setText(code);
            }

        }

    }
}
