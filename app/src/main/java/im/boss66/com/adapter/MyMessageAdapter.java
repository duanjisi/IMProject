package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import im.boss66.com.R;
import im.boss66.com.entity.MyMessage;

/**
 * Created by liw on 2017/2/23.
 */
public class MyMessageAdapter extends BaseRecycleViewAdapter {
    private Context context;

    public MyMessageAdapter(Context context) {

        this.context = context;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final MyMessageHolder holder1 = (MyMessageHolder) holder;
        MyMessage msg = (MyMessage) datas.get(position);
        Glide.with(context).load(msg.getImg1()).into(holder1.img_other);
        Glide.with(context).load(msg.getImg2()).into(holder1.img_content);
        holder1.tv_other.setText(msg.getTv1());
        holder1.tv_comment.setText(msg.getTv2());
        holder1.tv_time.setText(msg.getTv3());

        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder1.getAdapterPosition();
                itemListener.onItemClick(adapterPosition);
            }
        });



    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_my_message,parent,false);
        return new MyMessageHolder(view);
    }

    @Override
    public int getItemCount() {
        return datas!=null?datas.size():0;
    }
    public static class MyMessageHolder extends RecyclerView.ViewHolder{
        public ImageView img_other;
        public ImageView img_content;
        public TextView tv_other;
        public TextView tv_comment;
        public TextView tv_time;


        public MyMessageHolder(View itemView) {
            super(itemView);
            img_other = (ImageView) itemView.findViewById(R.id.img_other);
            img_content = (ImageView) itemView.findViewById(R.id.img_content);
            tv_other = (TextView) itemView.findViewById(R.id.tv_other);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }


}
