package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by liw on 2017/4/13.
 */

public class CreateTribeRequest extends BaseDataRequest<String> {
    public CreateTribeRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String name = (String) mParams[0];
        String province_id = (String) mParams[1];
        String city_id = (String) mParams[2];
        String county_id = (String) mParams[3];
        HashMap<String, String> map = new HashMap<>();
        map.put("name",name);
        map.put("province",province_id);
        map.put("city",city_id);
        map.put("district",county_id);
        return map;
    }

    @Override
    protected String getApiPath() {
        return  HttpUrl.CREATE_TRIBE;
    }
}
