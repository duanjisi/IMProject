package im.boss66.com.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import im.boss66.com.Constants;
import im.boss66.com.Utils.FileUtils;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.EmoEntity;

/**
 * Created by Johnny on 2017/2/9.
 */
public class EmojiAdapter extends ABaseAdapter<EmoEntity> {
    private ImageLoader imageLoader;
    private Context context;
    private float mImageHeight;

    public EmojiAdapter(Context context) {
        super(context);
        this.context = context;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 30)) / 3;
        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    @Override
    protected View setConvertView(int position, EmoEntity entity, View convertView) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        imageView.getLayoutParams().width = (int) mImageHeight;
        imageView.getLayoutParams().height = (int) mImageHeight;
//        Bitmap bitmap = getBitmap(entity);
//        if (bitmap != null) {
//            imageView.setImageBitmap(bitmap);
//        }
        if (entity != null) {
            imageLoader.displayImage(entity.getEmo_url(), imageView, ImageLoaderUtils.getDisplayImageOptions());
        }
        return imageView;
    }

    private Bitmap getBitmap(EmoEntity entity) {
        String path = Constants.EMO_DIR_PATH + File.separator +
                entity.getEmo_cate_id() + File.separator +
                entity.getEmo_group_id() + File.separator +
                entity.getEmo_code() + "." +
                entity.getEmo_format();
        return FileUtils.getBitmapByPath(path);
    }
}
