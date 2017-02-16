package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.AccountEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/15.
 */
public class WXLoginRequest extends BaseDataRequest<AccountEntity> {

    public WXLoginRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String wx_unionid = (String) mParams[0];
        String wx_username = (String) mParams[1];
        String wx_avatar = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("wx_unionid", wx_unionid);
        map.put("wx_username", wx_username);
        map.put("wx_avatar", wx_avatar);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.LOGIN_WX_URL;
    }
}
