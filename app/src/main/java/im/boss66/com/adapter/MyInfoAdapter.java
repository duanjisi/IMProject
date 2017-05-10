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
import im.boss66.com.entity.MyInfo;

/**
 * Created by liw on 2017/2/20.
 */
public class MyInfoAdapter extends BaseRecycleViewAdapter{
    private Context context;

    public MyInfoAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        switch (viewType){
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.item_string,parent,false);
                viewHolder= new StringHolder(view);
                break;
            case 2:
                view = LayoutInflater.from(context).inflate(R.layout.my_shchool,parent,false);
                viewHolder = new MySchoolholder(view);
                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
       switch (holder.getItemViewType()){

           case 1:
               final StringHolder holder1 = (StringHolder) holder;
               MyInfo.ResultBean.SchoolListBean item = (MyInfo.ResultBean.SchoolListBean) datas.get(position);
               holder1.tv_name.setText(item.getType());
               String type = item.getType();
               if("我的宗亲".equals(type)||"我的商会".equals(type)){
                   holder1.tv_more.setVisibility(View.VISIBLE);
               }else{
                   holder1.tv_more.setVisibility(View.GONE);
               }
               holder1.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       int adapterPosition = holder1.getAdapterPosition();
                       itemListener.onItemClick(adapterPosition);
                   }
               });
               break;
           case 2:
               final MySchoolholder holder2 = (MySchoolholder) holder;
               MyInfo.ResultBean.SchoolListBean item1 = (MyInfo.ResultBean.SchoolListBean) datas.get(position);

               holder2.tv_school_name.setText(item1.getName());
               String brief_desc = item1.getBrief_desc();
//               brief_desc=  brief_desc.replaceAll("<br>","\n");
//               brief_desc=  brief_desc.replaceAll("&nbsp"," ");
               holder2.tv_school_info.setText(brief_desc);
               Glide.with(context).load(item1.getLogo()).into(holder2.img_school);
               holder2.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       int adapterPosition = holder2.getAdapterPosition();
                       itemListener.onItemClick(adapterPosition);
                   }
               });
               holder2.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                   @Override
                   public boolean onLongClick(View v) {
                       int adapterPosition = holder2.getAdapterPosition();
                       itemListener.onItemLongClick(adapterPosition);
                       return true;
                   }
               });

       }

    }

    @Override
    public int getItemCount() {
        return datas!=null?datas.size():0;
    }

    @Override
    public int getItemViewType(int position) {
        MyInfo.ResultBean.SchoolListBean data= (MyInfo.ResultBean.SchoolListBean) datas.get(position);
        if(data.getFrom()==0){
            return 1;
        }else{
            return 2;
        }


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

    public static class StringHolder extends RecyclerView.ViewHolder{

        public TextView tv_name;
        public TextView tv_more;
        public StringHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_more = (TextView) itemView.findViewById(R.id.tv_more);
        }
    }
}
