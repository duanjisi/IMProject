package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.entity.SearchCofriendEntity;
import im.boss66.com.widget.MultiImageView;

/**
 * 全网搜索
 */
public class searchCircleAdapter extends ABaseAdapter {

    public searchCircleAdapter(Context context,List<SearchCofriendEntity> list) {
        super(context);
    }

    @Override
    protected View setConvertView(int position, Object entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_search_circle, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SearchCofriendEntity item = (SearchCofriendEntity) getItem(position);
        if (item != null){

        }
        return convertView;
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
        }
    }

}
