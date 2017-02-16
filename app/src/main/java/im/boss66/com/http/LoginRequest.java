package im.boss66.com.http;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.AccountEntity;

/**
 * Created by Johnny on 2017/1/20.
 */
public class LoginRequest extends BaseDataRequest<AccountEntity> {
    public LoginRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String mobile_phone = (String) mParams[0];
        String password = (String) mParams[1];
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile_phone", mobile_phone);
        map.put("password", password);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.LOGIN_URL;
    }
}
