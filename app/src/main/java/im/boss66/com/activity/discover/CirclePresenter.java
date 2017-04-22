package im.boss66.com.activity.discover;

import android.view.View;

import java.util.List;

import im.boss66.com.entity.CircleItem;
import im.boss66.com.entity.CommentConfig;
import im.boss66.com.entity.FavortItem;
import im.boss66.com.entity.FriendCircleItem;
import im.boss66.com.entity.FriendCircleTestData;
import im.boss66.com.listener.CircleContractListener;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public class CirclePresenter implements CircleContractListener.Presenter {
    //private CircleModel circleModel;
    private CircleContractListener.View view;

    public CirclePresenter(CircleContractListener.View view) {
        // circleModel = new CircleModel();
        this.view = view;
    }

    public void loadData(int loadType) {

        List<CircleItem> datas = FriendCircleTestData.createCircleDatas();
        if (view != null) {
            view.update2loadData(loadType, datas);
        }
    }

    /**
     * @param circleId
     * @return void    返回类型
     * @throws
     * @Title: deleteCircle
     * @Description: 删除动态
     */
    public void deleteCircle(final int circleId, int postion) {
        if (view != null) {
            view.update2DeleteCircle(circleId, postion);
        }
    }

    /**
     * @param circlePosition
     * @return void    返回类型
     * @throws
     * @Title: addFavort
     * @Description: 点赞
     */
    public void addFavort(final int circlePosition, int favortId) {
        FavortItem item = new FavortItem();
        if (view != null) {
            view.update2AddFavorite(circlePosition, favortId);
        }
    }

    /**
     * @param @param circlePosition
     * @param @param favortId
     * @return void    返回类型
     * @throws
     * @Title: deleteFavort
     * @Description: 取消点赞
     */
    public void deleteFavort(final int circlePosition, final int favortId) {
        if (view != null) {
            view.update2DeleteFavort(circlePosition, favortId);
        }
    }

    /**
     * @param content
     * @param config  CommentConfig
     * @return void    返回类型
     * @throws
     * @Title: addComment
     * @Description: 增加评论
     */
    public void addComment(final String content, final CommentConfig config) {
        if (config == null) {
            return;
        }
        FriendCircleItem newItem = new FriendCircleItem();
        newItem.setContent(content);
        if (config.commentType == CommentConfig.Type.PUBLIC) {

        } else if (config.commentType == CommentConfig.Type.REPLY) {
            //newItem.setToReplyUser(config.replyUser);
        }
        if (view != null) {
            view.update2AddComment(config.circlePosition, newItem);
        }
    }

    /**
     * @param @param circlePosition
     * @param @param commentId
     * @return void    返回类型
     * @throws
     * @Title: deleteComment
     * @Description: 删除评论
     */
    public void deleteComment(final int circlePosition, final String commentId, boolean isLong) {
        if (view != null) {
            view.update2DeleteComment(circlePosition, commentId, isLong);
        }
    }

    @Override
    public void addCollect(String url,String thumUrl,int type,String fromid) {
        if (view != null) {
            view.update2AddCollect(url,thumUrl,type,fromid);
        }
    }

    /**
     * @param commentConfig
     */
    public void showEditTextBody(CommentConfig commentConfig) {
        if (view != null) {
            view.updateEditTextBodyVisible(View.VISIBLE, commentConfig);
        }
    }

    /**
     * 清除对外部对象的引用，反正内存泄露。
     */
    public void recycle() {
        this.view = null;
    }
}
