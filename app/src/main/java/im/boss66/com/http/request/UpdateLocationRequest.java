package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * 更新位置
 */
public class UpdateLocationRequest extends BaseDataRequest<String> {
    public UpdateLocationRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String access_token = (String) mParams[0];
        String lng = (String) mParams[1];
        String lat = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("lng", lng);
        map.put("lat", lat);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.UPDATE_LOCATION;
    }
}
