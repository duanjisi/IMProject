package im.boss66.com.widget.circle;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.discover.CirclePresenter;
import im.boss66.com.entity.FriendCircleItem;
import im.boss66.com.entity.FriendCircleTestData;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public class CommentDialog extends Dialog implements
        android.view.View.OnClickListener {

    private Context mContext;
    private CirclePresenter mPresenter;
    private FriendCircleItem mCommentItem;
    private int mCirclePosition;

    public CommentDialog(Context context, CirclePresenter presenter,
                         FriendCircleItem commentItem, int circlePosition) {
        super(context, R.style.comment_dialog);
        mContext = context;
        this.mPresenter = presenter;
        this.mCommentItem = commentItem;
        this.mCirclePosition = circlePosition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_comment);
        initWindowParams();
        initView();
    }

    private void initWindowParams() {
        Window dialogWindow = getWindow();
        // 获取屏幕宽、高用
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (display.getWidth() * 0.65); // 宽度设置为屏幕的0.65

        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
    }

    private void initView() {
        TextView copyTv = (TextView) findViewById(R.id.copyTv);
        copyTv.setOnClickListener(this);
        TextView deleteTv = (TextView) findViewById(R.id.deleteTv);
        if (mCommentItem != null
                && FriendCircleTestData.curUser.getUserId().equals(
                mCommentItem.getUser().getUserId())) {
            deleteTv.setVisibility(View.VISIBLE);
        } else {
            deleteTv.setVisibility(View.GONE);
        }
        deleteTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copyTv:
                if (mCommentItem != null) {
                    ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(mCommentItem.getContent());
                }
                dismiss();
                break;
            case R.id.deleteTv:
                if (mPresenter != null && mCommentItem != null) {
                    mPresenter.deleteComment(mCirclePosition, mCommentItem.getId());
                }
                dismiss();
                break;
            default:
                break;
        }
    }

}
