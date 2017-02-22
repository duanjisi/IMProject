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
        String friend_note = (String) mParams[1];
        Map<String, String> map = new HashMap<String, String>();
        map.put("friend_note", friend_note);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.ADD_FRIEND_URL;
    }
}
