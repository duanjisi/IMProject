package im.boss66.com.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.db.dao.EmoLoveHelper;
import im.boss66.com.entity.EmoLove;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.EmoCollectionDeleteRequest;

/**
 * Created by Johnny on 2016/7/19.
 */
public class PictureAdapter extends BasePicAdapter {
    private final static String TAG = PictureAdapter.class.getSimpleName();
    private ImageLoader imageLoader;
    private Context context;
    private float mImageHeight;
    private ArrayList<EmoLove> images;
    private boolean isAddPager = false;
    private WeakHashMap<String, View> maps = new WeakHashMap<>();
//    private boolean isFirst = false;
//    public void setFirst(boolean first) {
//        isFirst = first;
//    }

    public void setAddPager(boolean addPager) {
        isAddPager = addPager;
    }

    public PictureAdapter(Context context) {
        super(context);
        this.context = context;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 4;
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        if (images == null) {
            images = new ArrayList<>();
        }
    }

    public void addDatas(ArrayList<EmoLove> items) {
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

    public void initData(ArrayList<EmoLove> list) {
        images.clear();
        images.addAll(list);
        EmoLove love = new EmoLove();
        love.setEmo_url("lastItem");
//        images.add("lastItem");
        images.add(love);
        notifyDataSetChanged();
    }

    public void initFaceData(ArrayList<EmoLove> list) {
        images.clear();
//        if (isFirst) {
//            images.add("firstItem");
//        }
        images.addAll(list);
        notifyDataSetChanged();
    }

    private ArrayList<EmoLove> getBefore(ArrayList<EmoLove> items, int count) {
        ArrayList<EmoLove> list = new ArrayList<>();
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

    public void addItem(EmoLove entity) {
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

    public void addDatas2(ArrayList<EmoLove> items) {
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
//    public void addItem2(String entity) {
//        int totals = images.size() - 1;
//        int count = 9 - totals;
//        if (count > 0) {
//            addItem(entity, totals);
//            if (images.size() - 1 == 9) {
//                images.remove(images.lastIndexOf("lastItem"));
//            }
//            notifyDataSetChanged();
//        } else {
//            Toast.makeText(context, "最多只能添加9张图片", Toast.LENGTH_LONG).show();
//        }
//    }

    public void addItem2(EmoLove entity) {
        int totals = images.size() - 1;
        if (!images.contains(entity)) {
            addItem(entity, totals);
            notifyDataSetChanged();
        } else {
            Toast.makeText(context, "已添加", Toast.LENGTH_LONG).show();
        }
    }

    public void addItem(EmoLove entity, int index) {
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
    public View getView(int position, View view, ViewGroup parent) {
        final EmoLove love = images.get(position);
        String key = love.getCollect_id();
        View convertView = maps.get(key);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_picture, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            if (!maps.containsKey(key)) {
                maps.put(key, convertView);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        convertView.getLayoutParams().width = (int) mImageHeight;
        convertView.getLayoutParams().height = (int) mImageHeight;
        String imageUrl = love.getEmo_url();
        if (imageUrl != null && !imageUrl.equals("")) {
            if (!imageUrl.equals("lastItem")) {
                Log.i("info", "============aaaaaaaa");
                if (imageUrl.equals("firstItem")) {
                    Log.i("info", "============11111111");
                    UIUtils.hindView(holder.ivClose);
                    holder.ivPic.setImageResource(R.drawable.compose_pic_add);
                } else {
//                    imageLoader.displayImage("file:/" + imageUrl, holder.ivPic, ImageLoaderUtils.getDisplayImageOptions());
                    imageLoader.displayImage(imageUrl, holder.ivPic, ImageLoaderUtils.getDisplayImageOptions());
                    if (isAddPager) {
                        UIUtils.showView(holder.ivClose);
                        holder.ivClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                removeItem(imageUrl);
                                reuqestDeleteEmoLove(love);
                            }
                        });
                    }
                }
            } else {
                UIUtils.hindView(holder.ivClose);
                Log.i("info", "============2222222222");
                holder.ivPic.setImageResource(R.drawable.compose_pic_add);
            }
        }
        return convertView;
    }

    private void removeItem(EmoLove love) {
        Iterator<EmoLove> stringIterator = images.iterator();
        while (stringIterator.hasNext()) {
            EmoLove s = stringIterator.next();
            if (s.getCollect_id().equals(love.getCollect_id())) {
                EmoLoveHelper.getInstance().delete(love);
                stringIterator.remove();
            }
        }
//        if (!images.contains("lastItem")) {
//            images.add("lastItem");
//        }

        EmoLove emo = new EmoLove();
        emo.setEmo_url("lastItem");
        if (!isContain(emo)) {
            images.add(emo);
        }
        notifyDataSetChanged();
    }

    private boolean isContain(EmoLove love) {
        boolean flag = false;
        for (EmoLove e : images) {
            if (e.getEmo_url().equals(love.getEmo_url())) {
                flag = true;
            }
        }
        return flag;
    }

    private void reuqestDeleteEmoLove(final EmoLove love) {
//        String collectid = EmoLoveHelper.getInstance().qureEmoByUrl(url);
//        if (TextUtils.isEmpty(collectid)) {
//            return;
//        }
        showLoadingDialog();
        EmoCollectionDeleteRequest request = new EmoCollectionDeleteRequest(TAG, love.getCollect_id());
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                cancelLoadingDialog();
                removeItem(love);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }

    private class ViewHolder {
        ImageView ivClose;
        ImageView ivPic;

        public ViewHolder(View view) {
            this.ivClose = (ImageView) view.findViewById(R.id.iv_close);
            this.ivPic = (ImageView) view.findViewById(R.id.iv_pic);
        }
    }

    public String[] getImages() {
        String[] strings = new String[images.size() - 1];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = images.get(i).getEmo_url();
        }
        return strings;
    }

    public ArrayList<EmoLove> getList() {
        ArrayList<EmoLove> item = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            EmoLove love = images.get(i);
            if (!love.getEmo_url().equals("lastItem")) {
                item.add(love);
            }
        }
        return item;
    }
}
