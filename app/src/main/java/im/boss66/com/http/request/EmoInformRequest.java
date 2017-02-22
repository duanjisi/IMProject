package im.boss66.com.http.request;

import java.util.HashMap;
import java.util.Map;

import im.boss66.com.entity.EmojiInform;
import im.boss66.com.http.BaseDataRequest;
import im.boss66.com.http.HttpUrl;

/**
 * Created by Johnny on 2017/2/18.
 * 表情包详细信息请求
 */
public class EmoInformRequest extends BaseDataRequest<EmojiInform> {

    public EmoInformRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return true;
    }

    @Override
    protected Map<String, String> getParams() {
        String packid = (String) mParams[0];
        Map<String, String> map = new HashMap<String, String>();
        map.put("packid", packid);
        return map;
    }

    @Override
    protected String getApiPath() {
        return HttpUrl.EMO_INFORM_DETAILS_URL;
    }
}
