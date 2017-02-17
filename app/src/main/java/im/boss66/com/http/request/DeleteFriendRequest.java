package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/16.
 * 删除好友请求
 */
public class DeleteFriendRequest extends BaseDataRequest<String> {

    public DeleteFriendRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String fid = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("fid", fid);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.DELETE_FRIEND_URL;
    }
}
