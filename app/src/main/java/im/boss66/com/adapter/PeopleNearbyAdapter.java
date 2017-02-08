package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.widget.RoundImageView;

/**
 * 附近的人
 */
public class PeopleNearbyAdapter extends RecyclerView.Adapter<PeopleNearbyAdapter.PeopleNearbyView>{

    private static int mscrenW;
    private MyItemClickListener mItemClickListener;
    private List<String> list;
    public PeopleNearbyAdapter(Context context, List<String> list){
        this.list = list;
        mscrenW = UIUtils.getScreenWidth(context)/8;
    }

    @Override
    public PeopleNearbyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_nearby, parent, false);
        return new PeopleNearbyView(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(PeopleNearbyView holder, int position) {
        String item = list.get(position);
        if (!TextUtils.isEmpty(item)){
            holder.tv_name.setText(""+item);
            holder.tv_des.setText(""+item +":"+position);
        }
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public void onDataChange(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public static class PeopleNearbyView extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MyItemClickListener mListener;
        private TextView tv_name, tv_distance,tv_des;
        private RoundImageView iv_head;

        public PeopleNearbyView(View itemView, MyItemClickListener mListener) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_distance = (TextView) itemView.findViewById(R.id.tv_distance);
            tv_des = (TextView) itemView.findViewById(R.id.tv_des);
            iv_head = (RoundImageView) itemView.findViewById(R.id.iv_head);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_head.getLayoutParams();
            params.width = mscrenW;
            params.height = mscrenW;
            this.mListener = mListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }

}
