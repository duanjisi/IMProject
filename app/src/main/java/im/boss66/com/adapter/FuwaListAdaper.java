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
import im.boss66.com.entity.FuwaListEntity;

/**
 * Created by liw on 2017/3/14.
 */
public class FuwaListAdaper extends  BaseRecycleViewAdapter{
    private Context context;

    public FuwaListAdaper(Context context) {
        this.context = context;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final FuwaListHodler hodler1 = (FuwaListHodler) holder;

        FuwaListEntity item = (FuwaListEntity) datas.get(position);
        hodler1.tv_number.setText(item.getS1());
        hodler1.tv_count.setText(item.getS2());
        Glide.with(context).load(item.getS3()).into(hodler1.img_fuwa);
        hodler1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = hodler1.getAdapterPosition();
                itemListener.onItemClick(adapterPosition);
            }
        });
}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fuwa_list,parent,false);
        return new FuwaListHodler(view);
    }

    @Override
    public int getItemCount() {
        return datas!=null?datas.size():0;
    }

    public class FuwaListHodler extends RecyclerView.ViewHolder{
        private TextView tv_number;
        private ImageView img_fuwa;
        private TextView tv_count;

        public FuwaListHodler(View itemView) {
            super(itemView);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            tv_count = (TextView) itemView.findViewById(R.id.tv_count);
            img_fuwa = (ImageView) itemView.findViewById(R.id.img_fuwa);
        }
    }
}
