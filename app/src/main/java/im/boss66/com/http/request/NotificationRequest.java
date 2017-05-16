package im.boss66.com.http.request;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataModel;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2016/8/25.
 */
public class NotificationRequest extends BaseDataModel<String> {

    public NotificationRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected Map<String, String> getParams() {
        String users = (String) mParams[0];
        String msgType = (String) mParams[1];
        String message = (String) mParams[2];
        String ext = (String) mParams[3];
        Map<String, String> map = new HashMap<String, String>();
        map.put("users", users);
        map.put("msgtype", msgType);
        map.put("message", message);
        Log.i("info", "================ext:" + ext);
        map.put("ext", ext);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.NOTIFICATION_LINK;
    }
}
