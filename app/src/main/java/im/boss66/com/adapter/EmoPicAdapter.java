package im.boss66.com.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.FileUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.EmoEntity;

/**
 * Created by Johnny on 2017/2/25.
 */
public class EmoPicAdapter extends ABaseAdapter<EmoEntity> {

    private Context context;
    private float mImageHeight;
    private PopupWindow popupWindow;

    public EmoPicAdapter(Context context) {
        super(context);
        this.context = context;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 4;
    }

    @Override
    protected View setConvertView(int position, EmoEntity entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_emo_pic, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        convertView.getLayoutParams().width = (int) mImageHeight;
        convertView.getLayoutParams().height = (int) mImageHeight;
        if (entity != null) {
            String imageUrl = entity.getUrl();
            Log.i("info", "========imageUrl:" + imageUrl);
            Uri uri = Uri.parse(imageUrl);
            //加载静态图片
            holder.ivPic.setImageURI(uri);
//            DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
//            holder.ivPic.setController(controller);
        }
        convertView.setOnTouchListener(new TouchListener(entity));
        return convertView;
    }

    private class TouchListener implements View.OnTouchListener {
        EmoEntity emoEntity;

        public TouchListener(EmoEntity emoEntity) {
            this.emoEntity = emoEntity;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (popupWindow == null) {
                        showCurrentEmo(emoEntity, view);
                    } else {
                        if (!popupWindow.isShowing()) {
                            showCurrentEmo(emoEntity, view);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    break;
            }
            return true;
        }
    }

    private void showCurrentEmo(EmoEntity entity, View v) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popwindows_item_emo, null);
//        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow = new PopupWindow(view, (int) mImageHeight,
                (int) mImageHeight, false);
        popupWindow.setAnimationStyle(R.style.PopupTitleBarAnim);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getDrawableFromRes(R.drawable.bg_popwindow));
        ImageView gif = (ImageView) view.findViewById(R.id.gif);
//        String imageUrl = entity.getUrl();
//        if (NetworkUtil.networkAvailable(context) && !imageUrl.equals("")) {
//            Uri uri = Uri.parse(imageUrl);
//            //加载动态图片
//            DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(uri).setAutoPlayAnimations(true).build();
//            gif.setController(controller);
//        }
        File file = FileUtil.getFileByPath(getPath(entity));
        if (file != null) {
            Glide.with(context).load(FileUtil.getBytesFromFile(file)).crossFade().into(gif);
        }
        int[] location = new int[2];
        v.getLocationOnScreen(location);
//        popupWindow.showAtLocation(Parent, Gravity.NO_GRAVITY, location[0], location[1]);
        int i  =location[0]; //当前位置横坐标
        int j = location[1]; //当前位置纵坐标

        if (i == 0 || j == 0) {
            Rect rect = new Rect();
            v.getGlobalVisibleRect(rect);
            i = rect.left;
            j = rect.top;
        }
        int x = (i + v.getWidth() / 2) - popupWindow.getWidth() / 2;//
        int y = j- popupWindow.getHeight();
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, x, y);
    }

    private String getPath(EmoEntity entity) {
        String path = Constants.EMO_DIR_PATH + File.separator +
                entity.getEmo_cate_id() + File.separator +
                entity.getEmo_group_id() + File.separator +
                entity.getEmo_code() + "." +
                entity.getEmo_format();
        return path;
    }

    private Drawable getDrawableFromRes(int resId) {
        Resources res = context.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resId);
        return new BitmapDrawable(bmp);
    }

    private class ViewHolder {
        SimpleDraweeView ivPic;

        public ViewHolder(View view) {
            this.ivPic = (SimpleDraweeView) view.findViewById(R.id.iv_pic);
        }
    }

}
