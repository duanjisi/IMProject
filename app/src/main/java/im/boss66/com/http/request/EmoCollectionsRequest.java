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
        String number = (String) mParams[0];
        String pass = (String) mParams[1];
        Map<String, String> map = new HashMap<String, String>();
        map.put("mobile_phone", number);
        map.put("password", pass);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.EMO_COLLECTIONS_URL;
    }
}
