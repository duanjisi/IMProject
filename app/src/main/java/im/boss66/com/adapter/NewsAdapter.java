package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.entity.NewsEntity;

/**
 * Created by liw on 2017/3/8.
 */
public class NewsAdapter extends BaseRecycleViewAdapter{
    private Context context;

    public NewsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final NewsViewholder holder1 = (NewsViewholder) holder;
        NewsEntity.ResultBean item  = (NewsEntity.ResultBean) datas.get(position);
        holder1.tv_name.setText(item.getTitle());
        holder1.tv_content.setText(item.getContent());
        Glide.with(context).load(item.getPics().get(0)).into(holder1.img_headimg);
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_famous_people, parent, false);

        return new NewsViewholder(view);
    }

    @Override
    public int getItemCount() {
        return datas!=null?datas.size():0;
    }

    class NewsViewholder extends RecyclerView.ViewHolder{

        private TextView tv_name;
        private TextView tv_content;
        private ImageView img_headimg;

        public NewsViewholder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            img_headimg = (ImageView) itemView.findViewById(R.id.img_headimg);
        }
    }
}
