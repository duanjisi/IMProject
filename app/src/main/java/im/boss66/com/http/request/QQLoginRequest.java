package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.AccountEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/15.
 */
public class QQLoginRequest extends BaseDataRequest<AccountEntity> {

    public QQLoginRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String qq_access_token = (String) mParams[0];
        String qq_username = (String) mParams[1];
        String qq_avatar = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("qq_access_token", qq_access_token);
        map.put("qq_username", qq_username);
        map.put("qq_avatar", qq_avatar);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.LOGIN_QQ_URL;
    }
}
