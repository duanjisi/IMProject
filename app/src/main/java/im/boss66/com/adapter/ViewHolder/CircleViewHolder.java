package im.boss66.com.adapter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.widget.CommentListView;
import im.boss66.com.widget.ExpandTextView;
import im.boss66.com.widget.PraiseListView;
import im.boss66.com.widget.SnsPopupWindow;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public abstract class CircleViewHolder extends RecyclerView.ViewHolder {

    public final static int TYPE_URL = 1;
    public final static int TYPE_IMAGE = 2;
    public final static int TYPE_VIDEO = 3;

    public int viewType;

    public ImageView headIv;
    public TextView nameTv;
    public TextView urlTipTv;
    /** 动态的内容 */
   public ExpandTextView contentTv;
    public TextView timeTv,addressTv;
    public TextView deleteBtn;
    public ImageView snsBtn;
    /** 点赞列表*/
    public PraiseListView praiseListView;

    public LinearLayout digCommentBody;
    public View digLine;

    /** 评论列表 */
    public CommentListView commentList;
    // ===========================
    public SnsPopupWindow snsPopupWindow;

    public CircleViewHolder(View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;

        ViewStub viewStub = (ViewStub) itemView.findViewById(R.id.viewStub);

        initSubView(viewType, viewStub);

        headIv = (ImageView) itemView.findViewById(R.id.headIv);
        nameTv = (TextView) itemView.findViewById(R.id.nameTv);
        digLine = itemView.findViewById(R.id.lin_dig);

        contentTv = (ExpandTextView) itemView.findViewById(R.id.contentTv);
        urlTipTv = (TextView) itemView.findViewById(R.id.urlTipTv);
        timeTv = (TextView) itemView.findViewById(R.id.timeTv);
        addressTv = (TextView) itemView.findViewById(R.id.addressTv);
        deleteBtn = (TextView) itemView.findViewById(R.id.deleteBtn);
        snsBtn = (ImageView) itemView.findViewById(R.id.snsBtn);
        LinearLayout.LayoutParams snsParams = (LinearLayout.LayoutParams) snsBtn.getLayoutParams();
        int sceenW = UIUtils.getScreenWidth(itemView.getContext());
        snsParams.width = sceenW/18;
        snsParams.height =sceenW/21;
        snsBtn.setLayoutParams(snsParams);
        praiseListView = (PraiseListView) itemView.findViewById(R.id.praiseListView);

        digCommentBody = (LinearLayout) itemView.findViewById(R.id.digCommentBody);
        commentList = (CommentListView)itemView.findViewById(R.id.commentList);

        snsPopupWindow = new SnsPopupWindow(itemView.getContext());

    }

    public abstract void initSubView(int viewType, ViewStub viewStub);
}
