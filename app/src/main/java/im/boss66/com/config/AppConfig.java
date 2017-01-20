package im.boss66.com.config;

import android.net.Uri;

import java.io.File;
import java.io.IOException;

import im.boss66.com.Utils.FileUtil;

/**
 * 应用产品信息
 *
 * @author zyu
 */
public class AppConfig {
    public static final String QQ_APP_ID = "1104059071";
    public static final boolean TEST = false;
    public static final String APP_FILE = "/mycs/";
    public static final String APP_VIDEO = APP_FILE + "Video/";
    public static final String APP_CRASH = APP_FILE + "Crash/";
    public static final String APP_ATTACHMENT = APP_FILE + "Attachment/";
    public static final String APP_AVATAR = APP_FILE + "Avatar/";
    public static final String APP_PICTURE = APP_FILE + "Picture/";
    public static final String APP_SPLASH = APP_FILE + "splash/";

    public boolean isLatest() {
        return true;
    }

    /**
     * 获取视频路径
     *
     * @param videoUrl
     * @return
     */
    // public static String getVideoPath(String videoUrl) {
    // return FileUtil.getExternalStoragePath() + AppConfig.APP_VIDEO +
    // videoUrl;
    // }

    /**
     * 获取视频文件路径
     * <p>
     * 视频ＩＤ
     *
     * @return
     */
//	public static String getVideoPath(int vidoeId) {
//		String dirPath = FileUtil.getExternalStoragePath() + AppConfig.APP_VIDEO;
//		mkDir(dirPath);
//		return dirPath + vidoeId + ".mp4";
//	}
    private static void mkDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 文件存储路径
     *
     * @return
     */
//	public static final String getAppFilePath() {
//		return FileUtil.getExternalStoragePath() + AppConfig.APP_FILE;
//	}

    /**
     * 头像图片存储路径
     *
     * @return
     */
    public static final Uri getAvatarPath() {
        String dirPath = FileUtil.getExternalStoragePath() + AppConfig.APP_AVATAR;
        mkDir(dirPath);
        String filePath = dirPath + "avatar.png";
        return Uri.fromFile(mkFile(filePath));
    }

    /**
     * 临时图片存储路径
     *
     * @return
     */
    public static final Uri getTempPath() {
        String dirPath = FileUtil.getExternalStoragePath() + AppConfig.APP_AVATAR;
        mkDir(dirPath);
        String filePath = dirPath + "temp.png";
        return Uri.fromFile(mkFile(filePath));
    }

    /**
     * 背景图片存储路径
     *
     * @return
     */
    public static final Uri getBackgroundPath() {
        String dirPath = FileUtil.getExternalStoragePath() + AppConfig.APP_AVATAR;
        mkDir(dirPath);
        String filePath = dirPath + "background.png";
        return Uri.fromFile(mkFile(filePath));
    }

    private static File mkFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
