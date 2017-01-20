package im.boss66.com.http;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import im.boss66.com.Utils.MycsLog;

/**
 * Http工具
 */
public class HttpUtil {
    private static final String ENCRYPTION_KEY = "authKey";
    private static final String DEVEICE_ID = "deveiceId";
    private static final String ENCRYPTION_VALUE = "51atgc.com";
    private static final String APP_PLATFORM = "platform";
    /**
     * 获取Post方式加密参数
     *
     * @param params
     * @return
     */
    public static final Map<String, String> getEncryptionParams(Map<String, String> params) {
        if (params == null) {
            return new HashMap<String, String>();
        }
        ArrayList<String> list = new ArrayList<String>();
        Map<String, String> map = new HashMap<>();
        /**
         * 每个请求接口(除了登录接口和不需要用户登录的接口)的参数里面都要附带登录接口返回的登录令牌，
         * 参数名称loginToken，和(登录用户的id)参数名称userId
         令牌规则，微秒时间戳+用户id，两次32位md5加密
         */
//        if (App.getInstance().isLogin()) {
//            params.put(K.Request.TOKEN, App.getInstance().getLoginToken());
//            params.put("login_user_id", App.getInstance().getAccount().getUser_id());
//        }

        MycsLog.v("排序前的数据" + params.toString());
        Iterator<String> iterator = params.keySet().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        Collections.sort(list);
        MycsLog.v("排序后的数据" + list.toString());
        String encryptionValue = "";
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String key = list.get(i);
            if (K.Request.UPLOAD_PHOTO_DATA.equals(key)) {
                map.put(key, params.get(key));
                continue;
            }
            encryptionValue = encryptionValue + params.get(key);
            map.put(key, params.get(key));
        }
        encryptionValue = encryptionValue + ENCRYPTION_VALUE;
        return map;
    }

    public static final String getString(JSONObject o, String key) throws JSONException {
        return HttpUtil.getString(o, key, "");
    }

    public static final String getString(JSONObject o, String key, String defValue)
            throws JSONException {
        String value = defValue;
        if (o.has(key)) {
            value = o.getString(key);
            if (TextUtils.isEmpty(value)) {
                value = defValue;
            }
        }
        return value;
    }

    /**
     * 默认值为0
     *
     * @param o
     * @param key
     * @return
     * @throws JSONException
     */
    public static final int getInt(JSONObject o, String key) throws JSONException {
        return HttpUtil.getInt(o, key, 0);
    }

    public static final int getInt(JSONObject o, String key, int defaultValue) throws JSONException {
        int value = defaultValue;
        if (o.has(key)) {
            value = o.getInt(key);
        }
        return value;
    }

    public static final long getLong(JSONObject o, String key) throws JSONException {
        return HttpUtil.getInt(o, key, 0);
    }

    public static final long getLong(JSONObject o, String key, long defaultValue)
            throws JSONException {
        long value = defaultValue;
        if (o.has(key)) {
            value = o.getLong(key);
        }
        return value;
    }

    @SuppressWarnings("unused")
    private static final void printMap(Map<String, String> map) {
        Map<String, String> m = map;
        Iterator<String> iterator = m.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = m.get(key);
            MycsLog.d("key = " + key + ", value = " + value);
        }
    }

    /**
     * 获取Post方式的Map
     *
     * @param params
     * @return
     */
//	public static final Map<String, String> getPostMap(Map<String, String> params) {
//		ArrayList<String> list = new ArrayList<String>();
//		Map<String, String> map = params;
//		Iterator<String> iterator = map.keySet().iterator();
//		while (iterator.hasNext()) {
//			list.add(iterator.next());
//		}
//		Collections.sort(list);
//		String encryptionValue = "";
//		int size = list.size();
//		for (int i = 0; i < size; i++) {
//			String key = list.get(i);
//			encryptionValue = encryptionValue + map.get(key);
//		}
//		encryptionValue = encryptionValue + Key.ENCRYPTION_VALUE;
//		map.put(Key.ENCRYPTION_KEY, MD5Util.toMD5(encryptionValue));
//		return map;
//
//	}
}
