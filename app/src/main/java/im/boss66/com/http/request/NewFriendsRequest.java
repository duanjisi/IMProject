package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BaseFriends;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/17.
 */
public class NewFriendsRequest extends BaseDataRequest<BaseFriends> {

    public NewFriendsRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> map = new HashMap<String, String>();
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.NEW_FRIENDS_URL;
    }
}
