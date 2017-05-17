package im.boss66.com.activity.discover;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.TextFormater;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.adapter.BaseRecycleViewAdapter;
import im.boss66.com.domain.VideoEntity;
import im.boss66.com.util.ImageResizer;
import im.boss66.com.widget.RecyclingImageView;

/**
 * 本地视频列表
 */
public class VideoListActivity extends BaseActivity {

    private TextView tv_back, tv_title;
    //private GridView gv_video;
    private ImageAdapter mAdapter;
    private List<VideoEntity> mList;
    private ImageResizer mImageResizer;
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private RecyclerView rv_video;
    private RImageAdapter rImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initView();
    }

    private void initView() {
        rv_video = (RecyclerView) findViewById(R.id.rv_video);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title);
        //gv_video = (GridView) findViewById(R.id.gv_video);
        tv_title.setText("选择小视频");
        rv_video.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
//        mImageThumbSize = getResources().getDimensionPixelSize(
//                R.dimen.image_thumbnail_size);
//        mImageThumbSpacing = getResources().getDimensionPixelSize(
//                R.dimen.image_thumbnail_spacing);
        mList = new ArrayList<>();
        rImageAdapter = new RImageAdapter(this);
        rv_video.addItemDecoration(new MyItemDecoration());
        rv_video.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position >= mList.size()) {
                    return;
                }
                VideoEntity vEntty = mList.get(position);
                Log.i("pos:", "" + position);
                if (vEntty != null) {
                    String filePath = vEntty.filePath;
                    Intent intent = new Intent();
                    intent.putExtra("filePath", filePath);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        }));
        rv_video.setAdapter(rImageAdapter);
//        getVideoFile();

//        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams();
//
//        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
        // app memory

        // The ImageFetcher takes care of loading images into our ImageView
        // children asynchronously
//        mImageResizer = new ImageResizer(this, mImageThumbSize);
//        mImageResizer.setLoadingImage(R.drawable.em_empty_photo);
//        mImageResizer.addImageCache(this.getSupportFragmentManager(),
//                cacheParams);

        new SearchThead().start();
        //getVideoFile();

