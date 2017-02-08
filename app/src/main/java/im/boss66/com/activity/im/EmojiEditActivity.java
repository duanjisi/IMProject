package im.boss66.com.activity.im;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;
import im.boss66.com.photoedit.OperateUtils;
import im.boss66.com.photoedit.OperateView;
import im.boss66.com.photoedit.TextObject;

/**
 * Created by Johnny on 2017/1/23.
 * 编辑表情
 */
public class EmojiEditActivity extends BaseActivity implements View.OnClickListener {
    public static final String filePath = Environment.getExternalStorageDirectory() + "/imBoss/";
    private TextView tvBack;
    private LinearLayout content_layout;
    private OperateView operateView;
    private String camera_path;
    private String mPath = null;
    private OperateUtils operateUtils;
    private InputMethodManager imm;
    private Button btnSend, btnEmojiAdd;
    private Button btnOption;
    private EditText editText;
    private View editBar;
    private PopupWindow popupWindow;
    private Bitmap resizeBmp = null;
    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (content_layout.getWidth() != 0) {
                    Log.i("LinearLayoutW", content_layout.getWidth() + "");
                    Log.i("LinearLayoutH", content_layout.getHeight() + "");
                    // 取消定时器
                    timer.cancel();
                    fillContent();
                    addFont();
                }
            }
        }
    };

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            myHandler.sendMessage(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji_edit);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        Intent intent = getIntent();
        camera_path = intent.getStringExtra("camera_path");
        operateUtils = new OperateUtils(this);
        initViews();
    }

    private void initViews() {
        tvBack = (TextView) findViewById(R.id.tv_back);
        editBar = findViewById(R.id.bottom_bar);
        content_layout = (LinearLayout) findViewById(R.id.mainLayout);
        btnSend = (Button) findViewById(R.id.btn_send);
        editText = (EditText) findViewById(R.id.editText);
        btnOption = (Button) findViewById(R.id.btn_option);
        btnEmojiAdd = (Button) findViewById(R.id.btn_emoji_add);

        btnOption.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        btnEmojiAdd.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        // 延迟每次延迟10 毫秒 隔1秒执行一次
        timer.schedule(task, 10, 1000);
        resizeBmp = operateUtils.compressionFiller(camera_path,
                content_layout);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
//                finish();
                btnSave();
                break;
            case R.id.btn_option://确定输入文字
                editBar.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
        }
    }


    private void addFont() {
        TextObject textObj = operateUtils.getTextObject("请在此添加文字",
                operateView, 5, 150, 150);
        if (textObj != null) {
//            if (menuWindow != null) {
//                textObj.setColor(menuWindow.getColor());
//            }
//            textObj.setTypeface(typeface);
            textObj.setTextSize(50);
            textObj.commit();
            operateView.addItem(textObj);
            operateView.setOnListener(new OperateView.MyListener() {
                public void onClick(TextObject tObject) {
                    alert(tObject);
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (editBar.getVisibility() == View.VISIBLE) {
                editBar.setVisibility(View.GONE);
            } else {
                finish();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void fillContent() {
//        Bitmap resizeBmp = BitmapFactory.decodeFile(camera_path);
        if (resizeBmp != null) {
            operateView = new OperateView(context, resizeBmp);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    resizeBmp.getWidth(), resizeBmp.getHeight());
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            operateView.setLayoutParams(layoutParams);
            content_layout.addView(operateView);
            operateView.setMultiAdd(false); //设置此参数，可以添加多个文字
        }
    }

//    private void compressed() {
//        Bitmap resizeBmp = operateUtils.compressionFiller(camera_path,
//                content_layout);
////        camera_path = SaveBitmap(resizeBmp, "saveTemp");
//    }

//    // 将生成的图片保存到内存中
//    public String SaveBitmap(Bitmap bitmap, String name) {
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            File dir = new File(Constants.filePath);
//            if (!dir.exists())
//                dir.mkdir();
//            File file = new File(Constants.filePath + name + ".jpg");
//            FileOutputStream out;
//            try {
//                out = new FileOutputStream(file);
//                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
//                    out.flush();
//                    out.close();
//                }
//                return file.getAbsolutePath();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return null;
//    }

    private void btnSave() {
        operateView.save();
        Bitmap bmp = getBitmapByView(operateView);
        if (bmp != null) {
            mPath = saveBitmap(bmp, "saveTemp");
            Intent okData = new Intent();
            okData.putExtra("camera_path", mPath);
            setResult(RESULT_OK, okData);
            this.finish();
        }
    }

    private void alert(final TextObject tObject) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            showPopChat(context, getWindow().getDecorView(), tObject);
        }

//        editBar.setVisibility(View.VISIBLE);
//        if (imm.isActive()) {
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            imm.showSoftInput(editText, 0);
//        } else {
//            imm.showSoftInput((View) editText.getWindowToken(),
//                    InputMethodManager.SHOW_FORCED);
//        }
//
//        editText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                return false;
//            }
//        });
//
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(final CharSequence s, int start, int before, int count) {
//                Log.i("info", "=====sss:" + s.toString());
//                myHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        tObject.setText(s.toString());
//                        tObject.commit();
//                        operateView.invalidate();
//                    }
//                });
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    // 将模板View的图片转化为Bitmap
    public Bitmap getBitmapByView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    // 将生成的图片保存到内存中
    public String saveBitmap(Bitmap bitmap, String name) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(filePath);
            if (!dir.exists())
                dir.mkdir();
            File file = new File(filePath + name + ".jpg");
            FileOutputStream out;

            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void showPopChat(final Context context, View parent, final TextObject tObject) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.popwindow_item_direct_chat, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, false);
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);

        popupWindow.setAnimationStyle(R.style.popwin_anim_style);

        int[] location = new int[2];
        parent.getLocationOnScreen(location);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(getDrawableFromRes(R.drawable.bg_popwindow));
        final EditText editText = (EditText) view.findViewById(R.id.editText);
        LinearLayout topArea = (LinearLayout) view.findViewById(R.id.top_area);

        if (imm.isActive()) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            imm.showSoftInput(editText, 0);
        } else {
            imm.showSoftInput((View) editText.getWindowToken(),
                    InputMethodManager.SHOW_FORCED);
        }

        String text = tObject.getText();
        if (text != null && !text.equals("")) {
            editText.setText(text);
            editText.setSelection(text.length());
        }

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tObject.setText(s.toString());
                tObject.commit();
                operateView.invalidate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        topArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                return false;
            }
        });

        view.findViewById(R.id.btn_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                String msg = editText.getText().toString().trim();
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    private Drawable getDrawableFromRes(int resId) {
        Resources res = getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resId);
        return new BitmapDrawable(bmp);
    }
}
