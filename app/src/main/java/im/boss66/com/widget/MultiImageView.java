package im.boss66.com.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.util.DensityUtil;

import java.util.List;

import im.boss66.com.R;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.entity.PhotoInfo;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public class MultiImageView extends LinearLayout {

    public static int MAX_WIDTH = 0;

    // 照片的Url列表
    private List<PhotoInfo> imagesList;

    /**
     * 长度 单位为Pixel
     **/
    private int pxOneMaxWandH;  // 单张图最大允许宽高
    private int pxMoreWandH = 0;// 多张图的宽高
    private int pxImagePadding = UIUtils.dip2px(getContext(), 3);// 图片间的间距

    private int MAX_PER_ROW_COUNT = 3;// 每行显示最大数

    private LinearLayout.LayoutParams onePicPara;
    private LinearLayout.LayoutParams morePara, moreParaColumnFirst;
    private LinearLayout.LayoutParams rowPara;

    private OnItemClickListener mOnItemClickListener;

    private int fromType = 0;

    private int sceenW;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public MultiImageView(Context context) {
        super(context);
    }

    public MultiImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setList(List<PhotoInfo> lists) throws IllegalArgumentException {
        if (lists == null) {
            throw new IllegalArgumentException("imageList is null...");
        }
        imagesList = lists;

        if (MAX_WIDTH > 0) {
            pxMoreWandH = (MAX_WIDTH - pxImagePadding * 2) / 3; //解决右侧图片和内容对不齐问题
            pxOneMaxWandH = MAX_WIDTH * 2 / 3;
            initImageLayoutParams();
        }

        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MAX_WIDTH == 0) {
            int width = measureWidth(widthMeasureSpec);
            if (width > 0) {
                MAX_WIDTH = width;
                if (imagesList != null && imagesList.size() > 0) {
                    setList(imagesList);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            // result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
            // + getPaddingRight();
            if (specMode == View.MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by
                // measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private void initImageLayoutParams() {
        int wrap = LinearLayout.LayoutParams.WRAP_CONTENT;
        int match = LinearLayout.LayoutParams.MATCH_PARENT;
        int wrap_1 = (int) (sceenW / 3 * 0.7);


        moreParaColumnFirst = new LinearLayout.LayoutParams(pxMoreWandH, pxMoreWandH);
        if (fromType == 1){
            morePara = new LinearLayout.LayoutParams(sceenW/7, pxMoreWandH);
            onePicPara = new LinearLayout.LayoutParams(sceenW/7, wrap_1);
        }else {
            onePicPara = new LinearLayout.LayoutParams(wrap_1, wrap_1);
            morePara = new LinearLayout.LayoutParams(pxMoreWandH, pxMoreWandH);
        }
        morePara.setMargins(pxImagePadding, 0, 0, 0);
        rowPara = new LinearLayout.LayoutParams(match, wrap);
    }

    // 根据imageView的数量初始化不同的View布局,还要为每一个View作点击效果
    private void initView() {
        this.setOrientation(VERTICAL);
        this.removeAllViews();
        if (MAX_WIDTH == 0) {
            //为了触发onMeasure()来测量MultiImageView的最大宽度，MultiImageView的宽设置为match_parent
            addView(new View(getContext()));
            return;
        }

        if (imagesList == null || imagesList.size() == 0) {
            return;
        }

        if (imagesList.size() == 1) {
            PhotoInfo info = imagesList.get(0);
            if (!info.file_url.contains(".png") || !info.file_url.contains(".jpg") || !info.file_url.contains(".jpeg")) {
                return;
            } else {
                addView(createImageView(0, false));
            }
        } else {
            int allCount = imagesList.size();
            if (fromType == 1 && allCount >= 2) {
                MAX_PER_ROW_COUNT = 2;
            } else {
                MAX_PER_ROW_COUNT = 3;
            }
            int rowCount = allCount / MAX_PER_ROW_COUNT
                    + (allCount % MAX_PER_ROW_COUNT > 0 ? 1 : 0);// 行数
            if (fromType == 1 && rowCount > 2) {
                rowCount = 2;
            }
            for (int rowCursor = 0; rowCursor < rowCount; rowCursor++) {
                LinearLayout rowLayout = new LinearLayout(getContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);

                rowLayout.setLayoutParams(rowPara);
                if (rowCursor != 0) {
                    rowLayout.setPadding(0, pxImagePadding, 0, 0);
                }

                int columnCount = allCount % MAX_PER_ROW_COUNT == 0 ? MAX_PER_ROW_COUNT
                        : allCount % MAX_PER_ROW_COUNT;//每行的列数
                if (rowCursor != rowCount - 1) {
                    columnCount = MAX_PER_ROW_COUNT;
                }
                addView(rowLayout);

                int rowOffset = rowCursor * MAX_PER_ROW_COUNT;// 行偏移
                for (int columnCursor = 0; columnCursor < columnCount; columnCursor++) {
                    int position = columnCursor + rowOffset;
                    PhotoInfo info = imagesList.get(position);
                    String url = info.file_url;
                    int type = info.type;
                    if (type != 1 && url != null && !url.contains(".png") && !url.contains(".jpg")
                            && !url.contains(".jpeg")) {
                        return;
                    } else {
                        rowLayout.addView(createImageView(position, true));
                    }
                }
            }
        }
    }

    private ImageView createImageView(int position, final boolean isMultiImage) {
        PhotoInfo photoInfo = imagesList.get(position);
        ImageView imageView;
        if (fromType == 1){
            imageView = new ImageView(getContext());
        }else {
            imageView = new ColorFilterImageView(getContext());
        }
        if (isMultiImage) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(position % MAX_PER_ROW_COUNT == 0 ? moreParaColumnFirst : morePara);
        } else {
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            //imageView.setMaxHeight(pxOneMaxWandH);

            int expectW = photoInfo.w;
            int expectH = photoInfo.h;

            if (expectW == 0 || expectH == 0) {
                imageView.setLayoutParams(onePicPara);
            } else {
                int actualW = 0;
                int actualH = 0;
                float scale = ((float) expectH) / ((float) expectW);
                if (expectW > pxOneMaxWandH) {
                    actualW = pxOneMaxWandH;
                    actualH = (int) (actualW * scale);
                } else if (expectW < pxMoreWandH) {
                    actualW = pxMoreWandH;
                    actualH = (int) (actualW * scale);
                } else {
                    actualW = expectW;
                    actualH = expectH;
                }
                imageView.setLayoutParams(new LinearLayout.LayoutParams(actualW, actualH));
            }
        }
        if (photoInfo.type == 0) {
            String url;
            if (!TextUtils.isEmpty(photoInfo.file_thumb)) {
                url = photoInfo.file_thumb;
            } else {
                url = photoInfo.file_url;
            }
            imageView.setId(url.hashCode());
            Glide.with(getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        } else {
            imageView.setId(photoInfo.resourceid);
            Glide.with(getContext()).load(photoInfo.resourceid).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }
        imageView.setOnClickListener(new ImageOnClickListener(position));
        imageView.setBackgroundColor(getResources().getColor(R.color.im_font_color_text_hint));


        return imageView;
    }

    private class ImageOnClickListener implements View.OnClickListener {

        private int position;

        public ImageOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, position);
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }

    public void setSceenW(int sceenW) {
        this.sceenW = sceenW;
    }

}
