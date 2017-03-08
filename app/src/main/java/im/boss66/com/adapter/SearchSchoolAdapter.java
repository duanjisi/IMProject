package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.entity.SearchSchoolListEntity;

/**
 * Created by liw on 2017/3/8.
 */
public class SearchSchoolAdapter extends BaseRecycleViewAdapter{
    private Context context;

    public SearchSchoolAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final SearchSchoolHolder holder1 = (SearchSchoolHolder) holder;
        SearchSchoolListEntity.ResultBean item  = (SearchSchoolListEntity.ResultBean) datas.get(position);
        holder1.tv_school.setText(item.getName());
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_school,parent,false);
        return new SearchSchoolHolder(view);
    }

    @Override
    public int getItemCount() {
        return datas!=null?datas.size():0;
    }
    public static class SearchSchoolHolder extends RecyclerView.ViewHolder{
        private TextView tv_school;

        public SearchSchoolHolder(View itemView) {
            super(itemView);
            tv_school = (TextView) itemView.findViewById(R.id.tv_school);
        }
    }
}
