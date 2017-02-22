package im.boss66.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.NewFriend;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.request.HandleFriendRequest;
/**
 * Created by Johnny on 2017/2/17.
 */
public class NewFriendsAdapter extends ABaseAdapter<NewFriend> {
    private final static String TAG = NewFriendsAdapter.class.getSimpleName();
    private ImageLoader imageLoader;

    public NewFriendsAdapter(Context context) {
        super(context);
        this.imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    @Override
    protected View setConvertView(int position, final NewFriend entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_new_friend, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (entity != null) {
            holder.tv_name.setText(entity.getUser_name());
            holder.tv_msg.setText(entity.getFriend_note());
            imageLoader.displayImage(entity.getAvatar(), holder.image, ImageLoaderUtils.getDisplayImageOptions());
            String mark = entity.getFeedback_mark();
            if (mark.equals("0")) {//已拒绝
                UIUtils.hindView(holder.tv_receiver);
                UIUtils.showView(holder.tv_added);
                holder.tv_added.setText("已拒绝");
            } else if (mark.equals("1")) {//处理中（接受）
                UIUtils.hindView(holder.tv_added);
                UIUtils.showView(holder.tv_receiver);
            } else if (mark.equals("2")) {//已同意（已添加）
                UIUtils.hindView(holder.tv_receiver);
                UIUtils.showView(holder.tv_added);
                holder.tv_added.setText("已添加");
            }
            holder.tv_receiver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestAddFriend(entity);
                }
            });
        }
        return convertView;
    }

    private void requestAddFriend(final NewFriend friend) {
        showLoadingDialog();
        HandleFriendRequest request = new HandleFriendRequest(TAG, friend.getId(), "2");
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                cancelLoadingDialog();
                friend.setFeedback_mark("2");
                notifyDataSetChanged();
                showToast("接受好友成功!", true);
            }

            @Override
            public void onFailure(String msg) {
                cancelLoadingDialog();
                showToast(msg, true);
            }
        });
    }


    private class ViewHolder {
        ImageView image;
        TextView tv_name;
        TextView tv_msg;
        TextView tv_receiver;
        TextView tv_added;

        public ViewHolder(View view) {
            this.image = (ImageView) view.findViewById(R.id.image);
            this.tv_name = (TextView) view.findViewById(R.id.tv_name);
            this.tv_msg = (TextView) view.findViewById(R.id.tv_msg);
            this.tv_receiver = (TextView) view.findViewById(R.id.tv_receiver);
            this.tv_added = (TextView) view.findViewById(R.id.tv_added);
        }
    }
}
