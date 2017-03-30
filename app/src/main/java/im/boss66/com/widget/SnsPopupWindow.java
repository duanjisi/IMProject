package im.boss66.com.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.ActionItem;

/**
 * Created by GMARUnity on 2017/2/3.
 * 朋友圈点赞评论的popupwindow
 */
public class SnsPopupWindow extends PopupWindow implements View.OnClickListener {

    private TextView digBtn;
    private TextView commentBtn;

    // 实例化一个矩形
    private Rect mRect = new Rect();
    // 坐标的位置（x、y）
    private final int[] mLocation = new int[2];
    // 弹窗子类项选中时的监听
    private OnItemClickListener mItemClickListener;
    // 定义弹窗子类项列表
    private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();
    private int p_h, p_w;

    public void setmItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public ArrayList<ActionItem> getmActionItems() {
        return mActionItems;
    }

    public void setmActionItems(ArrayList<ActionItem> mActionItems) {
        this.mActionItems = mActionItems;
    }


    public SnsPopupWindow(Context context) {
        int sceenW = UIUtils.getScreenWidth(context);
        if (sceenW < 720) {
            sceenW = 1000;
        }
        View view = LayoutInflater.from(context).inflate(R.layout.social_sns_popupwindow, null);
        LinearLayout ll_pop = (LinearLayout) view.findViewById(R.id.ll_pop);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ll_pop.getLayoutParams();
        p_w = sceenW / 8 * 5;
        p_h = sceenW / 8;
        layoutParams.width = p_w;
        layoutParams.height = p_h;
        ll_pop.setLayoutParams(layoutParams);
        digBtn = (TextView) view.findViewById(R.id.digBtn);
        commentBtn = (TextView) view.findViewById(R.id.commentBtn);
        digBtn.setOnClickListener(this);
        commentBtn.setOnClickListener(this);
        this.setContentView(view);

//        this.setWidth(UIUtils.dip2px(context, sceenW / 3));
//        this.setHeight(UIUtils.dip2px(context, sceenW / 16));
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.social_pop_anim);

        initItemData();
    }

    private void initItemData() {
        addAction(new ActionItem("赞"));
        addAction(new ActionItem("评论"));
    }

    public void showPopupWindow(View parent) {
        parent.getLocationOnScreen(mLocation);
        // 设置矩形的大小
        //mRect.set(mLocation[0], mLocation[1], mLocation[0] + parent.getWidth(), mLocation[1] + parent.getHeight());
        digBtn.setText(mActionItems.get(0).mTitle);
        if (!this.isShowing()) {
            Log.i("showPopupWindow:", "width:" + p_w + "height:" + p_h);
            showAtLocation(parent, Gravity.NO_GRAVITY, mLocation[0] - p_w
                    , mLocation[1] - ((p_h - parent.getHeight()) / 2));
        } else {
            dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.digBtn:
                mItemClickListener.onItemClick(mActionItems.get(0), 0);
                break;
            case R.id.commentBtn:
                mItemClickListener.onItemClick(mActionItems.get(1), 1);
                break;
            default:
                break;
        }
    }

    /**
     * 添加子类项
     */
    public void addAction(ActionItem action) {
        if (action != null) {
            mActionItems.add(action);
        }
    }

    /**
     * 功能描述：弹窗子类项按钮监听事件
     */
    public static interface OnItemClickListener {
        public void onItemClick(ActionItem item, int position);
    }
}
