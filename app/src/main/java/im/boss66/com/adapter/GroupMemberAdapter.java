package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.entity.MemberEntity;

/**
 * Created by Johnny on 2016/7/27.
 */
public class GroupMemberAdapter extends ABaseAdapter<MemberEntity> {
    private ImageLoader imageLoader;
    private boolean isShow;

    public GroupMemberAdapter(Context context) {
        super(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    @Override
    protected View setConvertView(int position, MemberEntity entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_group_member, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (entity != null) {
            holder.tvName.setText(entity.getNickname());
            imageLoader.displayImage(entity.getSnap(), holder.image, ImageLoaderUtils.getDisplayImageOptions());
            boolean ischecked = entity.isChecked();
            if (isShow) {
                holder.ivCheck.setVisibility(View.VISIBLE);
                if (ischecked) {
                    holder.ivCheck.setImageResource(R.drawable.sp_check_press);
                } else {
                    holder.ivCheck.setImageResource(R.drawable.sp_check_default);
                }
            } else {
                holder.ivCheck.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tvName;
        ImageView image;
        ImageView ivCheck;

        public ViewHolder(View view) {
            this.tvName = (TextView) view.findViewById(R.id.tvName);
            this.image = (ImageView) view.findViewById(R.id.image);
            this.ivCheck = (ImageView) view.findViewById(R.id.iv_selected_tag);
        }
    }
}
