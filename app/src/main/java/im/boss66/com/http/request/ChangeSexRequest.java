package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by GMARUnity on 2017/2/18.
 */
public class ChangeSexRequest extends BaseDataRequest<String>{

    public ChangeSexRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String sex = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("sex", sex);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.CHANE_NAME_SEX_AREA;
    }
}
