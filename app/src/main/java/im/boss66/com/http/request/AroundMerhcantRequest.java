package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BaseBaby;
import im.boss66.com.http.BaseRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/5/19.
 * 查询特定商家的福娃
 */
public class AroundMerhcantRequest extends BaseRequest<BaseBaby> {

    public AroundMerhcantRequest(String tag, Object... params) {
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
        String userid = (String) mParams[3];
        Map<String, String> map = new HashMap<String, String>();
        map.put("geohash", geohash);
        map.put("radius", radius);
        map.put("biggest", biggest);
        map.put("userid", userid);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.FIND_MERCHANTS_BABY;
    }
}
