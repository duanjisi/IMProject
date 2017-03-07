package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.CircleMsgListEntity;

/**
 * Created by GMARUnity on 2017/3/6.
 */
public class CircleMessageListAdapter extends BaseRecycleViewAdapter {

    private ImageLoader imageLoader;
    private static int mscrenW;

    public CircleMessageListAdapter(Context context) {
        mscrenW = UIUtils.getScreenWidth(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_circle_msg_list, parent, false);
        return new CircleMessageHolderView(view);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        CircleMsgListEntity.CircleMsgItem item = (CircleMsgListEntity.CircleMsgItem) getDatas().get(position);
        if (item != null) {
            CircleMessageHolderView holderView = ((CircleMessageHolderView) holder);
            holderView.tv_name.setText("" + item.getUser_name());
            holderView.tv_content.setText("" + item.getMsg_content());
            holderView.tv_time.setText("" + item.getAdd_time());
            imageLoader.displayImage(item.getAvatar(), holderView.iv_head,
                    ImageLoaderUtils.getDisplayImageOptions());
            String circleMsg = item.getFeed_file();
            if (!TextUtils.isEmpty(circleMsg)) {
                if (circleMsg.contains(".mp4")) {
                    holderView.iv_video_play.setVisibility(View.VISIBLE);
                } else {
                    holderView.iv_video_play.setVisibility(View.GONE);
                }
                imageLoader.displayImage(circleMsg, holderView.iv_content,
                        ImageLoaderUtils.getDisplayImageOptions());
            } else {
                holderView.iv_video_play.setVisibility(View.GONE);
            }
        }
    }

    public static class CircleMessageHolderView extends RecyclerView.ViewHolder {

        private ImageView iv_head, iv_video_play, iv_content;
        private TextView tv_name, tv_content, tv_time, tv_circle_msg;

        public CircleMessageHolderView(View itemView) {
            super(itemView);
            iv_content = (ImageView) itemView.findViewById(R.id.iv_content);
            iv_head = (ImageView) itemView.findViewById(R.id.iv_head);
            iv_video_play = (ImageView) itemView.findViewById(R.id.iv_video_play);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_circle_msg = (TextView) itemView.findViewById(R.id.tv_circle_msg);
            RelativeLayout.LayoutParams headParam = (RelativeLayout.LayoutParams) iv_head.getLayoutParams();
            headParam.width = mscrenW / 8;
            headParam.height = mscrenW / 8;
            iv_head.setLayoutParams(headParam);

            RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) iv_content.getLayoutParams();
            contentParam.width = mscrenW / 7;
            contentParam.height = mscrenW / 7;
            iv_content.setLayoutParams(contentParam);

            RelativeLayout.LayoutParams contentParam_ = (RelativeLayout.LayoutParams) iv_video_play.getLayoutParams();
            contentParam_.width = mscrenW / 10;
            contentParam_.height = mscrenW / 10;
            iv_video_play.setLayoutParams(contentParam_);
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
