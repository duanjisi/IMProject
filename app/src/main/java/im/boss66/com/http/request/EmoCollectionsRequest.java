package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BaseEmoCollection;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/20.
 */
public class EmoCollectionsRequest extends BaseDataRequest<BaseEmoCollection> {

    public EmoCollectionsRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String page = (String) mParams[0];
        String size = (String) mParams[1];
        String all = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", page);
        map.put("size", size);
        map.put("all", all);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.EMO_COLLECTIONS_URL;
    }
}
