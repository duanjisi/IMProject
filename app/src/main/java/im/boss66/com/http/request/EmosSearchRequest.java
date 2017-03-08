package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BaseEmo;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/3/7.
 */
public class EmosSearchRequest extends BaseDataRequest<BaseEmo> {

    public EmosSearchRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String key = (String) mParams[0];
        String cate = (String) mParams[1];
        String page = (String) mParams[2];
        String size = (String) mParams[3];
        Map<String, String> map = new HashMap<String, String>();
        map.put("key", key);
        map.put("cate", cate);
        map.put("page", page);
        map.put("size", size);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.EMOS_SEARCH;
    }
}
