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
        void update2DeleteCircle(int circleId,int postion);
        void update2AddFavorite(int circlePosition, int favortId);
        void update2DeleteFavort(int circlePosition, int favortId);
        void update2AddComment(int circlePosition, FriendCircleItem addItem);
        void update2DeleteComment(int circlePosition, String commentId,boolean isLong);
        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);
        void update2loadData(int loadType, List<CircleItem> datas);
        void update2AddCollect(String url,String thumUrl,int type,String fromid);
    }

    interface Presenter{
        void loadData(int loadType);
        void deleteCircle(final int circleId,int postion);
        void addFavort(final int circlePosition,final int favortId);
        void deleteFavort(final int circlePosition, final int favortId);
        void deleteComment(final int circlePosition, final String commentId,boolean isLong);
        void addCollect(String url,String thumUrl,int type,String fromid);
    }

}
