package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.EmoLove;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/20.
 */
public class EmoCollectionAddRequest extends BaseDataRequest<EmoLove> {

    public EmoCollectionAddRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String emourl = (String) mParams[0];
        String emoname = (String) mParams[1];
        String emodesc = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("emourl", emourl);
        map.put("emoname", emoname);
        map.put("emodesc", emodesc);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.EMO_COLLECTION_ADD_URL;
    }
}
