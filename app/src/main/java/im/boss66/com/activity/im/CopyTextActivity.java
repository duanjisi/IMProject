package im.boss66.com.activity.im;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2016/10/3.
 */
public class CopyTextActivity extends BaseActivity {

    private CharSequence message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_text);
        message = getIntent().getExtras().getCharSequence("msg", "");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public void copy(View view) {
        if (!message.equals("")) {
            ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(message);
            showToast("已复制", true);
        }
        finish();
    }

    public void rewarding(View view) {

    }
}
