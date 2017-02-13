package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;

/**
 * Created by Johnny on 2016/7/19.
 */
public class PictureAdapter extends BaseAdapter {
    private ImageLoader imageLoader;
    private Context context;
    private float mImageHeight;
    private ArrayList<String> images;

    public PictureAdapter(Context context) {
        this.context = context;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 3;
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add("lastItem");
    }

    public void addDatas(ArrayList<String> items) {
        int totals = images.size() - 1;
        int count = 6 - totals;
        if (count > 0) {
            images.addAll(totals, getBefore(items, count));
            if (images.size() - 1 == 6) {
                images.remove(images.lastIndexOf("lastItem"));
            }
            notifyDataSetChanged();
        } else {
            Toast.makeText(context, "最多只能添加6张图片", Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<String> getBefore(ArrayList<String> items, int count) {
        ArrayList<String> list = new ArrayList<>();
        if (count < items.size() || count == items.size()) {
            for (int i = 0; i < count; i++) {
                list.add(items.get(i));
            }
        } else {
            for (int i = 0; i < items.size(); i++) {
                list.add(items.get(i));
            }
        }
        return list;
    }

    public void addItem(String entity) {
        int totals = images.size() - 1;
        int count = 6 - totals;
        if (count > 0) {
            addItem(entity, totals);
            if (images.size() - 1 == 6) {
                images.remove(images.lastIndexOf("lastItem"));
            }
            notifyDataSetChanged();
        } else {
            Toast.makeText(context, "最多只能添加6张图片", Toast.LENGTH_LONG).show();
        }
    }

    public void addDatas2(ArrayList<String> items) {
        int totals = images.size() - 1;
        int count = 9 - totals;
        if (count > 0) {
            images.addAll(totals, getBefore(items, count));
            if (images.size() - 1 == 9) {
                images.remove(images.lastIndexOf("lastItem"));
            }
            notifyDataSetChanged();
        } else {
            Toast.makeText(context, "最多只能添加9张图片", Toast.LENGTH_LONG).show();
        }
    }

    public void addItem2(String entity) {
        int totals = images.size() - 1;
        int count = 9 - totals;
        if (count > 0) {
            addItem(entity, totals);
            if (images.size() - 1 == 9) {
                images.remove(images.lastIndexOf("lastItem"));
            }
            notifyDataSetChanged();
        } else {
            Toast.makeText(context, "最多只能添加9张图片", Toast.LENGTH_LONG).show();
        }
    }

    public void addItem(String entity, int index) {
        if (entity != null) {
            images.add(index, entity);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        ImageView imageView = new ImageView(context);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
//        imageView.getLayoutParams().width = (int) mImageHeight;
//        imageView.getLayoutParams().height = (int) mImageHeight;
//        String imageUrl = images.get(position);
//        if (imageUrl != null && !imageUrl.equals("")) {
//            if (!imageUrl.equals("lastItem")) {
////                imageLoader.displayImage("file://" + imageUrl, imageView, ImageLoaderUtils.getDisplayImageOptions());
////                loadBitmapFromUrl(imageView, imageUrl);
//                MycsLog.i("info", "===imageUrl:" + imageUrl);
//                imageLoader.displayImage("file:/" + imageUrl, imageView, ImageLoaderUtils.getDisplayImageOptions());
//            } else {
//                imageView.setImageResource(R.drawable.compose_pic_add);
//            }
//        }
//        return imageView;
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_picture, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        convertView.getLayoutParams().width = (int) mImageHeight;
        convertView.getLayoutParams().height = (int) mImageHeight;
        final String imageUrl = images.get(position);
        if (imageUrl != null && !imageUrl.equals("")) {
            if (!imageUrl.equals("lastItem")) {
                imageLoader.displayImage("file:/" + imageUrl, holder.ivPic, ImageLoaderUtils.getDisplayImageOptions());
//                UIUtils.showView(holder.ivClose);
//                holder.ivClose.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        removeItem(imageUrl);
//                    }
//                });
            } else {
//                UIUtils.hindView(holder.ivClose);
                holder.ivPic.setImageResource(R.drawable.compose_pic_add);
            }
        }
        return convertView;
    }

    private void removeItem(String url) {
        Iterator<String> stringIterator = images.iterator();
        while (stringIterator.hasNext()) {
            String s = stringIterator.next();
            if (s.equals(url)) {
                stringIterator.remove();
            }
        }
        if (images.size() - 1 != 9 && !images.contains("lastItem")) {
            images.add("lastItem");
        }
        notifyDataSetChanged();
    }

    private class ViewHolder {
        //        ImageView ivClose;
        ImageView ivPic;

        public ViewHolder(View view) {
//            this.ivClose = (ImageView) view.findViewById(R.id.iv_close);
            this.ivPic = (ImageView) view.findViewById(R.id.iv_pic);
        }
    }

    public String[] getImages() {
        String[] strings = new String[images.size() - 1];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = images.get(i);
        }
        return strings;
    }

    public ArrayList<String> getList() {
        ArrayList<String> item = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            String str = images.get(i);
            if (!str.equals("lastItem")) {
                item.add(str);
            }
        }
        return item;
    }

//    private void loadBitmapFromUrl(final ImageView iv, String picPath) {
//        if (picPath != null) {
//            MycsLog.d(picPath);
//            // Point point = null;
//            Point point = new Point(UIUtils.getScreenWidth(context), UIUtils.getScreenHeight(context));
//            PictureLoader.getInstance().loadImageNoLru(picPath, point, new PictureLoader.NativeImageCallBack() {
//                @Override
//                public void onImageLoader(Bitmap bitmap, String path) {
//                    if (bitmap != null) {
//                        iv.setImageBitmap(bitmap);
////                        upload(bitmap2Base64(bitmap), iv);
//                    }
//                }
//            });
//        } else {
//            MycsLog.d("url is null");
//        }
//    }
}
