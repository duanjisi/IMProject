package im.boss66.com.widget.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

public abstract class BaseCenterView extends FrameLayout {

	protected Context mContext;

	public BaseCenterView(Context context) {
		super(context);
		initView(context);
	}
	
	public BaseCenterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public BaseCenterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		mContext = context;
		LayoutInflater.from(context).inflate(getLayoutId(), this, true);
		registerListener();
	}
	
	/**
	 * 注册监听
	 */
	protected abstract void registerListener(); 
	
	/**
	 * 布局id
	 * @return
	 */
	protected abstract int getLayoutId();
}
