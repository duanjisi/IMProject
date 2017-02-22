package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/20.
 */
public class EmoCollectionDeleteRequest extends BaseDataRequest<String> {

    public EmoCollectionDeleteRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String collectid = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("collectid", collectid);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.EMO_COLLECTIONS_DELETE_URL;
    }
}
