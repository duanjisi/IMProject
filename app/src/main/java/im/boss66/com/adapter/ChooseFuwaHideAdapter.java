package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.entity.FuwaEntity;
import im.boss66.com.widget.RoundImageView;

/**
 * Created by GMARUnity on 2017/3/15.
 */
public class ChooseFuwaHideAdapter extends RecyclerView.Adapter<ChooseFuwaHideAdapter.FuViewHolder> {

    private List<FuwaEntity.Data> list;

    public ChooseFuwaHideAdapter(Context context, List<FuwaEntity.Data> bi) {
        list = bi;
    }

    @Override
    public FuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hide_fuwa, parent, false);
        return new FuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FuViewHolder holder, int position) {
        FuwaEntity.Data item = list.get(position);
        if (item != null) {
            List<String> num_list = item.getIdList();
            if (num_list != null) {
                holder.tv_fu_num.setText("" + num_list.size());
            }
            holder.tv_serial_number.setText(item.getId());
        }
    }

    public void onDataChange(List<FuwaEntity.Data> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class FuViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_serial_number, tv_fu_num;
        private ImageView iv_sel;

        public FuViewHolder(View itemView) {
            super(itemView);
            tv_serial_number = (TextView) itemView.findViewById(R.id.tv_serial_number);
            tv_fu_num = (TextView) itemView.findViewById(R.id.tv_fu_num);
            iv_sel = (ImageView) itemView.findViewById(R.id.iv_sel);
        }
    }

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private View childView;
        private RecyclerView touchView;

        public RecyclerItemClickListener(Context context, final OnItemClickListener mListener) {
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent ev) {
                    if (childView != null && mListener != null) {
                        mListener.onItemClick(childView, touchView.getChildPosition(childView));
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent ev) {
                    if (childView != null && mListener != null) {
                        mListener.onLongClick(childView, touchView.getChildPosition(childView));
                    }
                }
            });
        }

        GestureDetector mGestureDetector;

        @Override
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            mGestureDetector.onTouchEvent(motionEvent);
            childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
            touchView = recyclerView;
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);

            public void onLongClick(View view, int posotion);
        }
    }

}
