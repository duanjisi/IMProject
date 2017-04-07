package im.boss66.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.TimeUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.discover.WebViewActivity;
import im.boss66.com.entity.FriendCircle;
import im.boss66.com.entity.PhotoInfo;
import im.boss66.com.widget.MultiImageView;

/**
 * 个人相册adapter
 */
public class PersonalPhotoAlbumAdapter extends BaseRecycleViewAdapter {

    private int PARENT_TYPE = 4;
    public final int TYPE_URL = 3;
    public final int TYPE_IMG = 1;
    public final int TYPE_VIDEO = 2;
    private Context context;
    private int sceenW;
    private boolean isSelt;

    public PersonalPhotoAlbumAdapter(Context context) {
        this.context = context;
        sceenW = UIUtils.getScreenWidth(context);
    }

    @Override
    public int getItemViewType(int position) {
        int itemType = 0;
        FriendCircle entity = (FriendCircle) getDatas().get(position);
        if (entity.isParent()) {
            itemType = PARENT_TYPE;
        } else {
            if (entity.getFeed_type() == TYPE_IMG) {
                itemType = TYPE_IMG;
            } else if (entity.getFeed_type() == TYPE_VIDEO) {
                itemType = TYPE_VIDEO;
            } else if (entity.getFeed_type() == TYPE_URL) {
                itemType = TYPE_URL;
            }
        }
        return itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == PARENT_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_album_parent, parent, false);
            viewHolder = new YearPartent(view);
        } else {
            if (viewType == TYPE_IMG) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_album_img, parent, false);
                viewHolder = new ImageHolder(view);
            } else if (viewType == TYPE_VIDEO) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_album_video, parent, false);
                viewHolder = new VideoHolder(view);
            } else if (viewType == TYPE_URL) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_album_url, parent, false);
                viewHolder = new URLHolder(view);
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
        final FriendCircle item = (FriendCircle) getDatas().get(position);
        boolean isparent = item.isParent();
        String time = item.getAdd_time();
        String content = item.getContent();
        FriendCircle nextItem = null;
        int lastpos = position - 1;
        if (lastpos >= 0 && lastpos < getDatas().size()) {
            nextItem = (FriendCircle) getDatas().get(lastpos);
        }
        if (isparent) {
            ((YearPartent) holder).tv_years.setText(time);
        } else {
            int type = item.getFeed_type();
            String curMonth = item.getTimeMonth();
            String curDay = item.getTimeDay();
            switch (type) {
                case TYPE_IMG:
                    final List<PhotoInfo> photos = item.getFiles();
                    if (photos != null && photos.size() > 0) {
                        ((ImageHolder) holder).ll_img.setVisibility(View.VISIBLE);
                        ((ImageHolder) holder).tv_no_img_tx.setVisibility(View.GONE);
                        ((ImageHolder) holder).tv_content.setText("" + content);
                        ((ImageHolder) holder).tv_content.setBackgroundColor(context.getResources().getColor(R.color.white));
                        int filesize = photos.size();
                        if (filesize >= 2) {
                            ((ImageHolder) holder).tv_num.setText("共" + filesize + "张");
                        } else if (filesize == 1) {
                            String p_url = photos.get(0).file_url;
                            if (!TextUtils.isEmpty(p_url)) {
                                if (!p_url.contains(".png") && !p_url.contains(".jpg")
                                        && !p_url.contains(".jpeg")) {
                                    ((ImageHolder) holder).ll_img.setVisibility(View.GONE);
                                    ((ImageHolder) holder).tv_no_img_tx.setVisibility(View.VISIBLE);
                                    ((ImageHolder) holder).tv_no_img_tx.setText("" + content);
                                } else {
                                    ((ImageHolder) holder).ll_img.setVisibility(View.VISIBLE);
                                    ((ImageHolder) holder).tv_no_img_tx.setVisibility(View.GONE);
                                }
                            } else {
                                ((ImageHolder) holder).ll_img.setVisibility(View.GONE);
                                ((ImageHolder) holder).tv_no_img_tx.setVisibility(View.VISIBLE);
                                ((ImageHolder) holder).tv_no_img_tx.setText("" + content);
                            }
                        }
                        ((ImageHolder) holder).multiImagView.setVisibility(View.VISIBLE);
                        ((ImageHolder) holder).multiImagView.setSceenW(sceenW);
                        ((ImageHolder) holder).multiImagView.setList(photos);
                    } else {
                        ((ImageHolder) holder).ll_img.setVisibility(View.GONE);
                        ((ImageHolder) holder).tv_no_img_tx.setVisibility(View.VISIBLE);
                        ((ImageHolder) holder).tv_no_img_tx.setText("" + content);
                    }
                    ((ImageHolder) holder).tv_month.setText("" + curMonth);
                    ((ImageHolder) holder).tv_day.setText("" + curDay);
                    if (nextItem == null) {
                        ((ImageHolder) holder).tv_month.setVisibility(View.VISIBLE);
                        ((ImageHolder) holder).tv_day.setVisibility(View.VISIBLE);
                        try {
                            long curTime = Long.parseLong(item.getAdd_time());
                            String resTime = TimeUtil.getTimeisTodayOrYestday(curTime * 1000);
                            if (!TextUtils.isEmpty(resTime)) {
                                if ("今天".equals(resTime) && isSelt) {
                                    ((ImageHolder) holder).tv_month.setVisibility(View.GONE);
                                    ((ImageHolder) holder).tv_day.setVisibility(View.GONE);
                                } else {
                                    ((ImageHolder) holder).tv_month.setVisibility(View.GONE);
                                    ((ImageHolder) holder).tv_day.setVisibility(View.VISIBLE);
                                }
                                ((ImageHolder) holder).tv_day.setText(resTime);
                            }
                        } catch (Exception e) {

                        }
                    } else {
                        String lastMonth = nextItem.getTimeMonth();
                        String lastDay = nextItem.getTimeDay();
                        if (!TextUtils.isEmpty(curMonth) && !TextUtils.isEmpty(curDay) &&
                                !TextUtils.isEmpty(lastMonth) && !TextUtils.isEmpty(lastDay)) {
                            if (curMonth.equals(lastMonth) && curDay.equals(lastDay)) {
                                ((ImageHolder) holder).tv_month.setVisibility(View.GONE);
                                ((ImageHolder) holder).tv_day.setVisibility(View.GONE);
                            } else {
                                ((ImageHolder) holder).tv_month.setVisibility(View.VISIBLE);
                                ((ImageHolder) holder).tv_day.setVisibility(View.VISIBLE);
                                try {
                                    long curTime = Long.parseLong(time);
                                    String resTime = TimeUtil.getTimeisTodayOrYestday(curTime * 1000);
                                    if (!TextUtils.isEmpty(resTime)) {
                                        if ("今天".equals(resTime) && isSelt) {
                                            ((ImageHolder) holder).tv_month.setVisibility(View.GONE);
                                            ((ImageHolder) holder).tv_day.setVisibility(View.GONE);
                                        } else {
                                            ((ImageHolder) holder).tv_month.setVisibility(View.GONE);
                                            ((ImageHolder) holder).tv_day.setVisibility(View.VISIBLE);
                                        }
                                        ((ImageHolder) holder).tv_day.setText(resTime);
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                    break;
                case TYPE_VIDEO:
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ((VideoHolder) holder).iv_video_bg.getLayoutParams();
                    params.width = sceenW / 4;
                    params.height = sceenW / 2;
                    ((VideoHolder) holder).iv_video_bg.setLayoutParams(params);

                    RelativeLayout.LayoutParams params_p = (RelativeLayout.LayoutParams) ((VideoHolder) holder).iv_video_play.getLayoutParams();
                    params_p.width = sceenW / 8;
                    params_p.height = sceenW / 8;
                    ((VideoHolder) holder).iv_video_play.setLayoutParams(params_p);

                    String urlimg = item.getFiles().get(0).file_thumb;
                    Glide.with(context).load(urlimg).into(((VideoHolder) holder).iv_video_bg);

                    ((VideoHolder) holder).tv_content.setText("" + content);
                    ((VideoHolder) holder).tv_month.setText("" + curMonth);
                    ((VideoHolder) holder).tv_day.setText("" + curDay);
                    if (nextItem == null) {
                        ((VideoHolder) holder).tv_month.setVisibility(View.VISIBLE);
                        ((VideoHolder) holder).tv_day.setVisibility(View.VISIBLE);
                        try {
                            long curTime = Long.parseLong(item.getAdd_time());
                            String resTime = TimeUtil.getTimeisTodayOrYestday(curTime * 1000);
                            if (!TextUtils.isEmpty(resTime)) {
                                if ("今天".equals(resTime) && isSelt) {
                                    ((VideoHolder) holder).tv_month.setVisibility(View.GONE);
                                    ((VideoHolder) holder).tv_day.setVisibility(View.GONE);
                                } else {
                                    ((VideoHolder) holder).tv_month.setVisibility(View.GONE);
                                    ((VideoHolder) holder).tv_day.setVisibility(View.VISIBLE);
                                }
                                ((VideoHolder) holder).tv_day.setText(resTime);
                            }
                        } catch (Exception e) {

                        }
                    } else {
                        String lastMonth = nextItem.getTimeMonth();
                        String lastDay = nextItem.getTimeDay();
                        if (!TextUtils.isEmpty(curMonth) && !TextUtils.isEmpty(curDay) &&
                                !TextUtils.isEmpty(lastMonth) && !TextUtils.isEmpty(lastDay)) {
                            if (curMonth.equals(lastMonth) && curDay.equals(lastDay)) {
                                ((VideoHolder) holder).tv_month.setVisibility(View.GONE);
                                ((VideoHolder) holder).tv_day.setVisibility(View.GONE);
                            } else {
                                ((VideoHolder) holder).tv_month.setVisibility(View.VISIBLE);
                                ((VideoHolder) holder).tv_day.setVisibility(View.VISIBLE);
                                try {
                                    long curTime = Long.parseLong(item.getAdd_time());
                                    String resTime = TimeUtil.getTimeisTodayOrYestday(curTime * 1000);
                                    if (!TextUtils.isEmpty(resTime)) {
                                        if ("今天".equals(resTime) && isSelt) {
                                            ((VideoHolder) holder).tv_month.setVisibility(View.GONE);
                                            ((VideoHolder) holder).tv_day.setVisibility(View.GONE);
                                        } else {
                                            ((VideoHolder) holder).tv_month.setVisibility(View.GONE);
                                            ((VideoHolder) holder).tv_day.setVisibility(View.VISIBLE);
                                        }
                                        ((VideoHolder) holder).tv_day.setText(resTime);
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                    break;
                case TYPE_URL:
                    final List<PhotoInfo> files = item.getFiles();
                    if (files != null && files.size() > 0) {
                        String linkImg = files.get(0).file_thumb;
                        Glide.with(context).load(linkImg).into(((URLHolder) holder).iv_icon);
                        ((URLHolder) holder).iv_icon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, WebViewActivity.class);
                                intent.putExtra("url", files.get(0).file_url);
                                context.startActivity(intent);
                            }
                        });
                    }

                    ((URLHolder) holder).tv_content.setText("" + content);
                    ((URLHolder) holder).tv_month.setText("" + curMonth);
                    ((URLHolder) holder).tv_day.setText("" + curDay);
                    if (nextItem == null) {
                        ((URLHolder) holder).tv_month.setVisibility(View.VISIBLE);
                        ((URLHolder) holder).tv_day.setVisibility(View.VISIBLE);
                        try {
                            long curTime = Long.parseLong(item.getAdd_time());
                            String resTime = TimeUtil.getTimeisTodayOrYestday(curTime * 1000);
                            if (!TextUtils.isEmpty(resTime)) {
                                if ("今天".equals(resTime) && isSelt) {
                                    ((URLHolder) holder).tv_month.setVisibility(View.GONE);
                                    ((URLHolder) holder).tv_day.setVisibility(View.GONE);
                                } else {
                                    ((URLHolder) holder).tv_month.setVisibility(View.GONE);
                                    ((URLHolder) holder).tv_day.setVisibility(View.VISIBLE);
                                }
                                ((URLHolder) holder).tv_day.setText(resTime);
                            }
                        } catch (Exception e) {

                        }
                    } else {
                        String lastMonth = nextItem.getTimeMonth();
                        String lastDay = nextItem.getTimeDay();
                        if (!TextUtils.isEmpty(curMonth) && !TextUtils.isEmpty(curDay) &&
                                !TextUtils.isEmpty(lastMonth) && !TextUtils.isEmpty(lastDay)) {
                            if (curMonth.equals(lastMonth) && curDay.equals(lastDay)) {
                                ((URLHolder) holder).tv_month.setVisibility(View.GONE);
                                ((URLHolder) holder).tv_day.setVisibility(View.GONE);
                            } else {
                                ((URLHolder) holder).tv_month.setVisibility(View.VISIBLE);
                                ((URLHolder) holder).tv_day.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class YearPartent extends RecyclerView.ViewHolder {
        private TextView tv_years;

        public YearPartent(View itemView) {
            super(itemView);
            tv_years = (TextView) itemView.findViewById(R.id.tv_years);
        }
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {
        private TextView tv_content, tv_num, tv_day, tv_month, tv_no_img_tx;
        private MultiImageView multiImagView;
        private LinearLayout ll_img;

        public ImageHolder(View itemView) {
            super(itemView);
            tv_no_img_tx = (TextView) itemView.findViewById(R.id.tv_no_img_tx);
            ll_img = (LinearLayout) itemView.findViewById(R.id.ll_img);
            tv_month = (TextView) itemView.findViewById(R.id.tv_month);
            tv_day = (TextView) itemView.findViewById(R.id.tv_day);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_num = (TextView) itemView.findViewById(R.id.tv_num);
            multiImagView = (MultiImageView) itemView.findViewById(R.id.multiImagView);
            multiImagView.setFromType(1);
        }
    }

    public static class VideoHolder extends RecyclerView.ViewHolder {
        private ImageView iv_video_bg, iv_video_play;
        private TextView tv_content, tv_day, tv_month;

        public VideoHolder(View itemView) {
            super(itemView);
            tv_month = (TextView) itemView.findViewById(R.id.tv_month);
            tv_day = (TextView) itemView.findViewById(R.id.tv_day);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            iv_video_bg = (ImageView) itemView.findViewById(R.id.iv_video_bg);
            iv_video_play = (ImageView) itemView.findViewById(R.id.iv_video_play);
        }
    }

    public static class URLHolder extends RecyclerView.ViewHolder {
        private ImageView iv_icon;
        private TextView tv_content, tv_day, tv_month;

        public URLHolder(View itemView) {
            super(itemView);
            tv_month = (TextView) itemView.findViewById(R.id.tv_month);
            tv_day = (TextView) itemView.findViewById(R.id.tv_day);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
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

    public void getIsSelt(boolean isSelt) {
        this.isSelt = isSelt;
    }
}
