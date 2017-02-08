package im.boss66.com.listener;

import java.util.List;

import im.boss66.com.entity.CircleItem;
import im.boss66.com.entity.CommentConfig;
import im.boss66.com.entity.FavortItem;
import im.boss66.com.entity.FriendCircleItem;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public interface CircleContractListener {

    interface View{
        void update2DeleteCircle(String circleId);
        void update2AddFavorite(int circlePosition, FavortItem addItem);
        void update2DeleteFavort(int circlePosition, String favortId);
        void update2AddComment(int circlePosition, FriendCircleItem addItem);
        void update2DeleteComment(int circlePosition, String commentId);
        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);
        void update2loadData(int loadType, List<CircleItem> datas);
    }

    interface Presenter{
        void loadData(int loadType);
        void deleteCircle(final String circleId);
        void addFavort(final int circlePosition);
        void deleteFavort(final int circlePosition, final String favortId);
        void deleteComment(final int circlePosition, final String commentId);

    }

}
