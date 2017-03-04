package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.entity.ChooseLikeEntity;

/**
 * Created by admin on 2017/3/3.
 */

public class ChooseLikeAdapter extends BaseRecycleViewAdapter {
    private Context context;
    private List<Boolean> isChoose;
    private  ArrayList<ChooseLikeEntity.ResultBean> datas ;

    public ChooseLikeAdapter(Context context,ArrayList<ChooseLikeEntity.ResultBean> datas) {
        this.datas = datas;
        this.context = context;
        isChoose =new ArrayList<>();
        for(int  i =0;i<datas.size();i++){
            isChoose.add(false);
        }
    }

    public List<Boolean> getIsChoose() {
        return isChoose;
    }


    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final ChooseLikeHodler hodler1 = (ChooseLikeHodler) holder;
        String s = (String) datas.get(position).getTag_name();
        if(isChoose.get(position)){
            hodler1.tv_like.setBackgroundResource(R.drawable.shape_cancle_edit_grey);
        }else{
            hodler1.tv_like.setBackgroundResource(R.drawable.shape_cancle_edit_white);
        }
        hodler1.tv_like.setText(s);
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_choose_like, parent, false);
        return new ChooseLikeHodler(view);
    }

    @Override
    public int getItemCount() {
        return datas!=null?datas.size():0;
    }

    public static class ChooseLikeHodler extends RecyclerView.ViewHolder{
        public TextView tv_like;

        public ChooseLikeHodler(View itemView) {
            super(itemView);
            tv_like = (TextView) itemView.findViewById(R.id.tv_like);
        }

    }
}
