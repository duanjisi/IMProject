package im.boss66.com.http.request;


import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.UpdateInfoEntity;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2016/9/21.
 */
public class CheckUpdateRequest extends BaseDataRequest<UpdateInfoEntity> {

    public CheckUpdateRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> map = new HashMap<String, String>();
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.VERSION_UPDATE;
    }
}
