package im.boss66.com.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import im.boss66.com.App;
import im.boss66.com.Session;
import im.boss66.com.Utils.Base64Utils;
import im.boss66.com.Utils.PrefKey;
import im.boss66.com.Utils.PreferenceUtils;
import im.boss66.com.activity.ImageGridActivity;
import im.boss66.com.db.MessageDB;
import im.boss66.com.db.dao.ConversationHelper;
import im.boss66.com.domain.EaseUser;
import im.boss66.com.entity.AccountEntity;
import im.boss66.com.entity.BaseConversation;
import im.boss66.com.entity.MessageItem;

public class Utils {

    private Utils() {
    }

    public static String getMd5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("NewApi")
    public static void enableStrictMode() {
        if (Utils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                vmPolicyBuilder
                        .setClassInstanceLimit(ImageGridActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }


    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @TargetApi(VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;

    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= 19;
    }

    public static List<Size> getResolutionList(Camera camera) {
        Parameters parameters = camera.getParameters();
        List<Size> previewSizes = parameters.getSupportedPreviewSizes();
        return previewSizes;
    }

    public static class ResolutionComparator implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            if (lhs.height != rhs.height)
                return lhs.height - rhs.height;
            else
                return lhs.width - rhs.width;
        }

    }

    public static Bitmap returnBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    public synchronized static String getLocationStr(AMapLocation location) {
        if (null == location) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
        if (location.getErrorCode() == 0) {
            sb.append("定位成功" + "\n");
            sb.append("定位类型: " + location.getLocationType() + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\n");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
            sb.append("提供者    : " + location.getProvider() + "\n");

            sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
            sb.append("角    度    : " + location.getBearing() + "\n");
            // 获取当前提供定位服务的卫星个数
            sb.append("星    数    : " + location.getSatellites() + "\n");
            sb.append("国    家    : " + location.getCountry() + "\n");
            sb.append("省            : " + location.getProvince() + "\n");
            sb.append("市            : " + location.getCity() + "\n");
            sb.append("城市编码 : " + location.getCityCode() + "\n");
            sb.append("区            : " + location.getDistrict() + "\n");
            sb.append("区域 码   : " + location.getAdCode() + "\n");
            sb.append("地    址    : " + location.getAddress() + "\n");
            sb.append("兴趣点    : " + location.getPoiName() + "\n");
            //定位完成的时间
            sb.append("定位时间: " + formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
        } else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
        }
        //定位之后的回调时间
        sb.append("回调时间: " + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
        return sb.toString();
    }

    private static SimpleDateFormat sdf = null;

    public synchronized static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }

    public static void sendMessage(Context context, MessageItem item, EaseUser user, String msg) {
        boolean isGroupChat = false;
        String toUid = user.getUserid();
        String title = user.getNick();
        String toAvatar = user.getAvatar();
        if (user.getMsgType().equals("group")) {
            isGroupChat = true;
        } else {
            isGroupChat = false;
        }
        MessageDB mMsgDB = App.getInstance().getMessageDB();
        AccountEntity account = App.getInstance().getAccount();
        String mMsgId = account.getUser_id() + "_" + toUid;
        Log.i("MessageItem:", "item.getMsgType():" + item.getMsgType() +
                "   item.getMessage():" + item.getMessage() + "   item.getVoiceTime()" + item.getVoiceTime());
        MessageItem mode = new MessageItem(
                item.getMsgType(), "",
                System.currentTimeMillis(), item.getMessage(), 0,
                false, 0, item.getVoiceTime(), "" + System.currentTimeMillis(),
                account.getUser_name(), account.getUser_id(), account.getAvatar());

        MessageItem data = mMsgDB.saveMsg(mMsgId, mode);// 消息保存数据库
        Session.getInstance().sendImMessage(getString(data.getMsgType(), data.getMessage(), toUid, isGroupChat, title, toAvatar));
        if (data.getMsgType() == MessageItem.MESSAGE_TYPE_TXT) {
            saveConversation(context, isGroupChat, title, toAvatar, toUid, data.getMsgType(), data.getMessage());
        } else {
            saveConversation(context, isGroupChat, title, toAvatar, toUid, data.getMsgType(), "");
        }

        if (!TextUtils.isEmpty(msg)) {
            MessageItem mode1 = new MessageItem(
                    MessageItem.MESSAGE_TYPE_TXT, "",
                    System.currentTimeMillis(), msg, 0,
                    false, 0, item.getVoiceTime(), "" + System.currentTimeMillis(),
                    account.getUser_name(), account.getUser_id(), account.getAvatar());

            MessageItem data1 = mMsgDB.saveMsg(mMsgId, mode1);// 消息保存数据库
            Session.getInstance().sendImMessage(getString(MessageItem.MESSAGE_TYPE_TXT, msg, toUid, isGroupChat, title, toAvatar));
            saveConversation(context, isGroupChat, title, toAvatar, toUid, MessageItem.MESSAGE_TYPE_TXT, msg);
//            if (data1.getMsgType() == MessageItem.MESSAGE_TYPE_TXT) {
//                saveConversation(context, isGroupChat, title, toAvatar, toUid, MessageItem.MESSAGE_TYPE_TXT, msg);
//            } else {
//                saveConversation(context, isGroupChat, title, toAvatar, toUid, MessageItem.MESSAGE_TYPE_TXT, "");
//            }
        }
    }

    private static String getString(int msgType, String code, String toUid, boolean isGroupChat, String title, String toAvatar) {
        String tag = "";
        if (isGroupChat) {
            tag = "group";
        } else {
            tag = "unicast";
        }
        String str = "";
        switch (msgType) {
            case MessageItem.MESSAGE_TYPE_EMOTION:
                str = "emotion_" + toUid + "_" + code + "_" + tag + "_" + getExtension(isGroupChat, title, toAvatar);
                break;
            case MessageItem.MESSAGE_TYPE_IMG:
                str = "picture_" + toUid + "_" + code + "_" + tag + "_" + getExtension(isGroupChat, title, toAvatar);
                break;
            case MessageItem.MESSAGE_TYPE_VIDEO:
                str = "video_" + toUid + "_" + code + "_" + tag + "_" + getExtension(isGroupChat, title, toAvatar);
                break;
            case MessageItem.MESSAGE_TYPE_TXT:
                str = "text_" + toUid + "_" + code + "_" + tag + "_" + getExtension(isGroupChat, title, toAvatar);
                break;
            case MessageItem.MESSAGE_TYPE_AUDIO:
                str = "audio_" + toUid + "_" + code + "_" + tag + "_" + getExtension(isGroupChat, title, toAvatar);
                break;
        }
        return str;
    }

    private static String getExtension(boolean isGroupChat, String title, String toAvatar) {
        JSONObject object = new JSONObject();
        AccountEntity account = App.getInstance().getAccount();
        String userid = account.getUser_id();
        try {
            object.put("sender", account.getUser_name());
            object.put("senderID", userid);
            object.put("senderAvartar", account.getAvatar());
            if (isGroupChat) {
                object.put("conversation", title);
                object.put("conversationAvartar", toAvatar);
            } else {
                object.put("conversation", account.getUser_name());
                object.put("conversationAvartar", account.getAvatar());
            }
            return Base64Utils.encodeBase64(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveConversation(Context context, boolean isGroupChat, String name, String avatar, String userid, int type, String content) {
        BaseConversation sation = new BaseConversation();
        sation.setUser_name(name);
        sation.setAvatar(avatar);
        sation.setConversation_id(userid);
        if (isGroupChat) {
            sation.setNewest_msg_type("group");
        } else {
            sation.setNewest_msg_type("unicast");
        }
        sation.setNewest_msg_time("" + System.currentTimeMillis());
        ConversationHelper.getInstance().save(sation);
        String msg = "";
        switch (type) {
            case MessageItem.MESSAGE_TYPE_TXT:
                msg = "我：" + content;
                break;
            case MessageItem.MESSAGE_TYPE_EMOTION:
                if (isGroupChat) {
                    msg = "我发了一条[表情]";
                } else {
                    msg = "[表情]";
                }
                break;
            case MessageItem.MESSAGE_TYPE_IMG:
                if (isGroupChat) {
                    msg = "我发了一条[图片]";
                } else {
                    msg = "[图片]";
                }
                break;
            case MessageItem.MESSAGE_TYPE_AUDIO:
                if (isGroupChat) {
                    msg = "我发了一条[声音]";
                } else {
                    msg = "[声音]";
                }
                break;
            case MessageItem.MESSAGE_TYPE_VIDEO:
                if (isGroupChat) {
                    msg = "我发了一条[视频]";
                } else {
                    msg = "[视频]";
                }
                break;
        }
        String noticeKey = PrefKey.NEWS_NOTICE_KEY + "/" + userid;
        PreferenceUtils.putString(context, noticeKey, msg);
    }
}
