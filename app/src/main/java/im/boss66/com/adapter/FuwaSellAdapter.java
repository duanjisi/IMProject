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
import im.boss66.com.entity.FuwaSellEntity;

/**
 * Created by liw on 2017/3/15.
 */
public class FuwaSellAdapter extends BaseRecycleViewAdapter {
    private Context context;

    public boolean chooses[];

    public FuwaSellAdapter(Context context) {
        this.context = context;
    }


    public void setChooses() {
        chooses = new boolean[datas.size()];
        for (int i = 0; i < datas.size(); i++) {
            chooses[i] = false;
        }
    }


    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final FuwaSellHolder holder1 = (FuwaSellHolder) holder;

        if(datas.size()>0){
            FuwaSellEntity.DataBean item = (FuwaSellEntity.DataBean) datas.get(position);
            holder1.tv_number.setText(item.getFuwaid()+"");
            holder1.tv_price.setText(item.getAmount() + "");

            if (chooses[position]) {
                holder1.img_choose.setVisibility(View.VISIBLE);
            } else {
                holder1.img_choose.setVisibility(View.INVISIBLE);
            }
            holder1.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = holder1.getAdapterPosition();
                    itemListener.onItemClick(adapterPosition);
                }
            });

        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fuwa_sell_list, parent, false);
        return new FuwaSellHolder(view);
    }

    @Override
    public int getItemCount() {
        return datas!=null?datas.size():0;
    }

    public class FuwaSellHolder extends RecyclerView.ViewHolder {
        private TextView tv_number;
        private TextView tv_price;
        private ImageView img_choose;

        public FuwaSellHolder(View itemView) {
            super(itemView);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            img_choose = (ImageView) itemView.findViewById(R.id.img_choose);
        }
    }
}
