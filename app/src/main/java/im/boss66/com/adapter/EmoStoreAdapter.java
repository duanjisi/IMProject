package im.boss66.com.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.entity.EmoStore;

/**
 * Created by Johnny on 2017/2/20.
 */
public class EmoStoreAdapter extends ABaseAdapter<EmoStore> {
    private ImageLoader imageLoader;

    public EmoStoreAdapter(Context context) {
        super(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    @Override
    protected View setConvertView(int position, EmoStore entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_emo_store, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (entity != null) {
            holder.tvName.setText(getSpanned(entity.getSname()));
            holder.tvDescr.setText(getSpanned(entity.getSdesc()));
            imageLoader.displayImage(entity.getIcon(), holder.image, ImageLoaderUtils.getDisplayImageOptions());
        }
        return convertView;
    }

    private Spanned getSpanned(String str) {
        String s1 = str.replace("<em>", "<font color='#68C270'>");
        String s2 = s1.replace("</em>", "</font>");
        return Html.fromHtml(s2);
    }

    private class ViewHolder {
        ImageView image;
        TextView tvName;
        TextView tvDescr;

        public ViewHolder(View view) {
            this.image = (ImageView) view.findViewById(R.id.image);
            this.tvName = (TextView) view.findViewById(R.id.tv_name);
            this.tvDescr = (TextView) view.findViewById(R.id.tv_msg);
        }
    }
}
