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
import java.util.Map;

import im.boss66.com.R;
import im.boss66.com.entity.FuwaEntity;
import im.boss66.com.entity.FuwaListEntity;
import im.boss66.com.entity.MyFuwaEntity;

/**
 * Created by liw on 2017/3/14.
 */
public class FuwaListAdaper extends  BaseRecycleViewAdapter{
    private Context context;
    private Map<String,List<MyFuwaEntity.DataBean>>  map;

    public FuwaListAdaper(Context context) {
        this.context = context;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final FuwaListHodler hodler1 = (FuwaListHodler) holder;

        FuwaEntity.Data item = (FuwaEntity.Data) datas.get(position);
        if (item != null) {
            List<String> num_list = item.getIdList();
            if (num_list != null) {
                hodler1.tv_count.setText("" + num_list.size());
            }
            hodler1.tv_number.setText(item.getId());
        }

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
        private TextView tv_count;

        public FuwaListHodler(View itemView) {
            super(itemView);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            tv_count = (TextView) itemView.findViewById(R.id.tv_count);
        }
    }

}
