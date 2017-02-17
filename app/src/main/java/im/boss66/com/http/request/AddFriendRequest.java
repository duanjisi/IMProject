package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/16.
 * 发送好友请求
 */
public class AddFriendRequest extends BaseDataRequest<String> {

    public AddFriendRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String uid_to = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("uid_to", uid_to);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.ADD_FRIEND_URL;
    }
}
