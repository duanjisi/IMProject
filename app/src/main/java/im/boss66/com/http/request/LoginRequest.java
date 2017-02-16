package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.AccountEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/15.
 */
public class LoginRequest extends BaseDataRequest<AccountEntity> {

    public LoginRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String number = (String) mParams[0];
        String pass = (String) mParams[1];
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile_phone", number);
        map.put("password", pass);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.LOGIN_URL;
    }
}
