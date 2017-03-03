package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.GroupInform;
import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/27.
 * 查询群成员
 */
public class GroupMembersRequest extends BaseModelRequest<GroupInform> {

    public GroupMembersRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String groupid = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("groupid", groupid);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.QURE_GROUP_MEMBERS;
    }
}
