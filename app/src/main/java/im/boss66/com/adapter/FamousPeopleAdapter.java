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
import im.boss66.com.entity.FamousPeopleEntity;

/**
 * Created by liw on 2017/3/7.
 */
public class FamousPeopleAdapter extends BaseRecycleViewAdapter{
    private Context context;

    public FamousPeopleAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        FamousPeopleHodler hodler1 = (FamousPeopleHodler) holder;
        FamousPeopleEntity.ResultBean item = (FamousPeopleEntity.ResultBean) datas.get(position);

        hodler1.tv_name.setText(item.getName());
        hodler1.tv_content.setText(item.getDesc());
        Glide.with(context).load(item.getPhoto()).into(hodler1.img_headimg);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_famous_people,parent,false);

        return new FamousPeopleHodler(view);
    }

    @Override
    public int getItemCount() {
        return datas!=null?datas.size():0;
    }

    public static class FamousPeopleHodler extends RecyclerView.ViewHolder{

        private ImageView img_headimg;
        private TextView tv_name;
        private TextView tv_content;

        public FamousPeopleHodler(View itemView) {
            super(itemView);
            img_headimg = (ImageView) itemView.findViewById(R.id.img_headimg);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
