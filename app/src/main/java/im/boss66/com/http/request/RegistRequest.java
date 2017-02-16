package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/15.
 */
public class RegistRequest extends BaseDataRequest<String> {

    public RegistRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String phone = (String) mParams[0];
        String pass = (String) mParams[1];
        String verify_code = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile_phone", phone);
        map.put("password", pass);
        map.put("mobile_verifycode", verify_code);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.REGIST_URL;
    }
}
