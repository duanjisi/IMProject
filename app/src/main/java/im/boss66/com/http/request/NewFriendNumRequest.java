package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.FriendState;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/16.
 * 新朋友请求数
 */
public class NewFriendNumRequest extends BaseDataRequest<FriendState> {

    public NewFriendNumRequest(String tag, Object... params) {
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
        return HttpUrl.NEW_FRIEND_NUM_URL;
    }
}
