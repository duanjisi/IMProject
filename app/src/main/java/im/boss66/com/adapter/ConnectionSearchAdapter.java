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
import im.boss66.com.entity.ConnectionAllSearch;
import im.boss66.com.entity.MyInfo;

/**
 * Created by liw on 2017/6/19.
 */

public class ConnectionSearchAdapter extends BaseRecycleViewAdapter {

    private Context context;

    public ConnectionSearchAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 1:
                final MySchoolholder holder2 = (MySchoolholder) holder;
                ConnectionAllSearch.ResultBean.Bean item1 = (ConnectionAllSearch.ResultBean.Bean) datas.get(position);
                int type = item1.getType();
                switch (type) {
                    case 1:
                        holder2.tv_school_name.setText(item1.getName());
                        holder2.tv_school_info.setText(item1.getDesc());
                        Glide.with(context).load(item1.getLogo()).into(holder2.img_school);

                        break;
                    case 2:
                        holder2.tv_school_name.setText(item1.getName());
                        holder2.tv_school_info.setText(item1.getDesc());

                        break;
                    case 3:
                        //3的是加好友布局
                        break;
                    case 4:
                        holder2.tv_school_name.setText(item1.getName());
                        holder2.tv_school_info.setText(item1.getDesc());
                        break;
                }


                holder2.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int adapterPosition = holder2.getAdapterPosition();
                        itemListener.onItemClick(adapterPosition);
                    }
                });

                break;
            case 2:
                final StringHolder holder1 = (StringHolder) holder;
                ConnectionAllSearch.ResultBean.Bean item = (ConnectionAllSearch.ResultBean.Bean) datas.get(position);

                int from = item.getFrom();
                switch (from) {
                    case 1:
                        holder1.tv_name.setText("宗亲人脉");

                        break;
                    case 2:
                        holder1.tv_name.setText("家乡人脉");

                        break;
                    case 3:
                        holder1.tv_name.setText("好友人脉");

                        break;
                    case 4:
                        holder1.tv_name.setText("学校人脉");
                        break;
                }


                holder1.tv_more.setVisibility(View.GONE);

                break;

            case 3:
                final MyRecommendHolder holder3 = (MyRecommendHolder) holder;
                ConnectionAllSearch.ResultBean.Bean item3 = (ConnectionAllSearch.ResultBean.Bean) datas.get(position);
                Glide.with(context).load(item3.getAvatar()).into(holder3.img_follow);
                holder3.tv_follow_name.setText(item3.getUser_name());
                holder3.tv_same.setText("相似度" + item3.getSimilar() + "%"); //相似度

                holder3.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int adapterPosition = holder3.getAdapterPosition();
                        itemListener.onItemClick(adapterPosition);
                    }
                });
                break;

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.my_shchool, parent, false);
                viewHolder = new MySchoolholder(view);
                break;
            case 2:
                view = LayoutInflater.from(context).inflate(R.layout.item_string, parent, false);
                viewHolder = new StringHolder(view);
                break;
            case 3:
                view = LayoutInflater.from(context).inflate(R.layout.item_my_follow2, parent, false);
                viewHolder = new MyRecommendHolder(view);
                break;

        }

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        ConnectionAllSearch.ResultBean.Bean data = ( ConnectionAllSearch.ResultBean.Bean) datas.get(position);
        if (data.getUser_id() != null) {
            return 3;   //加好友布局
        } else {
            if (data.getFrom() == 0) {
                return 1; //通用布局
            } else {
                return 2; //title布局
            }
        }


    }


    public static class MySchoolholder extends RecyclerView.ViewHolder {
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

    public static class StringHolder extends RecyclerView.ViewHolder {

        public TextView tv_name;
        public TextView tv_more;

        public StringHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_more = (TextView) itemView.findViewById(R.id.tv_more);
        }
    }

    public static class MyRecommendHolder extends RecyclerView.ViewHolder {
        public ImageView img_follow;
        public TextView tv_follow_name;
        public TextView tv_follow_content;

        public TextView tv_same;
        public TextView tv_add_follow;

        public MyRecommendHolder(View itemView) {
            super(itemView);
            img_follow = (ImageView) itemView.findViewById(R.id.img_follow);
            tv_follow_name = (TextView) itemView.findViewById(R.id.tv_follow_name);
            tv_follow_content = (TextView) itemView.findViewById(R.id.tv_follow_content);

            tv_same = (TextView) itemView.findViewById(R.id.tv_same);
            tv_add_follow = (TextView) itemView.findViewById(R.id.tv_add_follow);
        }

    }
}
