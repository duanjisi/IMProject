package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.EmoEntity;

/**
 * Created by Johnny on 2017/2/9.
 */
public class FaceAdapter extends ABaseAdapter<EmoEntity> {
    private ImageLoader imageLoader;
    private Context context;
    private float mImageHeight;

    public FaceAdapter(Context context) {
        super(context);
        this.context = context;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 4;
        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    @Override
    protected View setConvertView(int position, EmoEntity entity, View convertView) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        imageView.getLayoutParams().width = (int) mImageHeight;
        imageView.getLayoutParams().height = (int) mImageHeight;
//        String imageUrl = entity.getEmo_code();
//        if (imageUrl != null && !imageUrl.equals("")) {
//
//        }
        imageView.setImageResource(R.drawable.f018);
        return imageView;
    }

}
