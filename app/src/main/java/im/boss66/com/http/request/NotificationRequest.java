package im.boss66.com.http.request;

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
        Map<String, String> map = new HashMap<String, String>();
        map.put("users", users);
        map.put("msgtype", msgType);
        map.put("message", message);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.NOTIFICATION_LINK;
    }
}
