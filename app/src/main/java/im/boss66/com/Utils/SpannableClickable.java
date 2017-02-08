package im.boss66.com.Utils;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import im.boss66.com.App;
import im.boss66.com.R;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public abstract class SpannableClickable extends ClickableSpan implements View.OnClickListener {

    private int DEFAULT_COLOR_ID = R.color.color_8290AF;
    /**
     * text颜色
     */
    private int textColor ;

    public SpannableClickable() {
        this.textColor = App.getInstance().getResources().getColor(DEFAULT_COLOR_ID);
    }

    public SpannableClickable(int textColor){
        this.textColor = textColor;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        ds.setColor(textColor);
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }
}
