package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;

import java.util.List;

import im.boss66.com.R;

/**
 * Created by GMARUnity on 2017/3/16.
 */
public class FuwaHideAddressAdapter extends RecyclerView.Adapter<FuwaHideAddressAdapter.FuViewHolder> {

    private List<PoiItem> poiItems;

    public FuwaHideAddressAdapter(List<PoiItem> poiItems) {
        this.poiItems = poiItems;
    }

    @Override
    public FuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fuwa_hide_address, parent, false);
        return new FuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FuViewHolder holder, int position) {
        PoiItem item = poiItems.get(position);
        if (item != null) {
            String name = item.getTitle();
            String address = item.getSnippet();
            holder.tv_title.setText("" + name);
            holder.tv_content.setText("" + address);
            Log.i("info", "================title:" + item.getTitle());
            Log.i("info", "================BusinessArea:" + item.getBusinessArea());
            Log.i("info", "================CityName:" + item.getCityName());
            Log.i("info", "================Snippet:" + item.getSnippet());
            Log.i("info", "================getAdName:" + item.getAdName());
            Log.i("info", "================IndoorData:" + item.getIndoorData().getFloorName());
        }
    }

    public void onDataChange(List<PoiItem> poiItems) {
        this.poiItems = poiItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return poiItems.size();
    }

    public static class FuViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title, tv_content;

        public FuViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
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
