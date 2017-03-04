package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by admin on 2017/3/3.
 */

public class ChooseLikeRequest extends BaseDataRequest<String > {

    public ChooseLikeRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        return new HashMap<>();
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.CHOOSE_LIKE_LIST;
    }
}
