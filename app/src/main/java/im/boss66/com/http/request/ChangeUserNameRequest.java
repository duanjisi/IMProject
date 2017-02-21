package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * 修改用户名
 */
public class ChangeUserNameRequest extends BaseDataRequest<String>{

    public ChangeUserNameRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String user_name = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_name", user_name);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.CHANE_NAME_SEX_AREA;
    }
}
