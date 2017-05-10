package im.boss66.com.activity.discover;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.util.DateUtils;
import com.hyphenate.util.TextFormater;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.domain.VideoEntity;
import im.boss66.com.util.ImageCache;
import im.boss66.com.util.ImageResizer;
import im.boss66.com.util.Utils;
import im.boss66.com.widget.RecyclingImageView;

/**
 * 本地视频列表
 */
public class VideoListActivity extends BaseActivity {

    private TextView tv_back, tv_title;
    private GridView gv_video;
    private ImageAdapter mAdapter;
    private List<VideoEntity> mList;
    private ImageResizer mImageResizer;
    private int mImageThumbSize;
    private int mImageThumbSpacing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initView();
    }

    private void initView() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title);
        gv_video = (GridView) findViewById(R.id.gv_video);
        tv_title.setText("选择小视频");
        mImageThumbSize = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(
                R.dimen.image_thumbnail_spacing);
        mList = new ArrayList<>();
        getVideoFile();
        mAdapter = new ImageAdapter(this);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams();

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
        // app memory

        // The ImageFetcher takes care of loading images into our ImageView
        // children asynchronously
        mImageResizer = new ImageResizer(this, mImageThumbSize);
        mImageResizer.setLoadingImage(R.drawable.em_empty_photo);
        mImageResizer.addImageCache(this.getSupportFragmentManager(),
                cacheParams);
        gv_video.setAdapter(mAdapter);
        gv_video.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mImageResizer.setPauseWork(true);
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
        });
        gv_video.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView,
                                             int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help
                    // with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageResizer.setPauseWork(true);
                    }
                } else {
                    mImageResizer.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        gv_video.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        final int numColumns = (int) Math.floor(gv_video
                                .getWidth()
                                / (mImageThumbSize + mImageThumbSpacing));
                        if (numColumns > 0) {
                            final int columnWidth = (gv_video.getWidth() / numColumns)
                                    - mImageThumbSpacing;
                            mAdapter.setItemHeight(columnWidth);

                            if (Utils.hasJellyBean()) {
                                gv_video.getViewTreeObserver()
                                        .removeOnGlobalLayoutListener(this);
                            } else {
                                gv_video.getViewTreeObserver()
                                        .removeGlobalOnLayoutListener(this);
                            }
                        }
                    }
                });
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
                if (url.endsWith(".mp4") || url.endsWith(".MP4")) {
                    Log.i("VideoEntity:", "title:" + title + "  size:" + size + "     url:" + url + "    mineType:" + mineType);
                }
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
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
                holder.imageView.setImageResource(R.drawable.em_empty_photo);
                mImageResizer.loadImage(entty.filePath, holder.imageView);
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
}
