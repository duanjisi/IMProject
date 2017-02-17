package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/16.
 * 处理好友请求(同意，拒绝)
 */
public class HandleFriendRequest extends BaseDataRequest<String> {

    public HandleFriendRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String id = (String) mParams[0];
        String feedback_mark = (String) mParams[1];//0:拒绝，2：同意
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("feedback_mark", feedback_mark);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.HANDLE_FRIEND_URL;
    }
}
