package im.boss66.com;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Johnny on 2017/2/21.
 */
public class Constants {
//    public static final String EMO_DIR_PATH =
//            android.os.Environment.getExternalStorageDirectory() + "/HaiMeng/" + ".nomedia/" + App.getInstance().getUid() + "/";
//    public static final String VIDEO_CACHE_PATH =
//            android.os.Environment.getExternalStorageDirectory() + "/HaiMeng/" + "video/";

    //    private static final String BASE_DIR = "/mnt/sdcard/HaiMeng/";
    private static final String BASE_DIR = App.getInstance().getFilesDir().getPath() + File.separator;
    //    public static final String EMO_DIR_PATH = getBaseDir() + ".nomedia/" + App.getInstance().getUid() + "/";
//    public static final String VIDEO_CACHE_PATH = getBaseDir() + "video/";

    public static String EMO_DIR_PATH;
    public static String VIDEO_CACHE_PATH;

    static {
        EMO_DIR_PATH = getBaseDir() + ".nomedia/" + App.getInstance().getUid() + "/";
        VIDEO_CACHE_PATH = getBaseDir() + "video/";
        Log.i("info", "==========EMO_DIR_PATH:" + EMO_DIR_PATH);
    }

    private static String getBaseDir() {
//        File dir;
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            dir = Environment.getExternalStorageDirectory();
//        } else {
//            dir = App.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        }
        String baseDir = "";
        if (Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 20) {
            baseDir = App.getInstance().getFilesDir().getPath() + "/HaiMeng/";
        } else {
            baseDir = Environment.getExternalStorageDirectory() + "/HaiMeng/";
        }
        return baseDir;
    }

    public static final String EMO_DIR_SYSTEM_PATH =
            android.os.Environment.getExternalStorageDirectory() + "/HaiMeng/";

    public static final String ACTION_LOGOUT_RESETING = "im.boss66.com.logout.reseting";

    public static final class Action {
        public static final String EMOJI_EDITED_SEND = "local.EMOJI_EDITED_SEND_ACTION";
        public static final String REFRSH_CHAT_PAGER_DATAS = "local.refresh.chat.pager.datas";
        public static final String REMOVE_CHAT_MESSAGE_DATA = "local.remove.chat.message.data";
        public static final String REFRSH_CHAT_PAGER_NAME = "local.refresh.chat.pager.title.name";
        public static final String REFRSH_CHAT_PAGER = "local.refresh.chat.pager";
        public static final String EXIT_CURRETN_GROUP_REFRESH_DATAS = "local.exist.current.group.refresh.data";
        public static final String CONTACTS_REMOVE_CURRETN_ITEM = "local.contact.remove.current.item";
        public static final String CHAT_NEW_MESSAGE_NOTICE = "local.chat.new.message.notice";
        public static final String CHAT_AGREE_FRIENDSHIP = "local.agree.us.friendship";
        public static final String CHAT_SERVICE_CLOSE = "local.chat.service.close";
        public static final String MAP_MARKER_REFRESH = "local.mapview.marker.refresh";
        public static final String NET_CONENECT_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
        public static final String EXIT_CURRENT_ACTIVITY = "im.boss66.com.EXIT_CURRENT_ACTIVITY";
        public static final String MENU_CAHNGE_CURRENT_TAB = "im.boss66.com.menu.change.current.tab.pager";
        public static final String UPDATE_ACCOUNT_INFORM = "im.boss66.com.update.account.inform";
        public static final String SWITCH_CHAT_MESSAGE = "im.boss66.com.switch.chat.message";
        public static final String VIDEO_CACHE_SUCCESSED = "im.boss66.com.video.cache.successed";
        public static final String VIDEO_CACHE_FAILE = "im.boss66.com.video.cache.fail";
        //显示主页
        public static final String SHOW_HOME_ACTION = "im.boss66.com.SHOW_HOME_ACTION";
        public static final String ACTION_START = "im.boss66.com.ACTION_START";
        public static final String ACTIVITY_CLOSE = "im.boss66.com.ACTIVITY_CLOSE";
        public static final String REFRESH_EMOJI_LIST = "im.boss66.com.refresh.emoji.list";
    }
}
