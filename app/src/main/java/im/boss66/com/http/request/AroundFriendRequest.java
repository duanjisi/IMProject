package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BaseBaby;
import im.boss66.com.http.BaseRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/5/19.
 */
public class AroundFriendRequest extends BaseRequest<BaseBaby> {

    public AroundFriendRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String geohash = (String) mParams[0];
        String radius = (String) mParams[1];
        String biggest = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("geohash", geohash);
        map.put("radius", radius);
        map.put("biggest", biggest);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.FIND_AROUND_FATE;
    }
}
