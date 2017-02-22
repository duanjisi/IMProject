package im.boss66.com.Utils;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: UIUtil
 * @Description: 布局相关方法操作类
 * @author: zyu
 * @date: 2014-12-6 下午2:44:11
 */
public final class UIUtils {
    /**
     * 验证手机格式
     */
    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String num = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    public static String getText(TextView tv) {
        return tv.getText().toString().trim();
    }

    public static void autoIncrement(final TextView target, final float start,
                                     final float end, long duration) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private FloatEvaluator evalutor = new FloatEvaluator();
            private DecimalFormat format = new DecimalFormat("####0.0#");

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                float currentValue = evalutor.evaluate(fraction, start, end);
                target.setText(format.format(currentValue));
            }
        });
        animator.setDuration(duration);
        animator.start();

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 字符串转换unicode
     */
    public static String stringUnicode(String string) {

        StringBuffer unicode = new StringBuffer();

        for (int i = 0; i < string.length(); i++) {

            // 取出每一个字符
            char c = string.charAt(i);

            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    /**
     * 转换dip为px
     *
     * @param context
     * @param dip
     * @return
     */
    public static int dip2px(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 转换px为dip
     *
     * @param context
     * @param px
     * @return
     */
    public static int px2dip(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f * (px >= 0 ? 1 : -1));
    }

    private static int ScreenHeight;
    private static int ScreenWidth;


    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        ScreenHeight = dm.heightPixels;
        return ScreenHeight;
    }

    public static int getScreenWidth(Context context) {
        if (context == null) {
            return 0;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        ScreenWidth = dm.widthPixels;
        return ScreenWidth;
    }

    public static int getStatusBarHeight() {
        return Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
    }

//    public static int getStatusBarHeight(Context context) {
//        Class<?> c = null;
//        Object obj = null;
//        Field field = null;
//        int x = 0, statusBarHeight = 0;
//        try {
//            c = Class.forName("com.android.internal.R$dimen");
//            obj = c.newInstance();
//            field = c.getField("status_bar_height");
//            x = Integer.parseInt(field.get(obj).toString());
//            statusBarHeight = context.getResources().getDimensionPixelSize(x);
//        } catch (Exception e1) {
//            e1.printStackTrace();
//        }
//        return statusBarHeight;
//    }
//
//    public static int getStatusBarHeight(Activity ac) {
//        Rect rect = new Rect();
//        ac.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//        return rect.top;
//    }

//    public static String bitmap2Base64(Bitmap bit) {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bit.compress(CompressFormat.JPEG, 100, bos);// 参数100表示不压缩
//        byte[] bytes = bos.toByteArray();
//        // saveTempFile(bytes);
//        return Base64.encodeToString(bytes, Base64.DEFAULT);
//    }
//
//    public static boolean isNumeric(String str) {
//        Pattern pattern = Pattern.compile("[0-9]+");
//        Matcher isNum = pattern.matcher(str);
//        MycsLog.d("isNumeric = " + isNum.matches());
//        return isNum.matches();
//    }

    public static int setCursorIm(Context context, ImageView im, int num) {
        int width = getScreenWidth(context) / num;
        LayoutParams lp = im.getLayoutParams();
        lp.width = width;

        return width;
    }

    /**
     * 符合标准的字符串。
     *
     * @param str
     * @param start
     * @param end
     * @return
     */
    public static boolean isStandardStr(String str, int start, int end) {
        if (str == null) {
            return false;
        }
        return str.length() >= start && str.length() <= end && isMatchStr(str);
    }

    /**
     * 中文、英文、数字
     *
     * @param str
     * @return
     */
    private static boolean isMatchStr(String str) {
        String content = str;
        String regex = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(content);
        return match.matches();
    }

//    public static void openEditActivityForResult(BaseActivity context, VideoInfoEntity videoInfoEntity, int type,
//                                                 String id) {
//        Intent intent = new Intent(context, VideoEditActivity.class);
//        intent.putExtra(IntentItem.DATA, videoInfoEntity);
//        intent.putExtra(IntentItem.EDIT_TYPE, type);
//        intent.putExtra(IntentItem.ID, id);
//        context.startActivityForResult(intent, Code.Request.EDIT);
//    }

//    public static void openPlayVideoActivity(Context context, VideoDetailEntity detail) {
//        if (!App.getInstance().isLogin()) {
////            Toast.makeText(context, context.getString(R.string.notice_state_login_first_please), Toast.LENGTH_SHORT).show();
//            DialogUtils.showLoginDialog(context);
//        } else {
//            Intent intent = new Intent(context, PlayVideoActivity.class);
//            intent.putExtra(IntentItem.VIDEO_DETAIL, detail);
//            context.startActivity(intent);
//        }
//    }

//    public static void frushCodeView(Context context, TextView btn, Handler handler, int times) {
//        String str = "" + times + context.getResources().getString(R.string.second);
//        btn.setText(str);
//        btn.setEnabled(false);
//        new MyThread(handler, times).start();
//    }

//    public static void frushCode(Context context, final TextView btnShow, int times) {
//
//        final String str1 = context.getResources().getString(R.string.text1);
//        final String str2 = context.getResources().getString(R.string.second);
//        String str = "" + times + str2;
//        final int black = context.getResources().getColor(R.color.font_black);
//        int gray = context.getResources().getColor(R.color.font_gray);
//
//        btnShow.setText(str);
//        btnShow.setTextColor(gray);
//        btnShow.setEnabled(false);
//        Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                switch (msg.what) {
//                    case 0:
//                        int time = msg.arg1;
//                        if (time != 0) {
//                            btnShow.setText("" + time + str2);
//                        } else {
//                            btnShow.setEnabled(true);
//                            btnShow.setTextColor(black);
//                            btnShow.setText(str1);
//                        }
//                        break;
//                }
//            }
//        };
//        new MyThread(handler, times).start();
//    }


    private static class MyThread extends Thread {
        private boolean flag;
        private int seconds;
        private Handler handler;

        public MyThread(Handler mHandler, int time) {
            this.handler = mHandler;
            this.flag = true;
            this.seconds = time;
        }

        @Override
        public void run() {
            super.run();
            while (flag) {
                try {
                    Thread.sleep(1000);
                    seconds--;
                    if (seconds >= 0) {
                        handler.sendMessage(handler.obtainMessage(0, seconds, 1));
                    } else {
                        flag = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
//
//    public static void setTextColor(Context context, String str1, String str2, TextView tv,
//                                    String comment) {
//        MycsLog.i("context:" + context);
//        int color = context.getResources().getColor(
//                R.color.main_color_blue);
//        MycsLog.i("color:" + color);
//        SpannableStringBuilder style;
//        if (str2 != null && !str2.equals("")) {
//            style = new SpannableStringBuilder(str1 + "回复" + str2 + ":"
//                    + comment);
//            style.setSpan(
//                    new ForegroundColorSpan(color), 0, str1.length(),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            style.setSpan(
//                    new ForegroundColorSpan(color), str1.length() + 2, str1.length()
//                            + 2 + str2.length(),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        } else {
//            style = new SpannableStringBuilder(str1 + ":" + comment);
//            style.setSpan(
//                    new ForegroundColorSpan(color), 0, str1.length(),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        tv.setText(style);
//    }

//    public static SpannableStringBuilder createBuilder(Context context, String str1, String str2,
//                                                       String comment) {
//        int color = context.getResources().getColor(
//                R.color.main_color_blue);
//        SpannableStringBuilder style;
//        if (str2 != null && !str2.equals("")) {
//            style = new SpannableStringBuilder(str1 + "回复" + str2 + ":"
//                    + comment);
//            style.setSpan(
//                    new ForegroundColorSpan(color), 0, str1.length(),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            style.setSpan(
//                    new ForegroundColorSpan(color), str1.length() + 2, str1.length()
//                            + 2 + str2.length(),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        } else {
//            style = new SpannableStringBuilder(str1 + ":" + comment);
//
//            MycsLog.i("str1:" + str1);
//            MycsLog.i("comment:" + comment);
//            MycsLog.i("style:" + style);
//
//            style.setSpan(
//                    new ForegroundColorSpan(color), 0, str1.length(),
//                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        return style;
//    }

//    public static SpannableStringBuilder createBuilder(Context context, int count) {
//        int color = context.getResources().getColor(
//                R.color.main_color_blue);
//        SpannableStringBuilder style;
//        String str1 = "全部 ";
//        String msg = str1 + count + "人";
//
//        style = new SpannableStringBuilder(msg);
//        style.setSpan(
//                new ForegroundColorSpan(color), 0, str1.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////        }
//        return style;
//    }


//    public static SpannableStringBuilder createBuilder(Context context, int count) {
//        int color = context.getResources().getColor(
//                R.color.main_color_blue);
//        SpannableStringBuilder style;
//        String str1 = "全部 ";
//        String msg = str1 + count + "人";
//
//        style = new SpannableStringBuilder(msg);
//        style.setSpan(
//                new ForegroundColorSpan(color), 0, str1.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return style;
//    }

//    public static SpannableStringBuilder createBuilder(Context context, CommentEntity comment) {
//        int color = context.getResources().getColor(
//                R.color.main_color_orange3);
//        SpannableStringBuilder style;
////        if (str2 != null && !str2.equals("")) {
////            style = new SpannableStringBuilder(str1 + "回复" + str2 + ":"
////                    + comment);
////            style.setSpan(
////                    new ForegroundColorSpan(color), 0, str1.length(),
////                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////            style.setSpan(
////                    new ForegroundColorSpan(color), str1.length() + 2, str1.length()
////                            + 2 + str2.length(),
////                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////        } else {
//        String str1 = comment.getUser_name() + ":";
//        style = new SpannableStringBuilder(str1 + comment.getContent());
//        style.setSpan(
//                new ForegroundColorSpan(color), 0, str1.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////        }
//        return style;
//    }

//    public static SpannableStringBuilder createBuilder(Context context, String str, String content) {
//        int color = context.getResources().getColor(
//                R.color.main_color_blue);
//        SpannableStringBuilder style;
//        String str1 = "@" + str;
//        style = new SpannableStringBuilder(str1 + content);
//        style.setSpan(
//                new ForegroundColorSpan(color), 0, str1.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////        }
//        return style;
//    }
//
//    public static SpannableStringBuilder createRedBuilder(Context context, String str, String content) {
//        int color = context.getResources().getColor(
//                R.color.color_red_heave);
//        SpannableStringBuilder style;
////        String str1 = "@" + str;
//        String str1 = "有人@我";
//        style = new SpannableStringBuilder(str1 + content);
//        style.setSpan(
//                new ForegroundColorSpan(color), 0, str1.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////        }
//        return style;
//    }

    public static void showView(View v) {
        v.setVisibility(View.VISIBLE);
    }

    public static void hindView(View v) {
        v.setVisibility(View.GONE);
    }

    /**
     * 以秒为单位的时间戳
     *
     * @param time
     * @return
     */
    public static String getTime(String time) {
        long a = Long.parseLong(time);
        long b = (long) (a * 1000.0);
        return TimeUtil.getDateTimeEN(b);
    }


    public static String getDateTime(String time) {
        long a = Long.parseLong(time);
        long b = (long) (a * 1000.0);
        return TimeUtil.getDateTimeEN(b);
    }

    public static String changValue(String si) {
        StringBuffer sb = new StringBuffer();
        String[] aa = {"", "十", "百", "千", "万", "十万", "百万", "千万", "亿", "十亿"};
        String[] bb = {"一", "二", "三", "四", "五", "六", "七", "八", "九"};
        char[] ch = si.toCharArray();
        int maxindex = ch.length;
        // 字符的转换
        // 两位数的特殊转换
        if (maxindex == 2) {
            for (int i = maxindex - 1, j = 0; i >= 0; i--, j++) {
                if (ch[j] != 48) {
                    if (j == 0 && ch[j] == 49) {
                        sb.append(aa[i]);
                        // System.out.print(aa[i]);
                    } else {
                        sb.append(bb[ch[j] - 49] + aa[i]);
                        // System.out.print(bb[ch[j] - 49] + aa[i]);
                    }
                }
            }
            // 其他位数的特殊转换，使用的是int类型最大的位数为十亿
        } else {
            for (int i = maxindex - 1, j = 0; i >= 0; i--, j++) {
                if (ch[j] != 48) {
                    sb.append(bb[ch[j] - 49] + aa[i]);
                    // System.out.print(bb[ch[j] - 49] + aa[i]);
                }
            }
        }
        return sb.toString();

    }

    /**
     * 表情图片匹配
     */
    private static Pattern facePattern = Pattern
            .compile("[\u4e00-\u9fa5]{1,3}");

//    public static SpannableStringBuilder parseFaceByText(Context context,
//                                                         String content) {
//        SpannableStringBuilder builder = new SpannableStringBuilder(content);
//        Matcher matcher = facePattern.matcher(content);
//        while (matcher.find()) {
//            // 使用正则表达式找出其中的数字
//            String str = matcher.group();
//            int resId = 0;
//            try {
//                for (int i = 0; i < GridViewFaceAdapter.getmImageTags().length; i++) {
//
//                    if (GridViewFaceAdapter.getmImageTags()[i].equals("[" + str
//                            + "]")) {
//                        resId = GridViewFaceAdapter.getImageIds()[i];
//                        Drawable d = context.getResources().getDrawable(resId);
//                        d.setBounds(0, 0, 35, 35);// 设置表情图片的显示大小
//                        ImageSpan span = new ImageSpan(d,
//                                ImageSpan.ALIGN_BOTTOM);
//                        builder.setSpan(span, matcher.start() - 1,
//                                matcher.end() + 1,
//                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    }
//                }
//
//            } catch (Exception e) {
//            }
//        }
//        return builder;
//    }

    /**
     * 显示软键盘
     *
     * @param view    软盘依附的View
     * @param context
     */
    public static void showSoftInput(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏软键盘
     *
     * @param view    软盘依附的View
     * @param context
     */
    public static void hideSoftInput(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 隐藏视图
     */

    public static void hintView(View v) {
        v.setVisibility(View.GONE);
    }


    //    /**
//     * 刷新发送验证码按钮的视图
//     *
//     * @param context
//     * @param btnShow 要刷新的控件
//     * @param times   倒计时
//     */
//    public static void frushCheckCodeView(Context context, final TextView btnShow, int times) {
//
//        final String str1 = "发送验证码";
//        final String str2 = context.getResources().getString(R.string.second);
//        String str = "剩余" + times + str2;
//
//        btnShow.setText(str);
//        btnShow.setEnabled(false);
//        Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                switch (msg.what) {
//                    case 0:
//                        int time = msg.arg1;
//                        if (time != 0) {
//                            btnShow.setText("剩余" + time + str2);
//                        } else {
//                            btnShow.setEnabled(true);
//                            btnShow.setText(str1);
//                        }
//                        break;
//                }
//            }
//        };
//        new MyThread(handler, times).start();
//    }
    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point out = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(out);
        } else {
            int width = display.getWidth();
            int height = display.getHeight();
            out.set(width, height);
        }
        return out;
    }

}
