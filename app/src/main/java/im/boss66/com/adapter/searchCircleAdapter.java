package im.boss66.com.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.PhotoInfo;
import im.boss66.com.entity.SearchCofriendEntity;
import im.boss66.com.widget.MultiImageView;

/**
 * 全网搜索-朋友圈
 */
public class SearchCircleAdapter extends ABaseAdapter<SearchCofriendEntity> {

    private ImageLoader imageLoader;
    private int sceenW;

    public SearchCircleAdapter(Context context) {
        super(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        sceenW = UIUtils.getScreenWidth(context);
    }

    @Override
    protected View setConvertView(int position, SearchCofriendEntity entity, View convertView) {
        ViewHolder holder = null;
        convertView = View.inflate(getContext(), R.layout.item_search_circle, null);
        holder = new ViewHolder(convertView);
        convertView.setTag(holder);
        SearchCofriendEntity item = getItem(position);
        if (item != null) {
            String name = item.getFeed_uname();
            if (!TextUtils.isEmpty(name)) {
                holder.tv_name.setText(name);
            } else {
                String useid = item.getFeed_uid();
                if (!TextUtils.isEmpty(useid)) {
                    holder.tv_name.setText(useid);
                }
            }
            String content = item.getContent();
            if (!TextUtils.isEmpty(content)) {
                holder.tv_content.setText(getSpanned(content));
            }
            String time = item.getAdd_time();
            if (!TextUtils.isEmpty(time)) {
                holder.tv_time.setText(time);
            }
            String head = item.getFeed_avatar();
            if (!TextUtils.isEmpty(head)) {
                imageLoader.displayImage(head, holder.iv_head, ImageLoaderUtils.getDisplayImageOptions());
            }
            List<String> files = item.getFiles();
            List<PhotoInfo> fileList = item.fileList;
            if (fileList == null) {
                fileList = new ArrayList<>();
            } else {
                fileList.clear();
            }
            if (files != null && files.size() > 0) {
                String type = item.getFeed_type();
                if (!TextUtils.isEmpty(type)) {
                    if ("1".equals(type)) {
                        for (String url : files) {
                            PhotoInfo photoInfo = new PhotoInfo();
                            photoInfo.file_url = url;
                            photoInfo.file_thumb = url;
                            fileList.add(photoInfo);
                        }
                        holder.fl_video.setVisibility(View.GONE);
                        holder.multiImagView.setVisibility(View.VISIBLE);
                        if (fileList != null && fileList.size() > 0) {
                            holder.multiImagView.setList(fileList);
                            holder.multiImagView.setSceenW(sceenW);
                        }
                    } else {
                        String v_url = files.get(0);
                        if (!TextUtils.isEmpty(v_url)) {
                            imageLoader.displayImage(v_url, holder.iv_video_bg, ImageLoaderUtils.getDisplayImageOptions());
                            holder.fl_video.setVisibility(View.VISIBLE);
                        }
                        holder.multiImagView.setVisibility(View.GONE);
                    }
                } else {
                    String firstFile = files.get(0);
                    if (!TextUtils.isEmpty(firstFile)) {
                        if (firstFile.contains(".png") || firstFile.contains(".jpg") || firstFile.contains(".jpeg")) {
                            for (String url : files) {
                                PhotoInfo photoInfo = new PhotoInfo();
                                photoInfo.file_url = url;
                                photoInfo.file_thumb = url;
                                fileList.add(photoInfo);
                            }
                            holder.fl_video.setVisibility(View.GONE);
                            holder.multiImagView.setVisibility(View.VISIBLE);
                            if (fileList != null && fileList.size() > 0) {
                                holder.multiImagView.setList(fileList);
                                holder.multiImagView.setSceenW(sceenW);
                            }
                        } else {
                            if (!TextUtils.isEmpty(firstFile)) {
                                imageLoader.displayImage(firstFile, holder.iv_video_bg, ImageLoaderUtils.getDisplayImageOptions());
                                holder.fl_video.setVisibility(View.VISIBLE);
                            }
                            holder.multiImagView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        return convertView;
    }

    private Spanned getSpanned(String str) {
        String s1 = str.replace("<em>", "<font color='#68C270'>");
        String s2 = s1.replace("</em>", "</font>");
        return Html.fromHtml(s2);
    }

    private class ViewHolder {
        ImageView iv_head;
        TextView tv_name, tv_time, tv_content;
        LinearLayout ll_content;
        MultiImageView multiImagView;
        FrameLayout fl_video;
        ImageView iv_video_bg, iv_video_play;

        public ViewHolder(View view) {
            this.iv_head = (ImageView) view.findViewById(R.id.iv_head);
            this.tv_name = (TextView) view.findViewById(R.id.tv_name);
            this.tv_time = (TextView) view.findViewById(R.id.tv_time);
            ll_content = (LinearLayout) view.findViewById(R.id.ll_content);
            this.tv_content = (TextView) view.findViewById(R.id.tv_content);
            this.multiImagView = (MultiImageView) view.findViewById(R.id.multiImagView);
            fl_video = (FrameLayout) view.findViewById(R.id.fl_video);
            iv_video_bg = (ImageView) view.findViewById(R.id.iv_video_bg);
            iv_video_play = (ImageView) view.findViewById(R.id.iv_video_play);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iv_video_bg.getLayoutParams();
            params.height = sceenW / 4;
            params.width = sceenW / 4;
            iv_video_bg.setLayoutParams(params);
            FrameLayout.LayoutParams params_ = (FrameLayout.LayoutParams) iv_video_play.getLayoutParams();
            params_.height = sceenW / 8;
            params_.width = sceenW / 8;
            iv_video_play.setLayoutParams(params_);
        }
    }

}
