package im.boss66.com.widget.video;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import im.boss66.com.R;


/**
 * 播放进度条：不能拖动
 * 
 * @author wzz
 * 
 */
public class DragSeekBar extends SeekBar {

	private boolean mDragable;

	public DragSeekBar(Context context) {
		super(context);
	}

	public DragSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DragSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray mTypedArray = context
				.obtainStyledAttributes(attrs, R.styleable.DragProgress);

		// 获取自定义属性和默认值
		mDragable = mTypedArray.getBoolean(R.styleable.DragProgress_isDrag, false);
		mTypedArray.recycle();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mBeforeTouchListener != null) {
			mBeforeTouchListener.doBeforeTouch();
		}
		if (!mDragable) {
			return true;// 不能拖动
		}
		return super.onTouchEvent(event);
	}

	public interface BeforeTouchListener {
		public void doBeforeTouch();
	}

	private BeforeTouchListener mBeforeTouchListener;

	public void setBeforeTouchListener(BeforeTouchListener l) {
		mBeforeTouchListener = l;
	}
	
	public void setDragable(boolean dragable){
		mDragable = dragable;
	}


	public boolean isDragable() {
		return mDragable;
	}
}
