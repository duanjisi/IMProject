package im.boss66.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.Utils.UrlUtils;
import im.boss66.com.activity.discover.CirclePresenter;
import im.boss66.com.activity.discover.ImagePagerActivity;
import im.boss66.com.activity.discover.PersonalNearbyDetailActivity;
import im.boss66.com.activity.discover.WebViewActivity;
import im.boss66.com.activity.player.VideoPlayerNewActivity;
import im.boss66.com.adapter.ViewHolder.CircleViewHolder;
import im.boss66.com.adapter.ViewHolder.ImageViewHolder;
import im.boss66.com.adapter.ViewHolder.URLViewHolder;
import im.boss66.com.adapter.ViewHolder.VideoViewHolder;
import im.boss66.com.entity.ActionItem;
import im.boss66.com.entity.CommentConfig;
import im.boss66.com.entity.FriendCircle;
import im.boss66.com.entity.FriendCircleCommentEntity;
import im.boss66.com.entity.FriendCirclePraiseEntity;
import im.boss66.com.entity.PhotoInfo;
import im.boss66.com.widget.CommentListView;
import im.boss66.com.widget.ExpandTextView;
import im.boss66.com.widget.MultiImageView;
import im.boss66.com.widget.PraiseListView;
import im.boss66.com.widget.SnsPopupWindow;
import im.boss66.com.widget.circle.CommentDialog;
import im.boss66.com.widget.circle.GlideCircleTransform;

/**
 * 朋友圈
 */
public class FriendCircleAdapter extends BaseRecycleViewAdapter {

    public static final int HEADVIEW_SIZE = 0;
    private CirclePresenter presenter;
    private Context context;
    public final int TYPE_URL = 3;
    public final int TYPE_IMG = 1;
    public final int TYPE_VIDEO = 2;
    private String curUserid;
    private int sceenW;

    public void setCirclePresenter(CirclePresenter presenter) {
        this.presenter = presenter;
    }

    public FriendCircleAdapter(Context context) {
        this.context = context;
        sceenW = UIUtils.getScreenWidth(context);
    }

    @Override
    public int getItemViewType(int position) {
        int itemType = 0;
        FriendCircle entity = (FriendCircle) datas.get(position);
        if (entity.getFeed_type() == TYPE_IMG) {
            itemType = CircleViewHolder.TYPE_IMAGE;
        } else if (entity.getFeed_type() == TYPE_VIDEO) {
            itemType = CircleViewHolder.TYPE_VIDEO;
        } else if (entity.getFeed_type() == TYPE_URL) {
            itemType = CircleViewHolder.TYPE_URL;
        }
        return itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_circle, parent, false);

