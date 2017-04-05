package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BaseResult;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * 修改密码
 */
public class ChangeUserPwRequest extends BaseDataRequest<String> {
    protected ChangeUserPwRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String old_pass = (String) mParams[0];
        String new_pass = (String) mParams[1];
        Map<String, String> map = new HashMap<String, String>();
        map.put("old_pass", old_pass);
        map.put("new_pass", new_pass);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.CHANGE_USER_PW;
    }
}
