package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * 点赞or取消赞
 */
public class DoPraiseRequest extends BaseDataRequest<String> {

    public DoPraiseRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String feed_id = (String) mParams[0];
        String type = (String) mParams[1];
        Map<String, String> map = new HashMap<String, String>();
        map.put("feed_id", feed_id);
        map.put("type",type);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.FRIEND_CIRCLE_PRAISE;
    }
}
