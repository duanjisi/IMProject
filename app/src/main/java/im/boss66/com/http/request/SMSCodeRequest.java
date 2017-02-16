package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/15.
 */
public class SMSCodeRequest extends BaseDataRequest<String> {

    public SMSCodeRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String mobile = (String) mParams[0];
        String type = (String) mParams[1];
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile", mobile);
        map.put("type", type);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.CODE_URL;
    }
}
