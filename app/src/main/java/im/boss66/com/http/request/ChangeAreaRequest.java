package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * 修改地区
 */
public class ChangeAreaRequest extends BaseDataRequest<String>{
    public ChangeAreaRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String province = (String) mParams[0];
        String city = (String) mParams[1];
        String district = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("province", province);
        map.put("city", city);
        map.put("district", district);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.CHANE_NAME_SEX_AREA;
    }
}
