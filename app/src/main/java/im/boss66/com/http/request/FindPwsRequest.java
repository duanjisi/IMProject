package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/15.
 */
public class FindPwsRequest extends BaseDataRequest<String> {

    public FindPwsRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String mobile_phone = (String) mParams[0];
        String mobile_verifycode = (String) mParams[1];
        String new_pass = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile_phone", mobile_phone);
        map.put("mobile_verifycode", mobile_verifycode);
        map.put("new_pass", new_pass);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.FIND_PWS_URL;
    }
}
