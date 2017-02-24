package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import im.boss66.com.R;
import im.boss66.com.Utils.ToastUtil;
import im.boss66.com.entity.MyFollow;

/**
 * Created by admin on 2017/2/24.
 */
public class RecommendAdapter extends BaseRecycleViewAdapter{
    private Context context;

    public RecommendAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final RecommendAdapter.MyRecommendHolder holder1 = (RecommendAdapter.MyRecommendHolder) holder;
        MyFollow item = (MyFollow) datas.get(position);
        Glide.with(context).load(item.getImg()).into(holder1.img_follow);
        holder1.tv_follow_name.setText(item.getTv1());
        holder1.tv_same.setText(item.getTv2());
        holder1.tv_follow_content.setText(item.getTv3());


        holder1.tv_add_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.show(context,"添加好友", Toast.LENGTH_SHORT);
            }
        });


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_my_follow2, parent, false);
        return new RecommendAdapter.MyRecommendHolder(view);
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public static class MyRecommendHolder extends RecyclerView.ViewHolder {
        public ImageView img_follow;
        public TextView tv_follow_name;
        public TextView tv_follow_content;
        public TextView tv_same;
        public TextView tv_add_follow;

        public MyRecommendHolder(View itemView) {
            super(itemView);
            img_follow = (ImageView) itemView.findViewById(R.id.img_follow);
            tv_follow_name = (TextView) itemView.findViewById(R.id.tv_follow_name);
            tv_follow_content = (TextView) itemView.findViewById(R.id.tv_follow_content);
            tv_same = (TextView) itemView.findViewById(R.id.tv_same);
            tv_add_follow = (TextView) itemView.findViewById(R.id.tv_add_follow);
        }

    }
}
