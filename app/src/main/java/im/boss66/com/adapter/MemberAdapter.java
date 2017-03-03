package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.MemberEntity;

/**
 * Created by Johnny on 2016/7/23.
 */
public class MemberAdapter extends ABaseAdapter<MemberEntity> {

    private ImageLoader imageLoader;

    public MemberAdapter(Context context) {
        super(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    @Override
    protected View setConvertView(int position, MemberEntity entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_member, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (entity != null) {
            int type = entity.getType();
            if (type == 1) {
                UIUtils.hindView(holder.tvName);
                holder.image.setImageResource(R.drawable.add_frimore);
            } else if (type == 2) {
                UIUtils.hindView(holder.tvName);
                holder.image.setImageResource(R.drawable.del_frimore);
            } else {
                UIUtils.showView(holder.tvName);
                holder.tvName.setText(entity.getNickname());
                imageLoader.displayImage(entity.getSnap(), holder.image, ImageLoaderUtils.getDisplayImageOptions());
            }
        }
        return convertView;
    }


    private class ViewHolder {
        TextView tvName;
        ImageView image;

        public ViewHolder(View view) {
            this.tvName = (TextView) view.findViewById(R.id.tvName);
            this.image = (ImageView) view.findViewById(R.id.image);
        }
    }
}
