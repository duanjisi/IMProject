package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * 更换手机号码
 */
public class ChangePhoneRequest extends BaseDataRequest<String> {
    public ChangePhoneRequest(String tag, Object... params) {
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
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile_phone", mobile_phone);
        map.put("mobile_verifycode",mobile_verifycode);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.CHANE_PHONE_NUM;
    }
}
