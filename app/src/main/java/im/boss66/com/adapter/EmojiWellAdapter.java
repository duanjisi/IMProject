package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.entity.EmojiWellEntity;

/**
 * Created by Johnny on 2017/1/23.
 */
public class EmojiWellAdapter extends ABaseAdapter<EmojiWellEntity> {
    private ImageLoader imageLoader;

    public EmojiWellAdapter(Context context) {
        super(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    @Override
    protected View setConvertView(int position, EmojiWellEntity entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_emoji_select_well, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (entity != null) {
            holder.tvName.setText(entity.getName());
            holder.tvDescr.setText(entity.getDesc());
            imageLoader.displayImage(entity.getEmojiUrl(),
                    holder.image,
                    ImageLoaderUtils.getDisplayImageOptions());
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        TextView tvName;
        TextView tvDescr;
        TextView tvDownLoad;

        public ViewHolder(View view) {
            this.image = (ImageView) view.findViewById(R.id.iv_emoji);
            this.tvName = (TextView) view.findViewById(R.id.tv_title);
            this.tvDescr = (TextView) view.findViewById(R.id.tv_tips);
            this.tvDownLoad = (TextView) view.findViewById(R.id.tv_download);
        }
    }
}
