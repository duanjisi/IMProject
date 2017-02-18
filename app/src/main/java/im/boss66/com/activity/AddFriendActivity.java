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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import im.boss66.com.App;
import im.boss66.com.R;
import im.boss66.com.Utils.ImageLoaderUtils;
import im.boss66.com.Utils.MakeQRCodeUtil;
import im.boss66.com.Utils.UIUtils;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.widget.RoundImageView;

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
    private AccountEntity account;
    private ImageLoader imageLoader;
    private int screenW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        initViews();
    }

    private void initViews() {
        account = App.getInstance().getAccount();
        imageLoader = ImageLoaderUtils.createImageLoader(context);
        tvBack = (TextView) findViewById(R.id.tv_back);
        etAccount = (EditText) findViewById(R.id.et_account);
        tvNumber = (TextView) findViewById(R.id.tv_my_account);
        iv_code = (ImageView) findViewById(R.id.iv_code);
        rlScanning = (RelativeLayout) findViewById(R.id.rl_scanning);
        rlContacts = (RelativeLayout) findViewById(R.id.rl_phone_contacts);
        rlTopBar = (RelativeLayout) findViewById(R.id.rl_top_bar);
        screenW = UIUtils.getScreenWidth(context) * 3 / 5;

        tvBack.setOnClickListener(this);
        rlScanning.setOnClickListener(this);
        rlContacts.setOnClickListener(this);
        iv_code.setOnClickListener(this);

        tvNumber.setText("我的账号：" + account.getUser_id());
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

        RoundImageView header = (RoundImageView) view.findViewById(R.id.iv_head);
        ImageView iv_qcode = (ImageView) view.findViewById(R.id.iv_qcode);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvSex = (TextView) view.findViewById(R.id.tv_sex);
        TextView tvArea = (TextView) view.findViewById(R.id.tv_area);

        tvName.setText(account.getUser_name());
        tvSex.setText(account.getSex());
        tvArea.setText(account.getDistrict_str());

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) iv_qcode.getLayoutParams();
        linearParams.height = screenW;
        linearParams.width = screenW;
        iv_qcode.setLayoutParams(linearParams);
        MakeQRCodeUtil.createQRImage("add_friend:" + account.getUser_id(), screenW, screenW, iv_qcode);
        imageLoader.displayImage(account.getAvatar(), header, ImageLoaderUtils.getDisplayImageOptions());

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
//        popupWindow.showAtLocation(Parent, Gravity.NO_GRAVITY, location[0], location[1]);
        popupWindow.showAsDropDown(parent);
    }

    private String getQrContent() {
        JSONObject object = new JSONObject();
        try {
            object.put("action", "add_friend");
            object.put("user_id", account.getUser_id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    private Drawable getDrawableFromRes(int resId) {
        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resId);
        return new BitmapDrawable(bmp);
    }
}
