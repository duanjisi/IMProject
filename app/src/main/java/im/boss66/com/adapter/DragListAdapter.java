package im.boss66.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import im.boss66.com.Constants;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.EmoGroup;

/***
 * �Զ������קListView������
 *
 * @author zihao
 */
public class DragListAdapter extends BaseAdapter {
    private List<EmoGroup> mDataList;// ��������
    private Context mContext;
    private ImageLoader imageLoader;
    private boolean isOrder = false;

    /**
     * DragListAdapter���췽��
     *
     * @param context  // �����Ķ���
     * @param dataList // ���ݼ���
     */
    public DragListAdapter(Context context, ArrayList<EmoGroup> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    public void setOrder(boolean order) {
        isOrder = order;
        notifyDataSetChanged();
    }

    /**
     * �����Ƿ���ʾ�½���Item
     *
     * @param showItem
     */
    public void showDropItem(boolean showItem) {
        this.mShowItem = showItem;
    }

    /**
     * ���ò��ɼ����λ�ñ��
     *
     * @param position
     */
    public void setInvisiblePosition(int position) {
        mInvisilePosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(
                R.layout.drag_list_item, null);
        initItemView(position, convertView);
        ImageView image = (ImageView) convertView.findViewById(R.id.drag_item_image);
        TextView titleTv = (TextView) convertView
                .findViewById(R.id.tv_title);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_emoji);
        EmoGroup entity = mDataList.get(position);
        titleTv.setText(entity.getGroup_name());
        imageLoader.displayImage(getPath(entity),
                imageView,
                ImageLoaderUtils.getDisplayImageOptions());
        if (isChanged) {// �ж��Ƿ����˸ı�
            if (position == mInvisilePosition) {
                if (!mShowItem) {// ����ק���̲�������ʾ��״̬�£�����Item��������
                    // ��Ϊitem����Ϊ��ɫ���ʶ�������Ҫ����Ϊȫ͸��ɫ��ֹ�а�ɫ�ڵ����⣨������ק��
                    convertView.findViewById(R.id.drag_item_layout)
                            .setBackgroundColor(0x0000000000);

                    // ����Item���������
                    int vis = View.INVISIBLE;
                    int gone = View.GONE;
                    if (isOrder) {
                        UIUtils.showView(image);
                    } else {
                        UIUtils.hindView(image);
                    }
                    convertView.findViewById(R.id.drag_item_close_layout)
                            .setVisibility(vis);
                    titleTv.setVisibility(vis);
                }
            }
            if (isOrder) {
                UIUtils.showView(image);
            } else {
                UIUtils.hindView(image);
            }

            if (mLastFlag != -1) {
                if (mLastFlag == 1) {
                    if (position > mInvisilePosition) {
                        Animation animation;
                        animation = getFromSelfAnimation(0, -mHeight);
                        convertView.startAnimation(animation);
                    }
                } else if (mLastFlag == 0) {
                    if (position < mInvisilePosition) {
                        Animation animation;
                        animation = getFromSelfAnimation(0, mHeight);
                        convertView.startAnimation(animation);
                    }
                }
            }
        }
        return convertView;
    }

    private String getPath(EmoGroup entity) {
        String path = "file:/" + Constants.EMO_DIR_PATH +
                entity.getCate_id() + File.separator +
                entity.getGroup_id() + File.separator +
                entity.getGroup_icon();
        return path;
    }

