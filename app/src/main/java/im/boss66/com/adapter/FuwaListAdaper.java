package im.boss66.com.adapter;

import android.content.Context;
import android.graphics.Color;
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
            List<String> idList = item.getIdList();
            if (idList.size()>0) { //有的福娃
                hodler1.tv_count.setText("" + idList.size());
                hodler1.view.setBackgroundColor(0xffff7f5b);
                hodler1.view2.setBackgroundColor(0xffff7f5b);
                hodler1.tv_count.setTextColor(0xffd3000f);
                hodler1.tv_x.setTextColor(0xffd3000f);
                hodler1.tv_number.setTextColor(0xffd3000f);
                hodler1.img_top.setImageResource(R.drawable.number_bg);
                hodler1.img_fuwa.setImageResource(R.drawable.fuwabig);
                hodler1.img_fuwa.setImageResource(R.drawable.fuwabig);

            }else{ //没有的福娃
                hodler1.tv_count.setTextColor(Color.GRAY);
                hodler1.tv_count.setText(0+"");
                hodler1.view.setBackgroundColor(Color.GRAY);
                hodler1.view2.setBackgroundColor(Color.GRAY);
                hodler1.tv_x.setTextColor(Color.GRAY);
                hodler1.tv_number.setTextColor(Color.GRAY);
                hodler1.img_top.setImageResource(R.drawable.nonumber_bg);
                hodler1.img_fuwa.setImageResource(R.drawable.school);
                hodler1.img_fuwa.setImageResource(R.drawable.fuwabig_no);
            }
            hodler1.tv_number.setText(item.getId());
        }

        if(((FuwaEntity.Data) datas.get(position)).getIdList().size()>0){

            hodler1.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = hodler1.getAdapterPosition();
                    itemListener.onItemClick(adapterPosition);
                }
            });
        }

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
        private TextView tv_x;
        private View view;
        private View view2;
        private ImageView img_top;
        private ImageView img_fuwa;

        public FuwaListHodler(View itemView) {
            super(itemView);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            tv_count = (TextView) itemView.findViewById(R.id.tv_count);
            tv_x = (TextView) itemView.findViewById(R.id.tv_x);
            view = itemView.findViewById(R.id.view);
            view2 = itemView.findViewById(R.id.view2);
            img_top = (ImageView) itemView.findViewById(R.id.img_top);
            img_fuwa = (ImageView) itemView.findViewById(R.id.img_fuwa);
        }
    }

}
