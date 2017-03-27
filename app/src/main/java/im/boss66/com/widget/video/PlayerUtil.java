package im.boss66.com.widget.video;

import java.util.List;
import java.util.Map;

import im.boss66.com.App;

/**
 * 播放器的相关工具类
 *
 * @author wzz
 */
public final class PlayerUtil {

    /**
     * 拼接字符串组成Map
     *
     * @param map
     * @param separator
     * @return
     */
    public static String[] keyAppendValue(Map<String, List<String>> map, String separator) {
        String[] strs = new String[2];
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        String str1 = "";
        String str2 = "";
        if (map.isEmpty()) {
            sb1.append("").append(separator);
            sb2.append("").append(separator);
        } else {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                sb1.append(entry.getKey()).append(separator);
                sb2.append(list2String(entry.getValue())).append(separator);
            }
        }
        str1 = sb1.toString();
        str2 = sb2.toString();
        strs[0] = str1.substring(0, str1.length() - 1);
        strs[1] = str2.substring(0, str2.length() - 1);
//        MycsLog.i("------------------------------------" + strs[0]);
//        MycsLog.i("------------------------------------" + strs[1]);
        return strs;
    }

    /**
     * 拼接字符串
     *
     * @param list
     * @param
     * @return
     */
    public static String list2String(List<String> list) {
        StringBuilder sb = new StringBuilder();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    /**
     * 获取播放视频在列表中的索引
     *
     * @param course
     * @param readyStudyId
     * @return
     */
//    public static int getPlayingIndex(CourseEntity course) {
//        int readyStudyId = course.getReadyStudyId();
//        if (readyStudyId == -1) {
//            return -1;
//        }
//        int index = 0;
//        if (course != null) {
//            int chapterSize = course.getChapters().size();
//            for (int i = 0; i < chapterSize; i++) {
//                if (course.getChapters().get(i).getId() == readyStudyId) {
//                    index = i;
//                    break;
//                }
//            }
//        }
//        return index;
//    }

    /**
     * 音量值[0, 15]
     *
     * @param progress
     * @return
     */
    public static double computeCurrentVolume(double progress) {
        int max = App.getInstance().getMaxVolume();
        double d = progress / 100.0 * max;
        if (d <= 0) {
            d = 0;
        }
        if (d >= max) {
            d = max;
        }
        return d;
    }

    /**
     * 获取音量值[0, 100]
     *
     * @return
     */
    public static double getCentumVolumeProgress() {
        double cur = App.getInstance().getDoubleVolume();
        int max = App.getInstance().getMaxVolume();
        double d = cur / (max * 1.0) * 100;
        if (d <= 0) {
            d = 0;
        }
        if (d >= 100) {
            d = 100;
        }
        return d;
    }
}