        if (viewType == CircleViewHolder.TYPE_URL) {
            viewHolder = new URLViewHolder(view);
        } else if (viewType == CircleViewHolder.TYPE_IMAGE) {
            viewHolder = new ImageViewHolder(view);
        } else if (viewType == CircleViewHolder.TYPE_VIDEO) {
            viewHolder = new VideoViewHolder(view);
        }
        return viewHolder;
    }


    @Override
    public void onBindItemHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        final FriendCircle entity = (FriendCircle) datas.get(position);
        final List<FriendCirclePraiseEntity> praise_list = entity.getPraise_list();//点赞列表
        final List<FriendCircleCommentEntity> comment_list = entity.getComment_list();//评论列表
        final int feed_id = entity.getFeed_id();
        String name = entity.getFeed_username();
        String headImg = entity.getFeed_avatar();
        String content = entity.getContent();
        String createTime = entity.getAdd_time();
        boolean hasFavort = entity.hasFavort();
        boolean hasComment = entity.hasComment();

        final int circlePosition = position - HEADVIEW_SIZE;
        final CircleViewHolder holder = (CircleViewHolder) viewHolder;

        Glide.with(context).load(headImg).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.color.bg_gray).
                transform(new GlideCircleTransform(context)).into(holder.headIv);
        holder.headIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = entity.getFeed_uid();
                Intent intent = new Intent(context, PersonalNearbyDetailActivity.class);
                intent.putExtra("classType", "QureAccountActivity");
                intent.putExtra("userid", uid);
                intent.putExtra("from", "friendcircle");
                context.startActivity(intent);
            }
        });
        holder.nameTv.setText(name);
        holder.timeTv.setText(createTime);

        if (!TextUtils.isEmpty(content)) {
            holder.contentTv.setExpand(entity.isExpand());
            holder.contentTv.setExpandStatusListener(new ExpandTextView.ExpandStatusListener() {
                @Override
                public void statusChange(boolean isExpand) {
                    entity.setExpand(isExpand);
                }
            });
            holder.contentTv.setText(UrlUtils.formatUrlString(content));
        }
        holder.contentTv.setVisibility(TextUtils.isEmpty(content) ? View.GONE : View.VISIBLE);
        holder.contentTv.setOnItemLongClickListener(new ExpandTextView.OnItemLongClickListener() {
            @Override
            public void onItemLongClick() {
                String content = entity.getContent();
                CommentDialog dialog = new CommentDialog(context, presenter, false, true, content, null, 0, entity.getFeed_uid());
                dialog.show();
            }
        });
        if (curUserid.equals(entity.getFeed_uid())) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
        }
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除
                if (presenter != null) {
                    presenter.deleteCircle(feed_id, position);
                }
            }
        });
        if (hasFavort || hasComment) {
            if (hasFavort) {//处理点赞列表
                holder.praiseListView.setOnItemClickListener(new PraiseListView.OnItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        String userId = praise_list.get(position).getUser_id();
                        Intent intent = new Intent(context, PersonalNearbyDetailActivity.class);
                        intent.putExtra("classType", "QureAccountActivity");
                        intent.putExtra("userid", userId);
                        context.startActivity(intent);
                    }
                });
                holder.praiseListView.setDatas(praise_list);
                holder.praiseListView.setVisibility(View.VISIBLE);
            } else {
                holder.praiseListView.setVisibility(View.GONE);
            }

            if (hasComment) {//处理评论列表
                holder.commentList.setOnItemClickListener(new CommentListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int commentPosition) {
                        FriendCircleCommentEntity commentItem = comment_list.get(commentPosition);
                        if (curUserid.equals(commentItem.getUid_from())) {//复制或者删除自己的评论
                            presenter.deleteComment(circlePosition, commentItem.getComm_id(), false);
                        } else {//回复别人的评论
                            if (presenter != null) {
                                CommentConfig config = new CommentConfig();
                                config.circlePosition = circlePosition;
                                config.commentPosition = commentPosition;
                                config.commentType = CommentConfig.Type.REPLY;
                                config.uid_to_name = commentItem.getUid_from_name();
                                config.commentFromId = commentItem.getUid_from();
                                config.pid = commentItem.getPid();
                                config.isReply = true;
                                config.commentId = commentItem.getComm_id();
                                config.feedid = Integer.parseInt(commentItem.getFeed_id());
                                presenter.showEditTextBody(config);
                            }
                        }
                    }
                });
                holder.commentList.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int commentPosition) {
                        //长按进行复制或者删除
                        FriendCircleCommentEntity commentItem = comment_list.get(commentPosition);
                        CommentDialog dialog = new CommentDialog(context, presenter, commentItem, circlePosition);
                        dialog.setMfeedId(entity.getFeed_uid());
                        dialog.show();
                    }
                });
                holder.commentList.getCurLoginUserId(curUserid);
                holder.commentList.setDatas(comment_list);
                holder.commentList.setVisibility(View.VISIBLE);

            } else {
                holder.commentList.setVisibility(View.GONE);
            }
            holder.digCommentBody.setVisibility(View.VISIBLE);
        } else {
            holder.digCommentBody.setVisibility(View.GONE);
        }

        holder.digLine.setVisibility(hasFavort && hasComment ? View.VISIBLE : View.GONE);

        final SnsPopupWindow snsPopupWindow = holder.snsPopupWindow;
        //判断是否已点赞
        int isPraise = entity.getIs_praise();
        if (isPraise == 1) {
            snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
        } else {
            snsPopupWindow.getmActionItems().get(0).mTitle = "点赞";
        }
        snsPopupWindow.update();
        snsPopupWindow.setmItemClickListener(new PopupItemClickListener(circlePosition, entity, feed_id));
        holder.snsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出popupwindow
                snsPopupWindow.showPopupWindow(view);
            }
        });

        holder.urlTipTv.setVisibility(View.GONE);
        switch (holder.viewType) {
            case CircleViewHolder.TYPE_URL:// 处理链接动态的链接内容和和图片
                if (holder instanceof URLViewHolder) {
                    final List<PhotoInfo> files = entity.getFiles();
                    if (files != null && files.size() > 0) {
                        String linkImg = files.get(0).file_thumb;
                        Glide.with(context).load(linkImg).into(((URLViewHolder) holder).urlImageIv);
                        ((URLViewHolder) holder).urlBody.setVisibility(View.VISIBLE);
                        ((URLViewHolder) holder).urlTipTv.setVisibility(View.VISIBLE);
                        ((URLViewHolder) holder).urlBody.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(context, WebViewActivity.class);
                                intent.putExtra("url", files.get(0).file_url);
                                context.startActivity(intent);
                            }
                        });
                    }
                    //String linkTitle = circleItem.getLinkTitle();
                    //((URLViewHolder) holder).urlContentTv.setText(linkTitle);
                }

                break;
            case CircleViewHolder.TYPE_IMAGE:// 处理图片
                if (holder instanceof ImageViewHolder) {
                    final List<PhotoInfo> photos = entity.getFiles();
                    if (photos != null && photos.size() > 0) {
                        ((ImageViewHolder) holder).multiImageView.setVisibility(View.VISIBLE);
                        ((ImageViewHolder) holder).multiImageView.setSceenW(sceenW);
                        ((ImageViewHolder) holder).multiImageView.setList(photos);
                        ((ImageViewHolder) holder).multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                //imagesize是作为loading时的图片size
                                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());

                                List<String> photoUrls = new ArrayList<String>();
                                for (PhotoInfo photoInfo : photos) {
                                    photoUrls.add(photoInfo.file_url);
                                }
                                ImagePagerActivity.startImagePagerActivity(context, photoUrls, position, imageSize, false);
                            }

                            @Override
                            public void onItemLongClick(View view, int postion) {
                                List<PhotoInfo> photos = entity.getFiles();
                                if (photos != null && photos.size() > 0 && photos.size() > postion) {
                                    PhotoInfo photoInfo = photos.get(postion);
                                    if (photoInfo != null) {
                                        CommentDialog dialog = new CommentDialog(context, presenter, false,
                                                false, photoInfo.file_url, photoInfo.file_thumb, 1, entity.getFeed_uid());
                                        dialog.show();
                                    }
                                }
                            }
                        });
                    } else {
                        ((ImageViewHolder) holder).multiImageView.setVisibility(View.GONE);
                    }
                }

                break;
            case CircleViewHolder.TYPE_VIDEO:
                if (holder instanceof VideoViewHolder) {
                    String urlimg = entity.getFiles().get(0).file_thumb;

                    LinearLayout.LayoutParams fl_param = (LinearLayout.LayoutParams) ((VideoViewHolder) holder).fl_video.getLayoutParams();
                    fl_param.width = sceenW / 4;
                    fl_param.height = sceenW / 2;
                    ((VideoViewHolder) holder).fl_video.setLayoutParams(fl_param);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ((VideoViewHolder) holder).iv_video_bg.getLayoutParams();
                    params.width = sceenW / 4;
                    params.height = sceenW / 2;
                    ((VideoViewHolder) holder).iv_video_bg.setLayoutParams(params);

                    FrameLayout.LayoutParams params_p = (FrameLayout.LayoutParams) ((VideoViewHolder) holder).iv_video_play.getLayoutParams();
                    params_p.width = sceenW / 8;
                    params_p.height = sceenW / 8;
                    ((VideoViewHolder) holder).iv_video_play.setLayoutParams(params_p);

                    Glide.with(context).load(urlimg).into(((VideoViewHolder) holder).iv_video_bg);
                    ((VideoViewHolder) holder).iv_video_bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            List<PhotoInfo> files = entity.getFiles();
                            if (files != null && files.size() > 0) {
                                String url = files.get(0).file_url;
                                String url_img = files.get(0).file_thumb;
                                if (!TextUtils.isEmpty(url)) {
                                    Intent intent = new Intent(context, VideoPlayerNewActivity.class);
                                    intent.putExtra("videoPath", url);
                                    intent.putExtra("imgurl", url_img);
                                    context.startActivity(intent);
                                }
                            }
                        }
                    });
                    ((VideoViewHolder) holder).iv_video_bg.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            List<PhotoInfo> files = entity.getFiles();
                            if (files != null && files.size() > 0) {
                                String url = files.get(0).file_url;
                                String url_img = files.get(0).file_thumb;
                                CommentDialog dialog = new CommentDialog(context, presenter, false, false, url, url_img, 2, entity.getFeed_uid());
                                dialog.show();
                            }
                            return true;
                        }
                    });
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();//有head需要加1
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener {
        private int mFavorId;
        //动态在列表中的位置
        private int mCirclePosition;
        private long mLasttime = 0;
        private FriendCircle mCircleItem;

        public PopupItemClickListener(int circlePosition, FriendCircle circleItem, int favorId) {
            this.mFavorId = favorId;
            this.mCirclePosition = circlePosition;
            this.mCircleItem = circleItem;
        }

        @Override
        public void onItemClick(ActionItem actionitem, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    if (System.currentTimeMillis() - mLasttime < 700)//防止快速点击操作
                        return;
                    mLasttime = System.currentTimeMillis();
                    if (presenter != null) {
                        if ("点赞".equals(actionitem.mTitle.toString())) {
                            presenter.addFavort(mCirclePosition, mFavorId);
                        } else {//取消点赞
                            presenter.deleteFavort(mCirclePosition, mFavorId);
                        }
                    }
                    break;
                case 1://发布评论
                    if (presenter != null) {
                        CommentConfig config = new CommentConfig();
                        config.circlePosition = mCirclePosition;
                        config.commentType = CommentConfig.Type.PUBLIC;
                        config.feedid = mFavorId;
                        config.isReply = false;
                        presenter.showEditTextBody(config);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void getCurUserId(String uid) {
        this.curUserid = uid;
    }

}
