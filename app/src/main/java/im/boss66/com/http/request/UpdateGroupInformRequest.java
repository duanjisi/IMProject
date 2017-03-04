package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/3/3.
 */
public class UpdateGroupInformRequest extends BaseModelRequest<String> {

    public UpdateGroupInformRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String notice = (String) mParams[0];
        String name = (String) mParams[1];
        Map<String, String> map = new HashMap<String, String>();
        map.put("notice", notice);
        map.put("name", name);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.UPDATE_GROUP_INFORMS;
    }
}
