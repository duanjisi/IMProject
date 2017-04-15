package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.domain.EaseUser;

/**
 * Created by Johnny on 2017/4/15.
 */
public class EaseUserAdapter extends ABaseAdapter<EaseUser> {

    private ImageLoader imageLoader;
    private int mImageHeight = 0;

    public EaseUserAdapter(Context context) {
        super(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 7;
    }

    @Override
    protected View setConvertView(int position, EaseUser entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_easuer, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (entity != null) {
            imageLoader.displayImage(entity.getAvatar(), holder.ivPic, ImageLoaderUtils.getDisplayImageOptions());
            convertView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
            convertView.getLayoutParams().width = (int) mImageHeight;
            convertView.getLayoutParams().height = (int) mImageHeight;
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView ivPic;

        public ViewHolder(View view) {
            this.ivPic = (ImageView) view.findViewById(R.id.iv_pic);
        }
    }
}
