package im.boss66.com.Utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Locale;

import im.boss66.com.BuildConfig;

/**
 * 名医传世自定义Log记录器
 * Created by linzq on 16/4/25.
 *
 * @author linzq
 */
public class MycsLog {

    public final static String TAG = "MycsLog";
    private final static int logLevel = BuildConfig.DEBUG ? Log.VERBOSE : Log.INFO;

    private MycsLog() {

    }

    @Nullable
    private static String getMethodInfo() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return "";
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(MycsLog.class.getName())) {
                continue;
            }
//            if (st.getClassName().equals(BaseDataModel.class.getName())) {
//                continue;
//            }
            return String.format(Locale.getDefault(),
                    "%s.%s(%s:%d)[%s]",
                    st.getClassName(), st.getMethodName(), st.getFileName(), st.getLineNumber(), Thread.currentThread().getName());
        }

        return "";
    }

    private static String appendMethodInfo(String msg) {
        return String.format(Locale.getDefault(), "%s\n%s", getMethodInfo(), msg);
    }

    private static String getTag(@Nullable String tag) {
        if (tag == null) {
            tag = TAG;
        }
        return tag;
    }

    /**
     * 记录,为了便于查看日志,最好调用
     * {@link #( String , String )}
     *
     * @param msg 调试信息
     */
    public static void v(String msg) {
        v(null, msg);
    }

    /**
     * 记录
     */
    public static void v(@Nullable String tag, String msg) {
        if (logLevel <= Log.VERBOSE) {
            Log.v(getTag(tag), appendMethodInfo(msg));
        }
    }

    /**
     * 调试记录,为了便于查看日志,最好调用
     * {@link #d(String, String)}
     *
     * @param msg 调试信息
     */
    public static void d(String msg) {
        d(null, msg);
    }

    /**
     * 调试记录
     */
    public static void d(@Nullable String tag, String msg) {
        if (logLevel <= Log.DEBUG) {
            Log.d(getTag(tag), appendMethodInfo(msg));
        }
    }

    /**
     * 记录信息
     */
    public static void i(String msg) {
        i(null, msg);
    }

    /**
     * 记录信息
     */
    public static void i(@Nullable String tag, String msg) {
        if (logLevel <= Log.INFO) {
            Log.i(getTag(tag), appendMethodInfo(msg));
        }
    }

    /**
     * 记录警告
     */
    public static void w(String msg) {
        w(null, msg);
    }

    /**
     * 记录警告
     */
    public static void w(@Nullable String tag, String msg) {
        if (logLevel <= Log.WARN) {
            Log.w(getTag(tag), appendMethodInfo(msg));
        }
    }

    /**
     * 记录有异常的警告
     */
    public static void w(@Nullable String tag, String msg, Throwable throwable) {
        if (logLevel <= Log.ERROR) {
            Log.w(getTag(tag), msg, throwable);
        } else {
//            reportError(App.getInstance(), throwable);
        }
    }

    /**
     * 记录错误
     */
    public static void e(String msg) {
        e(null, msg);
    }

    /**
     * 记录有异常的错误
     */
    public static void e(Throwable throwable) {
        if (logLevel <= Log.ERROR) {
            e(null, "出现异常错误:\n", throwable);
        } else {
//            reportError(App.getInstance(), throwable);
        }
    }

    /**
     * 记录有异常的错误
     */
    public static void e(Context context, Throwable throwable) {
        if (logLevel <= Log.ERROR) {
            e(null, "出现异常错误:\n", throwable);
        } else {
            reportError(context, throwable);
        }
    }

    /**
     * 记录错误
     */
    public static void e(@Nullable String tag, String msg) {
        if (logLevel <= Log.ERROR) {
            Log.e(getTag(tag), appendMethodInfo(msg));
        } else {
//            reportError(App.getInstance(), tag, msg);
        }
    }

    /**
     * 记录有异常的错误
     */
    public static void e(@Nullable String tag, String msg, Throwable throwable) {
        if (logLevel <= Log.ERROR) {
            Log.e(getTag(tag), appendMethodInfo(msg), throwable);
        } else {
//            reportError(App.getInstance(), throwable);
        }
    }

    /**
     * 把错误记录到友盟上
     */
    public static void reportError(Context context, String tag, String msg) {
        if (!BuildConfig.DEBUG) {
//            MobclickAgent.reportError(context,
//                    String.format(Locale.getDefault(), "%s \n %s", getTag(tag), appendMethodInfo(msg)));
        }
    }

    /**
     * 把异常上报到友盟
     */
    public static void reportError(Context context, Throwable throwable) {
        if (!BuildConfig.DEBUG) {
//            MobclickAgent.reportError(context, throwable);
        }
    }

}
