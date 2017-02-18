package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.entity.EmoGroup;

/**
 * Created by Johnny on 2017/1/23.
 */
public class EmojiRecordAdapter extends ABaseAdapter<EmoGroup> {

    private ImageLoader imageLoader;

    public EmojiRecordAdapter(Context context) {
        super(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    @Override
    protected View setConvertView(int position, final EmoGroup entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_emoji_record, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (entity != null) {
            holder.title.setText(entity.getGroup_name());
            imageLoader.displayImage(entity.getGroup_icon(), holder.imageView, ImageLoaderUtils.getDisplayImageOptions());
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView title;
        TextView date;
//        TextView delete;

        public ViewHolder(View view) {
            this.imageView = (ImageView) view.findViewById(R.id.iv_emoji);
            this.title = (TextView) view.findViewById(R.id.tv_title);
            this.date = (TextView) view.findViewById(R.id.tv_date);
        }
    }
}