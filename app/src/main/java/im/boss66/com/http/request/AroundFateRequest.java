package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BaseChildren;
import im.boss66.com.http.BaseDataModel;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/3/15.
 */
public class AroundFateRequest extends BaseDataModel<BaseChildren> {

    public AroundFateRequest(String tag, Object... params) {
        super(tag, params);
    }


    @Override
    protected Map<String, String> getParams() {
        String geohash = (String) mParams[0];
        String radius = (String) mParams[1];
        Map<String, String> map = new HashMap<String, String>();
        map.put("geohash", geohash);
        map.put("radius", radius);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.FIND_AROUND_FATE;
    }
}
