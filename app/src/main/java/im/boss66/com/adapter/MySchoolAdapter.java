package im.boss66.com.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import im.boss66.com.R;
import im.boss66.com.entity.MySchool;

/**
 * Created by admin on 2017/2/20.
 */
public class MySchoolAdapter extends BaseRecycleViewAdapter{
    private Context context;

    public MySchoolAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.my_shchool,parent,false);
        return new MySchoolholder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MySchoolholder holder1  = (MySchoolholder) holder;

        MySchool item = (MySchool)datas.get(position);
        holder1.tv_school_name.setText(item.getSchoolname());
        holder1.tv_school_info.setText(item.getSchoolinfo());
        Glide.with(context).load(item.getImg()).into(holder1.img_school);
        holder1.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = holder1.getAdapterPosition();
                itemListener.onItemClick(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class MySchoolholder extends RecyclerView.ViewHolder{
        public ImageView img_school;
        public TextView tv_school_name;
        public TextView tv_school_info;


        public MySchoolholder(View itemView) {
            super(itemView);
            img_school = (ImageView) itemView.findViewById(R.id.img_school);
            tv_school_name = (TextView) itemView.findViewById(R.id.tv_school_name);
            tv_school_info = (TextView) itemView.findViewById(R.id.tv_school_info);
        }
    }
}