    /**
     * ��ʼ��Item��ͼ
     *
     * @param convertView
     */
    private void initItemView(final int position, final View convertView) {

        if (convertView != null) {
            // ���ö�Ӧ�ļ���
            convertView.findViewById(R.id.drag_item_close_layout)
                    .setOnClickListener(new OnClickListener() {// ɾ��

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            removeItem(position);
                        }
                    });
        }
    }

    private int mInvisilePosition = -1;// ������ǲ��ɼ�Item��λ��
    private boolean isChanged = true;// ��ʶ�Ƿ����ı�
    private boolean mShowItem = false;// ��ʶ�Ƿ���ʾ��קItem������

    /***
     * ��̬�޸�ListView�ķ�λ.
     *
     * @param startPosition ����ƶ���position
     * @param endPosition   �ɿ�ʱ���position
     */
    public void exchange(int startPosition, int endPosition) {
        Object startObject = getItem(startPosition);

        if (startPosition < endPosition) {
            mDataList.add(endPosition + 1, (EmoGroup) startObject);
            mDataList.remove(startPosition);
        } else {
            mDataList.add(endPosition, (EmoGroup) startObject);
            mDataList.remove(startPosition + 1);
        }

        isChanged = true;
    }

    /**
     * ��̬�޸�Item����
     *
     * @param startPosition // ��ʼ��λ��
     * @param endPosition   // ��ǰͣ����λ��
     */
    public void exchangeCopy(int startPosition, int endPosition) {
        Object startObject = getCopyItem(startPosition);

        if (startPosition < endPosition) {// �����ƶ�
            mCopyList.add(endPosition + 1, (EmoGroup) startObject);
            mCopyList.remove(startPosition);
        } else {// �����϶����߲���
            mCopyList.add(endPosition, (EmoGroup) startObject);
            mCopyList.remove(startPosition + 1);
        }

        isChanged = true;
    }

    /**
     * ɾ��ָ����Item
     *
     * @param pos // Ҫɾ�����±�
     */
    private void removeItem(int pos) {
        if (mDataList != null && mDataList.size() > pos) {
            mDataList.remove(pos);
            this.notifyDataSetChanged();
        }
    }

    /**
     * ��ȡ����(��ק)Item��
     *
     * @param position
     * @return
     */
    public Object getCopyItem(int position) {
        return mCopyList.get(position);
    }

    /**
     * ��ȡItem����
     */
    @Override
    public int getCount() {
        return mDataList.size();
    }

    /**
     * ��ȡListView��Item��
     */
    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * ����϶���
     *
     * @param start // Ҫ������ӵ�λ��
     * @param obj
     */
    public void addDragItem(int start, Object obj) {
        mDataList.remove(start);// ɾ������
        mDataList.add(start, (EmoGroup) obj);// ���ɾ����
    }

    private ArrayList<EmoGroup> mCopyList = new ArrayList<EmoGroup>();

    public void copyList() {
        mCopyList.clear();
        for (EmoGroup str : mDataList) {
            mCopyList.add(str);
        }
    }

    public void pastList() {
        mDataList.clear();
        for (EmoGroup str : mCopyList) {
            mDataList.add(str);
        }
    }

    public List<EmoGroup> getList() {
        return mDataList;
    }

    private boolean isSameDragDirection = true;// �Ƿ�Ϊ��ͬ�����϶��ı��
    private int mLastFlag = -1;
    private int mHeight;
    private int mDragPosition = -1;

    /**
     * �����Ƿ�Ϊ��ͬ�����϶��ı��
     *
     * @param value
     */
    public void setIsSameDragDirection(boolean value) {
        isSameDragDirection = value;
    }

    /**
     * �����϶�������
     *
     * @param flag
     */
    public void setLastFlag(int flag) {
        mLastFlag = flag;
    }

    /**
     * ���ø߶�
     *
     * @param value
     */
    public void setHeight(int value) {
        mHeight = value;
    }

    /**
     * ���õ�ǰ�϶�λ��
     *
     * @param position
     */
    public void setCurrentDragPosition(int position) {
        mDragPosition = position;
    }

    /**
     * ��������ֵĶ���
     *
     * @param x
     * @param y
     * @return
     */
    private Animation getFromSelfAnimation(int x, int y) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, x,
                Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, y);
        translateAnimation
                .setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(100);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        return translateAnimation;
    }
}