package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.GroupEntity;
import im.boss66.com.http.BaseModelRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/27.
 */
public class GroupCreateRequest extends BaseModelRequest<GroupEntity> {

    public GroupCreateRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String creator = (String) mParams[0];
        String members = (String) mParams[1];
        String name = (String) mParams[2];
        Map<String, String> map = new HashMap<String, String>();
        map.put("creator", creator);
        map.put("members", members);
        map.put("name", name);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.CREATE_GROUP_URL;
    }
}
