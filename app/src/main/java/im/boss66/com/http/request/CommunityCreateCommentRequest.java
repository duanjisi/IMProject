package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * 帖子发表回复评论
 */
public class CommunityCreateCommentRequest extends BaseDataRequest<String> {

    public CommunityCreateCommentRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        String feed_id = (String) mParams[0];
        String content = (String) mParams[1];
        String pid = (String) mParams[2];
        String uid_to = (String) mParams[3];
        Map<String, String> map = new HashMap<String, String>();
        map.put("feed_id", feed_id);
        map.put("content", content);
        map.put("pid", pid);
        map.put("uid_to", uid_to);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.COMMUNITY_CREATE_COMMENT;
    }

}
