package im.boss66.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import im.boss66.com.App;
import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.connection.ClanClubActivity;
import im.boss66.com.entity.FuwaVideoEntity;
import im.boss66.com.entity.TribeEntity;
import im.boss66.com.http.HttpUrl;

/**
 * Created by liw on 2017/6/1.
 */

public class FuwaVideoAdapter extends BaseRecycleViewAdapter {

    private Context context;
    private ImageLoader imageLoader;
    public FuwaVideoAdapter(Context context) {
        this.context = context;

        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }


    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, final int position) {
        FuwaVideoHolder holder1 = (FuwaVideoHolder) holder;

        final FuwaVideoEntity.DataBean item = (FuwaVideoEntity.DataBean) datas.get(position);


        String height = item.getHeight();
        String width = item.getWidth();
//        Log.i("liwya",width+"实际宽度");
        int screenWidth = UIUtils.getScreenWidth(context)/2;
        int padding = UIUtils.dip2px(context, 6);
        screenWidth = screenWidth-padding;
//        Log.i("liwya",screenWidth+"屏幕宽度");

//        Log.i("liwya",v+"比例");



        if(!TextUtils.isEmpty(height)){
            holder1.setVisibility(true);
            double v = Double.parseDouble(width)/screenWidth;

            Integer img_height = Integer.parseInt(height);
            ViewGroup.LayoutParams lp = holder1.img_content.getLayoutParams();
            lp.height = (int) (img_height/v);
//            Log.i("liwya",height+"实际高度");
//            Log.i("liwya",lp.height+"显示高度");
            holder1.img_content.setLayoutParams(lp);
        }else{
            holder1.setVisibility(false);
        }


        String video = item.getVideo();
        video = video.substring(0, video.lastIndexOf("."))+".jpg";
//        Glide.with(context).load(video).into(holder1.img_content);

        imageLoader.displayImage(video, holder1.img_content,
                ImageLoaderUtils.getDisplayImageOptions());


//        Glide.with(context).load(item.getAvatar()).into(holder1.img_head);


        imageLoader.displayImage(item.getAvatar(), holder1.img_head,
                ImageLoaderUtils.getDisplayImageOptions());

        holder1.tv_name.setText(item.getName());
        String distance = item.getDistance()+"";
        distance = distance.substring(0,distance.indexOf("."));

        holder1.tv_distance.setText(distance+"米");
        if("男".equals(item.getGender())){
            Glide.with(context).load(R.drawable.man_1).into(holder1.img_sex);
        }else{
            Glide.with(context).load(R.drawable.lady_1).into(holder1.img_sex);
        }
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.onItemClick(position);
            }
        });
        holder1.img_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTribe(item.getUserid());
            }
        });

        holder1.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTribe(item.getUserid());
            }
        });
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

                        Intent intent = new Intent(context, ClanClubActivity.class);
                        intent.putExtra("isClan", 3);
                        intent.putExtra("name", name);
                        intent.putExtra("id", stribe_id+"");
                        intent.putExtra("user_id", user_id+"");
                        context.startActivity(intent);

                    } else {
                        //code==0 没数据，没部落 不作处理
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
                    ToastUtil.showShort(context,e.getMessage());
                }
            }
        });


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_fuwa_video,parent,false);
        return new FuwaVideoHolder(view);
    }

    @Override
    public int getItemCount() {
        return datas!=null?datas.size():0;
    }


    public static class FuwaVideoHolder extends RecyclerView.ViewHolder{
        public ImageView img_head;
        public ImageView img_sex;
        public ImageView img_content;

        public TextView tv_name;
        public TextView tv_distance;
        public RelativeLayout rl_top;


        public FuwaVideoHolder(View itemView) {
            super(itemView);
            img_head = (ImageView) itemView.findViewById(R.id.img_head);
            img_sex = (ImageView) itemView.findViewById(R.id.img_sex);
            img_content = (ImageView) itemView.findViewById(R.id.img_content);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_distance = (TextView) itemView.findViewById(R.id.tv_distance);
            rl_top = (RelativeLayout) itemView.findViewById(R.id.rl_top);
        }
        public void setVisibility(boolean isVisible){
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)itemView.getLayoutParams();
            if (isVisible){
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
            }else{
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
    }

}
