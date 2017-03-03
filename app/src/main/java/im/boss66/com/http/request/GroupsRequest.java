package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.BaseGrpMember;
import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/27.
 */
public class GroupsRequest extends BaseModelRequest<BaseGrpMember> {

    public GroupsRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String userid = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", userid);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.QURE_MY_GROUP;
    }
}
