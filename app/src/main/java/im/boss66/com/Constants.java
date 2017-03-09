package im.boss66.com;

/**
 * Created by Johnny on 2017/2/21.
 */
public class Constants {
    public static final String EMO_DIR_PATH =
            android.os.Environment.getExternalStorageDirectory() + "/HaiMeng/" + App.getInstance().getUid() + "/";
    public static final String ACTION_LOGOUT_RESETING = "im.boss66.com.logout.reseting";

    public static final class Action {
        public static final String EMOJI_EDITED_SEND = "local.EMOJI_EDITED_SEND_ACTION";
    }
}
