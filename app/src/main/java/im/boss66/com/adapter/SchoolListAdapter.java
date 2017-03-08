package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.entity.SchoolListEntity;

/**
 * Created by liw on 2017/3/6.
 */
public class SchoolListAdapter extends BaseRecycleViewAdapter {
    private Context context;

    public SchoolListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final SchoolListAdapter.SchoolListHolder holder1  = (SchoolListAdapter.SchoolListHolder) holder;
        SchoolListEntity.ResultBean item = (SchoolListEntity.ResultBean) datas.get(position);
        item.getLevel();
        if(item.getLevel()==5){
            holder1.tv_school_department.setVisibility(View.VISIBLE);
            holder1.tv_school_department.setText(item.getNote());
        }else {
            holder1.tv_school_department.setVisibility(View.GONE);
        }
        holder1.tv_school_name.setText(item.getSchool_name());
        holder1.tv_school_time.setText(item.getEdu_year()+"入学");
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder1.getAdapterPosition();
                itemListener.onItemClick(adapterPosition);

            }
        });
        holder1.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int adapterPosition = holder1.getAdapterPosition();
                itemListener.onItemLongClick(adapterPosition);
                return true;
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_school_info,parent,false);
        return new SchoolListHolder(view);
    }

    @Override
    public int getItemCount() {
        return datas!=null?datas.size():0;
    }

    public class SchoolListHolder extends RecyclerView.ViewHolder{
        private TextView tv_school_name ,tv_school_department ,tv_school_time;

        public SchoolListHolder(View itemView) {
            super(itemView);
            tv_school_name = (TextView) itemView.findViewById(R.id.tv_school_name);
            tv_school_department = (TextView) itemView.findViewById(R.id.tv_school_department);
            tv_school_time = (TextView) itemView.findViewById(R.id.tv_school_time);

        }
    }
}