//        gv_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                mImageResizer.setPauseWork(true);
//                if (position >= mList.size()) {
//                    return;
//                }
//                VideoEntity vEntty = mList.get(position);
//                Log.i("pos:", "" + position);
//                if (vEntty != null) {
//                    String filePath = vEntty.filePath;
//                    Intent intent = new Intent();
//                    intent.putExtra("filePath", filePath);
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            }
//        });
//        gv_video.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView,
//                                             int scrollState) {
//                // Pause fetcher to ensure smoother scrolling when flinging
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
//                    // Before Honeycomb pause image loading on scroll to help
//                    // with performance
//                    if (!Utils.hasHoneycomb()) {
//                        mImageResizer.setPauseWork(true);
//                    }
//                } else {
//                    mImageResizer.setPauseWork(false);
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView absListView, int firstVisibleItem,
//                                 int visibleItemCount, int totalItemCount) {
//            }
//        });
    }

    private void getVideoFile() {
        ContentResolver mContentResolver = getContentResolver();
        Cursor cursor = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // ID:MediaStore.Audio.Media._ID
                int id = cursor.getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media._ID));

                // title：MediaStore.Audio.Media.TITLE
                String title = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                // path：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String mineType = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                // duration：MediaStore.Audio.Media.DURATION
                int duration = cursor
                        .getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                // 大小：MediaStore.Audio.Media.SIZE
                int size = (int) cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                if (size > 0 && size <= 1024 * 1024 * 20 && (url.endsWith(".mp4") || url.endsWith(".MP4"))) {
                    VideoEntity entty = new VideoEntity();
                    entty.ID = id;
                    entty.title = title;
                    entty.filePath = url;
                    entty.duration = duration;
                    entty.size = size;
                    mList.add(entty);
                }
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
            mHandler.sendEmptyMessage(1);
            cursor = null;
        }
    }

    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private RelativeLayout.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return (position == 0) ? null : mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.em_choose_griditem, container, false);
                holder.imageView = (RecyclingImageView) convertView.findViewById(R.id.imageView);
                holder.icon = (ImageView) convertView.findViewById(R.id.video_icon);
                holder.tvDur = (TextView) convertView.findViewById(R.id.chatting_length_iv);
                holder.tvSize = (TextView) convertView.findViewById(R.id.chatting_size_iv);
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.imageView.setLayoutParams(mImageViewLayoutParams);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (holder.imageView.getLayoutParams().height != mItemHeight) {
                holder.imageView.setLayoutParams(mImageViewLayoutParams);
            }

            holder.icon.setVisibility(View.VISIBLE);
            if (mList != null && position < mList.size()) {
                VideoEntity entty = mList.get(position);
                holder.tvDur.setVisibility(View.VISIBLE);

                holder.tvDur.setText(DateUtils.toTime(entty.duration));
                holder.tvSize.setText(TextFormater.getDataSize(entty.size));
                //holder.imageView.setImageResource(R.drawable.em_empty_photo);
                //mImageResizer.loadImage(entty.filePath, holder.imageView);
                Picasso.with(context).load(entty.filePath).error(R.drawable.em_empty_photo).into(holder.imageView);
            }
            return convertView;
        }

        /**
         * Sets the item height. Useful for when we know the column width so the
         * height can be set to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, mItemHeight);
            mImageResizer.setImageSize(height);
            notifyDataSetChanged();
        }

        class ViewHolder {
            RecyclingImageView imageView;
            ImageView icon;
            TextView tvDur;
            TextView tvSize;
        }
    }

    private class RImageAdapter extends BaseRecycleViewAdapter {
        private Context mContext;

        public RImageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
            final ViewHolder holder1 = (ViewHolder) holder;

            holder1.icon.setVisibility(View.VISIBLE);
            if (mList != null && position < mList.size()) {
                VideoEntity entty = mList.get(position);
                holder1.tvDur.setVisibility(View.VISIBLE);
                holder1.tvDur.setText(DateUtils.toTime(entty.duration));
                holder1.tvSize.setText(TextFormater.getDataSize(entty.size));
                Glide.with(context).load(entty.filePath).error(R.drawable.em_empty_photo).into(holder1.imageView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_video_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageView icon;
            TextView tvDur;
            TextView tvSize;
            FrameLayout fl_p;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                icon = (ImageView) itemView.findViewById(R.id.video_icon);
                tvDur = (TextView) itemView.findViewById(R.id.chatting_length_iv);
                tvSize = (TextView) itemView.findViewById(R.id.chatting_size_iv);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                int mh = UIUtils.getScreenWidth(mContext) / 2 - 8;
                Log.i("mh", ":" + mh);
                imageView.getLayoutParams().height = mh;
                imageView.getLayoutParams().width = mh;
            }
        }
    }

    class MyItemDecoration extends RecyclerView.ItemDecoration {
        /**
         * @param outRect 边界
         * @param view    recyclerView ItemView
         * @param parent  recyclerView
         * @param state   recycler 内部数据管理
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //设定底部边距为1px
            outRect.set(2, 0, 2, 1);
        }
    }

    class SearchThead extends Thread {
        @Override
        public void run() {
            getVideoFile();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1 && mList != null) {
                rImageAdapter.notifyDataSetChanged();
//                mAdapter = new ImageAdapter(VideoListActivity.this);
//                gv_video.setAdapter(mAdapter);
//                gv_video.getViewTreeObserver().addOnGlobalLayoutListener(
//                        new ViewTreeObserver.OnGlobalLayoutListener() {
//                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//                            @Override
//                            public void onGlobalLayout() {
//                                final int numColumns = (int) Math.floor(gv_video
//                                        .getWidth()
//                                        / (mImageThumbSize + mImageThumbSpacing));
//                                if (numColumns > 0) {
//                                    final int columnWidth = (gv_video.getWidth() / numColumns)
//                                            - mImageThumbSpacing;
//                                    mAdapter.setItemHeight(columnWidth);
//
//                                    if (Utils.hasJellyBean()) {
//                                        gv_video.getViewTreeObserver()
//                                                .removeOnGlobalLayoutListener(this);
//                                    } else {
//                                        gv_video.getViewTreeObserver()
//                                                .removeGlobalOnLayoutListener(this);
//                                    }
//                                }
//                            }
//                        });
            }
        }
    };

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
