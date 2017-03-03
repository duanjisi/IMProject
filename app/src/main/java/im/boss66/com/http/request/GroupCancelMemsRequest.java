package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/27.
 * 踢出群成员
 */
public class GroupCancelMemsRequest extends BaseModelRequest<String> {

    public GroupCancelMemsRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String groupid = (String) mParams[0];
        String members = (String) mParams[1];
        Map<String, String> map = new HashMap<String, String>();
        map.put("groupid", groupid);
        map.put("members", members);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.REMOVE_GROUP_MEMBERS;
    }
}
