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
        Map<String, String> map = new HashMap<>();
        map.put("username", "dsada");
        map.put("password", "12321321");
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.BASE_URL;
    }
}
