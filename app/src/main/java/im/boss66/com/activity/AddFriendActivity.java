package im.boss66.com.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

/**
 * Created by Johnny on 2017/1/16.
 */
public class AddFriendActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvBack;
    private EditText etAccount;
    private TextView tvNumber;
    private ImageView iv_code;
    private PopupWindow popupWindow;
    private RelativeLayout rlScanning, rlContacts, rlTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        etAccount = (EditText) findViewById(R.id.et_account);
        tvNumber = (TextView) findViewById(R.id.tv_my_account);
        iv_code = (ImageView) findViewById(R.id.iv_code);
        rlScanning = (RelativeLayout) findViewById(R.id.rl_scanning);
        rlContacts = (RelativeLayout) findViewById(R.id.rl_phone_contacts);
        rlTopBar = (RelativeLayout) findViewById(R.id.rl_top_bar);

        tvBack.setOnClickListener(this);
        rlScanning.setOnClickListener(this);
        rlContacts.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.rl_scanning:
                Intent intent = new Intent(context, CaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_phone_contacts:

                break;
            case R.id.iv_code:
                if (popupWindow == null) {
                    showQrCodePopWindows(context, rlTopBar);
                } else {
                    if (!popupWindow.isShowing()) {
                        showQrCodePopWindows(context, rlTopBar);
                    }
                }
                break;
        }
    }

    private void showQrCodePopWindows(Context context, View parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popwindows_qr_code, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);

        popupWindow.setAnimationStyle(R.style.PopupTitleBarAnim);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getDrawableFromRes(R.drawable.bg_popwindow));


        int[] location = new int[2];
        parent.getLocationOnScreen(location);
//        popupWindow.showAtLocation(Parent, Gravity.NO_GRAVITY, location[0], location[1]);
        popupWindow.showAsDropDown(parent);
    }

    private Drawable getDrawableFromRes(int resId) {
        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resId);
        return new BitmapDrawable(bmp);
    }
}
