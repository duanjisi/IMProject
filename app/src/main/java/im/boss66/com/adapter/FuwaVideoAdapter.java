package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import im.boss66.com.R;
import im.boss66.com.entity.FuwaVideoEntity;

/**
 * Created by liw on 2017/6/1.
 */

public class FuwaVideoAdapter extends BaseRecycleViewAdapter {

    private Context context;

//    private List<Integer> mHeights;
    public FuwaVideoAdapter(Context context) {
        this.context = context;
//        mHeights = new ArrayList<>();
    }


    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, final int position) {
        FuwaVideoHolder holder1 = (FuwaVideoHolder) holder;

        FuwaVideoEntity.DataBean item = (FuwaVideoEntity.DataBean) datas.get(position);


        // 随机高度, 模拟瀑布效果.
//        if (mHeights.size() <= position) {
//            double random = Math.random();
//            if(random>0.5){
//                mHeights.add(400);
//            }else {
//                mHeights.add(200);
//            }
//        }

        String height = item.getHeight();
        Integer img_height = Integer.parseInt(height);

        ViewGroup.LayoutParams lp = holder1.rl_top.getLayoutParams();
        lp.height = img_height/3;
        Log.i("liwya",lp.height+"");
        holder1.rl_top.setLayoutParams(lp);

        String video = item.getVideo();
        video = video.substring(0, video.lastIndexOf("."))+".jpg";
        Log.i("liwya2",video);
        Glide.with(context).load(video).into(holder1.img_content);

        Glide.with(context).load(item.getAvatar()).into(holder1.img_head);
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
    }
}
