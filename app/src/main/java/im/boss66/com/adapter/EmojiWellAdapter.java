package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.entity.EmoBagEntity;

/**
 * Created by Johnny on 2017/1/23.
 */
public class EmojiWellAdapter extends BaseAdapter {
    private ImageLoader imageLoader;
    private Context context;
    private int downloadedColor;
    private int defaultColor;
    private MyClickListener mListener;
    private ArrayList<EmoBagEntity> mList;
    private String userid;

    public EmojiWellAdapter(Context context, MyClickListener clickListener) {
        this.context = context;
        this.mListener = clickListener;
        userid = App.getInstance().getUid();
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        downloadedColor = context.getResources().getColor(R.color.btn_green_noraml);
        defaultColor = context.getResources().getColor(R.color.top_bar_color);
        if (mList == null) {
            mList = new ArrayList<>();
        }
    }

    private boolean isDownload(EmoBagEntity entity) {
        String key = PrefKey.EMOJI_DOWNLOAD_KEY + "/" + userid + "/" + entity.getGroup_id();
        return PreferenceUtils.getBoolean(context, key, false);
    }

    public void saveDownloadedStatus(EmoBagEntity entity) {
        String key = PrefKey.EMOJI_DOWNLOAD_KEY + "/" + userid + "/" + entity.getGroup_id();
        PreferenceUtils.putBoolean(context, key, true);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public ArrayList<EmoBagEntity> getData() {
        return mList;
    }

    public void initData(ArrayList<EmoBagEntity> list) {
        mList.clear();
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void addData(ArrayList<EmoBagEntity> list) {
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    ViewHolder holder = null;

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_emoji_select_well, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EmoBagEntity entity = mList.get(position);
        if (entity != null) {
            holder.tvName.setText(entity.getGroup_name());
            holder.tvDescr.setText(entity.getGroup_desc());
            imageLoader.displayImage(entity.getGroup_icon(),
                    holder.image,
                    ImageLoaderUtils.getDisplayImageOptions());
            if (isDownload(entity)) {
                holder.tvDownLoad.setEnabled(false);
                holder.tvDownLoad.setTextColor(downloadedColor);
                holder.tvDownLoad.setBackgroundResource(R.drawable.bg_frame_emo_default);
            } else {
                holder.tvDownLoad.setEnabled(true);
                holder.tvDownLoad.setTextColor(defaultColor);
                holder.tvDownLoad.setBackgroundResource(R.drawable.bg_frame_emo_downloaded);
            }
            holder.tvDownLoad.setOnClickListener(mListener);
            holder.tvDownLoad.setTag(position);
        }
        return convertView;
    }

    /**
     * 用于回调的抽象类
     */
    public static abstract class MyClickListener implements View.OnClickListener {
        /**
         * 基类的onClick方法
         */
        @Override
        public void onClick(View v) {
            myOnClick((Integer) v.getTag(), v);
        }

        public abstract void myOnClick(int position, View v);
    }

    private static class ViewHolder {
        ImageView image;
        TextView tvName;
        TextView tvDescr;
        TextView tvDownLoad;
        ProgressBar progressBar;

        public ViewHolder(View view) {
            this.image = (ImageView) view.findViewById(R.id.iv_emoji);
            this.tvName = (TextView) view.findViewById(R.id.tv_title);
            this.tvDescr = (TextView) view.findViewById(R.id.tv_tips);
            this.tvDownLoad = (TextView) view.findViewById(R.id.tv_download);
            this.progressBar = (ProgressBar) view.findViewById(R.id.download_pb);
        }
    }
}
