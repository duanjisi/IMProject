package im.boss66.com.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.CircleMovementMethod;
import im.boss66.com.Utils.SpannableClickable;
import im.boss66.com.Utils.UrlUtils;
import im.boss66.com.activity.connection.ClanClubActivity;
import im.boss66.com.activity.connection.PeopleCenterActivity;
import im.boss66.com.activity.connection.SchoolHometownActivity;
import im.boss66.com.activity.discover.PersonalNearbyDetailActivity;
import im.boss66.com.entity.FriendCircleCommentEntity;

/**
 * 处理朋友圈评论列表
 */
public class CommentListView extends LinearLayout {
    private int itemColor;
    private int itemSelectorColor;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private List<FriendCircleCommentEntity> mDatas;
    private LayoutInflater layoutInflater;
    private String curUserId, classType;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setDatas(List<FriendCircleCommentEntity> datas) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        mDatas = datas;
        notifyDataSetChanged();
    }

    public List<FriendCircleCommentEntity> getDatas() {
        return mDatas;
    }

    public CommentListView(Context context) {
        super(context);
    }

    public CommentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
    }

    public CommentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.PraiseListView, 0, 0);
        try {
            //textview的默认颜色
            itemColor = typedArray.getColor(R.styleable.PraiseListView_item_color, getResources().getColor(R.color.praise_item_default));
            itemSelectorColor = typedArray.getColor(R.styleable.PraiseListView_item_selector_color, getResources().getColor(R.color.praise_item_selector_default));

        } finally {
            typedArray.recycle();
        }
    }

    public void notifyDataSetChanged() {

        removeAllViews();
        if (mDatas == null || mDatas.size() == 0) {
            return;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < mDatas.size(); i++) {
            final int index = i;
            View view = getView(index);
            if (view == null) {
                throw new NullPointerException("listview item layout is null, please check getView()...");
            }

            addView(view, index, layoutParams);
        }

    }

    private View getView(final int position) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(getContext());
        }
        View convertView = layoutInflater.inflate(R.layout.item_comment, null, false);

        TextView commentTv = (TextView) convertView.findViewById(R.id.commentTv);
        final CircleMovementMethod circleMovementMethod = new CircleMovementMethod(itemSelectorColor, itemSelectorColor);

        final FriendCircleCommentEntity bean = mDatas.get(position);
        String name = bean.getUid_from_name();
        String id = bean.getComm_id();
        String toReplyName = bean.getUid_to_name();

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(setClickableSpan(name, bean.getUid_from()));
        String from_id = bean.getUid_from();
        String uid_to = bean.getUid_to();
        if (from_id != null && !TextUtils.isEmpty(toReplyName) && !uid_to.equals(from_id)) {
            builder.append(" 回复 ");
            builder.append(setClickableSpan(toReplyName, uid_to));
        }
        builder.append(": ");
        //转换表情字符
        String contentBodyStr = bean.getContent();
        builder.append(UrlUtils.formatUrlString(contentBodyStr));
        commentTv.setText(builder);

        commentTv.setMovementMethod(circleMovementMethod);
        commentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleMovementMethod.isPassToTv()) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            }
        });
        commentTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (circleMovementMethod.isPassToTv()) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.onItemLongClick(position);
                    }
                    return true;
                }
                return false;
            }
        });

        return convertView;
    }

    @NonNull
    private SpannableString setClickableSpan(final String textStr, final String id) {
        SpannableString subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new SpannableClickable(itemColor) {
                                    @Override
                                    public void onClick(View widget) {
                                        if (!TextUtils.isEmpty(classType) && "FriendCircleActivity".equals(classType)) {
                                            Intent intent = new Intent(getContext(), PersonalNearbyDetailActivity.class);
                                            intent.putExtra("classType", "QureAccountActivity");
                                            intent.putExtra("userid", id);
                                            getContext().startActivity(intent);
                                            return;
                                        }
                                        Intent intent = new Intent(getContext(), PeopleCenterActivity.class);
                                        intent.putExtra("name", textStr);
                                        intent.putExtra("other", true);
                                        intent.putExtra("uid", id);
                                        intent.putExtra("nodetail", true);
                                        List<String> uidList = App.getInstance().getUidList();
                                        if (uidList != null && uidList.contains(id)) {
                                            return;
                                        }
                                        if (!TextUtils.isEmpty(classType)) {
                                            if ("SchoolHometownActivity".equals(classType)) {
                                                ((SchoolHometownActivity) getContext()).startActivityForResult(intent, 101);
                                            } else if ("ClanClubActivity".equals(classType)) {
                                                ((ClanClubActivity) getContext()).startActivityForResult(intent, 101);
                                            } else {
                                                getContext().startActivity(intent);
                                            }
                                        } else {
                                            getContext().startActivity(intent);
                                        }
                                    }
                                }, 0, subjectSpanText.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }

    public static interface OnItemClickListener {
        public void onItemClick(int position);
    }

    public static interface OnItemLongClickListener {
        public void onItemLongClick(int position);
    }

    public void getCurLoginUserId(String id) {
        this.curUserId = id;

    }

    public void getClassType(String classType) {
        this.classType = classType;
    }
}
