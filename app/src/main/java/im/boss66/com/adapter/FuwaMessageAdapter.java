package im.boss66.com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.widget.RoundImageView;

/**
 * Created by GMARUnity on 2017/3/20.
 */
public class FuwaMessageAdapter extends RecyclerView.Adapter<FuwaMessageAdapter.FuwaMessageView> {


    @Override
    public FuwaMessageView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fuwa_msg, parent, false);
        return new FuwaMessageView(view);
    }

    @Override
    public void onBindViewHolder(FuwaMessageView holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class FuwaMessageView extends RecyclerView.ViewHolder {

        RoundImageView iv_head;
        View v_point;
        TextView tv_title, tv_content, tv_time;

        public FuwaMessageView(View itemView) {
            super(itemView);
            iv_head = (RoundImageView) itemView.findViewById(R.id.iv_head);
            v_point = itemView.findViewById(R.id.v_point);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
