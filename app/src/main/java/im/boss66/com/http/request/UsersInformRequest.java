package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BaseUserInform;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/15.
 * 查询用户
 */
public class UsersInformRequest extends BaseDataRequest<BaseUserInform> {

    public UsersInformRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String key = (String) mParams[0];
        String page = (String) mParams[1];
        String size = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("key", key);
        map.put("page", page);
        map.put("size", size);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.QURE_ACCOUNT;
    }
}
