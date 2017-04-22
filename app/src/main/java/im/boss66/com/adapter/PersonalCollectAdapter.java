package im.boss66.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.personage.PersonalCollectActivity;
import im.boss66.com.entity.CollectEntity;
import im.boss66.com.widget.RoundImageView;

/**
 * Created by GMARUnity on 2017/4/21.
 */
public class PersonalCollectAdapter extends BaseRecycleViewAdapter {

    private static int mscrenW;
    private Context context;

    public PersonalCollectAdapter(Context context) {
        this.context = context;
        mscrenW = UIUtils.getScreenWidth(context);
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, final int position) {
        CollectEntity entity = (CollectEntity) datas.get(position);
        CollectHolderView holderView = (CollectHolderView) holder;
        if (entity != null) {
            Glide.with(context).load(entity.getFrom_avatar()).error(R.drawable.zf_default_album_grid_image).into(holderView.riv_head);
            String groupName = entity.getGroup_name();
            String name = entity.getFrom_name();
            if (TextUtils.isEmpty(groupName) || " ".equals(groupName)) {
                holderView.tv_name.setText(name);
            } else {
                holderView.tv_name.setText(groupName);
            }
            String time = entity.getAdd_time();
            holderView.tv_time.setText(time);
            String type = entity.getType();
            switch (type) {
                case "0":
                    holderView.tv_content.setVisibility(View.VISIBLE);
                    holderView.fl_video_dialog_img.setVisibility(View.GONE);
                    holderView.iv_content.setVisibility(View.GONE);
                    String tx = entity.getText();
                    holderView.tv_content.setText(tx);
                    break;
                case "1":
                    holderView.tv_content.setVisibility(View.GONE);
                    holderView.fl_video_dialog_img.setVisibility(View.GONE);
                    holderView.iv_content.setVisibility(View.VISIBLE);
                    Glide.with(context).load(entity.getUrl()).error(R.drawable.zf_default_album_grid_image).into(holderView.iv_content);
                    break;
                case "2":
                    holderView.tv_content.setVisibility(View.GONE);
                    holderView.fl_video_dialog_img.setVisibility(View.VISIBLE);
                    holderView.iv_content.setVisibility(View.GONE);
                    Glide.with(context).load(entity.getThum()).error(R.drawable.zf_default_album_grid_image).into(holderView.iv_video_img);
                    break;
            }
            holderView.ll_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.i("onLongClick:", "" + position);
                    ((PersonalCollectActivity) context).showDelectDiaglog(position);
                    return false;
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collect_personal, parent, false);
        return new CollectHolderView(view);
    }

    @Override
    public int getItemCount() {
        return datas.size();//有head需要加1
    }

    public static class CollectHolderView extends RecyclerView.ViewHolder {

        private RoundImageView riv_head;
        private ImageView iv_content, iv_video_img, iv_video_play;
        private FrameLayout fl_video_dialog_img;
        private TextView tv_name, tv_content, tv_time;
        private LinearLayout ll_parent;

        public CollectHolderView(View itemView) {
            super(itemView);
            ll_parent = (LinearLayout) itemView.findViewById(R.id.ll_parent);
            fl_video_dialog_img = (FrameLayout) itemView.findViewById(R.id.fl_video_dialog_img);
            iv_video_play = (ImageView) itemView.findViewById(R.id.iv_video_play);
            iv_content = (ImageView) itemView.findViewById(R.id.iv_content);
            riv_head = (RoundImageView) itemView.findViewById(R.id.riv_head);
            iv_video_img = (ImageView) itemView.findViewById(R.id.iv_video_img);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);

            RelativeLayout.LayoutParams tvParam = (RelativeLayout.LayoutParams) tv_name.getLayoutParams();
            tvParam.width = mscrenW / 5 * 2;
            tv_name.setLayoutParams(tvParam);

            RelativeLayout.LayoutParams headParam = (RelativeLayout.LayoutParams) riv_head.getLayoutParams();
            headParam.width = mscrenW / 10;
            headParam.height = mscrenW / 10;
            riv_head.setLayoutParams(headParam);

            LinearLayout.LayoutParams contentParam = (LinearLayout.LayoutParams) iv_content.getLayoutParams();
            contentParam.width = mscrenW / 5 * 3;
            contentParam.height = mscrenW / 5 * 2;
            iv_content.setLayoutParams(contentParam);

            FrameLayout.LayoutParams contentParam_ = (FrameLayout.LayoutParams) iv_video_img.getLayoutParams();
            contentParam_.width = mscrenW / 5 * 3;
            contentParam_.height = mscrenW / 5 * 2;
            iv_video_img.setLayoutParams(contentParam_);
            FrameLayout.LayoutParams videoParam_ = (FrameLayout.LayoutParams) iv_video_play.getLayoutParams();
            videoParam_.width = mscrenW / 10;
            videoParam_.height = mscrenW / 10;
            iv_video_play.setLayoutParams(videoParam_);
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
            void onItemClick(View view, int position);

            void onLongClick(View view, int posotion);
        }
    }
}
