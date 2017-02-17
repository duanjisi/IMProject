package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.SharkIfOffEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by GMARUnity on 2017/2/16.
 */
public class SharkItOffRequest extends BaseDataRequest<SharkIfOffEntity> {

    public SharkItOffRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String access_token = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.SHAKE_IT_OFF;
    }
}
