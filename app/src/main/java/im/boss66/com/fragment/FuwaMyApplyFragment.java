package im.boss66.com.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import java.util.LinkedList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.MakeQRCodeUtil;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.activity.treasure.FuwaPackageActivity;
import im.boss66.com.adapter.FuwaListAdaper;
import im.boss66.com.entity.FuwaEntity;
import im.boss66.com.http.HttpUrl;
import im.boss66.com.listener.RecycleViewItemListener;

/**
 * Created by liw on 2017/5/3.
 */

public class FuwaMyApplyFragment extends BaseFragment implements View.OnClickListener{
    private RecyclerView rcv_fuwalist;
    private FuwaListAdaper adaper;
    private String uid;


    private List<FuwaEntity.Data> fuwaList = new ArrayList<>();

    private boolean first = true;  //第一次进页面存储福娃gid list
    private List<String> gidList;
    private Long time1 = 0L;


    private Dialog dialog;  //福娃详情
    private int choosePosition;
    private int vp_position;
    private TextView tv_num;
    private TextView tv_count;
    private ImageView img_left;
    private ImageView img_right;
    private ViewPager vp_fuwa;

    private int count; //福娃vp 页数
    private ViewAdapter viewAdapter;
    private List<FuwaEntity.FuwaDetail> fuwas;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fuwa_my_apply, container, false);
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
        String url = HttpUrl.QUERY_MY_APPLY_FUWA + uid + "&time=" + System.currentTimeMillis();
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
                        fuwas.add(new FuwaEntity.FuwaDetail(bill.getGid(), bill.getId(), bill.isAwarded(), bill.getPos(), bill.getCreator()));
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
                    fuwas.add(new FuwaEntity.FuwaDetail(bill.getGid(), bill.getId(), bill.isAwarded(), bill.getPos(), bill.getCreator()));
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
        rcv_fuwalist.setAdapter(adaper);

        adaper.setItemListener(new RecycleViewItemListener() {
            @Override
            public void onItemClick(int postion) {
                //弹pop
                long time2 = System.currentTimeMillis();
                if (time2 - time1 > 500L) {
                    choosePosition = postion;
                    vp_position = 0; //点击后给vp_position设为0
                    if (fuwaList.get(postion).getIdList() != null && fuwaList.get(postion).getIdList().size() > 0) {
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
    }

    @Override
    public void onClick(View v) {

    }

    //弹第一个福娃dialog
    private void showFuwaDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_fuwa_new, null);

        dialog = new Dialog(context, R.style.dialog_ios_style);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        tv_num = (TextView) view.findViewById(R.id.tv_num);
        tv_count = (TextView) view.findViewById(R.id.tv_count);

        img_left = (ImageView) view.findViewById(R.id.img_left);
        img_right = (ImageView) view.findViewById(R.id.img_right);

        vp_fuwa = (ViewPager) view.findViewById(R.id.vp_fuwa);


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
                    TextView tv_number = (TextView) convertView.findViewById(R.id.tv_number);
                    ImageView img_fuwa = (ImageView) convertView.findViewById(R.id.img_fuwa);
                    ImageView award = (ImageView) convertView.findViewById(R.id.award);
                    RelativeLayout rl_top = (RelativeLayout) convertView.findViewById(R.id.rl_top);

                    viewHolder = new ViewAdapter.ViewHolder();
                    viewHolder.tv_fuwa_num = tv_fuwa_num;
                    viewHolder.tv_from = tv_from;
                    viewHolder.tv_type = tv_type;
                    viewHolder.tv_catch = tv_catch;
                    viewHolder.img_fuwa = img_fuwa;
                    viewHolder.award = award;
                    viewHolder.tv_number = tv_number;
                    viewHolder.rl_top = rl_top;

                    convertView.setTag(viewHolder);

                } else {
                    convertView = mViewCache.removeFirst();
                    viewHolder = (ViewAdapter.ViewHolder) convertView.getTag();
                }
                String gid = datas.get(position).gid;
                if(gid.contains("fuwa_c")){
                    viewHolder.tv_type.setText("用    途: 寻宝" );
                }else {
                    viewHolder.tv_type.setText("用    途: 社交" );
                }


                viewHolder.tv_fuwa_num.setText(datas.get(position).id + "号福娃");

                viewHolder.tv_from.setText("申请人: " + datas.get(position).creator);
                viewHolder.img_fuwa.setImageResource(R.drawable.fuwabig);
                viewHolder.rl_top.setVisibility(View.VISIBLE);
                viewHolder.tv_number.setText(datas.get(position).id+"");


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
            private RelativeLayout rl_top;
            private TextView tv_number;
        }
    }

}
