package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.entity.UserInform;

/**
 * Created by Johnny on 2017/2/28.
 * 用户
 */
public class UserAdapter extends ABaseAdapter<UserInform> {

    private Context context;
    private ImageLoader imageLoader;

    public UserAdapter(Context context) {
        super(context);
        this.context = context;
        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    @Override
    protected View setConvertView(int position, UserInform entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.ease_row_contact, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (entity != null) {
            holder.tvName.setText(entity.getUser_name());
            imageLoader.displayImage(entity.getAvatar(), holder.header, ImageLoaderUtils.getDisplayImageOptions());
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView header;
        TextView tvName;

        public ViewHolder(View view) {
            this.header = (ImageView) view.findViewById(R.id.avatar);
            this.tvName = (TextView) view.findViewById(R.id.name);
        }
    }
}
