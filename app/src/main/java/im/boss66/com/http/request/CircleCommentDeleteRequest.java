package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * 朋友圈删除评论
 */
public class CircleCommentDeleteRequest extends BaseDataRequest<String> {
    public CircleCommentDeleteRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String comm_id = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("comm_id", comm_id);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.FRIEND_CIRCLE_COMMET_DELETE;
    }
}
